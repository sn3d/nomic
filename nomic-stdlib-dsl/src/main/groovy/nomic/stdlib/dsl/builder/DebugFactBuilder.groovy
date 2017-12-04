package nomic.stdlib.dsl.builder

import nomic.core.Fact
import nomic.core.FactBuilder
import nomic.stdlib.DebugFact

/**
 * @author vrabel.zdenko@gmail.com
 */
class DebugFactBuilder implements FactBuilder {

    String message;

    @Override
    Fact build() {
        return new DebugFact(message);
    }
}
