package nomic.hdfs.dsl

import nomic.core.Fact
import nomic.core.FactBuilder
import nomic.hdfs.ResourceFact

/**
 * @author vrabel.zdenko@gmail.com
 */
class ResourceBuilder implements FactBuilder {

    private String source;
    private String dest;
    private String workingDir;
    private boolean keepIt = false;


    @Override
    Fact build() {
        return new ResourceFact(source, dest, keepIt);
    }


    public ResourceBuilder to(String dest) {
        if (dest.startsWith("/")) {
            this.dest = dest;
        } else {
            this.dest = workingDir + "/" + dest
        }
        return this;
    }


    public ResourceBuilder keepIt(boolean keepIt) {
        this.keepIt = keepIt
        return this;
    }

}
