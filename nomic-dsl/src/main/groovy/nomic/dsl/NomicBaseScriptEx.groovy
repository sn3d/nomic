package nomic.dsl

import nomic.core.FactBuilder
import nomic.dsl.builder.ModuleFactBuilder

/**
 * This base script class basically contains everything what
 * every 'nomic.box' scripts can use as globals (methods, parameters etc.)
 * Also this script is cummulate the FacBuilder-s the compiler use for building
 * concrete facts at the end.
 *
 *
 * @author vrabel.zdenko@gmail.com
 */
abstract class NomicBaseScriptEx extends Script {

    //-------------------------------------------------------------------------------------------------
    // hidden, not API functions
    //-------------------------------------------------------------------------------------------------

    void registerBuilder(FactBuilder builder) {
        if (this.binding.variables["factBuilders"] == null) {
            this.binding.variables["factBuilders"] = new LinkedList<FactBuilder>()
        }
        ((List<FactBuilder>)this.binding.variables["factBuilders"]).add(builder);
    }


    //-------------------------------------------------------------------------------------------------
    // global parameters
    //-------------------------------------------------------------------------------------------------

    def getName() {
        return this.binding.variables['name']
    }

    void setName(name) {
        this.binding.variables['name'] = name
    }

    def getGroup() {
        return this.binding.variables['group']
    }

    void setGroup(group) {
        this.binding.variables['group'] = group
    }

    def getVersion() {
        return this.binding.variables['version']
    }

    void setVersion(version) {
        this.binding.variables['version'] = version
    }

    def getAppDir() {
        return this.binding.variables['appDir']
    }

    void setAppDir(appDir) {
        this.binding.variables['appDir'] = appDir
    }

    def getHomeDir() {
        return this.binding.variables['homeDir']
    }

    void setHomeDir(homeDir) {
        this.binding.variables['homeDir'] = homeDir
    }

    def getUser() {
        return this.binding.variables['user']
    }

    void setUser(user) {
        this.binding.variables['user'] = user
    }

    def getDefaultSchema() {
        return this.binding.variables['defaultSchema'].toString()
    }

    void setDefaultSchema(String defaultSchema) {
        this.binding.variables['defaultSchema'] = defaultSchema
    }

    //-------------------------------------------------------------------------------------------------
    // global methods
    //-------------------------------------------------------------------------------------------------

    void module(String name) {
        registerBuilder(new ModuleFactBuilder(name: name))
    }
}
