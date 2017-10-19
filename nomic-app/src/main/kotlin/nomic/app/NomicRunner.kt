package nomic.app

import nomic.app.cli.ConfigCliCommand
import nomic.app.cli.InstallCliCommand
import nomic.app.cli.ListCliCommand
import nomic.app.cli.RemoveCliCommand
import kotlin.system.exitProcess

/**
 * @author zdenko.vrabel@wirecard.com
 */
object NomicRunner {

    @JvmStatic fun main(args: Array<String>) {
        if (args.size == 0) {
			printCommandsHelp()
            System.exit(1)
        }

        val commandArgs = args.copyOfRange(1, args.size)
        when(args[0]) {
            "install" -> InstallCliCommand.main(commandArgs)
            "list"    -> ListCliCommand.main(commandArgs)
            "remove"  -> RemoveCliCommand.main(commandArgs)
            "config"  -> ConfigCliCommand.main(commandArgs)
            else -> printCommandsHelp()
        }
    }

    fun printCommandsHelp() {
        println("Available commands are:")
        println("   install")
		println("   remove")
        println("   list")
		println("   config")
        exitProcess(1)
    }
}