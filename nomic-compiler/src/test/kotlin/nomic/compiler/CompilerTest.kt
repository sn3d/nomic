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
package nomic.compiler

import nomic.core.*
import nomic.core.fact.GroupFact
import nomic.core.fact.ModuleFact
import nomic.core.fact.NameFact
import nomic.core.fact.VersionFact
import nomic.core.script.ClasspathScript
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

/**
 * @author vrabel.zdenko@gmail.com
 */
class CompilerTest {

	@Test
	fun testSimpleCompilation() {
		val script = ClasspathScript("/script1.box")
		val facts = Compiler.compile(script)

		assertThat(facts)
			.contains(
				NameFact("script1"),
				GroupFact("nomic-example"),
				VersionFact("1.0.0"),
				ModuleFact("module-a"),
				ModuleFact("module-b"))
	}

}