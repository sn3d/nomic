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