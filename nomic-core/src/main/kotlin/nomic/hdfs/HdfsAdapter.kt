package nomic.hdfs

import java.io.InputStream
import java.io.OutputStream

/**
 * @author zdenko.vrabel@wirecard.com
 */
interface HdfsAdapter {

    fun create(path: String): OutputStream
    fun open(path: String): InputStream
    fun mkdirs(path: String): Boolean
    fun delete(path: String): Boolean
    fun exist(path: String): Boolean
    fun isDirectory(path: String): Boolean
    fun listFiles(path: String, recursive: Boolean): Sequence<String>

    val homeDirectory: String;

}