package nomic.hdfs.adapter

import nomic.core.exception.WtfException
import java.io.*
import java.nio.file.*
import java.nio.file.attribute.BasicFileAttributes

/**
 * This is simulator implementation of [HdfsAdapter] used mostly for testing.
 * The implementation simulates HDFS on any FileSystem. You can also simulate
 * HDFS in-memory via in-memory implementation of [java.nio.file.FileSystem].
 *
 * @author zdenko.vrabel@wirecard.com
 */
class HdfsSimulator(val baseDir: Path) : HdfsAdapter {

    init {
        if (!Files.exists(baseDir)) {
            Files.createDirectories(baseDir)
        }
    }

    /**
     * Create HDFS Adapter for given [fileSystem] on given [baseDir].
     */
    constructor(baseDir: String, fileSystem: FileSystem = FileSystems.getDefault()) : this(fileSystem.getPath(baseDir))

    //-------------------------------------------------------------------------------------------------
    // public API
    //-------------------------------------------------------------------------------------------------

    /**
     * create and open file for writing. If directories where
     */
    override fun create(path: String): OutputStream {
        val absolutePath = absolutize(path);

        //just open existing file
        if (Files.exists(absolutePath)) {
            return Files.newOutputStream(absolutePath);
        }

        // create parent dirs if not exist
        if (!Files.exists(absolutePath.parent)) {
            Files.createDirectories(absolutePath.parent);
        }

        // create new file
        val newCreatedFile = Files.createFile(absolutePath);
        return Files.newOutputStream(newCreatedFile);
    }


    /**
     * open existing file for reading as [InputStream].
     * If file doesn't exist, the exception is thrown
     */
    override fun open(path: String): InputStream =
        Files.newInputStream(absolutize(path))


    /**
     * this method create directory. If parent directories doesn't exist, the
     * method create them as well.
     */
    override fun mkdirs(path: String): Boolean {
        val createdDir = Files.createDirectories(absolutize(path));
        return Files.isDirectory(createdDir)
    }


    /**
     * check if path is valid and file or directory exist
     */
    override fun exist(path: String): Boolean =
        Files.exists(absolutize(path))


    /**
     * check if path point to existing directory
     */
    override fun isDirectory(path: String): Boolean =
        Files.isDirectory(absolutize(path))


    /**
     * delete file or directory if exist.
     */
    override fun delete(path: String): Boolean {
        Files.walkFileTree(absolutize(path), object : SimpleFileVisitor<Path>() {
            override fun visitFile(file:Path, attrs: BasicFileAttributes):FileVisitResult {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }

            override fun postVisitDirectory(dir:Path, exc: IOException?):FileVisitResult {
                Files.delete(dir);
                return FileVisitResult.CONTINUE;
            }
        })
        return true;
    }


    /**
     * return the user home direcoty
     */
    override val homeDirectory: String
        get() = "/user/" + System.getProperty("user.name")


    override val nameNode: String
        get() = "hdfs://localhost"

    /**
     * return sequence of all files in directory
     * @param [recursive] if scanning goes also recursive into sub-directories
     */
    override fun listFiles(path: String, recursive: Boolean ): Sequence<String> {
        val absolutePath = absolutize(path)
        val sequence = when (recursive) {
            true -> listFilesRecursive(absolutePath)
            false -> listFilesNonRecursive(absolutePath)
        }
        return sequence.map(this::relativize)
    }

    //-------------------------------------------------------------------------------------------------
    // hidden private functions
    //-------------------------------------------------------------------------------------------------
    
    private fun listFilesRecursive(dir: Path): Sequence<Path> =
        Files.newDirectoryStream(dir).asSequence()
            .flatMap { f ->
                when {
                    Files.isDirectory(f) -> listFilesRecursive(f)
                    Files.isRegularFile(f) -> sequenceOf(f)
                    else -> throw WtfException()
                }
            }


    private fun listFilesNonRecursive(dir: Path): Sequence<Path> =
        Files.newDirectoryStream(dir).asSequence()
            .filter { f -> Files.isRegularFile(f) }

    private fun absolutize(path:String): Path =
        baseDir.resolve(path.removePrefix("/"));

    private fun relativize(path:Path): String =
        "/" + baseDir.relativize(path).toString();


}