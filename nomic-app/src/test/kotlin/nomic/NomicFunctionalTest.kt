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
package nomic

import com.google.common.jimfs.Configuration
import com.google.common.jimfs.Jimfs
import nomic.app.NomicApp
import nomic.app.SimpleConfig
import nomic.core.BoxRef
import nomic.core.Bundle
import nomic.core.findBox
import nomic.hdfs.HdfsPlugin
import nomic.hdfs.ResourceFact
import org.apache.commons.io.IOUtils
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import java.nio.file.FileSystem
import java.nio.file.Files

/**
 * @author vrabel.zdenko@gmail.com
 */
class NomicFunctionalTest {

	private lateinit var fs: FileSystem
	private lateinit var app: NomicInstance

	@Before
	fun setup() {
		// prepare FS
		fs = Jimfs.newFileSystem(Configuration.unix());
		//fs = FileSystems.getDefault()

		//prepare HDFS folder
		Files.createDirectories(fs.getPath("./build/hdfs"));

		//prepare test data for 'simple-bundle'
		Files.createDirectories(fs.getPath("./build/bundles/simple-bundle"));
		fs.copyResource("/bundles/simple-bundle/nomic.box", "./build/bundles/simple-bundle/nomic.box")
		fs.copyResource("/bundles/simple-bundle/file.txt",  "./build/bundles/simple-bundle/file.txt")
		fs.copyResource("/bundles/simple-bundle/file-update.txt",  "./build/bundles/simple-bundle/file-update.txt")

		//prepare test data for 'simple-bundle-ng'
		Files.createDirectories(fs.getPath("./build/bundles/simple-bundle-ng"));
		fs.copyResource("/bundles/simple-bundle-ng/nomic.box", "./build/bundles/simple-bundle-ng/nomic.box")
		fs.copyResource("/bundles/simple-bundle-ng/file-ng.txt",  "./build/bundles/simple-bundle-ng/file-ng.txt")
		fs.copyResource("/bundles/simple-bundle-ng/file-update.txt",  "./build/bundles/simple-bundle-ng/file-update.txt")

		// create Nomic app instance
		val conf = SimpleConfig(
			"nomic.user" to "testuser",
			"hdfs.simulator.basedir" to "./build/hdfs",
			"nomic.hdfs.home" to "/user/testuser",
			"nomic.hdfs.app.dir" to "/user/testuser/app",
			"nomic.hdfs.repository.dir" to "/user/testuser/nomic",
			"nomic.default.schema" to "test"
		)

		app = NomicApp(conf, HdfsPlugin.initSimulator(conf, fs))
	}


	@Test
	fun `simple scenario with opening, installing and uninstalling box`() {

		// open and install bundle
		val bundle = Bundle.open(fs.getPath("./build/bundles/simple-bundle"))
		val box = app.open(bundle)
		app.install(bundle)

		// checks
		assertThat(fs.getPath("./build/hdfs/user/testuser/app/examples/simple/file.txt"))
			.isRegularFile()
			.hasContent("Hello World Simple!")

		assertThat(fs.getPath("./build/hdfs/user/testuser/nomic/examples.simple.avro"))
			.isRegularFile()

		// listing if was installed and check
		val installedBoxes = app.installedBoxes()
		val installedBoxRef = installedBoxes.findBox("examples:simple:*")!!
		val installedBox = app.details(installedBoxRef)!!

		// checks
		assertThat(installedBoxes)
			.contains(BoxRef(group = "examples", name ="simple", version = "1.0.0"))

		assertThat(installedBox.facts)
			.hasSize(5)
			.contains(ResourceFact("/file.txt", "/user/testuser/app/examples/simple/file.txt", false))

		// uninstall and check
		app.uninstall(installedBoxRef)

		assertThat(fs.getPath("./build/hdfs/user/testuser/app/examples/simple/file.txt"))
			.doesNotExist()

		assertThat(fs.getPath("./build/hdfs/user/testuser/nomic/examples.simple.avro"))
			.doesNotExist()
	}


	@Test
	fun `upgrading the box to new version`() {

		//install v1 bundle and then upgrade to v2
		app.install(Bundle.open(fs.getPath("./build/bundles/simple-bundle")))
		app.upgrade(Bundle.open(fs.getPath("./build/bundles/simple-bundle-ng")))

		// checks
		assertThat(app.installedBoxes())
			.contains(BoxRef(group = "examples", name = "simple", version = "2.0.0"))

		assertThat(fs.getPath("./build/hdfs/user/testuser/app/examples/simple/file-update.txt"))
			.isRegularFile()
			.hasContent("Hello World Simple 2.0.0!")

		assertThat(fs.getPath("./build/hdfs/user/testuser/app/examples/simple/file-ng.txt"))
			.isRegularFile()

		assertThat(fs.getPath("./build/hdfs/user/testuser/app/examples/simple/file.txt"))
			.doesNotExist()

	}

}


//-------------------------------------------------------------------------------------------------
// extension functions
//-------------------------------------------------------------------------------------------------

fun FileSystem.copyResource(fromResource: String, toFile: String) {
	val destFile = Files.createFile(this.getPath(toFile))
	this.javaClass.getResourceAsStream(fromResource).use { src ->
		Files.newOutputStream(destFile).use { dest ->
			IOUtils.copy(src, dest)
		}
	}
}