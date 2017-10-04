package nomic.hdfs.definition

import nomic.box.BoxInfo
import nomic.definition.*
import nomic.hdfs.plugin.HdfsPlugin

/**
 * @author zdenko.vrabel@wirecard.com
 */
class ResourceDef(val source: String, val dest: String) : Definition, Installable, Removable {


    override fun apply(context: InstallContext) {
		// get source and absolute destination
		val entry = context.box.entry(source);

		//copy the file
		val hdfs = context.instanceOf(HdfsPlugin::class.java)
        hdfs.copyToHdfs(entry, dest)
    }


    override fun revert(context: UninstallContext) {
		// delete the file in HDFS
		val hdfs = context.instanceOf(HdfsPlugin::class.java)
        hdfs.delete(dest);
    }

	override fun toString(): String {
		return "ResourceDef('$source' -> '$dest')"
	}
}