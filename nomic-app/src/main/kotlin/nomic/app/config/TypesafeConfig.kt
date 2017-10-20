package nomic.app.config

import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import nomic.core.NomicConfig

/**
 * @author zdenko.vrabel@wirecard.com
 */
class TypesafeConfig(private val typesafeConfig: Config) : NomicConfig() {

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
			return TypesafeConfig()
		}
	}

	constructor() : this (ConfigFactory.load())

	override fun get(name: String): String? =
		typesafeConfig.getString(name);

}