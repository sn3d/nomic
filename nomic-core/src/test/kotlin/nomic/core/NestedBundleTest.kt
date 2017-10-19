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

import com.google.common.collect.ImmutableList
import com.google.common.jimfs.Configuration
import com.google.common.jimfs.Jimfs
import nomic.core.asBundleFrom
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import java.nio.charset.StandardCharsets
import java.nio.file.FileSystem
import java.nio.file.Files

/**
 * @author vrabel.zdenko@gmail.com
 */
class NestedBundleTest {

	private lateinit var fs: FileSystem

	/**
	 * create in-memory filesystem for testing purpose in '/nested-bundle'
	 * with structure:
	 * 	 - ./nomic.box
	 * 	 - ./module-1/nomic.box
	 * 	 - ./module-1/subfolder/file.txt
	 * 	 - ./module-2/nomic.box
	 * 	 - ./module-2/submodule-1/nomic.box
	 *	 - ./module-2/submodule-2/nomic.box
	 */
	@Before
	fun createSimpleFileSystem() {
		fs = Jimfs.newFileSystem(Configuration.unix());
		Files.createDirectories(fs.getPath("/nested-bundle"));
		val p = Files.createFile(fs.getPath("/nested-bundle/nomic.box"));
		Files.write(p, ImmutableList.of("hello-world"), StandardCharsets.UTF_8);

		Files.createDirectories(fs.getPath("/nested-bundle/module-1"));
		Files.createFile(fs.getPath("/nested-bundle/module-1/nomic.box"));

		Files.createDirectories(fs.getPath("/nested-bundle/module-1/subfolder"));
		Files.createFile(fs.getPath("/nested-bundle/module-1/subfolder/file.txt"));

		Files.createDirectories(fs.getPath("/nested-bundle/module-2"));
		Files.createFile(fs.getPath("/nested-bundle/module-2/nomic.box"));

		Files.createDirectories(fs.getPath("/nested-bundle/module-2/submodule-1"));
		Files.createFile(fs.getPath("/nested-bundle/module-2/submodule-1/nomic.box.1"));

		Files.createDirectories(fs.getPath("/nested-bundle/module-2/submodule-2"));
		Files.createFile(fs.getPath("/nested-bundle/module-2/submodule-2/nomic.box.2"));
	}

	@Test
	fun testOneLevelNestedBundle() {
		val parentBundle = "/nested-bundle".asBundleFrom(fs)
		val nestedBundle = NestedBundle(parentBundle, "/module-1")

		val nomicFile = nestedBundle.entry("/nomic.box")!!
		val subfolderFile = nestedBundle.entry("/subfolder/file.txt")!!
		val entries = nestedBundle.entries()

		assertThat(nomicFile.name).isEqualTo("/nomic.box")
		assertThat(subfolderFile.name).isEqualTo("/subfolder/file.txt")
		assertThat(entries).hasSize(2)
	}

	@Test
	fun testNestedOfNestedBundle() {
		val parentBundle = "/nested-bundle".asBundleFrom(fs)
		val nestedBundle = NestedBundle(parentBundle, "/module-2")
		val nestedNestedBundle = NestedBundle(nestedBundle, "/submodule-2")
		val entry = nestedNestedBundle.entry("/nomic.box.2")!!

		assertThat(entry.name).isEqualTo("/nomic.box.2")
	}
}