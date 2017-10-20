package nomic.db

import nomic.core.exception.NotCompiledBoxException
import nomic.compiler.Compiler
import nomic.core.*

/**
 * @author vrabel.zdenko@gmail.com
 */
class NotCompiledBox(
	override val name: String,
	override val group: String,
	override val version: String,
	override val script: Script,
	override val dependencies: List<BoxRef>) : InstalledBox {

	override val facts: List<Fact>
		get() = throw NotCompiledBoxException()

	fun compileWith(compiler: Compiler): InstalledBox {
		val f = compiler.compile(script)
		return CompiledBox(f)
	}

	private inner class CompiledBox(override val facts: List<Fact>) : InstalledBox {

		override val name: String
			get() = this@NotCompiledBox.name

		override val group: String
			get() = this@NotCompiledBox.group

		override val version: String
			get() = this@NotCompiledBox.version

		override val script: Script
			get() = this@NotCompiledBox.script

		override val dependencies: List<BoxRef>
			get() = this@NotCompiledBox.dependencies
	}
}