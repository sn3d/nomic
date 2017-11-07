package nomic.core

import nomic.core.exception.RequiredConfigPropertyException

/**
 * @author zdenko.vrabel@wirecard.com
 */
abstract class NomicConfig {

	abstract operator fun get(name: String): String?

	open val user: String
		get() = this["nomic.user"] ?: throw RequiredConfigPropertyException("nomic.user")

	open val hdfsHomeDir: String
		get() = this["nomic.hdfs.home"] ?: throw RequiredConfigPropertyException("nomic.hdfs.home")

	open val hdfsAppDir: String
		get() = this["nomic.hdfs.app.dir"] ?: throw RequiredConfigPropertyException("nomic.hdfs.app.dir")

	open val hdfsRepositoryDir: String
		get() = this["nomic.hdfs.repository.dir"] ?: throw RequiredConfigPropertyException("nomic.hdfs.repository.dir")

}