package nomic.hdfs

import org.apache.commons.io.IOUtils
import org.junit.Assert
import org.junit.Ignore
import org.junit.Test
import java.io.File
import java.net.URI
import java.util.*

/**
 * @author zdenko.vrabel@wirecard.com
 */
@Ignore
class RealHdfsITest {

	val HADOOP_HOME = File("/Users/zdenko.vrabel/opt/hadoop")
	val testId = UUID.randomUUID()
	val TEST_FOLDER = "/tmp/nomic/${testId}"
	val TEST_FILE = "${TEST_FOLDER}/hello-world.txt"

	@Test
	fun testUploadDownloadFile() {
		var hdfs = RealHdfs.create(HADOOP_HOME)

		// create test folder
		hdfs.mkdirs(TEST_FOLDER)
		Assert.assertTrue(hdfs.isDirectory(TEST_FOLDER))

		// upload the file
		val input = this.javaClass.getResourceAsStream("/hello-world.txt")!!
		hdfs.create(TEST_FILE).use { output ->
			IOUtils.copy(input, output)
		}
		Assert.assertTrue(hdfs.exist(TEST_FILE))

		// download the file
		hdfs.open(TEST_FILE).reader().use {
			val content = it.readText()
			Assert.assertTrue(content.contains("Hello World"))
		}

		// delete the file
		hdfs.delete(TEST_FILE)
		Assert.assertFalse(hdfs.exist(TEST_FILE))


		val filesInApp = hdfs.listFiles("/tmp", true).toList()
		Assert.assertTrue(filesInApp.isNotEmpty())
	}

	@Test
	fun testDeleteDirectory() {
		var hdfs = RealHdfs.create(URI("hdfs://d-cldera-dn02.wirecard.sys:8020"));
		val homeDir = hdfs.homeDirectory;
		hdfs.delete("/user/zdenko.vrabel/dir-remove")
	}
}