package nomic.itest.stepdef;

import cucumber.api.PendingException;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.When;
import net.thucydides.core.annotations.Steps;
import nomic.box.Box;
import nomic.box.BoxLoader;
import nomic.itest.BoxSteps;
import nomic.itest.HdfsSteps;
import nomic.itest.HiveSteps;
import nomic.itest.NomicSteps;

import java.io.File;

/**
 * @author zdenko.vrabel@wirecard.com
 */
public class SharedStepDefs {

	@Steps
	public HdfsSteps hdfsSteps;

	@Steps
	public NomicSteps nomicSteps;

	@Steps
	public HiveSteps hiveSteps;

	@Steps
	public BoxSteps boxSteps;

	@Given("^the box \"([^\"]*)\" is installed$")
	public void theBoxIsInstalled(String path) throws Throwable {

		Box box = boxSteps.loadBox(path);
		String boxExpr = box.getInfo().toString();
		boolean isInstalled = nomicSteps.isBoxInstalled(boxExpr);
		if (isInstalled) {
			nomicSteps.uninstallBox(boxExpr);
		}
		nomicSteps.installBox(path);
	}

	@When("^the box \"([^\"]*)\" is removed$")
	public void theBoxIsRemoved(String boxExpr) throws Throwable {
		nomicSteps.checkIfBoxExist(boxExpr);
		nomicSteps.uninstallIfExist(boxExpr);
	}

	@Given("^the \"([^\"]*)\" box is not installed yet$")
	public void theBoxIsNotInstalledYet(String boxExpr) throws Throwable {
		nomicSteps.uninstallIfExist(boxExpr);
	}
}
