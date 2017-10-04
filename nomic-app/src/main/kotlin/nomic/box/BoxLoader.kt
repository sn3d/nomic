package nomic.box

import nomic.InvalidBundleException
import nomic.bundle.ArchiveBundle
import nomic.bundle.Bundle
import nomic.bundle.DirectoryBundle
import nomic.dsl.GroovyEngine
import java.io.File

/**
 * @author zdenko.vrabel@wirecard.com
 */
object BoxLoader {

	private val defaultScriptEngine by lazy {
		GroovyEngine()
	}

	fun load(bundle: Bundle): Box = BundledBox.load(bundle, defaultScriptEngine)

	fun load(file: File): Box = when {
		file.isDirectory -> load(DirectoryBundle(file))
		file.isFile -> load(ArchiveBundle(file))
		else -> throw IllegalStateException("missing ${file}")
	}
}