package nomic.bundle

import nomic.InvalidBundleException
import nomic.InvalidPathInBundleException
import java.io.File

/**
 * This implementation of bundle is used by bundles they're as
 * unzipped in some folder on File System
 *
 * @author zdenko.vrabel@wirecard.com
 */
class DirectoryBundle(val root: File) : Bundle {

    /** overridden constructor */
    constructor(_root: String) : this(File(_root))

    init {
        if (!root.exists() || !root.isDirectory) {
            throw InvalidBundleException("The ${root} must be existing directory")
        }
    }


    /**
     * return the entry in bundle by path or null if not exist
     * (or it's directory)
     */
    override fun entry(path: String): Entry {
        val f = File(root, path)
        if (f.exists() && f.isFile) {
            return FileSystemEntry(this, f);
        }
        throw InvalidPathInBundleException("There is no file on ${path} in ${this} bundle.");
    }


    /**
     * return collection of all available entries in bundle
     */
    override fun entries(filter: (Entry) -> Boolean): List<Entry> =
            allEntriesIn(root)
                    .filter(filter)
                    .toList()


    /**
     * go recursively through directories and gather all files
     */
    private fun allEntriesIn(dir: File): Sequence<Entry> =
        dir.listFiles()
            .asSequence()
            .flatMap {
                if (it.isDirectory) allEntriesIn(it) else sequenceOf(FileSystemEntry(this, it))
            }

    /**
     * toString implementation
     */
    override fun toString(): String {
        return "FileSystemBundle($root)"
    }


}