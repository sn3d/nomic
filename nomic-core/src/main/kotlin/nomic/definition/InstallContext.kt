package nomic.definition

import nomic.box.BundledBox
import nomic.plugin.NomicPlugin
import nomic.plugin.PluginRegistry

/**
 * @author zdenko.vrabel@wirecard.com
 */
data class InstallContext(val box: BundledBox, private val pluginRegistry: PluginRegistry) {

	/*
    fun absolutePath(path: String):String {
        if (!path.startsWith("/")) {
			val normalizedAppDir = config.hdfsAppDir.removeSuffix("/")
			return "${normalizedAppDir}/${path}"
		} else {
            return path;
		}
    }*/

	fun<T : NomicPlugin> instanceOf(clazz: Class<T>): T = pluginRegistry.instanceOf(clazz)


}