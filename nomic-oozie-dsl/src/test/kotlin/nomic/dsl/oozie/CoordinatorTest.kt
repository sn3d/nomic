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
package nomic.dsl.oozie

import nomic.core.Exposable
import nomic.core.findFactType
import nomic.core.findFactsType
import nomic.core.script.ClasspathScript
import nomic.oozie.CoordinatorFact
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

/**
 * @author vrabel.zdenko@gmail.com
 */
class CoordinatorTest {

	val hdfsExposer: Exposable = object : Exposable {
		override val exposedVariables: List<Pair<String, String>>
			get() = listOf(
				"nameNode" to "hdfs://localhost"
			)

	}

	@Test
	fun `compilation of script with simple coordinator should build a fact`() {
		// compile
		val compiler = nomic.compiler.Compiler(appDir = "/app", user = "nomicuser", expos = listOf(hdfsExposer))
		val facts = compiler.compile(ClasspathScript("/simple-coordinator.box"))
		val coordinatorFact = facts.findFactType(CoordinatorFact::class.java)

		// checking
		assertThat(coordinatorFact.xmlSource).isEqualTo("coordinator.xml")
		assertThat(coordinatorFact.hdfsDest).isEqualTo("/app/nomic/test/coordinator.xml")

		assertThat(coordinatorFact.parameters)
			.containsEntry("DATABASE_SCHEMA_PREFIX", "nomic")
			.containsEntry("user.name", "nomicuser")
			.containsEntry("nameNode", "hdfs://localhost")
			.containsEntry("oozie.coord.application.path", "/app/nomic/test/coordinator.xml")
	}


	@Test
	fun `compilation of script with coordinator on different path in HDFS`() {
		// compile
		val compiler = nomic.compiler.Compiler(appDir = "/app", user = "nomicuser")
		val facts = compiler.compile(ClasspathScript("/coordinator-with-different-target.box"))

		// check relative coordinator
		val relativeCoordinator = facts.findFactsType(CoordinatorFact::class.java).find { f -> f.name == "relative-coordinator" }!!
		assertThat(relativeCoordinator.xmlSource).isEqualTo("coordinator.xml")
		assertThat(relativeCoordinator.hdfsDest).isEqualTo("/app/nomic/test/custompath/coordinator-2.xml")

		// check absolute coordinator
		val absoluteCoordinator = facts.findFactsType(CoordinatorFact::class.java).find { f -> f.name == "absolute-coordinator"  }!!
		assertThat(absoluteCoordinator.xmlSource).isEqualTo("coordinator.xml")
		assertThat(absoluteCoordinator.hdfsDest).isEqualTo("/absolute/coordinator-3.xml")
	}

}