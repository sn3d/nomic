/*
 * Copyright 2017 vrabel.zdenko@gmail.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package nomic.hive.adapter

import com.github.mustachejava.DefaultMustacheFactory
import nomic.hive.InvalidHiveQueryException
import java.io.Reader
import java.io.StringReader
import java.io.StringWriter
import java.sql.Connection
import java.sql.DriverManager

/**
 * This implementation of [HiveAdapter] use JDBC for connecting to
 * HIVE instance. For placeholder replacing is used Mustache as
 * template engine
 *
 * @author vrabel.zdenko@gmail.com
 */
class JdbcHiveAdapter(jdbcUrl: String, username: String, password: String) : HiveAdapter {

	/// ref to JDBC connections
	private val connection: Connection;

	/// ref to Mustache template engine
	private val mustache: DefaultMustacheFactory;

	/**
	 * initialize and create connection to Hive
	 */
	init {
		Class.forName("org.apache.hive.jdbc.HiveDriver");
		connection = DriverManager.getConnection(jdbcUrl, username, password)
		mustache = DefaultMustacheFactory()
	}

	//-------------------------------------------------------------------------------------------------
	// implemented functions
	//-------------------------------------------------------------------------------------------------


	/**
	 * parse script to queries,replace placeholders with fields, and execute these
	 * queries.
	 */
	override fun exec(script: Reader, fields: Map<String, Any>):Boolean {
		val replacedScript = replacePlaceholders(script, fields)
		return exec(replacedScript)
	}


	/**
	 * parse script to queries and execute them
	 */
	override fun exec(script: Reader):Boolean {
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


	/**
	 * parse string to queries,replace placeholders with fields, and execute these
	 * queries.
	 */
	override fun exec(query: String, fields: Map<String, Any>):Boolean {
		val replacedQuery = replacePlaceholders(StringReader(query), fields).readText()
		return exec(replacedQuery)
	}


	/**
	 * parse string to queries and execute them
	 */
	override fun exec(query: String):Boolean {
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



	//-------------------------------------------------------------------------------------------------
	// private functions
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
	 * "stolen" from HIVE CLI (https://github.com/apache/hive/blob/master/cli/src/java/org/apache/hadoop/hive/cli/CliDriver.java#428)
	 * but I need exactly this functionality to keep it compatible.
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