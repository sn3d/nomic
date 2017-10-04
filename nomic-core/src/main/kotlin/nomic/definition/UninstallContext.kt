package nomic.definition

import nomic.box.InstalledBox
import nomic.plugin.NomicPlugin
import nomic.plugin.PluginRegistry

/**
 * @author zdenko.vrabel@wirecard.com
 */
data class UninstallContext(val box: InstalledBox, private val pluginRegistry: PluginRegistry) {

	fun<T : NomicPlugin> instanceOf(clazz: Class<T>): T = pluginRegistry.instanceOf(clazz)
}