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

import nomic.core.fact.GroupFact
import nomic.core.fact.NameFact
import nomic.core.fact.VersionFact
import nomic.core.script.BundleScript
import java.nio.ByteBuffer

/**
 * This is [Box] that is loaded from [Bundle] and can be installed to your system.
 * @author vrabel.zdenko@gmail.com
 */
open class BundledBox : Box, Bundle {
	
	//-------------------------------------------------------------------------------------------------
	// public API
	//-------------------------------------------------------------------------------------------------

	protected val bundle: Bundle

	override val script: Script
		get() = BundleScript(bundle)


	constructor(bundle: Bundle, facts: List<Fact> = emptyList()) {
		this.bundle = bundle;
		this.facts = facts;
	}

	constructor(id: BoxRef, bundle: Bundle) :
		this(id.group, id.name, id.version, bundle)

	constructor(id: BoxRef, bundle: Bundle, facts: List<Fact>) :
		this(id.group, id.name, id.version, bundle, facts)

	constructor(group: String, name: String, version: String, bundle: Bundle, facts: List<Fact> = emptyList()) :
		this(bundle, listOf(NameFact(name), GroupFact(group), VersionFact(version)) + facts)

	companion object {
		val empty = BundledBox(InMemoryBundle(), listOf())
	}

	//-------------------------------------------------------------------------------------------------
	// implemented methods & properties
	//-------------------------------------------------------------------------------------------------

	override val facts: List<Fact>

	override val name: String
		get() = facts.findFactType(NameFact::class.java).name

	override val group: String
		get() = facts.findFactType(GroupFact::class.java).group

	override val version: String
		get() = facts.findFactType(VersionFact::class.java).version

	override fun entry(path: String): Entry? = bundle.entry(path)

	override fun entries(filter: (Entry) -> Boolean): List<Entry> = bundle.entries(filter)

	override fun toString(): String {
		return "BundledBox(${ref()})"
	}
}

class ApplicationBox(bundle: Bundle, facts: List<Fact>) : BundledBox(bundle, facts) {
	fun toModuleBox() = ModuleBox(this.bundle, this.facts)
}

class ModuleBox(bundle: Bundle, facts: List<Fact>) : BundledBox(bundle, facts)