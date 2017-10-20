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

import nomic.core.NomicConfig
import nomic.core.exception.RequiredConfigPropertyException
import nomic.core.exception.WtfException
import nomic.core.*
import nomic.hdfs.adapter.HdfsAdapter
import nomic.hdfs.adapter.HdfsSimulator
import nomic.hdfs.adapter.RealHdfs
import nomic.hdfs.adapter.copyToHdfs
import java.io.File
import java.nio.file.FileSystem
import java.nio.file.FileSystems

/**
 * Route HDFS related [Fact]s to concrete handlers.
 *
 * @see ResourceFact
 * @see DirFact
 */
class HdfsPlugin(val hdfs: HdfsAdapter) : Plugin() {

	companion object {

		/**
		 * create [HivePlugin] based on configuration in [NomicConfig].
		 */
		fun init(config: NomicConfig): HdfsPlugin =
			when (config["hdfs.adapter"]) {
				"simulator" -> initSimulator(config)
				"hdfs" -> initRealHdfs(config)
				else -> throw WtfException()
			}


		fun initSimulator(config: NomicConfig, fs: FileSystem = FileSystems.getDefault()): HdfsPlugin =
			HdfsPlugin(
				HdfsSimulator(
					baseDir = config["hdfs.simulator.basedir"] ?: throw RequiredConfigPropertyException("hdfs.simulator.basedir"),
					fileSystem = fs
				)
			)


		fun initRealHdfs(config: NomicConfig): HdfsPlugin =
			HdfsPlugin(
				RealHdfs.create(
					coreSiteXml = File(config["hdfs.core.site"] ?: throw RequiredConfigPropertyException("hdfs.core.site")),
					hdfsSiteXml = File(config["hdfs.hdfs.site"] ?: throw RequiredConfigPropertyException("hdfs.hdfs.site"))
				)
			)

	}

	/**
	 * this implementation route the HDFS facts into handlers
	 *
	 * @see [ResourceFact]
	 * @see [DirFact]
	 */
	override fun configureMapping(): FactMapping =
		listOf(
			ResourceFact::class.java to { ResourceFactHandler(hdfs) },
			DirFact::class.java to { DirFactHandler(hdfs) }
		)
}





/**
 * This implementation processing [ResourceFact]s and copy source from
 * [BundledBox] to HDFS destination. The rollback then remove the copied file
 * from HDSF.
 *
 * @see ResourceFact
 */
private class ResourceFactHandler(private val hdfs: HdfsAdapter) : FactHandler<ResourceFact> {

	override fun commit(box: BundledBox, fact: ResourceFact) {
		val entry = box.entry(fact.source)!!
		hdfs.copyToHdfs(entry, fact.dest)
	}

	override fun rollback(box: InstalledBox, fact: ResourceFact) {
		if (!fact.keepIt) {
			hdfs.delete(fact.dest)
		}
	}
}


/**
 * This implementation processing [DirFact] and create empty directory on HDFS. The Rollback
 * then remove directory.
 *
 * @see DirFact
 */
private class DirFactHandler(private val hdfs: HdfsAdapter) : FactHandler<DirFact> {

	override fun commit(box: BundledBox, fact: DirFact) {
		hdfs.mkdirs(fact.dir)
	}

	override fun rollback(box: InstalledBox, fact: DirFact) {
		if (!fact.keepIt) {
			hdfs.delete(fact.dir)
		}
	}

}
