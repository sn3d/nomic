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

import nomic.core.*
import nomic.db.avro.DependencyDto
import nomic.db.avro.InstalledBoxDto
import nomic.hdfs.adapter.HdfsAdapter
import org.apache.avro.file.DataFileStream
import org.apache.avro.file.DataFileWriter
import org.apache.avro.specific.SpecificDatumReader
import org.apache.avro.specific.SpecificDatumWriter

/**
 * @author vrabel.zdenko@gmail.com
 */
class AvroDb(hdfs: HdfsAdapter, nomicHome: String) : NomicDb {

	private val nomicHome: String;
	private val hdfs: HdfsAdapter;

	init {
		this.hdfs = hdfs;
		this.nomicHome = nomicHome;
	}

	override fun insertOrUpdate(box: Box): Box {
		// convert to DTO
		val dto = box.toDto()

		// save to avro file
		val path = normalizedPath(box.ref())
		hdfs.create(path).use {
			val boxWriter = SpecificDatumWriter<InstalledBoxDto>(InstalledBoxDto::class.java)
			val dataFileWriter = DataFileWriter<InstalledBoxDto>(boxWriter)
			val writer = dataFileWriter.create(dto.schema, it)
			writer.append(dto)
			writer.close()
		}

		return box;
	}


	override fun loadAll(): List<NotCompiledBox> =
		hdfs.listFiles(nomicHome, true)
			.filter(this::isAvroFile)
			.flatMap(this::loadAvroFile)
			.map(InstalledBoxDto::toNomicBox)
			.toList()


	override fun load(ref: BoxRef): NotCompiledBox? =
		loadAvroFile(normalizedPath(ref))
			.map(InstalledBoxDto::toNomicBox)
			.firstOrNull()


	override fun delete(ref: BoxRef) =
		hdfs.delete(normalizedPath(ref))


	private fun isAvroFile(file: String): Boolean =
		file.endsWith(".avro")

	private fun loadAvroFile(file: String): Sequence<InstalledBoxDto> {

		if (!hdfs.exist(file)) {
			return emptySequence()
		}

		hdfs.open(file).use {
			val boxDatumReader = SpecificDatumReader<InstalledBoxDto>(InstalledBoxDto::class.java)
			val dataReader = DataFileStream<InstalledBoxDto>(it, boxDatumReader)
			val list = dataReader.toList()
			return list.asSequence();
		}
	}

	private fun normalizedPath(ref: BoxRef): String {
		val normalizedGroup = ref.group.replace("/", "-").replace(".","-")
		val normalizedName  = ref.name.replace("/", "-").replace(".", "-")
		return "${nomicHome}/${normalizedGroup}.${normalizedName}.avro"
	}

}


/**
 * function convert any [Box] to Avro's [InstalledBoxDto]
 */
private fun Box.toDto(): InstalledBoxDto =
	InstalledBoxDto().apply {
		// transform basic fields
		setName(this@toDto.name)
		setGroup(this@toDto.group)
		setVersion(this@toDto.version)

		// transform script
		this@toDto.script.open().use {
			setScript(it.readText())
		}

		// transform dependencies
		val dependencies =
			this@toDto.facts.asSequence()
				.filterIsInstance(RequireFact::class.java)
				.map(RequireFact::toDto)
				.toList()

		setDependencies(dependencies)
	}


/**
 * function convert [RequireFact] to Avro's [DependencyDto]
 */
private fun RequireFact.toDto(): DependencyDto =
	DependencyDto().apply {
		setName(this@toDto.box.name)
		setGroup(this@toDto.box.group)
		setVersion(this@toDto.box.version)
	}

private fun DependencyDto.toNomicBoxRef(): BoxRef =
	BoxRef(
		group = this!!.getGroup(),
		name  = this.getName(),
		version = this.getVersion()
	)

private fun InstalledBoxDto.toNomicBox():NotCompiledBox =
	NotCompiledBox(
		name = this.getName()!!,
		group = this.getGroup() ?: "",
		version = this.getVersion() ?: "",
		script = InMemoryScript(this.getScript()!!),
		dependencies = this.getDependencies().filterNotNull().map(DependencyDto::toNomicBoxRef)
	)
