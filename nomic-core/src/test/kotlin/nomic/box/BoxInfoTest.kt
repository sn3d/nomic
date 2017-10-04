package nomic.box

import org.junit.Test

/**
 * @author zdenko.vrabel@wirecard.com
 */
class BoxInfoTest {

	@Test
	fun testMatchTo() {
		val boxInfo  = BoxInfo("mybox", "mygroup", "1.0.0")
		val boxInfo2 = BoxInfo("mybox", "", "1.0.0")

		// positive
		assert(boxInfo.matchTo("mybox")) { "the 'mybox' doesn't match to ${boxInfo}" }
		assert(boxInfo.matchTo("mygroup:mybox")) {"the 'mygroup:mybox' doesn't match to ${boxInfo}"}
		assert(boxInfo.matchTo("mygroup:mybox:1.0.0")) {"the 'mygroup:mybox:1.0.0' doesn't match to ${boxInfo}"}
		assert(boxInfo.matchTo("mygroup::")) {"the 'mygroup::' doesn't match to ${boxInfo}"}
		assert(boxInfo.matchTo("mygroup:*")) {"the 'mygroup:*' doesn't match to ${boxInfo}"}
		assert(boxInfo.matchTo("mygroup:*:*")) {"the 'mygroup:*:*' doesn't match to ${boxInfo}"}

		//negative
		assert(!boxInfo.matchTo("custombox")) { "the 'custombox' match to ${boxInfo} and shouldn't" }
		assert(!boxInfo.matchTo("mygroup:custombox")) {"the 'mygroup:mybox' match to ${boxInfo} and shouldn't"}
		assert(!boxInfo.matchTo("mygroup:mybox:1.0.1")) {"the 'mygroup:mybox:1.0.0' match to ${boxInfo} and shouldn't"}
		assert(!boxInfo.matchTo("customgroup:*:*")) {"the 'customgroup:*:*' match to ${boxInfo} and shouldn't"}
		assert(!boxInfo.matchTo("customgroup::")) {"the 'customgroup:*:*' match to ${boxInfo} and shouldn't"}
		assert(!boxInfo.matchTo("mygroup:*:1.0.1")) {"the 'mygroup:*:1.0.1' match to ${boxInfo} and shouldn't"}

		//empty group
		assert(boxInfo2.matchTo(":mybox:1.0.0")) {"the ':mygroup:1.0.0' doesn't match to ${boxInfo2}"}
		assert(!boxInfo.matchTo(":mybox:1.0.0")) {"the ':mygroup:1.0.0' doesn't match to ${boxInfo}"}
	}
}