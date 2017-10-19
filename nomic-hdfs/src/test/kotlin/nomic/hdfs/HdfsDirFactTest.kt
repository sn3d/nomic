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
package nomic.hdfs

import com.google.common.jimfs.Configuration
import com.google.common.jimfs.Jimfs
import nomic.core.BundledBox
import nomic.core.InstalledBox
import nomic.core.asBundleFrom
import nomic.hdfs.adapter.HdfsSimulator
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import java.nio.file.FileSystem
import java.nio.file.Files

/**
 * @author vrabel.zdenko@gmail.com
 */
class HdfsDirFactTest {

	private lateinit var fs: FileSystem
	private lateinit var box: BundledBox
	private lateinit var plugin: HdfsPlugin

	@Before
	fun setupPlugin() {
		// prepare FS
		fs = Jimfs.newFileSystem(Configuration.unix());
		Files.createDirectories(fs.getPath("/hdfs"));

		// create box as source
		box = BundledBox("/bundle".asBundleFrom(fs));

		// create HDFS adapter and plugin
		plugin = HdfsPlugin(HdfsSimulator("/hdfs", fs))
	}


	@Test
	fun testCommitAndRollbackDir() {
		var fact = DirFact(dir = "/test/dir")

		// commit resource fact and check
		plugin.commit(box, fact)
		assertThat(fs.getPath("/hdfs/test/dir")).isDirectory()

		// rollback same resource fact
		plugin.rollback(InstalledBox.notAvailable, fact)
		assertThat(fs.getPath("/hdfs/test/dir")).doesNotExist()
	}


	@Test
	fun testCommitAndRollbackDirWithKeepIt() {
		var fact = DirFact(dir = "/test/keepit", keepIt = true)

		// commit resource fact and check
		plugin.commit(box, fact)
		assertThat(fs.getPath("/hdfs/test/keepit")).isDirectory()

		// rollback same resource fact
		plugin.rollback(InstalledBox.notAvailable, fact)
		assertThat(fs.getPath("/hdfs/test/keepit")).isDirectory()
	}
}