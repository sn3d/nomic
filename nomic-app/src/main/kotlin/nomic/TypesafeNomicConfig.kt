package nomic

import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import java.io.File
import java.net.URI

/**
 * @author zdenko.vrabel@wirecard.com
 */
class TypesafeNomicConfig : NomicConfig {

	constructor() : this (ConfigFactory.load())

	constructor(typesafeConfig: Config) : super(
		user = typesafeConfig.getString("nomic.user"),
		hdfsAppDir = typesafeConfig.getString("nomic.hdfs.app.dir"),
		hdfsRepositoryDir = typesafeConfig.getString("nomic.hdfs.repository.dir"),

		simulatorEnabled = typesafeConfig.getBoolean("simulator.enabled"),
		simulatorPath = typesafeConfig.getString("simulator.path"),

		hadoopNameNode = typesafeConfig.getString("hadoop.namenode"),
		hadoopCoreSiteXml = typesafeConfig.getString("hadoop.core.site"),
		hadoopHdfsSiteXml = typesafeConfig.getString("hadoop.hdfs.site"),

		hiveJdbcUrl = typesafeConfig.getString("hive.jdbc.url"),
		hiveUsername = typesafeConfig.getString("hive.user"),
		hivePassword = typesafeConfig.getString("hive.password"),
		hiveSchema = typesafeConfig.getString("hive.schema")

	) {	}

	companion object {
		@JvmStatic
		fun loadDefaultConfiguration(): NomicConfig {
			var home = System.getProperty("nomic.home");
			if (home.isNullOrBlank()) {
				home = System.getProperty("user.home")
			}

			val configFile = System.getProperty("config.file")
			if (configFile.isNullOrBlank()) {
				System.setProperty("config.file", "${home}/.nomic/conf/nomic.conf")
			}
			return TypesafeNomicConfig()
			//val appConfigFile = File("${home}/conf/nomic.conf")
			//val appConfig = ConfigFactory.parseFile(appConfigFile)
			//val finalConfig = ConfigFactory.load(appConfig)
			//return TypesafeNomicConfig(finalConfig)
		}
	}
}