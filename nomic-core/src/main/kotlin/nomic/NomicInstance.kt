package nomic

import nomic.box.Box
import nomic.box.BoxInfo
import nomic.box.InstalledBox
import nomic.bundle.ArchiveBundle
import nomic.bundle.Bundle
import nomic.bundle.DirectoryBundle
import java.io.File
import java.util.*

/**
 * @author zdenko.vrabel@wirecard.com
 */
interface NomicInstance {

    val config: NomicConfig

    fun install(bundle: Bundle, forceIt:Boolean = false): Box
	fun upgrade(bundle: Bundle): Box
	fun uninstall(info: BoxInfo): Boolean
	fun installedBoxes(): List<BoxInfo>
	fun details(info: BoxInfo): Optional<InstalledBox>


}