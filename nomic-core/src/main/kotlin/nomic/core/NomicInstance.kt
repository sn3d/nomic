package nomic.core


/**
 * @see NomicApp
 * @author zdenko.vrabel@wirecard.com
 */
interface NomicInstance {

	/**
	 * ref. to how instance is configured
	 */
	val config: NomicConfig

	/**
	 * Method compile bundle into [BundledBox]
	 */
	fun compile(bundle: Bundle): BundledBox

	/**
	 * Method compile bundle and all submodules in bundle and return
	 * list of all compiled boxes. This is because one bundle might
	 * contain multiple boxes.
	 */
	fun compileAll(bundle: Bundle): List<BundledBox>

	/**
	 * compile the bundle (with submodules) and install it if it's not installed yet.
	 * The submodules are installed in right order (depends on [RequireFact] facts)
	 *
	 * @param force if it set to true, the bundle will be installed
	 *        even if box is already present. It's good for
	 *        fixing bad installations.
	 *
	 * @return references to all installed boxes from bundle in order how
	 *         they was installed.
	 */
    fun install(bundle: Bundle, force: Boolean = false): List<BoxRef>

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

	//-------------------------------------------------------------------------------------------------
	// default impl functions
	//-------------------------------------------------------------------------------------------------

	/**
	 * default implementation for filesystem
	 */
	fun compile(path: String): BundledBox =
		compile(Bundle.create(path))

}