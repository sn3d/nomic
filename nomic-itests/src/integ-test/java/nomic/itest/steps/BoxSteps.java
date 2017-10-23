package nomic.itest.steps;

import net.thucydides.core.annotations.Step;
import nomic.app.BoxFactory;
import nomic.core.Box;
/**
 * @author zdenko.vrabel@wirecard.com
 */
public class BoxSteps {

	@Step
	public Box loadBox(String path) {
		return BoxFactory.INSTANCE.compileBundle(path);
	}
}
