package nomic.bundle

/**
 * @author zdenko.vrabel@wirecard.com
 */
class NestedBundle(val parent: Bundle, val root: String) : Bundle {


    override fun entries(filter: (Entry) -> Boolean): List<Entry> =
        parent.entries(this::filterRootEntries).map(this::wrapToNestedEntry)

    override fun entry(path: String): Entry {
        return NestedEntry(path, parent.entry("${root}/${path}"))
    }

	private fun filterRootEntries(entry: Entry): Boolean = entry.name.startsWith(root)

    private fun wrapToNestedEntry(entry: Entry): Entry = NestedEntry( entry.name.removePrefix(root), entry )

}