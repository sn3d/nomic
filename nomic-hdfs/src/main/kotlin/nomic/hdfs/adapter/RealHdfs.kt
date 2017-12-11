package nomic.hdfs.adapter

import nomic.core.exception.WtfException
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.FileSystem
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.io.OutputStream
import java.net.URI

import org.apache.hadoop.fs.Path as HadoopPath

/**
 * @author zdenko.vrabel@wirecard.com
 */
class RealHdfs private constructor(conf: Configuration) : HdfsAdapter {

    //-------------------------------------------------------------------------------------------------
    // members & construction
    //-------------------------------------------------------------------------------------------------

    private val hdfs: FileSystem = FileSystem.get(conf)

	companion object {

		/**
		 * create HDFS adapter based on uri (e.g. hdfs://localhost:9000)
		 */
		fun create(nameNodeUri: URI): HdfsAdapter {
			val conf = Configuration()
			conf.set("fs.default.name", nameNodeUri.toASCIIString())
			return RealHdfs(conf)
		}

		/**
		 * create HDFS adapter based on etc/hadoop/core-site.xml and etc/hadoop/hdfs-site.xml
		 * placed in HADOOP_HOME folder.
		 */
		fun create(hadoopHome: File): HdfsAdapter =
			create(File(hadoopHome, "etc/hadoop/core-site.xml"), File(hadoopHome, "etc/hadoop/hdfs-site.xml"))

		/**
		 * create HDFS adapter based on core-site.xml and hdfs-site.xml
		 */
		fun create(coreSiteXml: File, hdfsSiteXml: File): HdfsAdapter {
			val conf = Configuration()
			conf.addResource(FileInputStream(coreSiteXml))
			conf.addResource(FileInputStream(hdfsSiteXml));
			conf.reloadConfiguration()
			return RealHdfs(conf)
		}

	}

    //-------------------------------------------------------------------------------------------------
    // implemented methods
    //-------------------------------------------------------------------------------------------------

    override fun create(path: String): OutputStream {
		val hdfsPath = HadoopPath(path)
		if (!hdfs.exists(hdfsPath.parent)) {
			hdfs.mkdirs(hdfsPath.parent)
		}
		return hdfs.create(HadoopPath(path)) ?: throw WtfException()
	}

    override fun mkdirs(path: String): Boolean = hdfs.mkdirs(HadoopPath(path))
    override fun delete(path: String): Boolean = hdfs.delete(HadoopPath(path), true)
    override fun isDirectory(path: String): Boolean = hdfs.isDirectory(HadoopPath(path))
    override fun exist(path: String): Boolean = hdfs.exists(HadoopPath(path))
    override fun open(path: String): InputStream = hdfs.open(HadoopPath(path))

    override fun listFiles(path: String, recursive: Boolean): Sequence<String> {
        val iterator = hdfs.listFiles(HadoopPath(path), recursive);
        return generateSequence {
            if (iterator.hasNext()) {
                iterator.next()
            } else {
                null;
            }
        }.map { f -> f.path.toUri().path }
    }


    override val homeDirectory: String
        get() = hdfs.homeDirectory.toUri().path

	override val nameNode: String
		get() = hdfs.conf.get("fs.defaultFS")
}