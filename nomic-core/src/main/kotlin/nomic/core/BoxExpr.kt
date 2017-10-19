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
 * @author vrabel.zdenko@gmail.com
 */
class BoxExpr(
	val group: String = "*",
	val name: String = "",
	val version: String = "*"
) {

	fun matchTo(metadata: BoxRef): Boolean {
		val idMatch = (this.name == "*") || (this.name == metadata.name)
		val groupMatch = (this.group == "*") || (this.group == metadata.group)
		val versionMatch = (this.version == "*") || (this.version == metadata.version)
		return idMatch && groupMatch && versionMatch
	}

	companion object {
		@JvmStatic
		fun parse(expr: String) = expr.parseToBoxInfo()
	}

}


fun String.match(box: Box): Boolean = this.match(BoxRef.createReferenceTo(box))
fun String.match(ref: BoxRef): Boolean = this.parseToBoxInfo().matchTo(ref)
fun List<BoxRef>.findBox(expr: BoxExpr) = this.find { b -> expr.matchTo(b) }
fun List<BoxRef>.findBox(expr: String) = this.find { b -> expr.match(b) }

//-------------------------------------------------------------------------------------------------
// private methods
//-------------------------------------------------------------------------------------------------

private fun String.parseToBoxInfo(): BoxExpr {
	if (!this.contains(":")) {
		return BoxExpr(name = this, group = "*", version = "*")
	} else {
		val values = this.split(":", ignoreCase = true, limit = 3)

		val group: String
		if (values.size == 3 && values[0].isBlank()) {
			group = ""
		} else {
			group = values.getValue(0)
		}

		val box = BoxExpr(
			group = group,
			name = values.getValue(1),
			version = values.getValue(2)
		)
		return box;
	}
}

// if it's null, not existing (out of range), or empty use the '*'
private fun  List<String>.getValue(i: Int): String = when {
	this.getOrElse(i, {""}).isBlank() -> "*"
	else -> this[i]
}
