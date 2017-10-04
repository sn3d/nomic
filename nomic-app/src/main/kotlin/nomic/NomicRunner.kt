package nomic

import java.io.File
import nomic.box.BoxInfo
import java.io.FileNotFoundException
import nomic.bundle.Bundle


/**
 * @author zdenko.vrabel@wirecard.com
 */
object NomicRunner {

    @JvmStatic fun main(args: Array<String>) {
        if (args.size == 0) {
            printCommandsHelp()
            System.exit(1)
        }

        when(args[0]) {
            "install" -> NomicRunner.install( args.copyOfRange(1, args.size))
            "list" -> NomicRunner.list( args.copyOfRange(1, args.size))
            "remove" -> NomicRunner.remove( args.copyOfRange(1, args.size) )
            "config" -> NomicRunner.config( args.copyOfRange(1, args.size) )
            else -> printCommandsHelp()
        }
    }


    fun install(args: Array<String>) {
        //parse file from args
        if (args.size == 0) {
            printInstallHelp()
            System.exit(1);
        }

        val file = File(args[0])
        if (!file.exists()) {
            throw FileNotFoundException(file.absolutePath)
        }

        // init nomic
        val nomic = Nomic(TypesafeNomicConfig())

        // install box
        nomic.install(Bundle.open(args[0]));
    }


    fun list(args: Array<String>) {
        val nomic = Nomic(TypesafeNomicConfig())
        nomic.installedBoxes().forEach(this::printInstalled)
    }

    fun config(args: Array<String>) {
        val nomic = Nomic(TypesafeNomicConfig())
        println("nomic.user                 ${nomic.config.user}")
        println("nomic.hdfs.app.dir:        ${nomic.config.hdfsAppDir}")
		println("nomic.hdfs.repository.dir: ${nomic.config.hdfsRepositoryDir}")
        println("nomic.hdfs.home.dir:       ${nomic.config.hdfsHomeDir}")
		println("")
		println("simulator.enabled: ${nomic.config.simulatorEnabled}")
        println("simulator.path:    ${nomic.config.simulatorPath}")
        println("")
		println("hadoop.core.site: ${nomic.config.hadoopCoreSiteXml}")
		println("hadoop.hdfs.site: ${nomic.config.hadoopHdfsSiteXml}")
		println("")
    }

    fun remove(args: Array<String>) {
        if (args.size == 0) {
            printRemoveHelp()
            System.exit(1)
        }

        val nomic = Nomic(TypesafeNomicConfig())
        nomic.uninstall(BoxInfo.parse(args[0]))
    }


    fun printInstalled(info: BoxInfo) {
        println("${info.group}:${info.id}:${info.version}")
    }


    fun printCommandsHelp() {
        println("Available commands are:")
        println("   install")
		println("   remove")
        println("   list")
		println("   config")
    }


    fun printInstallHelp() {
        println("nomic install [path]")
        println("   path - path to bundle (*.npk or directory)")
    }

    fun printRemoveHelp() {
        println("nomic remove [boxId]")
    }
}