package nomic.bundle

import java.io.InputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipFile

/**
 * @author zdenko.vrabel@wirecard.com
 */
class ArchiveEntry(val zip: ZipFile, val zipEntry: ZipEntry) : Entry {

    override val name: String
        get() = zipEntry.name

    override fun openInputStream(): InputStream = zip.getInputStream(zipEntry);

}