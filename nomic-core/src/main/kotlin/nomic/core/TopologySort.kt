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
 * Representing Node with vertices in Graph. This this kind of nodes you can
 * do Topology sorting e.g. Kahn's algorithm etc.
 *
 * @author vrabel.zdenko@gmail.com
 */
data class Node<T>(val value: T, val verticles:MutableList<T> = mutableListOf<T>()) {
	fun hasNoIncommingEdge():Boolean = verticles.isEmpty()
}


/**
 * Kahn's algorithm of topology sort.
 *
 * !!NOTE!! This is real place that can be improved because this was implemented
 * in time pressure and it's not-optimized. (be precise it's functional but it's poorly optimalized).
 */
fun <T> List<Node<T>>.topologySort(): List<T> {
	val result = mutableListOf<T>()
	var noedge = this.filter(Node<T>::hasNoIncommingEdge).toMutableList();

	while(noedge.isNotEmpty()) {
		val n = noedge[0]
		noedge = noedge.subList(1, noedge.size)
		result.add(n.value)

		for (x in this) {
			if(x.verticles.size > 0) {
				x.verticles.removeIf { v -> v == n.value }
				if (x.verticles.size == 0) {
					noedge.add(x)
				}
			}
		}
	}
	return result
}
