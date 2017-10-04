package nomic.hive.dsl

import nomic.definition.Definition
import nomic.dsl.DefBuilder
import nomic.hive.definition.HiveScriptDefinition

/**
 * @author zdenko.vrabel@wirecard.com
 */
class HiveScriptDefBuilder implements DefBuilder {

    String scriptPath = "";
    Map<String, ?> fields = new HashMap<>();

    @Override
    public Definition build() {
        return new HiveScriptDefinition(scriptPath, fields)
    }

    HiveScriptDefBuilder with(Map fields) {
        this.fields = fields;
        return this;
    }

}
