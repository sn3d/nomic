package nomic.definition

/**
 * @author zdenko.vrabel@wirecard.com
 */
interface Installable {

    fun apply(context: InstallContext)

}