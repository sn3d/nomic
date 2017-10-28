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
package nomic.oozie.adapter

/**
 * representing Job in Oozie
 */
data class OozieJob(val jobId: String, val jobName: String = "", val jobPath: String = "", val status: String = "")


/**
 * @author vrabel.zdenko@gmail.com
 */
interface OozieAdapter {

	fun findJob(jobId: String): OozieJob
	fun createAndStartJob(params:Map<String, String>):OozieJob
	fun killJob(job: OozieJob)
	fun findRunningCoordinatorJobs(filter: (OozieJob) -> Boolean = { _ -> true}): List<OozieJob>

	fun createAndStartJob(vararg params:Pair<String, String>):OozieJob =
		createAndStartJob(params.toMap())

	fun killJob(jobId: String) =
		killJob(OozieJob(jobId = jobId))


}