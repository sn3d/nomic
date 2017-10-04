package nomic.hdfs.plugin

import nomic.NomicConfig
import nomic.bundle.Entry
import nomic.hdfs.HdfsAdapter
import nomic.hdfs.HdfsSimulator
import nomic.hdfs.RealHdfs
import nomic.hdfs.copyToHdfs
import nomic.plugin.NomicPlugin
import java.io.File
import java.io.OutputStream
import java.net.URI

/**
 * @author zdenko.vrabel@wirecard.com
 */
class HdfsPlugin(val hdfsAdapter: HdfsAdapter) : NomicPlugin {

	fun copyToHdfs(entry: Entry, dest: String) = hdfsAdapter.copyToHdfs(entry, dest)
	fun delete(path: String): Boolean = hdfsAdapter.delete(path)
	fun mkdirs(path: String): Boolean = hdfsAdapter.mkdirs(path)
	fun exist(path: String): Boolean = hdfsAdapter.exist(path)
	fun create(path: String): OutputStream = hdfsAdapter.create(path)

	companion object {
		fun createPlugin(config: NomicConfig) = HdfsPlugin(createAdapter(config))

		fun createAdapter(config: NomicConfig): HdfsAdapter = when {
			config.simulatorEnabled -> HdfsSimulator(File(config.simulatorPath))
			config.hadoopNameNode.isNotBlank() -> RealHdfs.create(URI(config.hadoopNameNode))
			else -> RealHdfs.create(File(config.hadoopCoreSiteXml), File(config.hadoopHdfsSiteXml))
		}

	}

}