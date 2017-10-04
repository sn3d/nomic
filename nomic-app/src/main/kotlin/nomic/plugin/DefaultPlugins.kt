package nomic.plugin

import nomic.NomicConfig
import nomic.hdfs.plugin.HdfsPlugin
import nomic.hive.plugin.HivePlugin

/**
 * @author zdenko.vrabel@wirecard.com
 */
class DefaultPlugins(config: NomicConfig) : PluginRegistry {

	private val plugins:List<NomicPlugin> =
		listOf(
			HivePlugin.createPlugin(config),
			HdfsPlugin.createPlugin(config)
		)

	override fun <T : NomicPlugin> instanceOf(clazz: Class<T>): T =
		plugins
			.filter { p -> clazz.isInstance(p) }
			.map { p -> clazz.cast(p) }
			.first()
}