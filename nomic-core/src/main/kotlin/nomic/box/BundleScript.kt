package nomic.box

import nomic.bundle.Bundle
import java.io.Reader

/**
 * @author zdenko.vrabel@wirecard.com
 */
class BundleScript(val bundle: Bundle) : DescriptorScript {
	override fun reader(): Reader = bundle.descriptorAsInputStream().reader()
}