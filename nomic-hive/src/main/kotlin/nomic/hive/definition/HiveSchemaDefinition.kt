package nomic.hive.definition

import nomic.definition.*
import nomic.hive.plugin.HivePlugin

/**
 * @author zdenko.vrabel@wirecard.com
 */
class HiveSchemaDefinition(val name: String) : Definition, Installable{

	override fun apply(context: InstallContext) {
		val hive = context.instanceOf(HivePlugin::class.java)
		hive.exec("CREATE SCHEMA IF NOT EXISTS ${name}")
	}

}