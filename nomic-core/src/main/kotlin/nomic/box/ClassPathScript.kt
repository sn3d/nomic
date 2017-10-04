package nomic.box

import java.io.Reader

/**
 * @author zdenko.vrabel@wirecard.com
 */
class ClassPathScript(private val path: String) : DescriptorScript {

    override fun reader(): Reader = this.javaClass.getResourceAsStream(path).reader()

}