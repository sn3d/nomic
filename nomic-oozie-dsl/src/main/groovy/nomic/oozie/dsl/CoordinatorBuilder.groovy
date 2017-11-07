package nomic.oozie.dsl

import nomic.core.Fact
import nomic.core.FactBuilder
import nomic.oozie.CoordinatorFact

/**
 * @author vrabel.zdenko@gmail.com
 */
class CoordinatorBuilder implements FactBuilder {

    //val coordinatorXml: String, val hdfsDest: String, val parameters: Map<String, String>, val keepIt: Boolean

    private String name;
    private String xmlSource;
    private String hdfsDest;
    private String workingDir;
    private Map<String, String> parameters = new HashMap<String, String>();
    private Boolean keepIt;

    @Override
    Fact build() {
        return new CoordinatorFact(name, xmlSource, hdfsDest, parameters, keepIt);
    }

    CoordinatorBuilder keepIt(boolean keepIt) {
        this.keepIt = keepIt;
        return this;
    }

    CoordinatorBuilder parameters(Map<String, String> parameters) {
        for (param in parameters) {
            this.parameters.put(param.key.toString(), param.value.toString())
        }
        //this.parameters.putAll(parameters);
        return this;
    }

    public CoordinatorBuilder from(String source) {
        this.xmlSource = source;
        return this;
    }

    public CoordinatorBuilder to(String dest) {
        if (dest.startsWith("/")) {
            this.hdfsDest = dest;
        } else {
            this.hdfsDest = workingDir + "/" + dest
        }
        return this;
    }


}
