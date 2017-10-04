package nomic.hive.dsl

import nomic.dsl.NomicBaseScript

/**
 * @author zdenko.vrabel@wirecard.com
 */
class HiveBaseScriptExt {


    static void hive(final NomicBaseScript self, @DelegatesTo(HiveDslContext) Closure c) {
        def context = new HiveDslContext(self, self.defaultSchema);
        c.delegate = context;
        c.call();
    }

    static void hive(final NomicBaseScript self, String schema, @DelegatesTo(HiveDslContext) Closure c) {
        def builder = new HiveSchemaDefBuilder(schema);
        self.registerDefBuilder(builder);
        def context = new HiveDslContext(self, schema);
        c.delegate = context;
        c.call();
    }


}
