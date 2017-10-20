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
 * All facts are extending this interface. The facts are immutable
 * data classes they can be consumed by [Plugin]-s and they're created
 * from DSL [Script] via [Compiler]
 *
 * @author vrabel.zdenko@gmail.com
 */
interface Fact {
}

fun <T> List<Fact>.findFactType(type: Class<T>): T =
	this.asSequence()
		.filter { it -> type.isInstance(it) }
		.map { type.cast(it) }
		.first()


fun <T> List<Fact>.findFactsType(type: Class<T>): List<T> =
	this.asSequence()
		.filter { it -> type.isInstance(it) }
		.map { type.cast(it) }
		.toList()
