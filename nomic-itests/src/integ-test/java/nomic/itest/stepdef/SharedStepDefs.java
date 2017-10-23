package nomic.itest.stepdef;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.When;
import net.thucydides.core.annotations.Steps;
import nomic.core.Box;
import nomic.core.BoxKt;
import nomic.itest.steps.BoxSteps;
import nomic.itest.steps.HdfsSteps;
import nomic.itest.steps.HiveSteps;
import nomic.itest.steps.NomicSteps;

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
		String boxExpr = BoxKt.ref(box).toString();
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
