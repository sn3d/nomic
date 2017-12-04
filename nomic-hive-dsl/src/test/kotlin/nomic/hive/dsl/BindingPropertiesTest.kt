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
package nomic.hive.dsl

import nomic.core.Exposable
import nomic.core.script.ClasspathScript
import nomic.hive.SchemaFact
import org.assertj.core.api.Assertions
import org.junit.Test

/**
 * @author vrabel.zdenko@gmail.com
 */
class BindingPropertiesTest {

	val hiveExposer: Exposable = object : Exposable {
		override val exposedVariables: List<Pair<String, String>>
			get() = listOf(
				"hiveSchema" to "TEST",
				"hiveJdbcUrl" to "jdbc://hive",
				"hiveUser" to "test_user"
			)
	}

	@Test
	fun `the hive schema, jdbcUrl and user must be exposed`() {
		val compiler = nomic.compiler.Compiler(expos = listOf(hiveExposer))
		val facts = compiler.compile(ClasspathScript("/test-properties.box"))

	}

}