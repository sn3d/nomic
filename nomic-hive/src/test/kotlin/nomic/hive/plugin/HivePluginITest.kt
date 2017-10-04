package nomic.hive.plugin

import org.junit.Ignore
import org.junit.Test

/**
 * @author zdenko.vrabel@wirecard.com
 */
@Ignore
class HivePluginITest {

	@Test
	fun testConnection() {
		val plugin = HivePlugin("jdbc:hive2://d-cldera-app02.wirecard.sys:10000", "zdenko.vrabel", "", "vrabel")

		val fields = mapOf(
			"DATABASE_SCHEMA" to "vrabel",
			"APP_ROOT_DIR" to "/user/zdenko.vrabel/data"
		)

		this.javaClass.getResourceAsStream("/query.q").reader().use {
			plugin.exec(it, fields);
		}

	}
}