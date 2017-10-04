package nomic.hdfs.dsl

import nomic.definition.Definition
import nomic.dsl.NomicBaseScript
import nomic.hdfs.definition.DirDef
import nomic.dsl.DefBuilder

/**
 * @author zdenko.vrabel@wirecard.com
 */
class DirDefBuilder implements DefBuilder {

    private String workingDir;
    private String path;
    private boolean keepIt = false;

    DirDefBuilder(NomicBaseScript script, String path) {
        if (!path.startsWith("/")) {
            this.path = "${script.workingDir}/${path}"
        } else {
            this.path = path;
        }
    }

    @Override
    Definition build() {
        return new DirDef(path, keepIt)
    }

    public DirDefBuilder keepIt() {
        return setKeepIt(true)
    }

    public DirDefBuilder keepIt(Boolean keepIt) {
        this.keepIt = keepIt;
        return this;
    }

    public void setKeepIt(Boolean keepIt) {
        this.keepIt = keepIt;
    }


}
