package nomic.hive.definition

import nomic.definition.*
import nomic.hive.CannotDropHiveTableException
import nomic.hive.plugin.HivePlugin

/**
 * @author zdenko.vrabel@wirecard.com
 */
class HiveTableDefinition : Definition, Installable, Removable {

	private val schema: String
	private val table: String
	private val keepIt: Boolean
	private val scriptDef: HiveScriptDefinition

	constructor(schema: String, table: String, script:String, fields: MutableMap<String, Any>, keepIt: Boolean) {
		this.schema = schema;
		this.table  = table;
		this.keepIt = keepIt;
		this.scriptDef = HiveScriptDefinition(script, fields)
	}

	override fun apply(context: InstallContext) {
		//first, create schema if not exist
		context.box.definitions.asSequence()
			.filter { def -> def is HiveSchemaDefinition && def.name == this.schema }
			.map { def -> def as HiveSchemaDefinition }
			.forEach {
				def -> def.apply(context)
			}

		scriptDef.apply(context)
	}

	override fun revert(context: UninstallContext) {
		if (!keepIt) {
			val hive = context.instanceOf(HivePlugin::class.java)
			hive.exec("DROP TABLE ${schema}.${table}");
		}
	}
}