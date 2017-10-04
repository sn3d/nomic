package nomic.box


/**
 * @author zdenko.vrabel@wirecard.com
 */
class BoxInfo(
	val id: String,
	val group: String = "",
	val version: String = "") {

	override fun toString(): String {
		return "$group:$id:$version"
	}

	fun sameAs(info: BoxInfo): Boolean =
		(this.group == info.group) && (this.id == info.id)

	fun exactlySameAs(info: BoxInfo): Boolean =
		this.group == info.group &&
		this.id == info.id &&
		this.version == info.version

	fun sameIdAs(info: BoxInfo): Boolean =
		(this.id == id)

	fun matchTo(expr: String):Boolean {
		val info = expr.parseToBoxInfo()
		return matchTo(info)
	}

	fun matchTo(info: BoxInfo): Boolean {
		val idMatch = (info.id == "*") || (info.id == this.id)
		val groupMatch = (info.group == "*") || (info.group == this.group)
		val versionMatch = (info.version == "*") || (info.version == this.version)
		return idMatch && groupMatch && versionMatch
	}

	companion object {

		@JvmStatic
		fun parse(expr: String) = expr.parseToBoxInfo()
	}
}

fun String.match(info: BoxInfo): Boolean = info.matchTo(this)

private fun String.parseToBoxInfo(): BoxInfo {
	if (!this.contains(":")) {
		return BoxInfo(id = this, group = "*", version = "*")
	} else {
		val values = this.split(":", ignoreCase = true, limit = 3)

		val group: String
		if (values.size == 3 && values[0].isBlank()) {
			group = ""
		} else {
			group = values.getValue(0)
		}

		val box = BoxInfo(
			group = group,
			id = values.getValue(1),
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
