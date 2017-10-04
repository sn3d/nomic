package nomic.hdfs.dsl

import nomic.definition.Definition
import nomic.dsl.NomicBaseScript
import nomic.dsl.NomicScriptEngine
import nomic.hdfs.definition.TemplateDef
import nomic.dsl.DefBuilder

/**
 * @author zdenko.vrabel@wirecard.com
 */
class TemplateDefBuilder implements DefBuilder {

    String workingDir;
    String source;
    String dest = "";
    Map<String, ?> bindings = new HashMap<>();

    public TemplateDefBuilder(NomicBaseScript script, String source) {
        this.workingDir = script.workingDir;
        this.source = source;
        this.dest = "${workingDir}/${source}"

        if (dest.endsWith(".mustache")) {
            this.dest = dest.substring(0, dest.length() - ".mustache".length())
        }
    }


    @Override
    Definition build() {
        return new TemplateDef(source, dest, bindings)
    }

    TemplateDefBuilder to(String dest) {
        if (!dest.startsWith("/")) {
            this.dest = "${workingDir}/${dest}"
        } else {
            this.dest = dest;
        }
        return this;
    }

    TemplateDefBuilder with(Map bindings) {
        this.bindings = bindings;
        return this;
    }
}
