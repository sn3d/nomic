package nomic.box

import java.io.Reader

/**
 * @author zdenko.vrabel@wirecard.com
 */
interface DescriptorScript {

    fun reader(): Reader
}