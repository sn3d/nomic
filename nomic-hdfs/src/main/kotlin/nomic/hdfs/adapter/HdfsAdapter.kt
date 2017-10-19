package nomic.hdfs.adapter

import org.apache.hadoop.fs.Path
import java.io.InputStream
import java.io.OutputStream

/**
 * The whole Nomic is accessing to HDFS through this adapter. That allows me
 * to create [HdfsSimulator] for testing and we don't need real
 * HDFS system.
 *
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