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

/**
 * @author vrabel.zdenko@gmail.com
 */
class BoxExprTest {

	@Test
	fun testExpressions() {
		val box  = BoxRef("mygroup", "mybox", "1.0.0")

		// positive
		assert(BoxExpr.parse("mybox").matchTo(box)) { "the 'mybox' doesn't match to ${box}" }
		assert(BoxExpr.parse("mygroup:mybox").matchTo(box)) { "the 'mygroup:mybox' doesn't match to ${box}" }
		assert(BoxExpr.parse("mygroup:mybox:1.0.0").matchTo(box)) { "the 'mygroup:mybox:1.0.0' doesn't match to ${box}" }
		assert(BoxExpr.parse("mygroup::").matchTo(box)) { "the 'mygroup::' doesn't match to ${box}" }
		assert(BoxExpr.parse("mygroup:*").matchTo(box)) { "the 'mygroup:*' doesn't match to ${box}" }
		assert(BoxExpr.parse("mygroup:mybox").matchTo(box)) { "the 'mygroup:mybox' doesn't match to ${box}" }
		assert(BoxExpr.parse("mygroup:*:*").matchTo(box)) { "the 'mygroup:*:*' doesn't match to ${box}" }
	}

	@Test
	fun testNegativeExpressions() {
		val box  = BoxRef("mybox", "mygroup", "1.0.0")

		//negative
		assert(!BoxExpr.parse("custombox").matchTo(box)) { "the 'custombox' match to ${box} and shouldn't" }
		assert(!BoxExpr.parse("mygroup:custombox").matchTo(box)) { "the 'mygroup:mybox' match to ${box} and shouldn't" }
		assert(!BoxExpr.parse("mygroup:mybox:1.0.1").matchTo(box)) { "the 'mygroup:mybox:1.0.1' match to ${box} and shouldn't" }
		assert(!BoxExpr.parse("customgroup:*:*").matchTo(box)) { "the 'customgroup:*:*' match to ${box} and shouldn't" }
		assert(!BoxExpr.parse("customgroup::").matchTo(box)) { "the 'customgroup::' match to ${box} and shouldn't" }
		assert(!BoxExpr.parse("mygroup:*:1.0.1").matchTo(box)) { "the 'mygroup:*:1.0.1' match to ${box} and shouldn't" }
		assert(!BoxExpr.parse(":mybox:1.0.0").matchTo(box)) { "the ':mybox:1.0.0' match to ${box} and shouldn't" }
	}

	@Test
	fun testEmptyGroup() {
		val boxWithEmptyGroup = BoxRef("", "mybox", "1.0.0")
		assert(BoxExpr.parse(":mybox:1.0.0").matchTo(boxWithEmptyGroup)) { "the ':mygroup:1.0.0' doesn't match to ${boxWithEmptyGroup}" }
	}

}