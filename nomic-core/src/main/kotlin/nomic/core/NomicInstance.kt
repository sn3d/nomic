package nomic.core

import nomic.core.*

/**
 * @see NomicApp
 * @author zdenko.vrabel@wirecard.com
 */
interface NomicInstance {

	/**
	 * Method compile bundle into [BundledBox]
	 */
	fun compile(bundle: Bundle): BundledBox

	/**
	 * compile the bundle and install it if it's not installed yet.
	 *
	 * @param force if it set to true, the bundle will be installed
	 *        even if box is already present. It's good for
	 *        fixing bad installations.
	 */
    fun install(bundle: Bundle, force: Boolean = false): BoxRef

	/**
	 * compile the bundle and upgrade it, or install if it's not
	 * present
	 */
	fun upgrade(bundle: Bundle)

	/**
	 * uninstall the box by reference if it's present
	 *
	 * @param force if it is set to true, the method will
	 *        uninstall the [InstalledBox] even any error occurred.
	 */
	fun uninstall(ref: BoxRef, force: Boolean = false)

	/**
	 * return list of boxes installed/available in system
	 */
	fun installedBoxes(): List<BoxRef>

	/**
	 * return concrete box with facts if it's present, otherwise
	 * returns null.
	 */
	fun details(info: BoxRef): InstalledBox?

}