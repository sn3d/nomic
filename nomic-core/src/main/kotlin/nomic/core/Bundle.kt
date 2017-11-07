/*
 * Copyright 2017 vrabel.zdenko@gmail.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package nomic.core

import nomic.core.exception.BundleDoesNotExistException
import nomic.core.exception.WtfException
import nomic.core.script.BundleScript
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.nio.ByteBuffer
import java.nio.file.*
import java.util.zip.ZipInputStream


/**
 * It's kind of package. It's bundled [Script] with all content the box
 * need as archive or directory. Every bundle must have 'nomic.box' [Script]
 * on root.
 *
 * @author vrabel.zdenko@gmail.com
 */
interface Bundle {

	/**
	 * find and return entry on given path. Each path must start with root '/'. */
	fun entry(path: String): Entry?

	/** return collection of all entries in bundle, folders are excluded.*/
	fun entries(filter: (Entry) -> Boolean = { true }): List<Entry>

	companion object {

		fun create(path: String): Bundle =
			create(FileSystems.getDefault().getPath(path))

		fun create(path: Path): Bundle {
			if (Files.isDirectory(path)) {
				return FileSystemBundle(path)
			} else if (Files.isRegularFile(path)) {
				return ArchiveBundle(path)
			} else {
				throw BundleDoesNotExistException(path)
			}
		}
	}
}

//-------------------------------------------------------------------------------------------------
// extensions
//-------------------------------------------------------------------------------------------------

val Bundle.script: Script
	get() = BundleScript(this)

fun String.asBundle(): Bundle =
	Bundle.create(this)

fun String.asBundleFrom(fs: FileSystem): Bundle =
	Bundle.create(fs.getPath(this))

//-------------------------------------------------------------------------------------------------
// implementations
//-------------------------------------------------------------------------------------------------

/**
 * This implementation operates with bundle as directory on
 * FileSystem where bundle's [Entry]-ies are all files inside root
 * directory.
 */
internal class FileSystemBundle(val root: Path) : Bundle {

	constructor(root: String, fs: FileSystem = FileSystems.getDefault()) : this(fs.getPath(root))

	override fun entry(path: String): Entry? {
		val entryPath =
			when {
				path.startsWith("/") -> root.resolve(path.removePrefix("/"))
				else -> root.resolve(path)
			}

		if (Files.exists(entryPath) && Files.isRegularFile(entryPath)) {
			return FileSystemEntry(entryPath)
		} else {
			return null;
		}
	}

	override fun entries(filter: (Entry) -> Boolean): List<Entry> =
		listDirectory(root)
			.map { file -> FileSystemEntry(file) }
			.filter { entry -> filter(entry) }
			.toList()


	private fun listDirectory(path: Path) : Sequence<Path> =
		Files.newDirectoryStream(path).asSequence()
			.flatMap { p ->
				when {
					Files.isDirectory(p) -> listDirectory(p)
					Files.isRegularFile(p) -> sequenceOf(p)
					else -> throw WtfException()
				}
			}


	private inner class FileSystemEntry(private val path: Path) : Entry {
		override val name: String by lazy {
			"/" + root.relativize(path).normalize().toString()
		}

		override fun openInputStream(): InputStream =
			Files.newInputStream(root.resolve(name.removePrefix("/")))

		override fun toString(): String {
			return "FileSystemEntry($name)"
		}
	}
}


/**
 * This implementation operates with bundle inside another bundle (nested bundle). This is used
 * mainly in big bundles they're grouping multiple smaller bundles via 'module'.
 *
 * @author vrabel.zdenko@gmail.com
 */
class NestedBundle : Bundle {

	private val parent: Bundle
	private val root: String

	constructor(parent: Bundle, root: String) {
		this.parent = parent;
		this.root = root;
	}

	override fun entry(path: String): Entry? = when {
		path.startsWith("/") -> parent.entry("${root}${path}")?.let { this.NestedEntry(it) }
		else -> parent.entry("${root}/${path}")?.let { this.NestedEntry(it) }
	}

	override fun entries(filter: (Entry) -> Boolean): List<Entry> =
		parent.entries(this::isNestedEntry).map(this::toNestedEntry)

