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
package nomic.oozie

import nomic.core.*
import nomic.core.exception.RequiredConfigPropertyException
import nomic.oozie.adapter.OozieAdapter
import nomic.hdfs.adapter.HdfsAdapter
import nomic.hdfs.adapter.copyToHdfs
import nomic.oozie.adapter.RestOozieAdapter

/**
 * Route OOZIE related [Fact]s to concrete handlers.
 *
 * @author vrabel.zdenko@gmail.com
 */
class OoziePlugin(
	private val oozie: OozieAdapter,
	private val hdfs: HdfsAdapter,
	private val jobTracker: String) : Plugin()
{

	companion object {

		fun init(config: NomicConfig, hdfs: HdfsAdapter): OoziePlugin =
			OoziePlugin(
				hdfs = hdfs,
				oozie = RestOozieAdapter(config["oozie.url"] ?: throw RequiredConfigPropertyException("oozie.url")),
				jobTracker = config["oozie.jobTracker"] ?: throw RequiredConfigPropertyException("oozie.jobTracker")
			)

	}

	override fun configureMapping(): FactMapping =
		listOf(
			CoordinatorFact::class.java to { CoordinatorFactHandler(oozie, hdfs, jobTracker) }
		)
}


/**
 * This handler is processing [CoordinatorFact]
 */
class CoordinatorFactHandler(private val oozie: OozieAdapter, private val hdfs: HdfsAdapter, private val jobTracker: String) : FactHandler<CoordinatorFact> {

	/**
	 * commiting the [CoordinatorFact]
	 */
	override fun commit(box: BundledBox, fact: CoordinatorFact) {
		val entry = box.entry(fact.xmlSource) ?: throw CoordinatorXmlNotFoundException(fact.xmlSource, box)
		hdfs.copyToHdfs(entry, fact.hdfsDest)
		val enrichedParams = fact.parameters + ("jobTracker" to jobTracker)
		oozie.createAndStartJob(enrichedParams)
	}

	/**
	 * rollback the [CoordinatorFact]
	 */
	override fun rollback(box: InstalledBox, fact: CoordinatorFact) {
		if (!fact.keepIt) {
			val coordinator = readCoordinatorXml(fact)
			coordinator.findAndKill()
			hdfs.delete(fact.hdfsDest)
		}
	}

	private fun readCoordinatorXml(fact:CoordinatorFact) =
		hdfs.open(fact.hdfsDest).use {
			OozieCoordinatorXml(it)
		}

	private fun OozieCoordinatorXml.findAndKill() {
		val job = oozie.findRunningCoordinatorJobs({ job -> job.jobName == appName }).first()
		oozie.killJob(job.jobId)

	}

}