package nomic.box

import nomic.bundle.Bundle
import nomic.bundle.Entry
import nomic.definition.Definition
import nomic.definition.findBoxInfoDef
import nomic.definition.onlyDependencies
import nomic.dsl.NomicScriptEngine
import java.io.InputStream
import java.io.Reader

/**
 * @author zdenko.vrabel@wirecard.com
 */
class BundledBox(
	private val bundle: Bundle,
	override val info: BoxInfo,
	override val definitions: List<Definition> = emptyList(),
	val dependencies: List<BoxInfo> = emptyList()
) : Box, Bundle {

	override fun entry(path: String): Entry = bundle.entry(path)
	override fun entries(filter: (Entry) -> Boolean): List<Entry> = bundle.entries(filter)

	override val descriptor: DescriptorScript by lazy {
		BundleScript(bundle)
	}

	inner class BundleResource(private val path: String) : Resource {
		override fun inputStream(): InputStream = bundle.entry(path).openInputStream()
		override fun reader(): Reader = inputStream().reader()
	}

	companion object {
		fun load(bundle: Bundle, scriptEngine: NomicScriptEngine): Box {
			val defs = scriptEngine.eval(bundle)
			return BundledBox(
				bundle = bundle,
				definitions = defs,
				info = defs.findBoxInfoDef().toBoxInfo(),
				dependencies = defs.onlyDependencies().map { required -> required.box }.toList()
			)
		}
	}

}