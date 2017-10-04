package nomic.definition
import nomic.box.BoxInfo

/**
 * @author zdenko.vrabel@wirecard.com
 */
class RequireDef(val box: BoxInfo) : Definition {

	override fun toString(): String {
		return "RequireDef($box)"
	}
}