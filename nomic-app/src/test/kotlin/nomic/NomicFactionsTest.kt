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
import nomic.core.BoxRef
import nomic.core.Bundle
import nomic.core.NomicInstance
import nomic.core.SimpleConfig
import nomic.hdfs.HdfsPlugin
import nomic.hdfs.ResourceFact
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import java.nio.file.FileSystem
import java.nio.file.Files

/**
 * @author vrabel.zdenko@gmail.com
 */
class NomicFactionsTest {

	private lateinit var fs: FileSystem
	private lateinit var app: NomicInstance

	@Before
	fun `setup FileSystem`() {
		// prepare FS
		fs = Jimfs.newFileSystem(Configuration.unix());
		//fs = FileSystems.getDefault()

		//prepare HDFS folder
		Files.createDirectories(fs.getPath("./build/hdfs"));

		//prepare test data for 'factions-bundle'
		Files.createDirectories(fs.getPath("./build/bundles/factions-bundle"));
		fs.copyResource("/bundles/factions-bundle/nomic.box", "./build/bundles/factions-bundle/nomic.box")
		fs.copyResource("/bundles/factions-bundle/global.txt", "./build/bundles/factions-bundle/global.txt")
		fs.copyResource("/bundles/factions-bundle/phase1.txt", "./build/bundles/factions-bundle/phase1.txt")
		fs.copyResource("/bundles/factions-bundle/phase2.txt", "./build/bundles/factions-bundle/phase2.txt")
		fs.copyResource("/bundles/factions-bundle/phase3.txt", "./build/bundles/factions-bundle/phase3.txt")
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
	fun `the box with factions should install resources in right order`() {
		val bundle = Bundle.create(fs.getPath("./build/bundles/factions-bundle"))

		// check the order of facts they will be installed
		val box = app.compile(bundle)
		assertThat((box.facts[3] as ResourceFact).source).isEqualTo("/global.txt")
		assertThat((box.facts[4] as ResourceFact).source).isEqualTo("/phase1.txt")
		assertThat((box.facts[5] as ResourceFact).source).isEqualTo("/phase2.txt")
		assertThat((box.facts[6] as ResourceFact).source).isEqualTo("/phase3.txt")

		// install the bundle
		app.install(bundle)

		// check the order of facts how they were installed
		val installedBox = app.details(BoxRef.createReferenceTo(box))!!
		assertThat((installedBox.facts[3] as ResourceFact).source).isEqualTo("/global.txt")
		assertThat((installedBox.facts[4] as ResourceFact).source).isEqualTo("/phase1.txt")
		assertThat((installedBox.facts[5] as ResourceFact).source).isEqualTo("/phase2.txt")
		assertThat((installedBox.facts[6] as ResourceFact).source).isEqualTo("/phase3.txt")

	}
}
