package nomic.definition

import nomic.NomicInstance
import nomic.bundle.NestedBundle
import nomic.db.BoxRepository

/**
 * @author zdenko.vrabel@wirecard.com
 */
class ModuleDef(val path: String, val definitions: List<Definition> = emptyList()) : Definition {

    constructor(path: String) : this(path, emptyList())

    fun installModule(context: InstallContext, nomic: NomicInstance, repository: BoxRepository) {
        val parentBox = context.box
		val nestedBundle = NestedBundle(parentBox, path)
		val installedBox = nomic.install(nestedBundle, forceIt = true)

		repository.insertDependency(
			parentBox.info,
			installedBox.info
		)
	}

}