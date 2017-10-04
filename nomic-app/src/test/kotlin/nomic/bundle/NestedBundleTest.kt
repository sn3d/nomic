package nomic.bundle

import org.junit.Assert
import org.junit.Test

/**
 * @author zdenko.vrabel@wirecard.com
 */
class NestedBundleTest {

    @Test
    fun testSimpleNestedEntries() {
        // open bundles
        val mainBundle = DirectoryBundle("./src/test/bundles/nested")
        val nestedBundle = NestedBundle(mainBundle, "analysis-2")

        // test 'entries' method
        val allEntries = mainBundle.entries()
        val nestedEntries = nestedBundle.entries()

        Assert.assertEquals(2, nestedEntries.size)
        Assert.assertTrue(nestedEntries.size < allEntries.size)

    }


    @Test
    fun testSimpleNestedEntry() {
        // open bundles
        val mainBundle = DirectoryBundle("./src/test/bundles/nested")
        val nestedBundle = NestedBundle(mainBundle, "analysis-2")

        // test 'entry' method
        val nomicDescriptor = nestedBundle.descriptor()
        nomicDescriptor.openInputStream().reader().use {
            val content = it.readText()
            Assert.assertTrue(content.contains("analysis-2"))
        }
    }

}