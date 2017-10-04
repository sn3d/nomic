package nomic.itest.stepdef;

import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import net.thucydides.core.annotations.Steps;
import nomic.NomicConfig;
import nomic.itest.steps.HdfsSteps;
import nomic.itest.steps.NomicSteps;

/**
 * @author zdenko.vrabel@wirecard.com
 */
public class CoreStepDefs {

	@Steps
	public NomicSteps nomicSteps;

	@Steps
	public HdfsSteps hdfsSteps;


	@When("^the box \"([^\"]*)\" in version \"([^\"]*)\" is installed$")
	public void iInstallTheBoxInVersion(String path, String version) throws Throwable {
		nomicSteps.installBox(path);
	}

	@Then("^box \"([^\"]*)\" must be available in nomic$")
	public void boxMustBeAvailableInNomic(String expr) throws Throwable {
		nomicSteps.checkIfBoxExist(expr);
	}

	@Given("^the box \"([^\"]*)\" is not present$")
	public void theBoxIsNotPresent(String expr) throws Throwable {
		nomicSteps.uninstallIfExist(expr);
	}

	@Given("^the box \"([^\"]*)\" is present as \"([^\"]*)\"$")
	public void theBoxIsInstalledInVersion(String path, String expr) throws Throwable {
		nomicSteps.installBoxForce(path);
		nomicSteps.checkIfBoxExist(expr);
	}

	@When("^the box is upgraded to new version from \"([^\"]*)\"$")
	public void theBoxIsUpgradedToVersionFrom(String path) throws Throwable {
		nomicSteps.upgradeBox(path);
	}

	@And("^file \"([^\"]*)\" must be available in HDFS$")
	public void fileFromNewVersionMustBeAvailableInHDFS(String file) throws Throwable {
		NomicConfig conf = nomicSteps.loadNomicConfiguration();
		String path = conf.getHdfsAppDir() + "/nomic-examples/hello-world/" + file;
		hdfsSteps.fileOrDirExist(path);
	}

	@And("^file \"([^\"]*)\" must be missing in HDFS$")
	public void fileMustBeMissing(String file) throws Throwable {
		NomicConfig conf = nomicSteps.loadNomicConfiguration();
		String path = conf.getHdfsAppDir() + "/nomic-examples/hello-world/" + file;
		hdfsSteps.fileOrDirNotExist(path);
	}

	@Given("^the boxes \"([^\"]*)\" and \"([^\"]*)\" are not yet installed$")
	public void theBoxesAndAreNotYetInstalled(String parent, String submodule) throws Throwable {
		nomicSteps.uninstallIfExist(parent);
		nomicSteps.checkIfBoxNotExist(parent);
		nomicSteps.checkIfBoxNotExist(submodule);
	}

	@Then("^module \"([^\"]*)\" with submodule \"([^\"]*)\" are present in nomic$")
	public void moduleWithSubmoduleIsPresentInNomic(String parent, String submodule) throws Throwable {
		nomicSteps.checkIfBoxExist(parent);
		nomicSteps.checkIfBoxExist(submodule);
	}

	@Then("^module \"([^\"]*)\" with submodule \"([^\"]*)\" are not present in nomic$")
	public void moduleWithSubmoduleAreNotPresentInNomic(String parent, String submodule) throws Throwable {
		nomicSteps.checkIfBoxNotExist(parent);
		nomicSteps.checkIfBoxNotExist(submodule);
	}
}
