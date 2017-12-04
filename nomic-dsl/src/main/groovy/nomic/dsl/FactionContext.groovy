package nomic.dsl

import nomic.core.FactBuilder

/**
 * @author vrabel.zdenko@gmail.com
 */
class FactionContext implements ScriptContext {

    private final String factionId;
    private final String dependsOn;
    private final ScriptContext script;
    private final List<FactBuilder> factBuilders;

    FactionContext(ScriptContext script, String factionId, String dependsOn) {
        this.script = script
        this.factionId = factionId
        this.dependsOn = dependsOn
        this.factBuilders = []
    }

    @Override
    void registerBuilder(FactBuilder builder) {
        factBuilders.add(builder)
    }

    @Override
    String getBinding(String name) {
        return script.getBinding(name)
    }

    @Override
    String getName() {
        return script.getName()
    }

    @Override
    void setName(Object name) {
        script.setName(name)
    }

    @Override
    String getGroup() {
        return script.getGroup()
    }

    @Override
    void setGroup(Object group) {
        script.setGroup(group)
    }

    @Override
    String getVersion() {
        return script.getVersion()
    }

    @Override
    void setVersion(Object version) {
        script.setVersion(version)
    }

    @Override
    String getAppDir() {
        return script.getAppDir()
    }

    @Override
    void setAppDir(Object appDir) {
        script.setAppDir(appDir)
    }

    @Override
    String getHomeDir() {
        return script.getHomeDir()
    }

    @Override
    void setHomeDir(Object homeDir) {
        script.setHomeDir(homeDir)
    }

    @Override
    String getUser() {
        return script.getUser()
    }

    @Override
    void setUser(Object user) {
        script.setUser(user)
    }

    @Override
    String getNameNode() {
        return script.getUser()
    }


    /**
     * @author vrabel.zdenko@gmail.com
     */
    class Faction {
        String factionId;
        String dependsOn
        List<FactBuilder> factBuilders;

        Faction() {
            this.factionId = FactionContext.this.factionId
            this.dependsOn = FactionContext.this.dependsOn
            this.factBuilders = FactionContext.this.factBuilders
        }

        @Override
        public String toString() {
            return "Faction{" +
                    "factionId='" + factionId + '\'' +
                    '}';
        }
    }

}