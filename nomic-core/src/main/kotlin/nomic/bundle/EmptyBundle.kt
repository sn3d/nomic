package nomic.bundle

/**
 * @author zdenko.vrabel@wirecard.com
 */
class EmptyBundle : Bundle {
    override fun entry(path: String): Entry {
        throw UnsupportedOperationException()
    }

    override fun entries(filter: (Entry) -> Boolean): List<Entry> {
        throw UnsupportedOperationException()
    }
}