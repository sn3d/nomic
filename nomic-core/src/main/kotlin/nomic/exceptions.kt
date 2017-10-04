package nomic

import nomic.box.Box
import nomic.box.BoxInfo
import nomic.dsl.DefBuilder

/**
 * @author zdenko.vrabel@wirecard.com
 */
class InvalidPathInBundleException(message: String) : RuntimeException(message)
class InvalidBundleException(message: String) : RuntimeException(message)
class NotInstalledBoxException(box: Box): RuntimeException("The ${box.info.group}:${box.info.id} must be installed first!")
class BoxAlreadyInstalledException(box: Box): RuntimeException("The ${box.info.group}:${box.info.id} is already installed")
class BoxNotInstalledException(box: BoxInfo): RuntimeException("The ${box.group}:${box.id} is not installed")
class BoxNotFoundException(boxId: String): RuntimeException("The ${boxId} not found")
class DefinitionCannotBeCreatedException(builder: DefBuilder) : RuntimeException("The definition cannot be created from ${builder}. Check the nomic.box or DefBuilder")

class WtfException : RuntimeException {
	constructor() : super("What a failure!")
	constructor(e:Throwable): super("What a failure!", e)
}