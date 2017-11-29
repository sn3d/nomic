package nomic.hive.dsl

import nomic.dsl.NomicBaseScriptEx

/**
 * @author vrabel.zdenko@gmail.com
 */
class HiveContext {

    String hiveSchema;
    private NomicBaseScriptEx script;
    private Map<String, ?> fields = new HashMap<>();

    HiveContext(NomicBaseScriptEx script, String hiveSchema, boolean keepIt) {
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
