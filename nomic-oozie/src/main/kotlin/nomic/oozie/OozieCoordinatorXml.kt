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
package nomic.oozie

import nomic.core.exception.WtfException
import org.w3c.dom.Document
import java.io.InputStream
import javax.xml.parsers.DocumentBuilderFactory

/**
 * This class read the Ozzie coordinator XML and provide access to some
 * values in this XML
 *
 * @author vrabel.zdenko@gmail.com
 */
class OozieCoordinatorXml {

	private val doc: Document

	/**
	 * constrcuct Oozie Coordinator class from XML [Document]
	 */
	constructor(doc: Document) {
		this.doc = doc
	}

	/**
	 * parse [input] stream XML to Oozie Coordinator object
	 */
	constructor(input: InputStream) {
		val factory = DocumentBuilderFactory.newInstance()
		val builder = factory.newDocumentBuilder()
		this.doc = builder.parse(input)
	}

	/**
	 * name from <coodrinator-app> root element
	 */
	val appName:String
		get() = doc.documentElement.getAttribute("name") ?: throw WtfException()


}