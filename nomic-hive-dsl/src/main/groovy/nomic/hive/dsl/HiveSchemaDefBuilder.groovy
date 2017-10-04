package nomic.hive.dsl

import nomic.definition.Definition
import nomic.dsl.DefBuilder
import nomic.hive.definition.HiveSchemaDefinition

/**
 * @author zdenko.vrabel@wirecard.com
 */
class HiveSchemaDefBuilder implements DefBuilder {

    private String name;

    HiveSchemaDefBuilder(String name) {
        this.name = name;
    }

    @Override
    Definition build() {
        return new HiveSchemaDefinition(name)
    }
}
