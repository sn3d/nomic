import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.FileSystem
import org.apache.hadoop.fs.Path
import org.junit.Ignore
import org.junit.Test
import java.io.FileInputStream

/**
 * @author zdenko.vrabel@wirecard.com
 */
@Ignore
class HdfsExperiment {



    @Test
    fun listOfFiles() {
        //create configuration & init FS
        val conf = Configuration()
        conf.set("fs.default.name", "hdfs://d-cldera-dn02.wirecard.sys/")
        val fs = FileSystem.get(conf);

        // get the /app content
        val files = fs.listFiles(Path("/app"), true)
        while (files.hasNext()) {
            val file = files.next();
            System.out.println("File: ${file.path}");
        }
    }


	@Test
	fun testSiteXml() {
		val hadoopHome = "/Users/zdenko.vrabel/opt/hadoop"
		val coreSiteXml = "${hadoopHome}/etc/hadoop/core-site.xml"
		val hdfsSiteXml = "${hadoopHome}/etc/hadoop/hdfs-site.xml"

		//create configuration & init FS
		val conf = Configuration()
        conf.addResource(FileInputStream(coreSiteXml))
		conf.addResource(FileInputStream(hdfsSiteXml))
		val fs = FileSystem.get(conf);

		// get the /app content
		val files = fs.listFiles(Path("/user"), true)
		while (files.hasNext()) {
			val file = files.next();
			System.out.println("File: ${file.path}");
		}
	}



	@Test
    fun createFolder() {
        //create configuration & init FS
        val conf = Configuration()
        conf.set("fs.default.name", "hdfs://d-cldera-dn02.wirecard.sys/")
        val fs = FileSystem.get(conf);

        fs.mkdirs(Path("/user/zdenko.vrabel/test"))
    }

}
