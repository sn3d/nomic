package nomic.hive.dsl

import nomic.core.Fact
import nomic.core.FactBuilder
import nomic.hive.SchemaFact

/**
 * @author vrabel.zdenko@gmail.com
 */
class SchemaBuilder implements FactBuilder {

    private String name
    private boolean keepIt

    @Override
    Fact build() {
        return new SchemaFact(name, keepIt)
    }

    SchemaBuilder keepIt(boolean keepIt) {
        this.keepIt = keepIt;
        return this;
    }
}
