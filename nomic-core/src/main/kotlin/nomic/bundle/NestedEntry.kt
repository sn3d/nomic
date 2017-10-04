package nomic.bundle

import java.io.InputStream

/**
 * @author zdenko.vrabel@wirecard.com
 */
class NestedEntry(override val name: String, private val parent: Entry) : Entry {

    override fun openInputStream(): InputStream = parent.openInputStream()

}