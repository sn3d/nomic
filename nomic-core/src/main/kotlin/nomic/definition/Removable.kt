package nomic.definition

/**
 * @author zdenko.vrabel@wirecard.com
 */
interface Removable {

    fun revert(context: UninstallContext)

}