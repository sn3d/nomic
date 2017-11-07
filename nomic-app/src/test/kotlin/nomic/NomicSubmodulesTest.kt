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
import nomic.core.fact.RequireFact
import nomic.hdfs.HdfsPlugin
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import java.nio.file.FileSystem
import java.nio.file.Files

/**
 * @author vrabel.zdenko@gmail.com
 */
class NomicSubmodulesTest {

	private lateinit var fs: FileSystem
	private lateinit var app: NomicApp



	@Before
	fun `setup testing filesystem with box with submodules`() {
		fs = Jimfs.newFileSystem(Configuration.unix());
		//fs = FileSystems.getDefault()

		//prepare HDFS folder
		Files.createDirectories(fs.getPath("./build/hdfs"));

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

	}


	@Before
	fun `create testing Nomic app instance`() {
		// create Nomic app instance
		val conf = SimpleConfig(
			"nomic.user" to "testuser",
			"hdfs.simulator.basedir" to "./build/hdfs",
			"nomic.hdfs.home" to "/user/testuser",
			"nomic.hdfs.app.dir" to "/user/testuser/app",
			"nomic.hdfs.repository.dir" to "/user/testuser/nomic"
		)

		app = NomicApp(conf, listOf(HdfsPlugin.initSimulator(conf, fs)))
	}


	@Test
	fun `multimodule bundle should have only one application box with module boxes`() {
		val bundle = "./build/bundles/multimodule-bundle".asBundleFrom(fs)
		val boxes = app.compileAll(bundle)

		assertThat(boxes).hasSize(4)
		assertThat(boxes.filterIsInstance(ApplicationBox::class.java)).hasSize(1)
	}


	@Test
	fun `multimodule bundle should have application box with require facts to nested modules`() {
		val bundle = "./build/bundles/multimodule-bundle".asBundleFrom(fs)
		val boxes = app.compileAll(bundle)
		val root = boxes.filterIsInstance(ApplicationBox::class.java).first()

		assertThat(root.facts)
			.contains(RequireFact(box = BoxRef.parse("multimodule:module-a:1.0.0")))
			.contains(RequireFact(box = BoxRef.parse("multimodule:module-b:1.0.0")))
	}



}

