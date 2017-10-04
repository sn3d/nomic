package nomic.itest.stepdef;

import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import net.thucydides.core.annotations.Steps;
import nomic.itest.steps.HiveSteps;
import nomic.itest.steps.NomicSteps;

/**
 * @author zdenko.vrabel@wirecard.com
 */
public class HiveTableStepDefs {

	@Steps
	public HiveSteps hiveSteps;

	@Steps
	public NomicSteps nomicSteps;


	@When("^I install the box with hive table from \"([^\"]*)\"$")
	public void iInstallTheBoxWithHiveTableFrom(String path) throws Throwable {
		nomicSteps.installBox(path);
	}

	@Given("^The box with hive table from \"([^\"]*)\" is installed$")
	public void theBoxWithHiveTableFromIsInstalled(String path) throws Throwable {
		nomicSteps.installBox(path);
	}

	@Then("^The new table 'books' in hive must be created$")
	public void theNewTableBooksInHiveMustBeCreated() throws Throwable {
		hiveSteps.checkIfTableExist("books");
	}


	@Then("^The new table 'books' must be removed$")
	public void theNewTableBooksMustBeRemoved() throws Throwable {
		hiveSteps.checkIfTableNotExist("books");
	}

	@When("^Uninstall the \"([^\"]*)\" box$")
	public void uninstallTheBox(String boxId) throws Throwable {
		nomicSteps.uninstallBox(boxId);
	}

	@Then("^the schema \"([^\"]*)\" must be created$")
	public void theSchemaMustBeCreated(String schema) throws Throwable {
		hiveSteps.checkIfSchemeExist(schema);
	}

	@Then("^table \"([^\"]*)\" in hive must be removed$")
	public void tableInHiveMustBeRemoved(String table) throws Throwable {
		hiveSteps.checkIfTableNotExist(table);
	}

	@Then("^table \"([^\"]*)\" in hive must exist$")
	public void tableInHiveMustBeCreated(String table) throws Throwable {
		hiveSteps.checkIfTableExist(table);
	}

	@Then("^table \"([^\"]*)\" in schema \"([^\"]*)\" must be removed$")
	public void tableInSchemeMustBeRemoved(String table, String scheme) throws Throwable {
		hiveSteps.checkIfTableNotExist("${scheme}.${table}");
	}

	@And("^table \"([^\"]*)\" in schema \"([^\"]*)\" must exist$")
	public void tableInSchemeMustExist(String arg0, String arg1) throws Throwable {
		hiveSteps.checkIfTableExist("${scheme}.${table}");
	}

	@And("^the \"([^\"]*)\" table does not exist$")
	public void theTableDoesNotExist(String table) throws Throwable {
		hiveSteps.dropTableIfExist(table);
	}
}
