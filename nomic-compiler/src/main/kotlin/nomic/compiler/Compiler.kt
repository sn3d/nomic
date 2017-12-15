/*
 * Copyright 2017 vrabel.zdenko@gmail.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package nomic.compiler

import groovy.lang.GroovyShell
import nomic.core.exception.MissingNameInScriptException
import nomic.core.*
import nomic.core.fact.GroupFact
import nomic.core.fact.NameFact
import nomic.core.fact.VersionFact
import nomic.core.script.FileScript
import nomic.dsl.FactionContext
import org.codehaus.groovy.control.CompilerConfiguration
import org.codehaus.groovy.control.customizers.ImportCustomizer

/**
 * This is groovy compiler implementation.
 *
 * Simple example with default values:
 * ```
 * val bundle:Bundle = ...
 * val facts: List<Fact> = Compiler.compile(bundle)
 * ```
 *
 * You can also specify global variables available in script:
 * ```
 * val bundle:Bundle = ...
 * val compiler = Compiler(defaultSchema = "default")
 * val facts: List<Fact> = compiler.compile(bundle)
 * ```
 *
 * @property user is mapped into 'user' global variable in DSL script. Default value is system 'user.name'
 * @property homeDir is mapped into 'homeDir' global variable in DSL script. Default value is /user/${user}
 * @property appDir is mapped into 'appDir' global variable in DSL script. Default value is /user/${user}/app
 * @property nameNode is mapped into 'nameNode' global variable in DSL script. The default value is 'hdfs://localhost'
 *
 * @see compile
 * @see Descriptor
 * @see Fact
 * @author vrabel.zdenko@gmail.com
 */
class Compiler {

	val user: String
	val homeDir: String
	val appDir: String
	val expos: List<Exposable>

	/**
	 * parametrized constructor you can use when you need to
	 * change some global variable.
	 */
	constructor(user: String = "", homeDir: String = "", appDir: String = "", expos: List<Exposable> = emptyList()) {
		this.user = user
		this.homeDir = homeDir
		this.appDir = appDir
		this.expos = expos
	}


	/**
	 * construct the compiler with default values (used for testing)
	 */
	constructor() {
		this.user     = System.getProperty("user.name");
		this.homeDir  = "/users/${user}"
		this.appDir   = "${homeDir}/app"
		this.expos    = emptyList()
	}


	companion object {
		private val defaultCompiler by lazy {
			Compiler()
		}

		fun compile(script: Script): List<Fact> = defaultCompiler.compile(script)
		fun compile(path: String): List<Fact> = defaultCompiler.compile(FileScript(path))
	}


	/**
	 * this function do main compilation of the [Script] to
	 * [Fact]s.
	 */
	fun compile(script: Script): List<Fact> {
		val shell = setupShell()
		shell.bindGlobalVariables()
		shell.evaluate(script)
		val facts = shell.getGlobalFacts()
		val factionFacts = shell.getSortedFactionFacts()
		return facts + factionFacts;
	}


	/**
	 * this method prepare and initialize Groovy shell for DSL.
	 * The 'nomic.dsl' package is imported as default. Also the
	 * [NomicBaseScriptEx] will be used as global script class - that means the [Descriptor] script
	 * have all methods the [NomicBaseScriptEx] has.
	 */
	private fun setupShell(): GroovyShell {
		// set base script
		val compilerConfiguration = CompilerConfiguration()
		compilerConfiguration.scriptBaseClass = "nomic.dsl.NomicBaseScriptEx"

		// set imports
		val importCustomizer = ImportCustomizer()
		importCustomizer.addStarImports("nomic.dsl")
		compilerConfiguration.addCompilationCustomizers(importCustomizer)

		return GroovyShell(compilerConfiguration);
	}


	/**
	 * bind variables to shell. They will be available as
	 * global variables in script
	 */
	private fun GroovyShell.bindGlobalVariables() {
		this.setVariable("user", user)
		this.setVariable("appDir", appDir)
		this.setVariable("homeDir", homeDir)

		//get trough all Exposed items
		//and bind exposed variables
		expos.flatMap(Exposable::exposedVariables).forEach { variable ->
			this.setVariable(variable.first, variable.second)
		}
	}


	/**
	 * evaluate script
	 */
	private fun GroovyShell.evaluate(script: Script) {
		script.open().use {
			this.evaluate(it)
		}
	}


	/**
	 * retrieve the builders from 'factBuilders' global variable
	 * and create facts
	 */
	private fun GroovyShell.getGlobalFacts(): List<Fact> {
		// transform to facts
		val builders = this.context.variables["factBuilders"] as List<*>? ?: emptyList<Any>()
		val facts =
			builders.asSequence()
				.filterIsInstance(FactBuilder::class.java)
				.map(FactBuilder::build)
				.toList()

		val nameFact  = NameFact(this.context.variables["name"] as String? ?: throw MissingNameInScriptException())
		val groupFact = GroupFact(this.context.variables["group"] as String? ?: "")
		val versionFact = VersionFact(this.context.variables["version"] as String? ?: "")

		return listOf(nameFact, groupFact, versionFact) + facts
	}


	/**
	 * return a sorted list of facts they're defined in factions. The facts are sorted by
	 * factions dependencies.
	 */
	private fun GroovyShell.getSortedFactionFacts(): List<Fact> {
		// transform to facts
		val rawFactions = this.context.variables["factions"] as List<*>? ?: emptyList<Any>()
		val factions = rawFactions.asSequence().filterIsInstance(FactionContext.Faction::class.java).toList()

		val factionNodes =
			rawFactions.asSequence()
				.filterIsInstance(FactionContext.Faction::class.java)
				.map { faction -> Node<String>(faction.factionId, faction.extractDependencies()) }
				.toList()

		val sortedNodes = factionNodes.topologySort()
		val sortedFactions = factions.sortedBy { faction -> sortedNodes.indexOf(faction.factionId) }

		return sortedFactions
			.flatMap(this@Compiler::createFactorsFromFaction)
			.toList()
	}


	private fun FactionContext.Faction.extractDependencies() : MutableList<String> =
		if (this@extractDependencies.dependsOn.isNullOrEmpty()) {
			mutableListOf()
		} else {
			mutableListOf(this.dependsOn)
		}


	private fun createFactorsFromFaction(faction: FactionContext.Faction): List<Fact> =
		faction
			.factBuilders
			.map(FactBuilder::build)

}
