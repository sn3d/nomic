package nomic.db

import nomic.box.*
import nomic.dsl.GroovyEngine
import nomic.hdfs.HdfsSimulator
import nomic.hdfs.copyToHdfs
import org.junit.Assert
import org.junit.Test
import java.io.File

/**
 * @author zdenko.vrabel@wirecard.com
 */
class BoxRepositoryTest {

    val hdfs = HdfsSimulator(File("./target/hdfs/box-repository-test"))

    @Test
    fun updateBoxAndOpenIt() {
        // init repository and box that will be updated
        val box = BoxLoader.load(File("./src/test/bundles/simple"))
        val repository = HdfsBoxRepository("/user/test/.nomic/repository", hdfs, GroovyEngine())

        // save box
        repository.save(box.info, box.descriptor);

        // check
        Assert.assertTrue( hdfs.exist("/user/test/.nomic/repository/${box.info.group}/${box.info.id}/nomic.box") )
        Assert.assertTrue( hdfs.exist("/user/test/.nomic/repository/${box.info.group}/${box.info.id}/nomic.info") )
    }

    @Test
    fun insertAndGetNestedDependecnies() {
        // init repository for 'test' user
        val repository = HdfsBoxRepository("/user/test/.nomic/repository", hdfs, GroovyEngine())

        // prepare dependencies & boxes
        val parent =  BoxInfo(group="test-dependency", id="parent",   version="1.0.0")
        val parentB = BoxInfo(group="test-dependency", id="parent-b", version="1.0.0")
        val childA  = BoxInfo(group="test-dependency", id="child-a",  version="1.0.0")
        val childB  = BoxInfo(group="test-dependency", id="child-b",  version="1.0.0")

        hdfs.copyToHdfs(this.javaClass.getResourceAsStream("/parent.info"), "/user/test/.nomic/repository/test-dependency/parent/nomic.info")
        hdfs.copyToHdfs(this.javaClass.getResourceAsStream("/parent-b.info"), "/user/test/.nomic/repository/test-dependency/parent-b/nomic.info")

        // insert dependencies & check
        repository.insertDependency(parent, childA)
        repository.insertDependency(parent, childB)
        repository.insertDependency(parentB, childB)

        Assert.assertTrue( hdfs.exist("/user/test/.nomic/repository/test-dependency/parent/child-a.dep") )
        Assert.assertTrue( hdfs.exist("/user/test/.nomic/repository/test-dependency/parent/child-b.dep") )

        // get dependencies & check
        val dependencies = repository.dependenciesFor(parent)
        Assert.assertEquals(2, dependencies.size)

        // get used by & check
        var usedBy = repository.usedBy(childB);
        Assert.assertEquals(2, usedBy.size)

        usedBy = repository.usedBy(childA);
        Assert.assertEquals(1, usedBy.size)

    }

    @Test
    fun getInstalledBoxAndOpenIt() {
        // simulate the installed box
        val repository = HdfsBoxRepository("/user/test/.nomic/repository", hdfs, GroovyEngine())
        repository.save(BoxInfo("installed-box", "test", "1.0"), ClassPathScript("/installed-box.groovy"))

        // find installed package
        val b = repository.listInstalled()
                .filter { b -> b.id == "installed-box" }
                .first();

        // check
        Assert.assertNotNull(b)
        Assert.assertEquals("test", b.group)
        Assert.assertEquals("1.0", b.version)

        // open the Box
        val box = repository.open(b);
        Assert.assertTrue(box is InstalledBox)
    }
}