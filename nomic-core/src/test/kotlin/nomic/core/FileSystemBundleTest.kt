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
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import com.google.common.jimfs.Jimfs
import java.nio.charset.StandardCharsets
import java.nio.file.FileSystem
import java.nio.file.Files


/**
 * @author vrabel.zdenko@gmail.com
 */
class FileSystemBundleTest {

	private lateinit var fs: FileSystem

	/**
	 * create in-memory filesystem for testing purpose in '/test-data/simple-bundle'
	 * with structure:
	 * 	 - ./nomic.box
	 * 	 - ./dir-1/file-1.txt
	 * 	 - ./dir-2/file-2.txt
	 * 	 - ./dir-2/subdir/file-3.txt
	 */
	@Before
	fun createSimpleFileSystem() {
		fs = Jimfs.newFileSystem(Configuration.unix());
		Files.createDirectories(fs.getPath("/test-data/simple-bundle"));
		val p = Files.createFile(fs.getPath("/test-data/simple-bundle/nomic.box"));
		Files.write(p, ImmutableList.of("hello-world"), StandardCharsets.UTF_8);

		Files.createDirectories(fs.getPath("/test-data/simple-bundle/dir-1"));
		Files.createFile(fs.getPath("/test-data/simple-bundle/dir-1/file-1.txt"));

		Files.createDirectories(fs.getPath("/test-data/simple-bundle/dir-2"));
		Files.createFile(fs.getPath("/test-data/simple-bundle/dir-2/file-2.txt"));

		Files.createDirectories(fs.getPath("/test-data/simple-bundle/dir-2/subdir"));
		Files.createFile(fs.getPath("/test-data/simple-bundle/dir-2/subdir/file-3.txt"));
	}

	@Test
	fun `load the simple bundle`() {

		val bundle = "/test-data/simple-bundle".asBundleFrom(fs)
		val entry = bundle.entry("nomic.box")

		assertThat(entry).isNotNull()
		entry!!.openInputStream().reader().use {
			val text = it.readText()
			assertThat(text).contains("hello-world")
		}

	}

	@Test
	fun `testing list of entities in bundle`() {
		val bundle = "/test-data/simple-bundle".asBundleFrom(fs)

		val allEntries = bundle.entries()
		assertThat(allEntries).hasSize(4)

		val entriesWithFile = bundle.entries({e -> e.name.contains("file-")})
		assertThat(entriesWithFile).hasSize(3)
	}
}