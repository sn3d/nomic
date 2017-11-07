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

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

/**
 * @author vrabel.zdenko@gmail.com
 */
class TopologySortTest {

	@Test
	fun `test of simple topology sort`() {
		val graph = listOf<Node<String>>(
			Node("A"),
			Node("B", mutableListOf("A")),
			Node("C", mutableListOf("A")),
			Node("E", mutableListOf("D")),
			Node("D", mutableListOf("B", "C"))
		)

		val sortedGraph = graph.topologySort()
		val sortedGraphStr = sortedGraph.toString()

		assertThat(sortedGraphStr)
			.isEqualTo("[A, B, C, D, E]")
	}


	/**
	 * test of graph:  https://en.wikipedia.org/wiki/File:Directed_acyclic_graph_2.svg
	 */
	@Test
	fun `test of topology sort over advanced graph`() {
		val graph = listOf<Node<String>>(
			Node("5"),
			Node("7"),
			Node("3"),

			Node("11", mutableListOf("5", "7")),
			Node("8",  mutableListOf("7", "3")),

			Node("2",  mutableListOf("11")),
			Node("9",  mutableListOf("11", "8")),
			Node("10", mutableListOf("11", "3"))
		)

		val sortedGraph = graph.topologySort()
		val sortedGraphStr = sortedGraph.toString()

		assertThat(sortedGraphStr)
			.isEqualTo("[5, 7, 3, 11, 8, 2, 10, 9]")

	}


	/**
	 * test of more complicated graph with multiple starts (https://www.youtube.com/watch?v=tFpvX8T0-Pw)
	 */
	@Test
	fun `test of topology sort over advanced graph with two starts`() {
		val graph = listOf<Node<String>>(
			Node("A", mutableListOf("B")),
			Node("B", mutableListOf("E")),
			Node("C", mutableListOf("B")),
			Node("D", mutableListOf("A", "F")),
			Node("E", mutableListOf()),
			Node("F", mutableListOf("E")),
			Node("G", mutableListOf("C", "D", "J")),
			Node("H", mutableListOf("F", "I", "K")),
			Node("I", mutableListOf()),
			Node("J", mutableListOf("H")),
			Node("K", mutableListOf("I"))
		)

		val sortedGraph = graph.topologySort()
		val sortedGraphStr = sortedGraph.toString()

		assertThat(sortedGraphStr)
			.isEqualTo("[E, I, B, F, K, A, C, H, D, J, G]")

	}


}
