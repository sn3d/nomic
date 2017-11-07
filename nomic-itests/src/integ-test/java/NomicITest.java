import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import org.junit.runner.RunWith;

/**
 * @author zdenko.vrabel@wirecard.com
 */
@RunWith(Cucumber.class)
@CucumberOptions(
	features="src/integ-test/resources/features",
	glue = "nomic.itest"
	//tags = {"@debug"}
	)
public class NomicITest {
}
