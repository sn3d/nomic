package nomic.oozie.dsl

import nomic.dsl.ScriptContext

/**
 * @author vrabel.zdenko@gmail.com
 */
class OozieContext {
    private ScriptContext script;

    public CoordinatorBuilder coordinator(String name) {
        String workingDir = script.appDir + "/" + script.group + "/" + script.name
        String hdfsDest = workingDir + "/" + name;

        def builder = new CoordinatorBuilder(
                name: name,
                xmlSource: name,
                hdfsDest: hdfsDest,
                workingDir: workingDir,
                keepIt: false,
                parameters: [
                        "user.name": script.user,
                        "nameNode": script.nameNode,
                        "oozie.coord.application.path": hdfsDest
                ]
        )

        script.registerBuilder(builder);
        return builder;
    }
}
