package nomic.bundle

/**
 * @author zdenko.vrabel@wirecard.com
 */
class GroovyBundle(private val decorated: Bundle) : Bundle {

	override fun entries(filter: (Entry) -> Boolean): List<Entry> = decorated.entries(filter)
	override fun entry(path: String): Entry = decorated.entry(path)
	override fun descriptor(): Entry = entry("nomic.box")
}