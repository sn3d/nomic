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
package nomic.app.cli

import nomic.app.NomicApp
import nomic.app.TypesafeConfig
import nomic.core.BoxRef
import kotlin.system.exitProcess

/**
 * @author vrabel.zdenko@gmail.com
 */
object RemoveCliCommand {

	fun main(args: Array<String>) {
		if (args.size < 1) {
			printHelp()
			exitProcess(1)
		}

		val ref = BoxRef.parse(args[0])
		val app = NomicApp(TypesafeConfig.loadDefaultConfiguration())
		app.uninstall(ref)
	}

	fun printHelp() {
		println("nomic remove [box ref.]")
	}

}