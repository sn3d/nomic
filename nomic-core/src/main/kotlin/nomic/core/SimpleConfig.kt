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
 * Simple implementation of [NomicConfig] that is mostly used from code. It's good for
 * testing.
 *
 * example:
 * ```
 * val nomic = NomicApp(
 * 		config = SimpleConfig(
 * 			"nomic.user" to "customuser",
 * 			"nomic.home" to "customhome"
 * 			...
 * 		)
 * )
 * ```
 * @author vrabel.zdenko@gmail.com
 */
class SimpleConfig(private val properties: Map<String, String>) : NomicConfig() {

	constructor(vararg props: Pair<String, String>) : this(props.toMap())

	/// get the property from [properties]
	override fun get(name: String): String? =
		properties[name]

}