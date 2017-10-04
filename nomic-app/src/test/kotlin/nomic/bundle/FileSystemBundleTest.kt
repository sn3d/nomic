package nomic.bundle

import org.junit.Assert
import org.junit.Test

/**
 * @author zdenko.vrabel@wirecard.com
 */
class FileSystemBundleTest {


    @Test
    fun basicAccessToEntry() {
        val bundle = DirectoryBundle("./src/test/bundles/simple");

        // try to get the entry
        val entry = bundle.descriptor();
        Assert.assertEquals("nomic.box", entry.name)

        // read entry
        val entryContent = entry.openInputStream().reader().use {
            val content = it.readText();
            Assert.assertNotNull("entry content must be present", content)
        };
    }


    @Test
    fun getAllEntries() {
        //get all entries
        val bundle = DirectoryBundle("./src/test/bundles/simple");
        val entries = bundle.entries();
        Assert.assertTrue(entries.isNotEmpty())
    }


    @Test
    fun getEntriesByFilter() {
        //get only kotlin scripts
        val bundle = DirectoryBundle("./src/test/bundles/simple");
        val entries = bundle.entries(this::allGroovyEntries);
        Assert.assertEquals(1, entries.size)
    }

    fun allGroovyEntries(e: Entry):Boolean = e.name.endsWith(".box")

}