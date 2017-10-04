package nomic.box

import nomic.bundle.DirectoryBundle
import org.junit.Assert
import org.junit.Test

/**
 * @author zdenko.vrabel@wirecard.com
 */
class BoxTest {

	@Test
	fun loadNestedBundle() {
		var box = BoxLoader.load(DirectoryBundle("./src/test/bundles/nested"))
		Assert.assertEquals("nested-bundle", box.info.id)
	}

	@Test
	fun loadBundleWithHiveExt() {
		var box = BoxLoader.load(DirectoryBundle("./src/test/bundles/simple-hive"))
		Assert.assertEquals("simple-hive", box.info.id)
	}

}