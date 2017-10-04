package nomic.bundle

import java.io.File
import java.io.FileInputStream
import java.io.InputStream

/**
 * Implementation of [Entry] used directly by [DirectoryBundle]
 *
 * @author zdenko.vrabel@wirecard.com
 */
internal class FileSystemEntry(val bundle: DirectoryBundle, val path: File) : Entry {

    /** return path of entry in bundle */
    override val name: String by lazy {
        path.toRelativeString(bundle.root)
    }

    /** open entry's file as input stream */
    override fun openInputStream(): InputStream = FileInputStream(path)

    /** return string form */
    override fun toString(): String = "FileSystemEntry($name)"



}