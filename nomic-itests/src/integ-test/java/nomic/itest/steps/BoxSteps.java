package nomic.itest.steps;

import net.thucydides.core.annotations.Step;
import nomic.box.Box;
import nomic.box.BoxLoader;

import java.io.File;

/**
 * @author zdenko.vrabel@wirecard.com
 */
public class BoxSteps {

	@Step
	public Box loadBox(String path) {
		return BoxLoader.INSTANCE.load(new File(path));
	}
}
