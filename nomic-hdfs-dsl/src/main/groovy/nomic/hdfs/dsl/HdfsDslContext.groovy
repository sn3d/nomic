package nomic.hdfs.dsl

import nomic.dsl.NomicBaseScript

/**
 * @author zdenko.vrabel@wirecard.com
 */
class HdfsDslContext {

    private NomicBaseScript script;

    HdfsDslContext(NomicBaseScript script) {
        this.script = script;
    }

    ResourceDefBuilder resource(String source) {
        def builder =  new ResourceDefBuilder(script, source);
        script.registerDefBuilder(builder);
        return builder;
    }

    TemplateDefBuilder template(String from) {
        TemplateDefBuilder builder = new TemplateDefBuilder(script, from)
        script.registerDefBuilder(builder)
        return builder;
    }

    DirDefBuilder dir(String path) {
        DirDefBuilder builder = new DirDefBuilder(script, path)
        script.registerDefBuilder(builder)
        return builder
    }

}
