package nomic.dsl

import nomic.definition.Definition

/**
 * @author zdenko.vrabel@wirecard.com
 */
interface DefBuilder {
	fun build(): Definition?
}