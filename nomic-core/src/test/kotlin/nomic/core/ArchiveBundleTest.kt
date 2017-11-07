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
package nomic.core

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

/**
 * @author vrabel.zdenko@gmail.com
 */
class ArchiveBundleTest {

	@Test
	fun `load the ZIPped bundle and get all entries`() {
		val input = ArchiveBundleTest::class.java.getResourceAsStream("/submodules.nomic")!!
		val zippedBundle = ArchiveBundle(input)

		val entries = zippedBundle.entries()
		assertThat(entries).hasSize(5)
	}

	@Test
	fun `ZIPped bundle should open concrete entry and should open his input stream`() {
		val input = ArchiveBundleTest::class.java.getResourceAsStream("/submodules.nomic")!!
		val zippedBundle = ArchiveBundle(input)

		val entry = zippedBundle.entry("submodules/submodule-1/nomic.box")!!
		assertThat(entry.name).isEqualTo("submodules/submodule-1/nomic.box")
		entry.openInputStream().reader().use {
			val content = it.readText()
			assertThat(content)
				.contains("name")
				.contains("submodule-1")
		}
	}

	@Test
	fun `ZIPped bundle should open  concrete entry that start with slash`() {
		val input = ArchiveBundleTest::class.java.getResourceAsStream("/submodules.nomic")!!
		val zippedBundle = ArchiveBundle(input)

		val entry = zippedBundle.entry("/submodules/nomic.box")!!
		assertThat(entry.name).isEqualTo("submodules/nomic.box")
	}
}