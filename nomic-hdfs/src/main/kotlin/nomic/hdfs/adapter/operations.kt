package nomic.hdfs.adapter

import nomic.core.Entry
import org.apache.commons.io.IOUtils
import org.apache.commons.io.input.ReaderInputStream
import java.io.File
import java.io.InputStream
import java.io.Reader

/**
 * @author zdenko.vrabel@wirecard.com
 */
fun HdfsAdapter.copyToHdfs(entry: Entry, dest: String) =
    entry.openInputStream().use { input ->
        copyToHdfs(input, dest)
    }


fun HdfsAdapter.copyToHdfs(input: InputStream, dest: String) {

    // check if exist dest folder
    val destFolder = File(dest).parent
    if (!this.exist(destFolder)) {
        this.mkdirs(destFolder);
    }

    // copy the source to dest
    this.create(dest).use { output ->
        IOUtils.copy(input, output);
        output.flush()
    };
}

fun HdfsAdapter.copyToHdfs(input: Reader, dest: String) = copyToHdfs(ReaderInputStream(input), dest)