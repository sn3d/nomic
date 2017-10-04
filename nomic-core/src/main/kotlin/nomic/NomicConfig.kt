package nomic

import nomic.hdfs.HdfsAdapter
import java.net.URI

/**
 * @author zdenko.vrabel@wirecard.com
 */
open class NomicConfig(

	var hdfsHomeDir:String = "", // this can be changed by Nomic class

	val user: String = System.getProperty("user.name"),
	val hdfsAppDir: String = "/app",
	val hdfsRepositoryDir: String = "",

	val simulatorEnabled: Boolean = false,
	val simulatorPath: String = "",

	val hadoopNameNode: String = "",
	val hadoopCoreSiteXml: String = "",
	val hadoopHdfsSiteXml: String = "",

	val hiveJdbcUrl:  String = "",
	val hiveUsername: String = "",
	val hivePassword: String = "",
	val hiveSchema:   String = ""
) {

}