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

import nomic.core.exception.WtfException
import nomic.core.fact.RequireFact

/**
 * Main entity in Nomic application. The box is identified by unique [name] but also
 * by [group] and [version]. There are 2 main box groups:
 *
 * - [BundledBox] is box available as archive file or directory with content that
 *                can be installed to Nomic
 * - [InstalledBox] is box that is currently installed in system and can be removed
 *
 * @author vrabel.zdenko@gmail.com
 */
interface Box {
	abstract val name:String
	abstract val group: String
	abstract val version: String
	abstract val facts:List<Fact>
	abstract val script: Script
}


//-------------------------------------------------------------------------------------------------
// Box extensions
//-------------------------------------------------------------------------------------------------

/**
 * create reference to concrete box
 */
fun Box.ref() = BoxRef.createReferenceTo(this)


/**
 * this method returns you list of all boxes they have set direct
 * dependency to given [box]
 */
fun List<Box>.findDependantsFor(box:Box):List<Box> {
	return this.asSequence()
		.filter { b ->
			box.isRequiredBy(b)
		}
		.toList()
}


/**
 * find [Box] for reference
 */
fun List<Box>.find(ref:BoxRef):Box? =
	this.find { box -> box.ref() == ref }


/**
 * this utility function convert list of [Box]es to list of [BoxRef] references
 */
fun List<Box>.toRefs(): List<BoxRef>
	= this.map(Box::ref)

/**
 * This extended function for list of [Box]es add the topology sortingf based on
 * dependencies.
 *
 * The output is sorted list (order) of boxes how they can be installed safely.
 */
fun List<Box>.topologySort():List<Box> {
	val graph = this.map { box -> box.toGraphNode(this) }
	val sortedRefs = graph.topologySort()
	return sortedRefs.map { ref ->
		this.find(ref) ?: throw WtfException("There must be some box for reference! Probably broken code.")
	}
}


//-------------------------------------------------------------------------------------------------
// private functions
//-------------------------------------------------------------------------------------------------


/**
 * convert [Box] to [Node] with vertices they're dependencies on this [Box].
 *
 * If module-a and module-b they both require module-c, the [Node] for module-c
 * will have 2 vertices to module-a and module-b.
 */
private fun Box.toGraphNode(boxes: List<Box>):Node<BoxRef> {
	return Node<BoxRef>(
		value = this.ref(),
		verticles = this.vertices()
	)
	/*
	val dependants = boxes.findDependantsFor(this).map(Box::ref) // this line returns converted [BoxRef]-s as dependants
	return Node<BoxRef>(
		value = this.ref(),
		verticles = dependants.toMutableList()
	)*/
}

private fun Box.vertices():MutableList<BoxRef> =
	this.facts.filterIsInstance(RequireFact::class.java).map { requireFact -> requireFact.box  }.toMutableList()

/**
 * check if this [Box] is required <- by child
 */
private fun Box.isRequiredBy(child: Box):Boolean {
	val allRequires = child.facts.findFactsType(RequireFact::class.java)
	val matched = allRequires.filter { req ->
		req.box == this.ref()
	}.isNotEmpty()

	return matched;
}

