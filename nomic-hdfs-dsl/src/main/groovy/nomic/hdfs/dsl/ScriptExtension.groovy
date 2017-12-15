package nomic.hdfs.dsl

import nomic.dsl.NomicBaseScriptEx
import nomic.dsl.ScriptContext

/**
 * @author vrabel.zdenko@gmail.com
 */
class ScriptExtension {

    static void hdfs(final ScriptContext self, @DelegatesTo(HdfsContext) Closure c) {
        hdfs(self, "", c)
    }

    static void hdfs(final ScriptContext self, String workingDir, @DelegatesTo(HdfsContext) Closure c) {
        if (workingDir.isEmpty()) {
            workingDir = self.appDir + "/" + self.group + "/" + self.name;
        } else if (!workingDir.startsWith("/")) {
            workingDir = self.appDir + "/" + self.group + "/" + self.name + "/" + workingDir;
        }

        def context = new HdfsContext(script: self, workingDir: workingDir);
        c.delegate = context;
        c.call();
    }

}
