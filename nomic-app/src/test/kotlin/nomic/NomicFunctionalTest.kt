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
import nomic.app.config.SimpleConfig
import nomic.core.*
import nomic.hdfs.HdfsPlugin
import nomic.hdfs.ResourceFact
import org.apache.commons.io.IOUtils
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import java.nio.file.FileSystem
import java.nio.file.FileSystems
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

		//prepare test data for 'multimodule-bundle'
		Files.createDirectories(fs.getPath("./build/bundles/multimodule-bundle"));
		Files.createDirectories(fs.getPath("./build/bundles/multimodule-bundle/module-a"));
		Files.createDirectories(fs.getPath("./build/bundles/multimodule-bundle/module-b"));
		Files.createDirectories(fs.getPath("./build/bundles/multimodule-bundle/module-b/submodule-c"));
		fs.copyResource("/bundles/multimodule-bundle/nomic.box", "./build/bundles/multimodule-bundle/nomic.box")
		fs.copyResource("/bundles/multimodule-bundle/file.txt", "./build/bundles/multimodule-bundle/file.txt")
		fs.copyResource("/bundles/multimodule-bundle/module-a/nomic.box", "./build/bundles/multimodule-bundle/module-a/nomic.box")
		fs.copyResource("/bundles/multimodule-bundle/file.txt", "./build/bundles/multimodule-bundle/module-a/file-a.txt")
		fs.copyResource("/bundles/multimodule-bundle/module-b/nomic.box", "./build/bundles/multimodule-bundle/module-b/nomic.box")
		fs.copyResource("/bundles/multimodule-bundle/file.txt", "./build/bundles/multimodule-bundle/module-b/file-b.txt")
		fs.copyResource("/bundles/multimodule-bundle/module-b/submodule-c/nomic.box", "./build/bundles/multimodule-bundle/module-b/submodule-c/nomic.box")
		fs.copyResource("/bundles/multimodule-bundle/file.txt", "./build/bundles/multimodule-bundle/module-b/submodule-c/file-c.txt")

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
	fun `compilation of bundle should create BundledBox with facts etc`() {
		val bundle = "./build/bundles/simple-bundle".asBundleFrom(fs)
		val box = app.compile(bundle)

		assertThat(box.name).isEqualTo("simple")
		assertThat(box.group).isEqualTo("examples")
		assertThat(box.version).isEqualTo("1.0.0")

		assertThat(box.facts)
			.contains(
				ResourceFact("/file.txt",       "/user/testuser/app/examples/simple/file.txt"),
				ResourceFact("/file-update.txt","/user/testuser/app/examples/simple/file-update.txt"))

	}


	@Test
	fun `simple scenario with installing and uninstalling box`() {

		// install bundle
		val bundle = "./build/bundles/simple-bundle".asBundleFrom(fs)
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
		app.install("./build/bundles/simple-bundle".asBundleFrom(fs))
		app.upgrade("./build/bundles/simple-bundle-ng".asBundleFrom(fs))

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

	@Test
	fun `install the multimodule box should create files on HDFS and register installed box in nomic app`() {
		val multimoduleBundle = "./build/bundles/multimodule-bundle".asBundleFrom(fs)
		app.install(multimoduleBundle)

		// check files on HDFS
		assertThat(fs.getPath("./build/hdfs/user/testuser/app/multimodule/root/file.txt"))
			.isRegularFile()
		assertThat(fs.getPath("./build/hdfs/user/testuser/app/multimodule/module-a/file-a.txt"))
			.isRegularFile()
		assertThat(fs.getPath("./build/hdfs/user/testuser/app/multimodule/module-b/file-b.txt"))
			.isRegularFile()
		assertThat(fs.getPath("./build/hdfs/user/testuser/app/multimodule/submodule-c/file-c.txt"))
			.isRegularFile()

		val moduleB = app.details(BoxRef.parse("multimodule:module-b:1.0.0"))!!
		assertThat(moduleB.dependencies)
			.contains(BoxRef.parse("multimodule:submodule-c:1.0.0"))
	}


	@Test
	fun `uninstall the multimodule box should remove root module and all submodules`() {
		// first, install it
		val multimoduleBundle = "./build/bundles/multimodule-bundle".asBundleFrom(fs)
		val ref = app.install(multimoduleBundle)

		assertThat(app.details(BoxRef.parse("multimodule:module-a:1.0.0"))).isNotNull()
		assertThat(app.details(BoxRef.parse("multimodule:module-b:1.0.0"))).isNotNull()
		assertThat(app.details(BoxRef.parse("multimodule:submodule-c:1.0.0"))).isNotNull()

		// now you can uninstall it
		app.uninstall(ref)

		assertThat(app.details(BoxRef.parse("multimodule:module-a:1.0.0"))).isNull()
		assertThat(app.details(BoxRef.parse("multimodule:module-b:1.0.0"))).isNull()
		assertThat(app.details(BoxRef.parse("multimodule:submodule-c:1.0.0"))).isNull()

		assertThat(fs.getPath("./build/hdfs/user/testuser/app/multimodule/submodule-c/file-c.txt"))
			.doesNotExist()
		assertThat(fs.getPath("./build/hdfs/user/testuser/app/multimodule/root/file.txt"))
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