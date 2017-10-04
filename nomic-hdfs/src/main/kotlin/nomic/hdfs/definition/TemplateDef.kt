package nomic.hdfs.definition

import com.github.mustachejava.DefaultMustacheFactory
import com.github.mustachejava.Mustache
import nomic.bundle.Entry
import nomic.definition.*
import nomic.hdfs.plugin.HdfsPlugin
import java.io.File
import java.io.OutputStream


/**
 * @author zdenko.vrabel@wirecard.com
 */
class TemplateDef : Definition, Installable, Removable {

    private val templateSrc: String
    private val dest: String
    private val bindings: Map<String, Any>

    constructor(src: String, dest: String, bindings: MutableMap<String, Any>) {
        this.templateSrc = src
        this.dest = dest
        this.bindings = bindings.toMap()
    }


    //-------------------------------------------------------------------------------------------------
    // implemented methods
    //-------------------------------------------------------------------------------------------------

    override fun apply(context: InstallContext) {
        val hdfs = context.instanceOf(HdfsPlugin::class.java)
        val destFolder = File(dest).parent
        if (destFolder != null &&  !hdfs.exist(destFolder)) {
            hdfs.mkdirs(destFolder)
        }

        val templateEntry = context.box.entry(templateSrc)
        val template = compileEntryAsTemplate(templateEntry)
        renderAndWrite(template, hdfs.create(dest));
    }

    private fun compileEntryAsTemplate(entry: Entry): Mustache {
        entry.openInputStream().reader().use {
            val mf = DefaultMustacheFactory()
            val template = mf.compile(it, "")
            return template;
        }
    }

    private fun renderAndWrite(template: Mustache, out: OutputStream) {
        out.writer().use {
            template.execute(it, bindings)
        }
    }

    override fun revert(context: UninstallContext) {
		val hdfs = context.instanceOf(HdfsPlugin::class.java)
        hdfs.delete(dest);
    }

	override fun toString(): String {
		return "TemplateDef('$templateSrc' -> '$dest')"
	}


}