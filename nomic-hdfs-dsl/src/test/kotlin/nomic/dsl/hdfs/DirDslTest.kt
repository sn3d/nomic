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
package nomic.dsl.hdfs

import nomic.core.script.ClasspathScript
import nomic.hdfs.DirFact
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

/**
 * @author vrabel.zdenko@gmail.com
 */
class DirDslTest {

	@Test
	fun `compilation of script with dir should create dir fact correctly`() {
		val compiler = nomic.compiler.Compiler(appDir = "/app")
		val facts = compiler.compile(ClasspathScript("/test-dir.box"))

		assertThat(facts)
				.contains(DirFact(
					dir =  "/app/nomic/test/somedir",
					keepIt = false
				));
	}


	@Test
	fun `compilation of script with KeepIt dir, with dir should create dir fact correctly`() {
		val compiler = nomic.compiler.Compiler(appDir = "/app")
		val facts = compiler.compile(ClasspathScript("/test-dir-absolute-keepit.box"))

		assertThat(facts)
			.contains(DirFact(
				dir =  "/somedir",
				keepIt = true
			));
	}


	@Test
	fun `compile script with changed working dir should create fact correctly`() {
		val compiler = nomic.compiler.Compiler(appDir = "/app")
		val facts = compiler.compile(ClasspathScript("/test-dir-changed-workingdir.box"))

		assertThat(facts)
			.contains(DirFact(
				dir =  "/app/nomic/test/workingdir/somedir"
			));
	}
}

