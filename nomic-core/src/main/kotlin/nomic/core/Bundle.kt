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

import nomic.WtfException
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.nio.ByteBuffer
import java.nio.file.*


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
		fun open(path: String): Bundle =
			open(FileSystems.getDefault().getPath(path))

		fun open(path: Path): Bundle =
			FileSystemBundle(path)
	}
}

//-------------------------------------------------------------------------------------------------
// extensions
//-------------------------------------------------------------------------------------------------

val Bundle.script: Script
	get() = BundleScript(this)

fun String.asBundle(): Bundle =
	Bundle.open(this)

fun String.asBundleFrom(fs: FileSystem): Bundle =
	Bundle.open(fs.getPath(this))

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
class InMemoryBundle private constructor(private val entries: List<InMemoryEntry>) : Bundle {

	constructor(vararg entries: Pair<String, ByteBuffer>): this (entries.map { e -> InMemoryEntry(e.first, e.second) }.toList())

	override fun entry(path: String): Entry? =
		entries.asSequence()
			.find { e -> e.name == path }

	override fun entries(filter: (Entry) -> Boolean): List<Entry> =
		entries.asSequence()
			.filter(filter)
			.toList()

	class InMemoryEntry(override val name: String, private val data: ByteBuffer) : Entry {
		override fun openInputStream(): InputStream =ByteArrayInputStream(data.array())
	}


}