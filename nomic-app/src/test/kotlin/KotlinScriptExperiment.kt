import groovy.lang.GroovyShell
import nomic.dsl.DefBuilder
import org.codehaus.groovy.control.CompilerConfiguration
import org.codehaus.groovy.control.customizers.ImportCustomizer
import org.junit.Ignore
import org.junit.Test
import java.io.InputStreamReader
import javax.script.ScriptEngineManager



/**
 * @author zdenko.vrabel@wirecard.com
 */
@Ignore
class KotlinScriptExperiment {

	@Test
	fun findScriptEngine() {
		val em = ScriptEngineManager()
		val factories = em.engineFactories;
	}

	@Test
	fun testGroovyEngine() {

		val resource = this.javaClass.getResourceAsStream("bundles/analysis1/nomic.groovy")
		val reader = InputStreamReader(resource)

		val compilerConfiguration = CompilerConfiguration()
		compilerConfiguration.scriptBaseClass = "nomic.dsl.NomicBaseScript"

		val importCustomizer = ImportCustomizer()
		importCustomizer.addStarImports("nomic.dsl")
		compilerConfiguration.addCompilationCustomizers(importCustomizer)

		val shell = GroovyShell(compilerConfiguration)
		shell.evaluate(reader)
		shell.evaluate("finish()")

		val producers = shell.context.variables["defProducers"] as List<*>

		val definitions =
			producers.asSequence()
				.filter { builder -> builder is DefBuilder }
				.map { builder -> (builder as DefBuilder).build()}
				.toList()

	}

    /*
    @Test
    fun runSimpleScript() {
        //prepare engine
        val em = ScriptEngineManager()
        val engine = em.getEngineByExtension("kts")!!

        engine.put("context", NomicContext("qwe"))

        //load and execute the script
        val resource = this.javaClass.getResourceAsStream("script1.kts")
        val reader = InputStreamReader(resource)
        engine.eval(reader)

        val inv = engine as Invocable
        val res = inv.invokeFunction("main", NomicContext("qwe"))

        println("Output is ${res}")
    }



    @Test
    fun runNomicBundleScript() {
        //prepare engine
        val em = ScriptEngineManager()
        val engine = em.getEngineByExtension("kts")!!

        //load and execute the script
        val resource = this.javaClass.getResourceAsStream("bundle/nomic.kts")
        val reader = InputStreamReader(resource)
        val res = engine.eval(reader) as BundleContext

        //val inv = engine as Invocable
        //val res = inv.invokeFunction("main", NomicContext("qwe"))

        println("Output is ${res}")
    }
    */

}