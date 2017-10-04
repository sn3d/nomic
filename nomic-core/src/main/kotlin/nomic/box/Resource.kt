package nomic.box

import java.io.InputStream
import java.io.Reader

/**
 * @author zdenko.vrabel@wirecard.com
 */
interface Resource {

    fun inputStream():InputStream
    fun reader():Reader

}