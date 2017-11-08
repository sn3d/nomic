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
import nomic.core.BoxRef
import nomic.core.match

/**
 * @author vrabel.zdenko@gmail.com
 */
object ListCliCommand {

	fun main(args: Array<String>) {
		val app = NomicApp.createDefault()

		// set expression filter if expression is available as args
		val filter:(BoxRef) -> Boolean
		if (args.size > 0 && args[0].isNotEmpty()) {
			filter = { ref -> args[0].match(ref) }
		} else {
			filter = { ref -> true }
		}

		// list and print installed boxes
		app.installedBoxes()
			.filter(filter)
			.forEach(this::printToConsole)
	}

	private fun printToConsole(ref: BoxRef) {
		println("${ref.group}:${ref.name}:${ref.version}")
	}

}