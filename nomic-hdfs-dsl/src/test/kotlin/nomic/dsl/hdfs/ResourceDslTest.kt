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
import nomic.hdfs.ResourceFact
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

/**
 * @author vrabel.zdenko@gmail.com
 */
class ResourceDslTest {

	@Test
	fun `script with resource should be compiled into resource fact`() {
		val compiler = nomic.compiler.Compiler(appDir = "/app")
		val facts = compiler.compile(ClasspathScript("/test-compile-resource.box"))

		assertThat(facts)
			.contains(ResourceFact(
				source = "/somefile.txt",
				dest =  "/app/nomic/test/somefile.txt",
				keepIt = false
			));
	}


	@Test
	fun `the resource in script with destination as working dir should be compiled into correct fact`() {
		val compiler = nomic.compiler.Compiler(appDir = "/app")
		val facts = compiler.compile(ClasspathScript("/test-resource-dest-workingdir.box"))

		assertThat(facts)
			.contains(ResourceFact(
				source = "/somefile.txt",
				dest =  "/app/nomic/test/dir/file.txt"
			));
	}


	@Test
	fun `the resource with absolute dest dir should be compiled into correct fact`() {
		val compiler = nomic.compiler.Compiler(appDir = "/app")
		val facts = compiler.compile(ClasspathScript("/test-resource-dest-absolute.box"))

		assertThat(facts)
			.contains(ResourceFact(
				source = "/somefile.txt",
				dest =  "/dir/file.txt"
			));
	}


	@Test
	fun `compile resource with changed working dir should have correct dest dir`() {
		val compiler = nomic.compiler.Compiler(appDir = "/app")
		val facts = compiler.compile(ClasspathScript("/test-resource-changed-workingdir.box"))

		assertThat(facts)
			.contains(ResourceFact(
				source = "/somefile.txt",
				dest =  "/app/nomic/test/defaultdir/somefile.txt"
			));
	}


	@Test
	fun `compile resource with changed working dir to absolute`() {
		val compiler = nomic.compiler.Compiler(appDir = "/app")
		val facts = compiler.compile(ClasspathScript("/test-resource-changed-workingdir-absolute.box"))

		assertThat(facts)
			.contains(ResourceFact(
				source = "/somefile.txt",
				dest =  "/defaultdir/somefile.txt"
			));
	}


	@Test
	fun `compile reosurce with keepIt set to true`() {

		val compiler = nomic.compiler.Compiler(appDir = "/app")
		val facts = compiler.compile(ClasspathScript("/test-resource-keepit.box"))

		assertThat(facts)
			.contains(ResourceFact(
				source = "/somefile.txt",
				dest =  "/app/nomic/test/somefile.txt",
				keepIt = true
			));
	}
}