package nomic.dsl

import nomic.core.FactBuilder
import nomic.dsl.builder.ModuleFactBuilder
import nomic.dsl.builder.RequireFactBuilder

/**
 * This base script class basically contains everything what
 * every 'nomic.box' scripts can use as globals (methods, parameters etc.)
 * Also this script is cummulate the FacBuilder-s the compiler use for building
 * concrete facts at the end.
 *
 * The output facts are sorted how they should be executed. If there are some `factions` with
 * dependencies, the output will contain sorted facts of factions.
 *
 * @author vrabel.zdenko@gmail.com
 */
abstract class NomicBaseScriptEx extends Script implements ScriptContext {

    //-------------------------------------------------------------------------------------------------
    // hidden, not API functions
    //-------------------------------------------------------------------------------------------------

    @Override
    void registerBuilder(FactBuilder builder) {
        if (this.binding.variables["factBuilders"] == null) {
            this.binding.variables["factBuilders"] = new LinkedList<FactBuilder>()
        }
        ((List<FactBuilder>)this.binding.variables["factBuilders"]).add(builder);
    }


    private void registerFaction(FactionContext f) {
        if (this.binding.variables["factions"] == null) {
            this.binding.variables["factions"] = new LinkedList<FactionContext.Faction>()
        }
        def faction = new FactionContext.Faction(f)
        ((List<FactBuilder>)this.binding.variables["factions"]).add(faction);
    }


    //-------------------------------------------------------------------------------------------------
    // global parameters
    //-------------------------------------------------------------------------------------------------

    @Override
    def String getName() {
        return this.binding.variables['name']
    }

    @Override
    void setName(name) {
        this.binding.variables['name'] = name
    }

    @Override
    def String getGroup() {
        return this.binding.variables['group']
    }

    @Override
    void setGroup(group) {
        this.binding.variables['group'] = group
    }

    @Override
    def String getVersion() {
        return this.binding.variables['version']
    }

    @Override
    void setVersion(version) {
        this.binding.variables['version'] = version
    }

    @Override
    def String getAppDir() {
        return this.binding.variables['appDir']
    }

    @Override
    void setAppDir(appDir) {
        this.binding.variables['appDir'] = appDir
    }

    @Override
    def String getHomeDir() {
        return this.binding.variables['homeDir']
    }

    @Override
    void setHomeDir(homeDir) {
        this.binding.variables['homeDir'] = homeDir
    }

    @Override
    def String getUser() {
        return this.binding.variables['user']
    }

    @Override
    void setUser(user) {
        this.binding.variables['user'] = user
    }

    @Override
    def String getNameNode() {
        return this.binding.variables['nameNode']
    }

    @Override
    String getBinding(String name) {
        return this.binding.getProperty(name);
    }

    //-------------------------------------------------------------------------------------------------
    // global methods
    //-------------------------------------------------------------------------------------------------

    void module(String name) {
        registerBuilder(new ModuleFactBuilder(name: name))
    }

    void require(String ref) {
        registerBuilder(new RequireFactBuilder(ref))
    }

    void require(Map args) {
        registerBuilder(new RequireFactBuilder(args))
    }

    void faction(String factionId, @DelegatesTo(FactionContext) Closure c) {
        faction(factionId, "", c);
    }

    void faction(String factionId, String dependsOn, @DelegatesTo(ScriptContext) Closure c) {
        def context = new FactionContext(this, factionId, dependsOn) as ScriptContext
        c.resolveStrategy = Closure.DELEGATE_FIRST
        c.delegate = context;
        c.call();
        registerFaction(context as FactionContext);
    }
}
