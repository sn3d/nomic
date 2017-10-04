package nomic.bundle

import java.io.InputStream

/**
 * @author zdenko.vrabel@wirecard.com
 */
interface Entry {

    /** name/path of the entry in bundle */
    val name: String

    /** open the entry as Input Strean */
    fun openInputStream(): InputStream
    

}