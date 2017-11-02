package nomic.hive.dsl

import nomic.core.Exposable
import nomic.core.findFactType
import nomic.core.script.ClasspathScript
import nomic.hive.TableFact
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

/**
 * @author zdenko.vrabel@wirecard.com
 */
class TableDslTest {

	val hiveExposer: Exposable = object : Exposable {
		override val exposedVariables: List<Pair<String, String>>
			get() = listOf(
				"hiveSchema" to "TEST"
			)
	}


	@Test
	fun testTable() {
		val compiler = nomic.compiler.Compiler(expos = listOf(hiveExposer))
		val facts = compiler.compile(ClasspathScript("/test-table.box"))

		Assertions.assertThat(facts)
			.contains(TableFact(
				schema = "TEST",
				table =  "MY_TABLE",
				script = "table_script.q",
				keepIt = false
			));
	}


	@Test
	fun testTableWithDefaultSchema() {
		val compiler = nomic.compiler.Compiler(expos = listOf(hiveExposer))
		val facts = compiler.compile(ClasspathScript("/test-table-default-schema.box"))

		Assertions.assertThat(facts)
			.contains(TableFact(
				schema = "CUSTOM_SCHEMA",
				table =  "MY_TABLE",
				script = "table_script.q",
				keepIt = false
			));
	}


	@Test
	fun testTableWithKeepIt() {
		val compiler = nomic.compiler.Compiler(expos = listOf(hiveExposer))
		val facts = compiler.compile(ClasspathScript("/test-table-keepit.box"))

		Assertions.assertThat(facts)
			.contains(TableFact(
				schema = "TEST",
				table =  "MY_TABLE",
				script = "table_script.q",
				keepIt = true
			));
	}


	@Test
	fun testTableWithFields() {
		val compiler = nomic.compiler.Compiler(expos = listOf(hiveExposer))
		val facts = compiler.compile(ClasspathScript("/test-table-fields.box"))

		val fact = facts.findFactType(TableFact::class.java)
		val fields = fact.fields

		assertThat(fields)
			.containsEntry("FIELD_A", "A")
			.containsEntry("FIELD_B", "B OVERRIDE")
			.containsEntry("DEF_SCHEMA", "CUSTOM")
	}


}