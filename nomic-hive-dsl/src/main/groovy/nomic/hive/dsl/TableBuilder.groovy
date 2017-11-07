package nomic.hive.dsl

import nomic.core.Fact
import nomic.core.FactBuilder
import nomic.hive.TableFact

/**
 * @author vrabel.zdenko@gmail.com
 */
class TableBuilder implements FactBuilder {

    private String schema;
    private String table;
    private String script;
    private Map<String, Object> fields = new HashMap<>();
    private boolean keepIt = false;

    @Override
    Fact build() {
        return new TableFact(schema, table, script, fields, keepIt)
    }

    TableBuilder from(String script) {
        this.script = script;
        return this;
    }

    TableBuilder keepIt(boolean keepIt) {
        this.keepIt = keepIt;
        return this;
    }

    TableBuilder fields(Map<String, ?> fields) {
        this.fields.putAll(fields);
        return this;
    }

}
