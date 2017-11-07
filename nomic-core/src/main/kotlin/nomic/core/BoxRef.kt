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

/**
 * The [Box] can be references/identified by name, group and version.
 *
 * @author vrabel.zdenko@gmail.com
 */
data class BoxRef(
	val group: String = "",
	val name: String = "",
	val version: String = ""
) {

	companion object {
		@JvmStatic
		fun createReferenceTo(box: Box) = BoxRef(box.group, box.name, box.version)

		@JvmStatic
		fun parse(str: String):BoxRef {
			val values = str.split(":", ignoreCase = true, limit = 3)
			return BoxRef(group = values[0], name = values[1], version = values[2])
		}
	}

	override fun toString(): String {
		return "$group:$name:$version"
	}

}

