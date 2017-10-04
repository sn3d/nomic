package nomic.box

import java.io.Reader

/**
 * @author zdenko.vrabel@wirecard.com
 */
class ReaderScript(private val reader: Reader) : DescriptorScript {
	override fun reader(): Reader = reader
}