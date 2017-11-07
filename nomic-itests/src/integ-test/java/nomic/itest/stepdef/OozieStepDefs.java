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
package nomic.itest.stepdef;

import cucumber.api.java.en.Then;
import net.thucydides.core.annotations.Steps;
import nomic.core.NomicConfig;
import nomic.itest.steps.HdfsSteps;
import nomic.itest.steps.NomicSteps;

/**
 * @author vrabel.zdenko@gmail.com
 */
public class OozieStepDefs {

	@Steps
	public NomicSteps nomicSteps;

	@Steps
	public HdfsSteps hdfsSteps;


	@Then("^I install the box with ooozie coordinator$")
	public void installTheOozieCoordinator() throws Throwable {
		nomicSteps.installBox("../nomic-examples/oozie-coordinator");
	}

	@Then("^the coordinator XML should be available in HDFS and should start$")
	public void theCoordinatorXMLShouldBeAvailableInHDFSAndShouldStart() throws Throwable {
		NomicConfig conf = nomicSteps.loadNomicConfiguration();
		String path = conf.getHdfsAppDir() + "/nomic-examples/oozie-coordinator/coordinator.xml";
		hdfsSteps.fileOrDirExist(path);
	}
}
