package nomic.hive.dsl

import nomic.dsl.NomicBaseScriptEx
import nomic.dsl.ScriptContext

/**
 * @author vrabel.zdenko@gmail.com
 */
class ScriptExtension {

    static void hive(final ScriptContext self, @DelegatesTo(HiveContext) Closure c) {
        hive(self, self.getBinding('hiveSchema'), c)
    }

    static void hive(final ScriptContext self, String hiveSchema, @DelegatesTo(HiveContext) Closure c) {
        hive(self, hiveSchema, true, c)
    }

    static void hive(final ScriptContext self, String hiveSchema, boolean keepIt, @DelegatesTo(HiveContext) Closure c  ) {
        def context = new HiveContext(self, hiveSchema, keepIt);
        c.delegate = context;
        c.call();
    }

}
