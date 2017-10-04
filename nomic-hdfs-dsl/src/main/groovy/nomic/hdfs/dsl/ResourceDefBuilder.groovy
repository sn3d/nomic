package nomic.hdfs.dsl

import nomic.definition.Definition

import nomic.dsl.DefBuilder
import nomic.dsl.NomicBaseScript
import nomic.hdfs.definition.ResourceDef

/**
 * @author zdenko.vrabel@wirecard.com
 */
class ResourceDefBuilder implements DefBuilder {

    String workingDir;
    String source;
    String dest;

    ResourceDefBuilder(NomicBaseScript script, String source) {
        this.workingDir = script.workingDir;
        this.source = source;
        this.dest = "${workingDir}/${source}"
    }

    @Override
    Definition build() {
        return new ResourceDef(source, dest)
    }

    ResourceDefBuilder to(String dest) {
        if (!dest.startsWith("/")) {
            this.dest = "${workingDir}/${dest}"
        } else {
            this.dest = dest;
        }
        return this;
    }
}
