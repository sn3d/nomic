package nomic.itest.steps;

import net.thucydides.core.annotations.Step;
import nomic.app.NomicApp;
import nomic.core.BoxExpr;
import nomic.core.BoxRef;
import nomic.core.Bundle;
import nomic.core.InstalledBox;
import nomic.core.NomicConfig;
import nomic.core.NomicInstance;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * @author zdenko.vrabel@wirecard.com
 */
public class NomicSteps {

	@Step
	public void installBox(String path) {
		NomicInstance nomic = NomicApp.createDefault();
		nomic.install(Bundle.Companion.create(path), false);
	}


	@Step
	public void installBoxForce(String path) {
		NomicInstance nomic = NomicApp.createDefault();
		nomic.install(Bundle.Companion.create(path), true);
	}


	@Step
	public void upgradeBox(String path) {
		NomicInstance nomic = NomicApp.createDefault();
		nomic.upgrade(Bundle.Companion.create(path));
	}


	public void uninstallBox(String boxId) {
		NomicInstance nomic = NomicApp.createDefault();
		BoxRef ref = BoxRef.parse(boxId);
		nomic.uninstall(ref, false);
	}


	public NomicConfig loadNomicConfiguration() {
		NomicInstance nomic = NomicApp.createDefault();
		return nomic.getConfig();
	}


	@Step
	public void checkIfBoxExist(String boxExpr) {
		NomicInstance nomic = NomicApp.createDefault();
		BoxExpr expressions = BoxExpr.parse(boxExpr);
		BoxRef ref = nomic.installedBoxes().stream().filter(expressions::matchTo).findFirst().get();
		InstalledBox details = nomic.details(ref);
		assertThat(details).as("details for %s", boxExpr).isNotNull();
	}


	@Step
	public void checkIfBoxNotExist(String boxExpr) {
		NomicInstance nomic = NomicApp.createDefault();
		BoxExpr expressions = BoxExpr.parse(boxExpr);
		Optional<BoxRef> ref = nomic.installedBoxes().stream().filter(expressions::matchTo).findFirst();
		assertThat(ref).as("details for %s", boxExpr).isNotPresent();
	}


	@Step
	public void uninstallIfExist(String boxExpr) {
		NomicInstance nomic = NomicApp.createDefault();
		Optional<BoxRef> installedBox = nomic.installedBoxes()
			.stream()
			.filter((x) -> BoxExpr.parse(boxExpr).matchTo(x))
			.findFirst();

		if (installedBox.isPresent()) {
			nomic.uninstall(installedBox.get(), false);
		}
	}


	@Step
	public boolean isBoxInstalled(String boxExpr) {
		NomicInstance nomic = NomicApp.createDefault();
		InstalledBox details = nomic.details(BoxRef.parse(boxExpr));
		return details != null;
	}

}