	private fun isNestedEntry(parentEntry: Entry): Boolean =
		parentEntry.name.startsWith(root);

	private fun toNestedEntry(parentEntry: Entry): Entry =
		this.NestedEntry(parentEntry)


	private inner class NestedEntry(private val parentEntry: Entry) : Entry {

		override val name: String
			get() = parentEntry.name.removePrefix(root)

		override fun openInputStream(): InputStream = parentEntry.openInputStream()

		override fun toString(): String {
			return "NestedEntry($name)"
		}
	}
}


/**
 * This implementation holds all entries in-memory. It's useful for unit testing purpose because
 * it can be easily created.
 *
 * ```
 * 	val bundle = InMemoryBundle(
 * 		"/nomic.box" to ByteBuffer.wrap("script".toByteArray()),
 * 		"/file.txt"  to ByteBuffer.wrap("Hello World".toByteArray()),
 * 	)
 * ```
 */
open class InMemoryBundle private constructor(private val entries: List<InMemoryEntry>) : Bundle {

	constructor(vararg entries: Pair<String, ByteBuffer>): this(entries.toMap())
	constructor(entries: Map<String, ByteBuffer>) : this( entries.map { e -> InMemoryEntry(e.key, e.value) } )

	override fun entry(path: String): Entry? =
		entries.asSequence()
			.find { e -> e.name == path }

	override fun entries(filter: (Entry) -> Boolean): List<Entry> =
		entries.asSequence()
			.filter(filter)
			.toList()

	class InMemoryEntry(override val name: String, private val data: ByteBuffer) : Entry {
		override fun openInputStream(): InputStream = ByteArrayInputStream(data.array())
	}
}


/**
 * This implementation load bundle as ZIP archive and access to entries inside ZIP folder. This implementation
 * is used when you're trying open file instead of directory.
 *
 * @author vrabel.zdenko@gmail.com
 */
class ArchiveBundle : Bundle {

	// hold the unzipped entries in memory
	private val entries: List<ArchiveEntry>;


	/**
	 * unzip the file on [path] and hold the unzipped
	 * bundle in memory
	 */
	constructor(path:  Path) : this(Files.newInputStream(path))


	/**
	 * read/unzip the input stream content and load it
	 * into memory as [ArchiveEntry]
	 */
	constructor(input: InputStream) {
		entries = unzipArchive(input)
	}


	override fun entry(path: String): Entry? =
		entries.asSequence()
			.find { e -> e.name == normalized(path) }


	override fun entries(filter: (Entry) -> Boolean): List<Entry> =
		entries.asSequence()
			.filter(filter)
			.toList()


	/**
	 * main unzipping function. It's only for internal usage
	 */
	private fun unzipArchive(input:InputStream): List<ArchiveEntry> {
		val out = mutableListOf<ArchiveEntry>()
		ZipInputStream(input).use { zipStream ->
			var zipEntry = zipStream.nextEntry
			while(zipEntry != null) {
				// read data of entry
				if (!zipEntry.isDirectory) {
					val archiveEntry = ArchiveEntry(
						name = zipEntry.name,
						data = zipStream.readData()
					)
					out.add(archiveEntry)
				}
				zipEntry = zipStream.nextEntry
			}
		}
		return out;
	}


	private fun ZipInputStream.readData():ByteBuffer {
		ByteArrayOutputStream().use { outStream ->
			val buffer = ByteArray(2048)
			// loop for reading data
			var len = this@readData.read(buffer)
			while(len  > 0) {
				outStream.write(buffer, 0, len)
				len = this@readData.read(buffer)
			}
			return ByteBuffer.wrap(outStream.toByteArray())
		}
	}


	/**
	 * this method is normalizing paths (e.g. if start with '/' character, the character is removed)
	 */
	private fun normalized(path: String): String =
		when {
			path.startsWith("/") -> path.substring(1, path.length)
			else -> path
		}


	/**
	 * internal [Entry] implementation for [ArchiveBundle]
	 */
	private class ArchiveEntry(override val name: String, private val data: ByteBuffer) : Entry {
		override fun openInputStream(): InputStream = ByteArrayInputStream(data.array())
		override fun toString(): String {
			return "ArchiveEntry(name='$name')"
		}
	}
}
