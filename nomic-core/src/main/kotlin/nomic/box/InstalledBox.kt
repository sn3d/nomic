package nomic.box

import nomic.definition.Definition


/**
 * @author zdenko.vrabel@wirecard.com
 */
class InstalledBox(
	override val definitions: List<Definition>,
	override val info: BoxInfo,
	override val descriptor: DescriptorScript ) : Box {
}