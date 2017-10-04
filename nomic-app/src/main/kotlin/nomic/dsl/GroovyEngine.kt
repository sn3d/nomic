package nomic.dsl

import groovy.lang.GroovyShell
import nomic.DefinitionCannotBeCreatedException
import nomic.NomicConfig
import nomic.WtfException
import nomic.definition.Definition
import org.codehaus.groovy.control.CompilerConfiguration
import org.codehaus.groovy.control.customizers.ImportCustomizer
import java.io.Reader

/**
 * @author zdenko.vrabel@wirecard.com
 */
class GroovyEngine(private val config: NomicConfig) : NomicScriptEngine() {

	constructor() : this(NomicConfig())

	override fun eval(reader: Reader): List<Definition> {
		// prepare shell and variables
		val shell = setupShell()

		shell.setVariable("user", config.user)
		shell.setVariable("appDir", config.hdfsAppDir)
		shell.setVariable("homeDir", config.hdfsHomeDir)
		shell.setVariable("defaultSchema", config.hiveSchema)

		// evaluate script
		shell.evaluate(reader)
		shell.evaluate("finish()")

		// transform to definitions
		val builders = shell.context.variables["defProducers"] as List<*>;
		return transformToDefinition(builders);
	}

	private fun setupShell(): GroovyShell {
		val compilerConfiguration = CompilerConfiguration()
		compilerConfiguration.scriptBaseClass = "nomic.dsl.NomicBaseScript"

		val importCustomizer = ImportCustomizer()
		importCustomizer.addStarImports("nomic.dsl")
		compilerConfiguration.addCompilationCustomizers(importCustomizer)

		return GroovyShell(compilerConfiguration);
	}

	private fun transformToDefinition(builders: List<*>) =
		builders.map { b -> (b as DefBuilder).build() ?: throw DefinitionCannotBeCreatedException(b) }

}