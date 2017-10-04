package nomic.dsl

import nomic.definition.Definition
import nomic.definition.ModuleDef

/**
 * @author zdenko.vrabel@wirecard.com
 */
class ModuleDefBuilder implements DefBuilder {

    String path

    @Override
    Definition build() {
        return new ModuleDef(path)
    }

}
