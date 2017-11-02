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

typealias FactMapping = List<Pair<Class<out Fact>, () -> FactHandler<out Fact>>>

/**
 * By implenting this interface in your [Plugin] you can expose various global
 * variables into Compilation of [Script].
 */
interface Exposable {
	val exposedVariables:List<Pair<String, String>>
}

/**
 * Plugin is special kind of [FactHandler] that is group of simple handlers
 * mapped to concrete [Fact] types. The apply and revert basically do only
 * routing to right [FactHandler].
 *
 * Let's take an example: We've got 2 facts [NameFact] and [GroupFact]. Each
 *
 * ```
 * class NameFactHandler : FactHandler<NameFact> {
 *    override fun commit(f: NameFact)
 *    ...
 * }
 *
 * class GroupFactHandler : FactHandler<GroupFact> {
 *    override fun commit(f: GroupFact)
 *    ...
 * }
 * ```
 *
 * We would like to create a plugin that will route facts
 * to they'r handlers. We need to extend the [Plugin] and create the
 * mapping configuration:
 *
 * ```
 * class MyPlugin : Plugin() {
 *   override fun configureMapping(): FactMapping =
 *     listOf(
 *       NameFact::class.java to ::NameFactHandler,
 *       GroupFact::class.java to ::VersionFactHandler
 *     )
 * }
 * ```
 *
 * Now you can send any fact to plugin and plugin will route the fact to
 * correct handler. If fact is not matching to any mapping, it's ignored.
 *
 * ```
 * val plugin = MyPlugin()
 * plugin.commit(NameFact("my-name"))
 * ```
 *
 * As you've noticed, the type is mapped to handler producer () -> FactHandler<>.
 * This means the handler instance is created for each fact. If you want to share some
 * state between handlers, you can pass the plugin instance into handler and you can use
 * plugin as a state holder.
 *
 * ```
 * class NameFactHandler(val plugin: MyPlugin) : FactHandler<NameFact> {
 *    override fun commit(f: NameFact) {
 *    	// here you can use this.plugin
 *    }
 *    ...
 * }
 *
 * class MyPlugin : Plugin() {
 *   override fun configureMapping(): FactMapping =
 *     listOf(
 *       NameFact::class.java to { NameFactHandler(this) }, // pass the plugin instance into handler
 *       GroupFact::class.java to ::VersionFactHandler
 *     )
 * }
 * ```
 *
 * @see [FactHandler]
 * @see [Fact]
 * @author vrabel.zdenko@gmail.com
 */
abstract class Plugin : FactHandler<Fact> {

	/**
	 * holding key-value pairs where key is concrete fact type
	 * and value is producer function that is producing handler for
	 * given fact type
	 */
	val mappings : FactMapping by lazy {
		configureMapping()
	}


	/**
	 * Here you will implement your mapping the router
	 * will use.
	 */
	abstract fun configureMapping(): FactMapping


	override fun commit(box: BundledBox, fact: Fact) {
		mappings
			.filter { m -> m.first.isInstance(fact) }
			.map { m -> m.second() }
			.filterIsInstance<FactHandler<Fact>>() // this is needed otherwise unchecked cast warning
			.forEach { h -> h.commit(box, fact) }
	}

	override fun rollback(box: InstalledBox, fact: Fact) {
		mappings
			.filter { m -> m.first.isInstance(fact) }
			.map { m -> m.second() }
			.filterIsInstance<FactHandler<Fact>>() // this is needed otherwise unchecked cast warning
			.forEach { h -> h.rollback(box, fact) }
	}
}
