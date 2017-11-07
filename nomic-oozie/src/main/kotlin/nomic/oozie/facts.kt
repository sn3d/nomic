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

import nomic.core.Fact

/**
 * This fact is declaring where will be uploaded the [coordinatorXml] in HDFS
 * and how will be executed via OOZIE with given [parameters]
 *
 * @author vrabel.zdenko@gmail.com
 */
data class CoordinatorFact(val name: String, val xmlSource: String, val hdfsDest: String, val parameters: Map<String, String>, val keepIt: Boolean) : Fact