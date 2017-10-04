package nomic.hive.dsl

import nomic.dsl.NomicBaseScript

/**
 * @author zdenko.vrabel@wirecard.com
 */
class HiveDslContext {

    private NomicBaseScript baseScript;
    private String schema;
    private Map<String, ?> fields = new HashMap<>()

    HiveDslContext(NomicBaseScript baseScript, String schema) {
        this.baseScript = baseScript;
        this.schema = schema;
    }

    public HiveScriptDefBuilder script(String path) {
        def f = [:]
        f.putAll(this.baseScript.binding.variables)
        f.putAll(this.fields)
        def builder = new HiveScriptDefBuilder(scriptPath: path, fields: f)
        baseScript.registerDefBuilder(builder);
        return builder;
    }

    public fields(Map<String, ? > fields) {
        if (fields != null) {
            this.fields.putAll(fields);
        }
    }

    public HiveTableDefBuilder table(String name) {
        def builder = new HiveTableDefBuilder(schema, name);
        def f = [:]
        f.putAll(this.baseScript.binding.variables)
        f.putAll(this.fields)
        builder.with(f)
        baseScript.registerDefBuilder(builder);
        return builder;
    }
}
