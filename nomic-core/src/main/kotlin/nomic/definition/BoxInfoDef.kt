package nomic.definition

import nomic.box.BoxInfo

/**
 * @author zdenko.vrabel@wirecard.com
 */
class BoxInfoDef : Definition {

	val id: String
	val version: String
	val group: String

	constructor(id: String, version: String, group: String) {
		this.id = id;
		this.version = version;
		this.group = group;
	}

	fun toBoxInfo():BoxInfo =
		BoxInfo(
			id = this.id,
			version = this.version,
			group = this.group );

	override fun toString(): String {
		return "BoxInfoDef(id='$id', version='$version', group='$group')"
	}


}