package nomic.dsl

import nomic.bundle.DirectoryBundle
import nomic.bundle.GroovyBundle

import nomic.definition.onlyModules
import nomic.definition.findBoxInfoDef
import nomic.definition.onlyDependencies
import nomic.hdfs.definition.ResourceDef
import org.junit.Assert
import org.junit.Test


/**
 * @author zdenko.vrabel@wirecard.com
 */
class GroovyScriptEngineTest {

	@Test
	fun testSimpleGroovyBundle() {
		val engine = GroovyEngine()
		val bundle = GroovyBundle(DirectoryBundle("./src/test/bundles/simple"))
		val definitions = engine.eval(bundle)

		//check the result
		val boxInfo = definitions.findBoxInfoDef().toBoxInfo()
		Assert.assertEquals("simple-bundle", boxInfo.id)
		Assert.assertEquals("examples", boxInfo.group)
		Assert.assertEquals("1.0.0", boxInfo.version)

		val resourceDef = definitions.find { def -> def is ResourceDef } as ResourceDef
		Assert.assertEquals("hive/some-query.q", resourceDef.source)

	}


	@Test
	fun testNestedGroovyBundle() {
		val engine = GroovyEngine()
		val bundle = GroovyBundle(DirectoryBundle("./src/test/bundles/nested"))
		val definitions = engine.eval(bundle)

		//check the result
		val dependencies = definitions.onlyDependencies().toList()
		Assert.assertTrue(dependencies.isNotEmpty())

		val module = definitions.onlyModules().find { module -> module.path == "analysis-1" }!!
		Assert.assertTrue(module.definitions.isNotEmpty())

		val nestedInfo = module.definitions.findBoxInfoDef().toBoxInfo();
		Assert.assertEquals("analysis-1", nestedInfo.id)
	}

}