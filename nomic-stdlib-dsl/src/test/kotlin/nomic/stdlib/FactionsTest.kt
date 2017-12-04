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
package nomic.stdlib

import nomic.core.script.ClasspathScript
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.data.Index
import org.junit.Test

/**
 * @author vrabel.zdenko@gmail.com
 */
class FactionsTest {

	@Test
	fun `compilation of script with groups should create facts in these groups`() {
		val compiler = nomic.compiler.Compiler()
		val script = ClasspathScript("/factions_test.box")
		val facts = compiler.compile(script);

		assertThat(facts)
			.hasSize(8)
			.contains(DebugFact("Hello global"), Index.atIndex(3))
			.contains(DebugFact("Hello group 1"), Index.atIndex(4))
			.contains(DebugFact("Hello group 4"), Index.atIndex(7))
	}


}