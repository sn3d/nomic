package nomic.dsl.builder

import nomic.core.Fact
import nomic.core.FactBuilder
import nomic.core.fact.ModuleFact

/**
 * @author vrabel.zdenko@gmail.com
 */
class ModuleFactBuilder implements FactBuilder {

    String name;

    @Override
    Fact build() {
        return new ModuleFact(name)
    }
}
