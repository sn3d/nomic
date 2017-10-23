/*
 * Copyright 2017 vrabel.zdenko@gmail.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package nomic.core.script

import nomic.core.exception.MissingDescriptorScriptException
import nomic.core.Bundle
import nomic.core.Script
import java.io.Reader
import java.io.StringReader
import java.nio.file.FileSystems
import java.nio.file.Files
import java.nio.file.Path


/**
 * this is simple implementation of [Script] script as text/string.
 */
class InMemoryScript(val script: String) : Script {
	override fun open(): Reader =
		StringReader(script)
}


/**
 * this implementation representing '/nomic.box' [Script] for given [Bundle]
 */
class BundleScript(val bundle: Bundle) : Script {
	override fun open(): Reader =
		bundle.entry("/nomic.box")?.openInputStream()?.reader() ?: throw MissingDescriptorScriptException(bundle)
}


/**
 * this implementation representing [Script] on classpath
 */
class ClasspathScript(val resource: String) : Script {
	override fun open(): Reader =
		this.javaClass.getResourceAsStream(resource).reader()
}

/**
 * this implementation representing [Script] on Filesystem
 */
class FileScript(val path: Path) : Script {

	constructor(path: String) : this(FileSystems.getDefault().getPath(path))

	override fun open(): Reader =
		Files.newInputStream(path).reader()
}