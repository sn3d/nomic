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
package nomic.db

import com.google.common.jimfs.Configuration
import com.google.common.jimfs.Jimfs
import nomic.core.*
import nomic.hdfs.adapter.HdfsSimulator
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import java.nio.ByteBuffer
import java.nio.file.Files


/**
 * @author vrabel.zdenko@gmail.com
 */
class AvroDbTest {

	lateinit var db: AvroDb

	@Before
	fun setup() {
		val fs = Jimfs.newFileSystem(Configuration.unix());
		Files.createDirectories(fs.getPath("./build/hdfs"));
		val hdfs = HdfsSimulator("./build/hdfs", fs)
		db = AvroDb(hdfs, "nomic");
	}

	@Test
	fun `save and load set of boxes into Avro DB`() {

		// prepare 3 boxes 'app-1', 'app-2' and 'app-3'
		// where 'app-3' depends on 'app-1' and 'app-2'

		val boxes = listOf(
			BundledBox(
				group = "examples",
				name = "app-1",
				version = "1.0",
				bundle = InMemoryBundle("/nomic.box" to ByteBuffer.wrap("Hello Script".toByteArray()))
			),
			BundledBox(
				group = "examples",
				name = "app-2",
				version = "1.0",
				bundle = InMemoryBundle("/nomic.box" to ByteBuffer.wrap("Hello Script 2".toByteArray()))
			),
			BundledBox(
				group = "examples",
				name = "app-3",
				version = "1.0",
				bundle = InMemoryBundle("/nomic.box" to ByteBuffer.wrap("Hello Script 3".toByteArray())),
				facts = listOf(
					RequireFact(BoxRef(group = "examples", name = "app-1", version = "1.0")),
					RequireFact(BoxRef(group = "examples", name = "app-2", version = "1.0"))
				)
			)
		)

		// save boxes
		db.insertOrUpdate(boxes[0])
		db.insertOrUpdate(boxes[1])
		db.insertOrUpdate(boxes[2])

		// load all boxes from DB
		val loadedBoxes = db.loadAll()
		val boxReferences = loadedBoxes.map(Box::ref)

		assertThat(boxReferences).contains(
			BoxRef(group = "examples", name = "app-1", version = "1.0"),
			BoxRef(group = "examples", name = "app-2", version = "1.0"),
			BoxRef(group = "examples", name = "app-3", version = "1.0")
		)

		// load box with dependencies
		val app3Box = db.load(BoxRef(group = "examples", name = "app-3", version = "1.0"))

		assertThat(app3Box).isInstanceOf(NotCompiledBox::class.java)
		assertThat(app3Box!!.dependencies).contains(
			BoxRef(group = "examples", name = "app-1", version = "1.0"),
			BoxRef(group = "examples", name = "app-2", version = "1.0")
		)
	}


	@Test
	fun `delete the installed box`() {
		// prepare and save box
		db.insertOrUpdate(
			BundledBox(
				group = "examples",
				name = "app-for-delete",
				version = "1.0",
				bundle = InMemoryBundle("/nomic.box" to ByteBuffer.wrap("Hello Script".toByteArray()))
			)
		)

		val ref = BoxRef(group = "examples", name = "app-for-delete", version = "1.0")
		val existingBox = db.load(ref);
		assertThat(existingBox).isNotNull()

		// delete the box and check
		db.delete(ref);
		val notExistingBox = db.load(ref)
		assertThat(notExistingBox).isNull()
	}


	@Test
	fun `save box with new version`() {
		// prepare and save box
		var ref = db.insertOrUpdate(
			BundledBox(
				group = "examples",
				name = "app-for-upgrade",
				version = "1.0",
				bundle = InMemoryBundle("/nomic.box" to ByteBuffer.wrap("Hello Script".toByteArray()))
			)
		).ref()

		val existingBox = db.load(ref);
		assertThat(existingBox?.ref()).isEqualTo(BoxRef(group = "examples", name = "app-for-upgrade", version = "1.0"))

		// upgrade to next version
		ref = db.insertOrUpdate(
			BundledBox(
				group = "examples",
				name = "app-for-upgrade",
				version = "2.0",
				bundle = InMemoryBundle("/nomic.box" to ByteBuffer.wrap("Hello Script".toByteArray()))
			)
		).ref()

		val boxes = db.loadAll().map(Box::ref);

		assertThat(boxes)
			.contains(BoxRef(group = "examples", name = "app-for-upgrade", version = "2.0"))
			.doesNotContain(BoxRef(group = "examples", name = "app-for-upgrade", version = "1.0"))
	}

	@Test
	fun `save box with same name but different group`() {

		// insert box with 'examples' group
		db.insertOrUpdate(
			BundledBox(
				group = "examples",
				name = "app-duplicity",
				version = "1.0",
				bundle = InMemoryBundle("/nomic.box" to ByteBuffer.wrap("Hello Script".toByteArray()))
			)
		)

		// insert box with same name but different group
		db.insertOrUpdate(
			BundledBox(
				group = "different-group",
				name = "app-duplicity",
				version = "2.0",
				bundle = InMemoryBundle("/nomic.box" to ByteBuffer.wrap("Hello Script".toByteArray()))
			)
		)

		// check the state
		val installedBoxes = db.loadAll().map(Box::ref).toList()
		assertThat(installedBoxes)
			.contains(
				BoxRef(group = "examples", name = "app-duplicity", version = "1.0"),
				BoxRef(group = "different-group", name = "app-duplicity", version = "2.0")
			)
	}
}