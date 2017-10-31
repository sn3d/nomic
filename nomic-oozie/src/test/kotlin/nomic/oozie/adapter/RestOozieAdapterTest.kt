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
import com.github.tomakehurst.wiremock.stubbing.Scenario.STARTED
import org.assertj.core.api.Assertions.assertThat

import com.github.tomakehurst.wiremock.WireMockServer
import org.junit.*


/**
 * @author vrabel.zdenko@gmail.com
 */
public class RestOozieAdapterTest {

	//@get:Rule
	lateinit var adapter:RestOozieAdapter

	companion object {
		lateinit var oozieServer: WireMockServer

		@BeforeClass @JvmStatic fun setupClass() {
			oozieServer = WireMockServer(options().port(11000)) //No-args constructor will start on port 8080, no HTTPS

			var json = RestOozieAdapterTest::class.java.getResourceAsStream("/oozie-coordinator-resp.json").reader().readText()
			oozieServer.stubFor(
				get(urlEqualTo("/oozie/v1/jobs?jobtype=coord&filter=status=RUNNING"))
					.willReturn(okJson(json))
			)

			oozieServer.stubFor(
				post(urlEqualTo("/oozie/v1/jobs?action=start"))
					.withRequestBody(containing("<value>PATH</value>"))
					.withHeader("Content-Type", equalTo("application/xml"))
					.willReturn(okJson("{ id: 'job-123'}"))
			)

			oozieServer.stubFor(
				put(urlEqualTo("/oozie/v1/job/0000214-171019175534879-oozie-oozi-C"))
			)


			// scenario 'kill the running job'
			oozieServer.stubFor(
				put("/oozie/v1/job/job-to-kill-123?action=kill").inScenario("kill the running job")
					.whenScenarioStateIs(STARTED)
					.willReturn(ok())
					.willSetStateTo("KILLED")
			)

			json = RestOozieAdapterTest::class.java.getResourceAsStream("/job-to-kill-123.json").reader().readText()
			oozieServer.stubFor(
				get(urlEqualTo("/oozie/v1/job/job-to-kill-123")).inScenario("kill the running job")
					.whenScenarioStateIs("KILLED")
					.withHeader("Content-Type", equalTo("application/json"))
					.willReturn(okJson(json))
					.willSetStateTo("END")
			)

			oozieServer.start()
		}

		@AfterClass @JvmStatic fun stop() {
			oozieServer.stop()
		}
	}

	@Before
	fun setup() {
		adapter = RestOozieAdapter()
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