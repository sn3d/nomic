package nomic.db

import nomic.box.*
import nomic.definition.findBoxInfoDef
import nomic.dsl.NomicScriptEngine
import nomic.hdfs.HdfsAdapter
import nomic.hdfs.copyToHdfs
import org.apache.commons.io.IOUtils
import java.io.File
import java.io.OutputStreamWriter
import java.nio.ByteBuffer

/**
 * @author zdenko.vrabel@wirecard.com
 */
class HdfsBoxRepository : BoxRepository {

    //-------------------------------------------------------------------------------------------------
    // properties & constructors
    //-------------------------------------------------------------------------------------------------

    private val hdfs: HdfsAdapter;
    private val engine: NomicScriptEngine
    val repoDir: String;

    constructor(repoDir: String, hdfs: HdfsAdapter, engine: NomicScriptEngine) {
        this.engine = engine
        this.hdfs = hdfs
        this.repoDir = repoDir

        // create the directory if not exist
        if (!hdfs.exist(repoDir)) {
            hdfs.mkdirs(repoDir)
		}
    }

    //-------------------------------------------------------------------------------------------------
    // implemented methods
    //-------------------------------------------------------------------------------------------------

    override fun listInstalled(): Sequence<BoxInfo> =
        hdfs.listFiles(repoDir, true)
                .filter {
                    f -> f.endsWith("nomic.info")
                }.map { f -> loadInfo(File(f)) }


    override fun save(box: BoxInfo, descriptor: DescriptorScript) {
        val boxFolder = folderFor(box)

        // cleanup old data
        if (!hdfs.exist(boxFolder)) {
            hdfs.mkdirs(boxFolder)
        }

        // save new data
        saveInfo(box, File(boxFolder, "nomic.info"))
        descriptor.reader().use {
            hdfs.copyToHdfs(it, "${boxFolder}/nomic.box")
        }
    }


    override fun open(box: BoxInfo): InstalledBox? {
        val nomicFile = "${folderFor(box)}/nomic.box";

        if (!hdfs.exist(nomicFile)) {
            return null;
        }

        val descriptorBytes =
            hdfs.open("${folderFor(box)}/nomic.box").use {
				IOUtils.toByteArray(it)
            }

        val descriptor = ByteArrayScript(ByteBuffer.wrap(descriptorBytes))
        val definitions = engine.eval(descriptor)

        return InstalledBox(
			definitions = definitions,
			info = definitions.findBoxInfoDef().toBoxInfo(),
			//dependencies = emptyList(),
			descriptor = descriptor
		)
    }


    override fun remove(box: BoxInfo) {
        val boxFolder = folderFor(box)
        if (hdfs.exist(boxFolder)) {
            hdfs.delete(boxFolder)
        }
    }


    override fun insertDependency(from: BoxInfo, to: BoxInfo) {
        val fromFolder = folderFor(from);
        if (!hdfs.exist(fromFolder)) {
            hdfs.mkdirs(fromFolder);
        }

        val depFile = to.id.replace('/', '-') + ".dep"
        hdfs.create("$fromFolder/${depFile}").writer().use {
            to.writeTo(it)
        };
    }


    override fun dependenciesFor(box: BoxInfo): List<BoxInfo> =
        hdfs.listFiles(folderFor(box), true)
                .map(::File)
                .filter { it.extension == "dep" }
                .map(this::loadInfo)
                .toList()


    override fun usedBy(box: BoxInfo): List<BoxInfo> =
        hdfs.listFiles(repoDir, true)
                .map(::File)
                .filter { it.extension == "dep" }
                .filter { f ->
                    val info = loadInfo(f);
                    info.group == box.group && info.id == box.id && info.version == box.version
                }
                .map { f ->
                    val usedFile = File(f.parentFile, "nomic.info")
                    loadInfo(usedFile)
                }
                .toList()

    //-------------------------------------------------------------------------------------------------
    // private methods
    //-------------------------------------------------------------------------------------------------

    private fun initIfNotExist() {
		val repoDirFile = File(repoDir)
		if (!repoDirFile.exists()) {
            repoDirFile.mkdirs()
		}
	}


    private fun folderFor(box: BoxInfo): String {
        val id = box.id.replace('/', '-')

        if (box.group.isBlank()) {
            return "${repoDir}/_empty/${id}"
        } else {
            val group = box.group.replace('/', '-')
            return "${repoDir}/${group}/${id}"
        }
    }


    private fun saveInfo(box: BoxInfo, file: File) {
        hdfs.create(file.path).writer().use {
            box.writeTo(it)
        }
    }


    private fun loadInfo(file: File): BoxInfo =
        hdfs.open(file.path).reader().use {
            val lines = it.readLines()
            val group = lines.getParameter("group")
            val name = lines.getParameter("name")
            val version = lines.getParameter("version")
            return BoxInfo(name, group, version)
        }


    private fun List<String>.getParameter(param: String): String =
            this.asSequence().filter({ l -> l.startsWith("${param}: ") }).map({ l -> l.removePrefix("${param}: ") }).first();


    private fun BoxInfo.writeTo(writer: OutputStreamWriter) {
		writer.write("name: ${this.id}\n")
        writer.write("group: ${this.group}\n")
        writer.write("version: ${this.version}\n")
    }
}