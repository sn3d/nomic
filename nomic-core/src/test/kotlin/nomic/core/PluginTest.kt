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

import org.junit.Test


private class NameFactHandler(val plugin: TestPlugin) : FactHandler<NameFact> {
	override fun commit(box: BundledBox, fact: NameFact) {
		println("Message: ${plugin.welcomeMsg} ${fact.name}")
	}

	override fun rollback(box: InstalledBox, fact: NameFact) {
		println("Bye ${fact.name}")
	}

}

private class VersionFactHandler: FactHandler<VersionFact> {
	override fun commit(box: BundledBox, fact: VersionFact) {
		println("Version ${fact.version}")
	}

	override fun rollback(box: InstalledBox, fact: VersionFact) {
		println("Bye ${fact.version}")
	}
}

private class TestPlugin : Plugin() {

	val welcomeMsg: String = "Hello"

	override fun configureMapping(): FactMapping =
		listOf(
			NameFact::class.java to { NameFactHandler(this) },
			VersionFact::class.java to ::VersionFactHandler
		)
}

/**
 * @author vrabel.zdenko@gmail.com
 */
class PluginTest {

	@Test
	fun testFacts() {
		val plugin = TestPlugin()
		plugin.commit(BundledBox.empty, NameFact("Harry Potter"))
		plugin.commit(BundledBox.empty, VersionFact("1.0"))
	}
}