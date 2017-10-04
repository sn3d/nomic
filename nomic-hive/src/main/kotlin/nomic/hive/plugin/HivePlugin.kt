package nomic.hive.plugin

import nomic.plugin.NomicPlugin
import java.io.Reader
import java.sql.Connection
import java.sql.DriverManager
import com.github.mustachejava.DefaultMustacheFactory
import nomic.NomicConfig
import nomic.hive.InvalidHiveQueryException
import java.io.StringReader
import java.io.StringWriter


/**
 * @author zdenko.vrabel@wirecard.com
 */
class HivePlugin(jdbcUrl: String, username: String, password: String, schema: String) : NomicPlugin {

	/// ref to JDBC connections
	private val connection: Connection;

	/// ref to Mustache template engine
	private val mustache: DefaultMustacheFactory;

	/**
	 * initialize and create connection to Hive
	 */
	init {
		Class.forName("org.apache.hive.jdbc.HiveDriver");
		val jdbcConnectionString: String

		if (jdbcUrl.endsWith("/")) {
			jdbcConnectionString = "${jdbcUrl}${schema}"
		} else {
			jdbcConnectionString = "${jdbcUrl}/${schema}"
		}

		connection = DriverManager.getConnection(jdbcUrl + "/${schema}", username, password)
		mustache = DefaultMustacheFactory()
	}


	/**
	 * parse script to queries and execute them.
	 */
	fun exec(script: Reader, fields: Map<String, Any>):Boolean {
		val replacedScript = replacePlaceholders(script, fields)
		return exec(replacedScript)
	}


	fun exec(script: Reader):Boolean {
		val filteredScript = removeComments(script)
		val queries = splitSemiColon(filteredScript.readText());
		var allRes = true;
		for (query: String in queries) {
			var res = exec(query)
			if (!res) {
				allRes = false;
			}
		}
		return allRes;
	}


	fun exec(query: String, fields: Map<String, Any>):Boolean {
		val replacedQuery = replacePlaceholders(StringReader(query), fields).readText()
		return exec(replacedQuery)
	}


	fun exec(query: String):Boolean {
		val trimmed = query.trim()
		if (!trimmed.isBlank()) {
			try {
				val stmt = connection.prepareStatement(trimmed)
				return stmt.execute();
			} catch (e: Exception) {
				throw InvalidHiveQueryException(trimmed, e)
			}
		}
		return true;
	}

	companion object {

		fun createPlugin(config: NomicConfig) =
			HivePlugin(
				jdbcUrl = config.hiveJdbcUrl,
				username = config.hiveUsername,
				password = config.hivePassword,
				schema = config.hiveSchema
			)
	}


	//-------------------------------------------------------------------------------------------------
	// private methods
	//-------------------------------------------------------------------------------------------------


	private fun replacePlaceholders(script: Reader, fields:Map<String, Any>): Reader {
		val sm = "${'$'}{"
		val em = "}"
		val template = mustache.compile(script, "", sm, em);
		val result = StringWriter()
		template.execute(result, fields)
		return result.toString().reader()
	}


	/**
	 * remove lines starting with '--'
	 */
	private fun removeComments(query: Reader): Reader =
		query.readLines()
			.filter { line -> !line.startsWith("--") }
			.reduce {a, b -> a + " " + b }
			.reader()


	/**
	 * stolen from HIVE CLI (https://github.com/apache/hive/blob/master/cli/src/java/org/apache/hadoop/hive/cli/CliDriver.java#428)
	 */
	private fun splitSemiColon(line: String): List<String> {
		var insideSingleQuote = false
		var insideDoubleQuote = false
		var escape = false
		var beginIndex = 0
		val ret:MutableList<String> = mutableListOf()
		for (index in 0..line.length - 1) {
			if (line[index] == '\'') {
				// take a look to see if it is escaped
				if (!escape) {
					// flip the boolean variable
					insideSingleQuote = !insideSingleQuote
				}
			} else if (line[index] == '\"') {
				// take a look to see if it is escaped
				if (!escape) {
					// flip the boolean variable
					insideDoubleQuote = !insideDoubleQuote
				}
			} else if (line[index] == ';') {
				if (insideSingleQuote || insideDoubleQuote) {
					// do not split
				} else {
					// split, do not include ; itself
					ret.add(line.substring(beginIndex, index))
					beginIndex = index + 1
				}
			} else {
				// nothing to do
			}
			// set the escape
			if (escape) {
				escape = false
			} else if (line[index] == '\\') {
				escape = true
			}
		}
		ret.add(line.substring(beginIndex))
		return ret
	}

}