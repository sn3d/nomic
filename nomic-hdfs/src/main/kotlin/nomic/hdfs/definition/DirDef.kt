package nomic.hdfs.definition

import nomic.definition.*
import nomic.hdfs.plugin.HdfsPlugin

/**
 * @author zdenko.vrabel@wirecard.com
 */
class DirDef: Definition, Installable, Removable {

	private val path: String
	private val keepIt: Boolean

	constructor(path: String) : this(path, false) {}

	constructor(path: String, keepIt: Boolean) {
		this.path = path
		this.keepIt = keepIt
	}

	override fun apply(context: InstallContext) {
		val hdfs = context.instanceOf(HdfsPlugin::class.java)
		hdfs.mkdirs(path);
	}

	override fun revert(context: UninstallContext) {
		if (!keepIt) {
			val hdfs = context.instanceOf(HdfsPlugin::class.java)
			hdfs.delete(path)
		}
	}

}