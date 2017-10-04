package nomic.hdfs

import java.io.*

/**
 * @author zdenko.vrabel@wirecard.com
 */
class HdfsSimulator(val baseDir: File) : HdfsAdapter {

    init {
        if (!baseDir.exists()) {
            baseDir.mkdirs()
        }
    }

    override fun create(path: String): OutputStream {
        val fileToCreate = File(baseDir, path)
        if (!fileToCreate.parentFile.isDirectory) {
            fileToCreate.parentFile.mkdirs()
		}

        if (fileToCreate.exists()) {
            fileToCreate.delete()
        }

        if (!fileToCreate.createNewFile()) {
            throw IllegalStateException("cannot create file ${fileToCreate}")
        }

        return FileOutputStream(fileToCreate)
    }

    override fun mkdirs(path: String): Boolean {
        val realPath = File(baseDir, path)
        return realPath.mkdirs()
    }

    override fun exist(path: String): Boolean = File(baseDir, path).exists()
    override fun isDirectory(path: String): Boolean = File(baseDir, path).isDirectory

    override val homeDirectory: String
        get() = "/user/" + System.getProperty("user.name")

    override fun delete(path: String): Boolean = File(baseDir, path).deleteRecursively()

    override fun listFiles(path: String, recursive: Boolean): Sequence<String> =
        when (recursive) {
            true -> listFilesRecursive(File(baseDir, path)).map(this::normalize)
            false -> listFilesNonRecursive(File(baseDir, path)).map(this::normalize)
        }

    override fun open(path: String): InputStream = FileInputStream(File(baseDir, path))

    private fun listFilesRecursive(dir: File): Sequence<File> {
        val files = dir.listFiles() ?: emptyArray()
		return files.asSequence()
			.flatMap({ f -> if (f.isDirectory) listFilesRecursive(f) else sequenceOf(f) })
	}


    private fun listFilesNonRecursive(dir: File): Sequence<File> =
        dir.listFiles().asSequence()

    private fun normalize(f: File): String = f.path.removePrefix(baseDir.path)


}