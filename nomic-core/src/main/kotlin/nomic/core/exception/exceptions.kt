package nomic.core.exception

import nomic.core.Box
import nomic.core.BoxRef
import nomic.core.Bundle

/**
 * @author zdenko.vrabel@wirecard.com
 */
class InvalidPathInBundleException(message: String) : RuntimeException(message)
class InvalidBundleException(message: String) : RuntimeException(message)
class NotInstalledBoxException(box: Box): RuntimeException("The ${box.group}:${box.name} must be installed first!")
class BoxAlreadyInstalledException(box: Box): RuntimeException("The ${box.group}:${box.name} is already installed")
class BoxNotInstalledException(box: BoxRef): RuntimeException("The ${box.group}:${box.name} is not installed")
class BoxNotFoundException(boxId: String): RuntimeException("The ${boxId} not found")

class MissingNameInScriptException() : RuntimeException("The 'name' is required and it's missing in your script!")
class MissingDescriptorScriptException(val bundle: Bundle) : RuntimeException("The script 'nomic.box' is missing in bundle ${bundle}")
class NotCompiledBoxException() : RuntimeException("The box is not compiled, you have to compile it first, then you can ask for facts")
class RequiredConfigPropertyException(name: String) : RuntimeException("The '${name}' is required config property that is not set.")

class WtfException : RuntimeException {
	constructor() : super("What a failure!")
	constructor(e:Throwable): super("What a failure!", e)
}