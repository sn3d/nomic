package nomic.hive.dsl

import nomic.definition.Definition
import nomic.dsl.DefBuilder
import nomic.hive.definition.HiveTableDefinition

/**
 * @author zdenko.vrabel@wirecard.com
 */
class HiveTableDefBuilder implements DefBuilder {

    private String schema;
    private String tableName;
    private String createScript;
    public  boolean keepIt = false;
    private Map<String, ?> fields = new HashMap<>();

    HiveTableDefBuilder(String schema, String tableName) {
        this.schema = schema
        this.tableName = tableName
    }

    public HiveTableDefBuilder keepIt(boolean enabled) {
        this.keepIt = enabled;
        return this;
    }

    public HiveTableDefBuilder schema(String schema) {
        this.schema = schema;
        return this;
    }

    public HiveTableDefBuilder from(String createScript) {
        this.createScript = createScript;
        return this;
    }

    public HiveTableDefBuilder with(Map fields) {
        this.fields.putAll(fields);
        return this;
    }

    @Override
    Definition build() {
        return new HiveTableDefinition(schema, tableName, createScript, fields, keepIt)
    }
}
