package nomic.hdfs.dsl

import nomic.dsl.NomicBaseScriptEx

/**
 * @author vrabel.zdenko@gmail.com
 */
class HdfsContext {

    private def NomicBaseScriptEx script;
    private def String workingDir;


    public ResourceBuilder resource(String source) {
        if (!source.startsWith("/")) {
            source = "/" + source;
        }

        def builder = new ResourceBuilder(workingDir: workingDir, source: source, dest: workingDir + source);
        script.registerBuilder(builder);
        return builder;
    }


    public DirBuilder dir(String dir) {
        def finalDir = dir;
        if (!dir.startsWith("/")) {
            finalDir = workingDir + "/" + dir;
        }

        def builder = new DirBuilder(dir: finalDir);
        script.registerBuilder(builder);
        return builder;
    }

}
