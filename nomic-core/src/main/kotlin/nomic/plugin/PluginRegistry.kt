package nomic.plugin

/**
 * @author zdenko.vrabel@wirecard.com
 */
interface PluginRegistry {

	fun<T : NomicPlugin> instanceOf(clazz: Class<T>): T

}