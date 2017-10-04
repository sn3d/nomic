package nomic.bundle

import nomic.InvalidPathInBundleException
import java.io.File

/**
 * @author zdenko.vrabel@wirecard.com
 */
interface Bundle {

    /** find and return entry on given path */
	fun entry(path: String): Entry

    /** return collection of all entries in bundle, folders are excluded */
	fun entries(filter: (Entry) -> Boolean = { true }): List<Entry>

    fun descriptor(): Entry = entry("nomic.box")

    fun descriptorAsInputStream() = descriptor().openInputStream()

	companion object {
		fun open(path: String) = open(File(path))

		fun open(path: File): Bundle = when {
			path.isDirectory -> DirectoryBundle(path);
			path.isFile -> ArchiveBundle(path);
			else -> throw InvalidPathInBundleException("The ${path} doesn't exist.")
		}
	}
}