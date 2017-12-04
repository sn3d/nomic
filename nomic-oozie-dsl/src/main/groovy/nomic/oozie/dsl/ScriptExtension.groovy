package nomic.oozie.dsl

import nomic.dsl.NomicBaseScriptEx
import nomic.dsl.ScriptContext

/**
 * @author vrabel.zdenko@gmail.com
 */
class ScriptExtension {

    static void oozie(final ScriptContext self, @DelegatesTo(OozieContext) Closure c) {
        def context = new OozieContext(script: self);
        c.delegate = context;
        c.call();
    }

}
