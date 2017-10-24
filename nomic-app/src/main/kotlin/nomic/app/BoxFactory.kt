package nomic.app

import nomic.core.Bundle
import nomic.core.BundledBox
import nomic.compiler.Compiler
import nomic.core.script
import java.nio.file.Path

/**
 * @author vrabel.zdenko@gmail.com
 */
object BoxFactory {

	fun compile(bundle: Bundle): BundledBox =
		BundledBox(bundle, Compiler.compile(bundle.script))

	fun compileBundle(path: Path): BundledBox =
		compile(Bundle.create(path))

	fun compileBundle(path: String): BundledBox =
		compile(Bundle.create(path))


}