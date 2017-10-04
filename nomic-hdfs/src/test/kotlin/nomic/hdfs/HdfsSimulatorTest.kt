package nomic.hdfs

import org.junit.Assert
import org.junit.Test
import java.io.File

/**
 * @author zdenko.vrabel@wirecard.com
 */
class HdfsSimulatorTest {

	@Test
	fun testListOfEmptyHdfs() {
		val hdfs = HdfsSimulator(File("./target/hdfs/simulator-empty"))
		val listedFiles = hdfs.listFiles("/",true).toList()
		Assert.assertTrue(listedFiles.isEmpty())
	}
}