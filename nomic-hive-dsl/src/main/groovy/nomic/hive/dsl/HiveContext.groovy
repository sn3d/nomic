package nomic.hive.dsl

import nomic.dsl.ScriptContext

/**
 * @author vrabel.zdenko@gmail.com
 */
class HiveContext {

    String hiveSchema;
    private ScriptContext script;
    private Map<String, ?> fields = new HashMap<>();

    HiveContext(ScriptContext script, String hiveSchema, boolean keepIt) {
        this.hiveSchema = hiveSchema
        this.script = script
        schema(hiveSchema).keepIt(keepIt)
    }

    TableBuilder table(String name) {
        def builder = new TableBuilder(schema: hiveSchema, table: name, fields: fields);
        script.registerBuilder(builder);
        return builder;
    }

    void fields(Map<String, ?> f) {
        this.fields.putAll(f)
    }

    void field(Map<String, ?> f) {
        this.fields.putAll(f)
    }

    SchemaBuilder schema(String name) {
        def builder = new SchemaBuilder(name: name);
        script.registerBuilder(builder);
        return builder;
    }
}
