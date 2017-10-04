package nomic.box

import java.io.ByteArrayInputStream
import java.io.Reader
import java.nio.ByteBuffer

/**
 * @author zdenko.vrabel@wirecard.com
 */
class ByteArrayScript(private val data: ByteBuffer) : DescriptorScript {

    override fun reader(): Reader = ByteArrayInputStream(data.array()).reader();

}