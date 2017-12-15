package nomic.dsl

import nomic.core.FactBuilder

/**
 * @author vrabel.zdenko@gmail.com
 */
interface ScriptContext {
    void registerBuilder(FactBuilder builder);

    String getBinding(String name);
    String getNameNode();

    String getName();
    void setName(name);

    String getGroup();
    void setGroup(group);

    String getVersion();
    void setVersion(version);

    String getAppDir();
    void setAppDir(appDir);

    String getHomeDir();
    void setHomeDir(homeDir);

    String getUser();
    void setUser(user);


}
