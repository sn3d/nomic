package nomic.box

import nomic.bundle.ArchiveBundle
import nomic.bundle.Bundle
import nomic.bundle.DirectoryBundle
import nomic.definition.Definition
import nomic.definition.findBoxInfoDef
import nomic.dsl.NomicScriptEngine
import java.io.File

/**
 * @author zdenko.vrabel@wirecard.com
 */
interface Box {

	val info: BoxInfo
	val descriptor: DescriptorScript
	val definitions: List<Definition>

	companion object {

		fun load(file: File, scriptEngine: NomicScriptEngine): BundledBox =
			when {
				file.isDirectory -> load(DirectoryBundle(file), scriptEngine)
				file.isFile -> load(ArchiveBundle(file), scriptEngine)
				else -> throw IllegalStateException("missing ${file}")
			}

		fun load(bundle: Bundle, scriptEngine: NomicScriptEngine): BundledBox {
			val definitions = scriptEngine.eval(bundle);
			val info = definitions.findBoxInfoDef().toBoxInfo()
			return BundledBox(bundle, info, definitions)
		}
	}
}