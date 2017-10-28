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

import com.github.tomakehurst.wiremock.client.WireMock.*
import com.github.tomakehurst.wiremock.core.WireMockConfiguration.options
import com.github.tomakehurst.wiremock.junit.WireMockRule
import org.assertj.core.api.Assertions.assertThat

import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * @author vrabel.zdenko@gmail.com
 */
public class RestOozieAdapterTest {

	@get:Rule
	public val oozieServer: WireMockRule = WireMockRule(options().port(11000))

	lateinit var adapter:RestOozieAdapter


	@Before
	fun setup() {
		var json = this.javaClass.getResourceAsStream("/oozie-coordinator-resp.json").reader().readText()
		oozieServer.stubFor(
			get(urlEqualTo("/oozie/v1/jobs?jobtype=coord&filter=status=RUNNING"))
				.willReturn(okJson(json))
		)

		oozieServer.stubFor(
			post(urlEqualTo("/oozie/v1/jobs?action=start"))
				.withRequestBody(containing("<value>PATH</value>"))
				.withHeader("Content-Type", equalTo("application/xml;charset=UTF-8"))
				.willReturn(okJson("{ id: 'job-123'}"))
		)

		oozieServer.stubFor(
			put(urlEqualTo("/oozie/v1/job/0000214-171019175534879-oozie-oozi-C"))
		)

		//adapter = RestOozieAdapter()
		adapter = RestOozieAdapter("http://d-cldera-app02.wirecard.sys:11000")
	}

	@Test
	fun `get running coordinators`() {
		val coordinators = adapter.findRunningCoordinatorJobs();

		assertThat(coordinators).contains(
			OozieJob(
				jobId = "0000214-171019175534879-oozie-oozi-C",
				jobName = "daily",
				jobPath = "hdfs://oozie-server:8020/app/coordinator.xml",
				status = "RUNNING"
			)
		)
	}

	@Test
	fun `create and start the coordinator job`() {
		val job =
			adapter.createAndStartJob(
				"user.name" to "USERNAME",
				"oozie.coord.application.path" to "PATH",
				"jobTracker" to "JOB_TRACKER",
				"nameNode" to "NAME_NODE",
				"oozie.use.system.libpath" to "True",
				"HIVE_SERVER_JDBC_URL" to "JDBC_URL"
			)

		assertThat(job.jobId).isEqualToIgnoringCase("job-123")
	}


	@Test
	fun `kill the running job`() {
		adapter.killJob("job-to-kill-123")
		val killedJob = adapter.findJob("job-to-kill-123")
		assertThat(killedJob.status).isEqualToIgnoringCase("KILLED")
	}
}