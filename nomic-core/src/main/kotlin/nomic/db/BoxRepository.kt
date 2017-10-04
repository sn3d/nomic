package nomic.db

import nomic.box.Box
import nomic.box.BoxInfo
import nomic.box.DescriptorScript
import nomic.box.InstalledBox

/**
 * @author zdenko.vrabel@wirecard.com
 */
interface BoxRepository {
    fun listInstalled(): Sequence<BoxInfo>
    fun save(box: BoxInfo, descriptor: DescriptorScript)
    fun remove(box: BoxInfo)
    fun open(box: BoxInfo): InstalledBox?
    fun insertDependency(from: BoxInfo, to: BoxInfo)
    fun dependenciesFor(box: BoxInfo): List<BoxInfo>
    fun usedBy(box: BoxInfo): List<BoxInfo>
}