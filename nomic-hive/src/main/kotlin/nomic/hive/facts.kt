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
package nomic.hive

import nomic.core.Fact


/**
 * This fact representing 'table' in DSL and it's processed by [TableFactHandler]
 * @author vrabel.zdenko@gmail.com
 */
data class TableFact(val schema: String, val table: String, val script: String, val fields: Map<String, Any> = emptyMap(), val keepIt: Boolean = false) : Fact

/**
 * this fact representing 'schema' in Hive DSL and it's processed by [SchemaFactHandler]
 * @author vrabel.zdenko@gmail.com
 */
data class SchemaFact(val schema: String, val keepIt: Boolean = false) : Fact