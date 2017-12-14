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

import com.mashape.unirest.http.Unirest
import nomic.core.exception.WtfException
import nomic.oozie.OozieServerException
import org.json.JSONObject
import org.json.JSONTokener

/**
 * @author vrabel.zdenko@gmail.com
 */
class RestOozieAdapter(val oozieUrl: String = "http://localhost:11000") : OozieAdapter {


	/**
	 * this function return a list of all running coordinators in Oozie. You can also filter
	 * coordinators by [filter]
	 */
	override fun findRunningCoordinatorJobs(filter: (OozieJob) -> Boolean): List<OozieJob> {
		val json =
			Unirest.get("${oozieUrl}/oozie/v1/jobs?jobtype=coord&filter=status=PREP;status=RUNNING;status=PREPSUSPENDED;status=SUSPENDED;status=PREPPAUSED;status=PAUSED;")
				.header("Content-Type", "application/json")
				.asJson()

		val filteredCoordinators = json.body.`object`.getJSONArray("coordinatorjobs")
			.map(this::anyToJob)
			.filter(filter)
			.toList()

		return filteredCoordinators;
	}


	/**
	 * find concrete job by ID
	 */
	override fun findJob(jobId: String): OozieJob {
		val json =
			Unirest.get("${oozieUrl}/oozie/v1/job/${jobId}")
				.header("Content-Type", "application/json")
				.asJson()

		return jsonToJob(json.body.`object`);
	}


	/**
	 * killing the running job
	 */
	override fun killJob(job: OozieJob) {
		val resp = Unirest.put("${oozieUrl}/oozie/v1/job/${job.jobId}?action=kill").asString()
		if (resp.headers.containsKey("oozie-error-code")) {
			val message = resp.headers.getFirst("oozie-error-message")
			throw OozieServerException(message)
		}
	}


	/**
	 * create the coordinator with params and start it
	 */
	override fun createAndStartJob(params:Map<String, String>):OozieJob {
		val resp =
			Unirest.post("${oozieUrl}/oozie/v1/jobs?action=start")
				.header("Content-Type", "application/xml")
				.header("Accept-Encoding", "gzip,deflate")
				.body(paramsToXml(params))
				.asString()

		if (resp.headers.containsKey("oozie-error-code")) {
			val message = resp.headers.getFirst("oozie-error-message")
			throw OozieServerException(message)
		}

		// parse body to JSON
		val tokener = JSONTokener(resp.body)
		val jsonBody = JSONObject(tokener)

		return OozieJob(jobId = jsonBody.getString("id"))
	}


	//-------------------------------------------------------------------------------------------------
	// private functions
	//-------------------------------------------------------------------------------------------------

	private fun anyToJob(any: Any?) =
		if (any is JSONObject) jsonToJob(any) else throw WtfException()

	private fun jsonToJob(json: JSONObject) =
		OozieJob(
			jobId = json.getString("coordJobId"),
			jobName = json.getString("coordJobName"),
			jobPath = json.getString("coordJobPath"),
			status = json.getString("status")
		)

	//private fun paramsToXml(params:Array<out Pair<String, String>>): String {
	private fun paramsToXml(params:Map<String, String>): String {
		val xml = StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\"?>")
		xml.append("<configuration>")
		params.forEach { param ->
			val name = param.key.toString()
			val value = param.value.toString()
			xml.append("<property>")
			xml.append("<name>${name}</name>")
			xml.append("<value>${value}</value>")
			xml.append("</property>")
		}
		xml.append("</configuration>")
		return xml.toString()
	}

}