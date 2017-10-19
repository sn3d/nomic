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
package nomic.hdfs.adapter

import com.google.common.jimfs.Configuration
import com.google.common.jimfs.Jimfs
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import java.nio.file.FileSystem
import java.nio.file.Files

/**
 * @author vrabel.zdenko@gmail.com
 */
class HdfsSimulatorTest {

	private lateinit var fs: FileSystem

	@Before
	fun setupFileSystem() {
		fs = Jimfs.newFileSystem(Configuration.unix());
		Files.createDirectories(fs.getPath("/hdfs"));

		// files and directories for testDelete
		Files.createDirectories(fs.getPath("/hdfs/test-delete/dir-1"));
		Files.createDirectories(fs.getPath("/hdfs/test-delete/dir-2"));
		Files.createDirectories(fs.getPath("/hdfs/test-delete/dir-2/subdir"));
		Files.createFile(fs.getPath("/hdfs/test-delete/file-1.txt"));
		Files.createFile(fs.getPath("/hdfs/test-delete/dir-2/file-2.txt"));

		// files for testListFiles
		Files.createDirectories(fs.getPath("/hdfs/test-list/dir-1"));
		Files.createDirectories(fs.getPath("/hdfs/test-list/dir-2"));
		Files.createDirectories(fs.getPath("/hdfs/test-list/dir-2/subdir"));
		Files.createFile(fs.getPath("/hdfs/test-list/file-1.txt"));
		Files.createFile(fs.getPath("/hdfs/test-list/dir-2/file-2.txt"));
		Files.createFile(fs.getPath("/hdfs/test-list/dir-2/file-3.txt"));
		Files.createFile(fs.getPath("/hdfs/test-list/dir-2/subdir/file-4.txt"));

		// files for 'append'
		Files.createFile(fs.getPath("/hdfs/append.txt"));
	}

	@Test
	fun testCreateAndWrite() {
		// write to file
		val hdfs = HdfsSimulator("/hdfs", fs)
		hdfs.create("/folder/subfolder/text-file.txt").writer().use {
			it.write("Hello World")
		}

		//check result
		val path = fs.getPath("/hdfs/folder/subfolder/text-file.txt")
		assertThat(Files.isRegularFile(path)).isTrue()

		Files.newInputStream(path).reader().use {
			val content = it.readText()
			assertThat(content).contains("Hello World")
		}
	}

	@Test
	fun testDeleteDir() {
		// delete dir-2
		val hdfs = HdfsSimulator("/hdfs", fs)
		hdfs.delete("/test-delete/dir-2")

		//check result
		var path = fs.getPath("/hdfs/test-delete/dir-2/file-2.txt")
		assertThat(Files.exists(path)).isFalse()

		path = fs.getPath("/hdfs/test-delete/dir-2")
		assertThat(Files.exists(path)).isFalse()

		path = fs.getPath("/hdfs/test-delete/dir-1")
		assertThat(Files.exists(path)).isTrue()

		// delete all
		hdfs.delete("/test-delete")

		path = fs.getPath("/hdfs/test-delete")
		assertThat(Files.exists(path)).isFalse()
	}

	@Test
	fun testListFilesNonRecursive() {
		val hdfs = HdfsSimulator("/hdfs", fs)

		//non-recursive
		var files = hdfs.listFiles("/test-list/dir-2", recursive = false).toList()
		assertThat(files)
			.hasSize(2)
			.contains("/test-list/dir-2/file-2.txt")
			.contains("/test-list/dir-2/file-3.txt")
	}

	@Test
	fun testListFilesRecursive() {
		val hdfs = HdfsSimulator("/hdfs", fs)

		//non-recursive
		var files = hdfs.listFiles("/test-list", recursive = true).toList()
		assertThat(files)
			.hasSize(4)
			.contains("/test-list/file-1.txt")
			.contains("/test-list/dir-2/file-2.txt")
			.contains("/test-list/dir-2/file-3.txt")
			.contains("/test-list/dir-2/subdir/file-4.txt")
	}

}