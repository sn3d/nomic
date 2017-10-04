package nomic.bundle

import nomic.InvalidPathInBundleException
import java.io.File
import java.util.zip.ZipFile

/**
 * @author zdenko.vrabel@wirecard.com
 */
class ArchiveBundle : Bundle {

    val zip:ZipFile;

    constructor(path: File) {
        zip = ZipFile(path)
    }

    override fun entry(path: String): Entry {
        val zipEntry = zip.getEntry(path) ?: throw InvalidPathInBundleException("There is no file on ${path} in ${this} bundle.");
        return ArchiveEntry(zip, zipEntry);
    }

    override fun entries(filter: (Entry) -> Boolean): List<Entry> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}