package nomic

import nomic.box.Box
import nomic.box.BoxInfo
import nomic.box.InstalledBox
import nomic.bundle.Bundle
import nomic.db.BoxRepository
import nomic.db.HdfsBoxRepository
import nomic.definition.*
import nomic.dsl.GroovyEngine
import nomic.dsl.NomicScriptEngine
import nomic.hdfs.HdfsAdapter
import nomic.hdfs.plugin.HdfsPlugin
import nomic.plugin.DefaultPlugins
import nomic.plugin.PluginRegistry
import java.util.*

/**
 * @author zdenko.vrabel@wirecard.com
 */
class Nomic(override val config: NomicConfig) : NomicInstance {

	private val hdfs: HdfsAdapter
    private val repository: BoxRepository
    private val scriptEngine: NomicScriptEngine = GroovyEngine(config)
    private val pluginRegistry: PluginRegistry

    init {
		// init plugins
		pluginRegistry = DefaultPlugins(config)

        // init hdfs
        hdfs = pluginRegistry.instanceOf(HdfsPlugin::class.java).hdfsAdapter
        config.hdfsHomeDir = hdfs.homeDirectory;

		// init DB
        repository = HdfsBoxRepository(config.hdfsRepositoryDir, hdfs, scriptEngine)
    }


    //-------------------------------------------------------------------------------------------------
    // implemented methods
    //-------------------------------------------------------------------------------------------------

    override fun install(bundle: Bundle, forceIt: Boolean):Box {
        // load box from bundle
        val box = Box.load(bundle, scriptEngine)

		// check if already installed
		if (isAlreadyInstalled(box.info)) {
			if (!forceIt) {
				throw BoxAlreadyInstalledException(box)
			}
		}

        // create installation context and load definitions from descriptor
        val context = InstallContext(box, pluginRegistry)
        val definitions = scriptEngine.eval(box.descriptor)

        // perform all definitions
        definitions.forEach {
            def -> when(def) {
                is Installable -> def.apply(context)
                is ModuleDef -> def.installModule(context, this, repository)
            }
		}

        // finish installation
        repository.save(box.info, box.descriptor);

        return box;
    }

    override fun uninstall(concreteBox: BoxInfo): Boolean {

		// check if box is installed
        val box = repository.open(concreteBox) ?: throw BoxNotFoundException(concreteBox.toString())

		// create context and get dependencies
		val dependencies = repository.dependenciesFor(box.info)
        val context = UninstallContext(box, this.pluginRegistry)

        // go through all definitions and perform removing on them
        scriptEngine.eval(box.descriptor).forEach {
            def -> when(def) {
			    is Removable -> def.revert(context)
            }
		}

        // finish uninstall of this module
        this.repository.remove(box.info)

		// remove unused dependencies
		dependencies
			.filter(this::unusedDependency)
			.forEach {
				uninstall(it)
			}

		return true;
    }

	override fun upgrade(bundle: Bundle): Box {
		// load box from bundle
		val box = Box.load(bundle, scriptEngine)

		// check if already installed and uninstall it
		if (isAlreadyInstalled(box.info)) {
			uninstall(box.info)
		}

		// install new version
        return install(bundle)
	}

	override fun details(info: BoxInfo): Optional<InstalledBox> {
		val installedBox = repository.open(info)
		return Optional.ofNullable(installedBox)
	}

	override fun installedBoxes(): List<BoxInfo> {
		val installedBoxes = installedBoxesAsSeq().toList()
		return installedBoxes
	}

	//-------------------------------------------------------------------------------------------------
	// private methods
	//-------------------------------------------------------------------------------------------------

	private fun installedBoxesAsSeq(): Sequence<BoxInfo> = repository.listInstalled()

	/**
	 * check if box with same ID and group is already installed in nomic.
	 * Version is ignored.
 	 */
	private fun isAlreadyInstalled(info: BoxInfo): Boolean {
		val alreadyInstalled =
			installedBoxesAsSeq()
				.find { it.sameAs(info) }

		return alreadyInstalled != null
	}


	private fun unusedDependency(dependency: BoxInfo): Boolean {
		val res = repository.usedBy(dependency)
		return res.isEmpty();
	}

}