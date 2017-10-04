package nomic.itest;

import net.thucydides.core.annotations.Step;
import nomic.Nomic;
import nomic.NomicConfig;
import nomic.NomicInstance;
import nomic.TypesafeNomicConfig;
import nomic.box.BoxInfo;
import nomic.box.InstalledBox;
import nomic.bundle.Bundle;
import org.assertj.core.api.Assertions;

import java.io.File;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author zdenko.vrabel@wirecard.com
 */
public class NomicSteps {

	@Step
	public void installBox(String path) {
		NomicInstance nomic = new Nomic(TypesafeNomicConfig.Companion.loadDefaultConfiguration());
		nomic.install(Bundle.Companion.open(new File(path)), false);
	}

	@Step
	public void installBoxForce(String path) {
		NomicInstance nomic = new Nomic(TypesafeNomicConfig.Companion.loadDefaultConfiguration());
		nomic.install(Bundle.Companion.open(new File(path)), true);
	}


	@Step
	public void upgradeBox(String path) {
		NomicInstance nomic = new Nomic(TypesafeNomicConfig.Companion.loadDefaultConfiguration());
		nomic.upgrade(Bundle.Companion.open(new File(path)));
	}

	public void uninstallBox(String boxId) {
		NomicInstance nomic = new Nomic(TypesafeNomicConfig.Companion.loadDefaultConfiguration());
		BoxInfo boxInfo = BoxInfo.parse(boxId);
		nomic.uninstall(boxInfo);
	}

	public NomicConfig loadNomicConfiguration() {
		NomicInstance nomic = new Nomic(TypesafeNomicConfig.Companion.loadDefaultConfiguration());
		return nomic.getConfig();
	}

	@Step
	public void checkIfBoxExist(String boxExpr) {
		NomicInstance nomic = new Nomic(TypesafeNomicConfig.Companion.loadDefaultConfiguration());
		Optional<InstalledBox> details = nomic.details(BoxInfo.parse(boxExpr));
		assertThat(details).as("details for %s", boxExpr).isPresent();
	}

	@Step
	public void checkIfBoxNotExist(String boxExpr) {
		NomicInstance nomic = new Nomic(TypesafeNomicConfig.Companion.loadDefaultConfiguration());
		Optional<InstalledBox> details = nomic.details(BoxInfo.parse(boxExpr));
		assertThat(details).as("details for %s", boxExpr).isNotPresent();
	}


	@Step
	public void uninstallIfExist(String boxExpr) {
		NomicInstance nomic = new Nomic(TypesafeNomicConfig.loadDefaultConfiguration());
		nomic.installedBoxes()
			.stream()
			.filter((x) -> x.matchTo(boxExpr))
			.findFirst()
			.ifPresent(nomic::uninstall);
	}


	@Step
	public boolean isBoxInstalled(String boxExpr) {
		NomicInstance nomic = new Nomic(TypesafeNomicConfig.Companion.loadDefaultConfiguration());
		Optional<InstalledBox> details = nomic.details(BoxInfo.parse(boxExpr));
		return details.isPresent();
	}

}
