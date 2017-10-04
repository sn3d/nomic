package nomic.dsl

import nomic.box.BundleScript
import nomic.box.DescriptorScript
import nomic.bundle.Bundle
import nomic.bundle.GroovyBundle
import nomic.bundle.NestedBundle
import nomic.definition.Definition
import nomic.definition.ModuleDef
import nomic.definition.RequireDef
import nomic.definition.findBoxInfoDef
import java.io.Reader

/**
 * @author zdenko.vrabel@wirecard.com
 */
abstract class NomicScriptEngine {

	abstract fun eval(reader: Reader): List<Definition>

	fun eval(script: DescriptorScript): List<Definition> = eval(script.reader())

	fun eval(bundle: Bundle): List<Definition> =
		eval(BundleScript(bundle)).asSequence()
			.flatMap(enrichDefinitions(bundle))
			.toList()

	private fun enrichDefinitions(bundle: Bundle): (Definition) -> Sequence<Definition> = { def ->
		when (def) {
			is ModuleDef -> enrichByModule(bundle, def)
			else -> sequenceOf(def)
		}
	}

	private fun enrichByModule(bundle: Bundle, module: ModuleDef): Sequence<Definition> {
		val nestedBundle = GroovyBundle(NestedBundle(bundle, module.path))
		val nestedDefinitions = this.eval(nestedBundle)
		val info = nestedDefinitions.findBoxInfoDef().toBoxInfo()
		return sequenceOf(
			ModuleDef(module.path, nestedDefinitions),
			RequireDef(info)
		)
	}
}