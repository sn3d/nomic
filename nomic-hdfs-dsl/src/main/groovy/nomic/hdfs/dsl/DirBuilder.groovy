package nomic.hdfs.dsl

import nomic.core.Fact
import nomic.core.FactBuilder
import nomic.hdfs.DirFact

/**
 * @author vrabel.zdenko@gmail.com
 */
class DirBuilder implements FactBuilder {

    private String dir;
    private boolean keepIt = false;

    @Override
    Fact build() {
        return new DirFact(dir, keepIt)
    }

    public DirBuilder keepIt(boolean keepIt) {
        this.keepIt = keepIt
        return this;
    }
}
