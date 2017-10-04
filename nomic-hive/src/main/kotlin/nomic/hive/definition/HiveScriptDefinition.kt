package nomic.hive.definition

import nomic.definition.*
import nomic.hive.plugin.HivePlugin

/**
 * @author zdenko.vrabel@wirecard.com
 */
class HiveScriptDefinition : Definition, Installable {

	private val script: String
	private val fields: MutableMap<String, Any>

	constructor(script:String, fields: MutableMap<String, Any>) {
		this.script = script;
		this.fields = fields;
	}

	override fun apply(context: InstallContext) {
		val hive = context.instanceOf(HivePlugin::class.java)
		context.box.entry(script).openInputStream().reader().use { script ->
			hive.exec(script, fields)
		}
	}

}