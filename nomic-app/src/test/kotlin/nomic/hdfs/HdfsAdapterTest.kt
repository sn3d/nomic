package nomic.hdfs

import org.apache.commons.io.IOUtils
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.io.File

/**
 * @author zdenko.vrabel@wirecard.com
 */
class HdfsAdapterTest {

    lateinit var hdfs: HdfsAdapter

    @Before
    fun setup() {
        hdfs = HdfsSimulator(File("./target/hdfs/simulator"));
    }

    @Test
    fun createFile() {
        hdfs.mkdirs("/app/test/hive")
        val input = this.javaClass.getResourceAsStream("/test-file")
        val output = hdfs.create("/app/test/hive/query.q");

        IOUtils.copy(input, output)
    }

    @Test
    fun listFilesRecursive() {

        //create test folder structure
        hdfs.mkdirs("/test/struct")
        hdfs.copyToHdfs(this.javaClass.getResourceAsStream("/test-file"), "/test/struct/file1")
        hdfs.copyToHdfs(this.javaClass.getResourceAsStream("/test-file"), "/test/struct/file2")

        hdfs.mkdirs("/test/struct/sub1")
        hdfs.copyToHdfs(this.javaClass.getResourceAsStream("/test-file"), "/test/struct/sub1/file3")
        hdfs.copyToHdfs(this.javaClass.getResourceAsStream("/test-file"), "/test/struct/sub1/file4")

        hdfs.mkdirs("/test/struct/sub2")
        hdfs.copyToHdfs(this.javaClass.getResourceAsStream("/test-file"), "/test/struct/sub2/file5")
        hdfs.copyToHdfs(this.javaClass.getResourceAsStream("/test-file"), "/test/struct/sub2/file6")

        hdfs.mkdirs("/test/struct/sub2/sub")
        hdfs.copyToHdfs(this.javaClass.getResourceAsStream("/test-file"), "/test/struct/sub2/sub/file7")

        //list the folder recursive
        var files = hdfs.listFiles("/test/struct", true).toList()

        //check
        Assert.assertEquals(7, files.size)
        val file7 = files.find({ f -> f == "/test/struct/sub2/sub/file7" })!!
        Assert.assertTrue(hdfs.exist(file7))
    }

}