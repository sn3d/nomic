package nomic.oozie.dsl

import nomic.dsl.NomicBaseScriptEx

/**
 * @author vrabel.zdenko@gmail.com
 */
class ScriptExtension {

    static void oozie(final NomicBaseScriptEx self, @DelegatesTo(OozieContext) Closure c) {
        def context = new OozieContext(script: self);
        c.delegate = context;
        c.call();
    }

}
