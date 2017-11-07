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
package nomic.app

import com.sun.org.apache.xml.internal.security.utils.XalanXPathAPI.isInstalled
import nomic.app.config.TypesafeConfig
import nomic.compiler.Compiler
import nomic.core.*
import nomic.core.exception.BoxAlreadyInstalledException
import nomic.core.exception.BoxNotInstalledException
import nomic.core.exception.WtfException
import nomic.core.fact.ModuleFact
import nomic.core.fact.RequireFact
import nomic.db.AvroDb
import nomic.db.NomicDb
import nomic.hdfs.HdfsPlugin
import nomic.hdfs.adapter.HdfsAdapter
import nomic.hive.HivePlugin
import nomic.oozie.OoziePlugin

/**
 * @author vrabel.zdenko@gmail.com
 */
class NomicApp : NomicInstance {
	
	//-------------------------------------------------------------------------------------------------
	// properties & construction
	//-------------------------------------------------------------------------------------------------

	override val config: NomicConfig
	private val compiler: Compiler
	private val plugins: List<Plugin>
	private val hdfs: HdfsAdapter
	private val db: NomicDb

	constructor(config: NomicConfig, plugins: List<Plugin>) {

		this.plugins = plugins

		this.hdfs = plugins.filterIsInstance(HdfsPlugin::class.java).firstOrNull()?.hdfs ?: throw WtfException()
		this.db = AvroDb(
			hdfs = hdfs,
			nomicHome = config.hdfsRepositoryDir
		)

		this.config = this.Config(config)
		this.compiler = Compiler(
			user      = config.user,
			homeDir   = config.hdfsHomeDir,
			appDir    = config.hdfsAppDir,
			expos     = plugins.filterIsInstance(Exposable::class.java).toList()
		)
	}

	companion object {

		@JvmStatic
		fun createDefault(): NomicApp =
			createDefault(TypesafeConfig.loadDefaultConfiguration())

		@JvmStatic
		fun createDefault(config:NomicConfig): NomicApp {
			val config = TypesafeConfig.loadDefaultConfiguration()
			val hdfsPlugin = HdfsPlugin.init(config)
			val hivePlugin = HivePlugin.init(config)
			val ooziePlugin = OoziePlugin.init(config, hdfsPlugin.hdfs)
			return NomicApp(config, listOf(hdfsPlugin, hivePlugin, ooziePlugin))
		}
	}

	//-------------------------------------------------------------------------------------------------
	// inner classes
	//-------------------------------------------------------------------------------------------------

	private inner class Config(private val parent: NomicConfig): NomicConfig() {
		override fun get(name: String): String? {
			if (name == "nomic.hdfs.home") {
				return hdfs.homeDirectory
			} else {
				return parent[name]
			}
		}
	}

	//-------------------------------------------------------------------------------------------------
	// implemented methods
	//-------------------------------------------------------------------------------------------------


	/**
	 * open the bundle and compile it's root 'nomic.box' into [BundledBox] with facts
	 */
	override fun compile(bundle: Bundle): BundledBox =
		compileAll(bundle)
			.filterIsInstance(RootBox::class.java)
			.first()


	/**
	 * This function compile the bundle's box and all submodules.
	 *
	 * @see NomicInstance.compileAll
	 */
	override fun compileAll(bundle: Bundle): List<BundledBox> {
		val facts = compiler.compile(bundle.script)
		val rootBox = RootBox(bundle, facts)
		val dependenciesFacts = mutableListOf<RequireFact>()

		return facts.findFactsType(ModuleFact::class.java)
			.flatMap { moduleFact ->
				// compile child module and child's submodules (recursion)
				val childModuleBundle = NestedBundle(rootBox, moduleFact.name)
				val childModuleBoxes = compileAll(childModuleBundle)

				// find the root, create require fact
				val r = childModuleBoxes.filterIsInstance(RootBox::class.java).first()
				dependenciesFacts += RequireFact(box = r.ref())
				childModuleBoxes
			}
			.map { box ->
				// unwrap all roots (only one root might exist)
				if (box is RootBox) {
					box.unwrapRoot()
				} else {
					box
				}
			}
			.toList() + RootBox(rootBox, facts + dependenciesFacts)
	}


	/**
	 * compile the bundle and install it if it's not installed yet.
	 *
	 * @param force if it set to true, the bundle will be installed
	 *        even if box is already present. It's good for
	 *        fixing bad installations.
	 *
	 * @return references to all installed boxes from bundle in order how
	 *         they was installed.
	 */
	override fun install(bundle: Bundle, force: Boolean): List<BoxRef> {
		// compile bundle into boxes and do topology sort of them
		val boxes = compileAll(bundle)
		val sortedBoxes = boxes.topologySort()

		// install the boxes in right order
		val installedRefs =
			sortedBoxes.map { box ->
				install(box as BundledBox, force)
			}

		return installedRefs
	}


	/**
	 * install once concrete [BundledBox]
	 */
	fun install(box: BundledBox, force: Boolean): BoxRef {
		if (!canInstall(box, force)) {
			throw BoxAlreadyInstalledException(box)
		}

		// send all facts into plugins for commit
		for(fact in box.facts) {
			commitFact(box, fact)
		}

		//TODO: replace emptyList() by all require facts
		db.insertOrUpdate(box)
		return box.ref()
	}


	/**
	 * uninstall the box by reference
	 */
	override fun uninstall(ref: BoxRef, force: Boolean)  {
		val installedBox = details(ref)
		if (installedBox != null) {
			uninstall(installedBox, force)
		}
	}


	/**
	 * uninstall the [InstalledBox]
	 */
	fun uninstall(box: InstalledBox, force: Boolean) {
		// first uninstall all dependencies
		box.dependencies.forEach { dep ->
			uninstall(dep, force)
		}

		// send all facts into plugins for rollback
		for(fact in box.facts) {
			rollbackFact(box, fact)
		}

		db.delete(box.ref())
	}


	/**
	 * compile the bundle and upgrade it, or install if it's not
	 * present
	 */
	override fun upgrade(bundle: Bundle) =
		upgrade(compile(bundle))


	/**
	 * upgrade the box or install if it's not
	 * present
	 */
	fun upgrade(box: BundledBox) {
		if (isAlreadyInstalled(box)) {
			uninstall(box.ref(), false)
		}
		install(box, false)
	}


	/**
	 * return list of boxes installed/available in system
	 */
	override fun installedBoxes(): List<BoxRef> =
		db.loadAll().map(Box::ref)


	/**
	 * return concrete box with facts if it's present, otherwise
	 * returns null.
	 */
	override fun details(info: BoxRef): InstalledBox? =
		db.load(info)?.compileWith(compiler)



	//-------------------------------------------------------------------------------------------------
	// private methods
	//-------------------------------------------------------------------------------------------------

	private fun canUninstall(box: Box, force: Boolean): Boolean =
		isAlreadyInstalled(box) || force

	private fun canInstall(box:Box, force: Boolean): Boolean =
		isNotInstalled(box) || force

	private fun isAlreadyInstalled(box: Box): Boolean =
		!isNotInstalled(box)

	private fun isNotInstalled(box: Box): Boolean =
		db.load(box.ref()) == null

	private fun commitFact(box: BundledBox, fact: Fact) {
		plugins.asSequence()
			.forEach { p -> p.commit(box, fact) }
	}

	private fun rollbackFact(box: InstalledBox, fact: Fact) {
		plugins.asSequence()
			.forEach { p -> p.rollback(box, fact) }
	}

}