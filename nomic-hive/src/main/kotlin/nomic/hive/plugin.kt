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
package nomic.hive

import nomic.core.NomicConfig
import nomic.core.*
import nomic.hive.adapter.HiveAdapter
import nomic.hive.adapter.JdbcHiveAdapter


/**
 * Route HIVE related [Fact]s to concrete handlers.
 */
class HivePlugin(private val hive: HiveAdapter, private val schema:String) : Plugin(), Exposable {

	override val exposedVariables: List<Pair<String, String>>
		get() = listOf(
			"hiveSchema" to this.schema
		)

	companion object {

		/**
		 * create [HivePlugin] based on configuration in [NomicConfig].
		 */
		fun init(config: NomicConfig): HivePlugin =
			HivePlugin(
				hive =JdbcHiveAdapter(
					jdbcUrl  = config["hive.jdbc.url"] as String,
					username = config["hive.user"] as String,
					password = config["hive.password"] as String
				),
				schema = config["hive.schema"] as String
			)
	}

	override fun configureMapping(): FactMapping =
		listOf(
			TableFact::class.java to { TableHandler(this, hive) },
			SchemaFact::class.java to { SchemaHandler(hive) }
		)
}


/**
 * This implementation processing [TableFact]s, execute the table script
 * from [BundledBox]. The table is placed in some scheme. The handler first
 * look for [SchemaFact] that is matching to [TableFact] and commit it first. Then
 * the table is commited.
 *
 * @see TableFact
 */
private class TableHandler(private val plugin: HivePlugin, private val hive: HiveAdapter) : FactHandler<TableFact> {

	override fun commit(box: BundledBox, fact: TableFact) {
		// find and commit  schema for table
		val schemaFacts = box.facts.findFactsType(SchemaFact::class.java)
		schemaFacts.find {
			schemaFact -> schemaFact.schema == fact.schema
		}?.apply {
			plugin.commit(box, this)
		}

		// execute scripts
		val tableScript = box.entry(fact.script) ?: throw MissingTableScriptException(fact)
		tableScript.openInputStream().reader().use {
			hive.exec(it, fact.fields)
		}
	}

	override fun rollback(box: InstalledBox, fact: TableFact) {
		if (!fact.keepIt) {
			hive.exec("DROP TABLE IF EXISTS ${fact.schema}.${fact.table}")
		}
	}

}


/**
 * This implementation processing [SchemaFact] and create schema if not exist.
 * The rollback is dropping this schema with all content (CASCADE).
 *
 * @see SchemaFact
 */
private class SchemaHandler(private val hive: HiveAdapter) : FactHandler<SchemaFact> {

	override fun commit(box: BundledBox, fact: SchemaFact) {
		hive.exec("CREATE SCHEMA IF NOT EXIST ${fact.schema}")
	}

	override fun rollback(box: InstalledBox, fact: SchemaFact) {
		if (!fact.keepIt) {
			hive.exec("DROP SCHEMA IF EXIST ${fact.schema} CASCADE")
		}
	}

}