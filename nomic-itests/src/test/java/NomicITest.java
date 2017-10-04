import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import net.serenitybdd.cucumber.CucumberWithSerenity;
import org.junit.runner.RunWith;

/**
 * @author zdenko.vrabel@wirecard.com
 */
@RunWith(Cucumber.class)
@CucumberOptions(
	features="src/test/resources/features",
	glue = "nomic.itest")
public class NomicITest {
}
