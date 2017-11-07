package nomic.dsl.builder

import nomic.core.BoxRef
import nomic.core.Fact
import nomic.core.FactBuilder
import nomic.core.fact.RequireFact

/**
 * @author vrabel.zdenko@gmail.com
 */
class RequireFactBuilder implements FactBuilder {

    private BoxRef requiredBox;

    RequireFactBuilder(String ref) {
        requiredBox = BoxRef.parse(ref)
    }

    RequireFactBuilder(Map args) {
        if (args['version'] == null) {
            args['version'] = ""
        }
        requiredBox = new BoxRef(args['group'] as String, args['name'] as String, args['version'] as String)
    }

    @Override
    Fact build() {
        return new RequireFact(requiredBox)
    }
}
