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
package nomic.core

import nomic.MissingDescriptorScriptException
import java.io.Reader
import java.io.StringReader

/**
 * Representing descriptor script 'nomic.box' that can be compiled
 * by [Script] to [Fact]s.
 *
 * @see Compiler
 * @author vrabel.zdenko@gmail.com
 */
interface Script {

	/**
	 * this method is called when [Compiler] need to compile the descriptor script.
	 */
	fun open(): Reader
}


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