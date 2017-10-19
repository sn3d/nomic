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
package nomic.app.cli

import nomic.app.NomicApp
import nomic.app.TypesafeConfig

/**
 * @author vrabel.zdenko@gmail.com
 */
object ConfigCliCommand {

	fun main(args: Array<String>) {
		val app = NomicApp(TypesafeConfig.loadDefaultConfiguration())

		println("nomic.user:                " + app.config["nomic.user"])
		println("nomic.hdfs.home:           " + app.config["nomic.hdfs.home"])
		println("nomic.hdfs.app.dir:        " + app.config["nomic.hdfs.app.dir"])
		println("nomic.hdfs.repository.dir: " + app.config["nomic.hdfs.repository.dir"])
		println("nomic.default.schema:      " + app.config["nomic.default.schema"])
		println("")
		println("hdfs.adapter:           " + app.config["hdfs.adapter"])
		println("hdfs.simulator.basedir: " + app.config["hdfs.simulator.basedir"])
		println("hdfs.core.site:         " + app.config["hdfs.core.site"])
		println("hdfs.hdfs.site:         " + app.config["hdfs.hdfs.site"])
		println("")
		println("hive.jdbc.url:  " + app.config["hive.jdbc.url"])
		println("hive.user:      " + app.config["hive.user"])
		println("hive.password:  " + app.config["hive.password"])
	}

}