package nomic.itest.stepdef;

import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import net.thucydides.core.annotations.Steps;
import nomic.NomicConfig;
import nomic.itest.steps.HdfsSteps;
import nomic.itest.steps.NomicSteps;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author zdenko.vrabel@wirecard.com
 */
public class HdfsStepDefs {

	@Steps
	public HdfsSteps hdfsSteps;

	@Steps
	public NomicSteps nomicSteps;


	@When("^install the box with HDFS resources from \"([^\"]*)\"$")
	public void installTheBoxWithHDFSResourcesFrom(String path) throws Throwable {
		nomicSteps.installBox(path);
	}

	@Then("^the file \"([^\"]*)\" is copied into nomicHdfsAppDir/nomic-examples/hello-world folder in HDFS$")
	public void theFileIsCopiedIntoNomicHdfsAppDirNomicExamplesHelloWorldFolderInHDFS(String fileName) throws Throwable {
		NomicConfig conf = nomicSteps.loadNomicConfiguration();
		String path = conf.getHdfsAppDir() + "/nomic-examples/hello-world/" + fileName;
		hdfsSteps.fileOrDirExist(path);
	}

	@Then("^the file \"([^\"]*)\" is removed from nomicHdfsAppDir/nomic-examples/hello-world folder in HDFS$")
	public void theFileIsRemovedFromNomicHdfsAppDirNomicExamplesHelloWorldFolderInHDFS(String fileName) throws Throwable {
		NomicConfig conf = nomicSteps.loadNomicConfiguration();
		String path = conf.getHdfsAppDir() + "/nomic-examples/hello-world/" + fileName;
		hdfsSteps.fileOrDirNotExist(path);
	}

	@When("^install the box from \"([^\"]*)\"$")
	public void installTheBoxFrom(String path) throws Throwable {
		nomicSteps.installBox(path);
	}

	@Then("^the directory 'dir-stay' must exist$")
	public void theDirectoryDirStayMustExist() throws Throwable {
		NomicConfig conf = nomicSteps.loadNomicConfiguration();
		String path = conf.getHdfsHomeDir() + "/dir-stay";
		hdfsSteps.fileOrDirExist(path);
	}

	@Then("^the directory 'dir-stay' is created in nomicHdfsHomeDir in HDFS$")
	public void theDirectoryDirStayIsCreatedInNomicHdfsHomeDirInHDFS() throws Throwable {
		NomicConfig conf = nomicSteps.loadNomicConfiguration();
		String path = conf.getHdfsHomeDir() + "/dir-stay";
		hdfsSteps.fileOrDirExist(path);
	}

	@And("^also 'dir-remove' marked as removable must be created in nomicHdfsHomeDir$")
	public void alsoDirRemoveMarkedAsRemovableMustBeCreatedInNomicHdfsHomeDir() throws Throwable {
		NomicConfig conf = nomicSteps.loadNomicConfiguration();
		String path = conf.getHdfsHomeDir() + "/dir-remove";
		hdfsSteps.fileOrDirExist(path);
	}

	@And("^'dir-remove' marked as removable must be removed$")
	public void dirRemoveMarkedAsRemovableMustBeRemoved() throws Throwable {
		NomicConfig conf = nomicSteps.loadNomicConfiguration();
		String path = conf.getHdfsHomeDir() + "/dir-remove";
		hdfsSteps.fileOrDirNotExist(path);
	}

	@Then("^file 'simple-template\\.xml' is installed in app dir with replaced values$")
	public void fileSimpleTemplateXmlIsInstalledInAppDir() throws Throwable {
		NomicConfig conf = nomicSteps.loadNomicConfiguration();
		String path = conf.getHdfsAppDir() + "/nomic-examples/template/simple-template.xml";
		// load file's content
		String text = hdfsSteps.loadFileAsText(path);
		assertThat(text.contains("Clean Code")).as("replace ${title} to Clean Code").isTrue();
	}

	@And("^file 'beautiful-code\\.xml' is installed in /test folder$")
	public void fileBeautifulCodeXmlIsInstalledInNomicExamplesTemplateFolder() throws Throwable {
		String path = "/test/beautiful-code.xml";
		hdfsSteps.fileOrDirExist(path);
	}

	@Then("^the file \"([^\"]*)\" is copied into /test folder in HDFS root$")
	public void theFileIsCopiedIntoTestFolderInHDFSRoot(String filename) throws Throwable {
		String path = "/test/" + filename;
		hdfsSteps.fileOrDirExist(path);
	}

	@Then("^the file \"([^\"]*)\" is copied into working dir, into /test")
	public void theFileIsCopiedIntoWorkingDirIntoTestInHDFSRoot(String file) throws Throwable {
		NomicConfig conf = nomicSteps.loadNomicConfiguration();
		String path = conf.getHdfsAppDir() + "/nomic-examples/hello-world/test/" + file;
		hdfsSteps.fileOrDirExist(path);
	}

	@Then("^the directory 'dir-stay' must exists$")
	public void theDirectoryDirStayMustExists() throws Throwable {
		NomicConfig conf = nomicSteps.loadNomicConfiguration();
		String path = conf.getHdfsHomeDir() + "/dir-stay";
		hdfsSteps.fileOrDirExist(path);
	}
}
