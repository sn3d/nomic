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

import nomic.core.script.InMemoryScript

/**
 * @author vrabel.zdenko@gmail.com
 */
interface InstalledBox : Box {

	val dependencies: List<BoxRef>

	companion object {

		/**
		 * this instance is used when the box is not available in Nomic (not found). It's kind of
		 * null pattern.
		 */
		val notAvailable by lazy {
			NotAvailableBox() as InstalledBox
		}
	}
}


/**
 * representing NULL installed box used by [InstalledBox.notAvailable]
 */
private class NotAvailableBox : InstalledBox {

	override val dependencies: List<BoxRef>
		get() = emptyList()

	override val script: Script
		get() = InMemoryScript("NaN")

	override val name: String
		get() = "NaN"

	override val group: String
		get() = "NaN"

	override val version: String
		get() = "NaN"

	override val facts: List<Fact>
		get() = emptyList()

}
