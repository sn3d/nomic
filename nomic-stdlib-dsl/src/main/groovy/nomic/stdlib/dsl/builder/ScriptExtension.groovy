package nomic.stdlib.dsl.builder

import nomic.dsl.FactionContext
import nomic.dsl.NomicBaseScriptEx
import nomic.dsl.ScriptContext

/**
 * @author vrabel.zdenko@gmail.com
 */
public class ScriptExtension {

    public static void debug(final ScriptContext self, String message) {
        self.registerBuilder(new DebugFactBuilder(message: message))
    }


}
