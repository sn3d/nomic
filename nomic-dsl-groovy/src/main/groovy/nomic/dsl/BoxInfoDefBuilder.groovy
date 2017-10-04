package nomic.dsl

import nomic.definition.BoxInfoDef
import nomic.definition.Definition

/**
 * @author zdenko.vrabel@wirecard.com
 */
class BoxInfoDefBuilder implements DefBuilder {

    String group;
    String name;
    String version;

    @Override
    Definition build() {
        return new BoxInfoDef(name, version, group)
    }
}
