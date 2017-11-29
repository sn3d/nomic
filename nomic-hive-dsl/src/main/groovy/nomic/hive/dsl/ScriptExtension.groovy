package nomic.hive.dsl

import nomic.dsl.NomicBaseScriptEx

/**
 * @author vrabel.zdenko@gmail.com
 */
class ScriptExtension {

    static void hive(final NomicBaseScriptEx self, @DelegatesTo(HiveContext) Closure c) {
        hive(self, self.binding['hiveSchema'] as String, c)
    }

    static void hive(final NomicBaseScriptEx self, String hiveSchema, @DelegatesTo(HiveContext) Closure c) {
        def context = new HiveContext(self, hiveSchema, false);
        c.delegate = context;
        c.call();
    }

    static void hive(final NomicBaseScriptEx self, String hiveSchema, boolean keepIt, @DelegatesTo(HiveContext) Closure c) {
        def context = new HiveContext(self, hiveSchema, keepIt);
        c.delegate = context;
        c.call();
    }


}
