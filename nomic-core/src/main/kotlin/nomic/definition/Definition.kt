package nomic.definition

/**
 * @author zdenko.vrabel@wirecard.com
 */
interface Definition {

}

fun List<Definition>.findBoxInfoDef(): BoxInfoDef =
	this.asSequence()
		.filter { def -> def is BoxInfoDef }
		.first() as BoxInfoDef

fun List<Definition>.onlyModules(): Sequence<ModuleDef> =
	this.asSequence()
		.filter { def -> def is ModuleDef }
		.map { def -> def as ModuleDef }

fun List<Definition>.onlyDependencies(): Sequence<RequireDef> =
	this.asSequence()
		.filter { def -> def is RequireDef }
		.map { def -> def as RequireDef }