package nomic.dsl

import nomic.NomicConfig

/**
 * @author zdenko.vrabel@wirecard.com
 */
abstract class NomicBaseScript extends Script {

    void finish() {
        def infoDefBuilder = new BoxInfoDefBuilder(
            group: this.binding.variables["group"] != null ? this.binding.variables["group"] : "",
            name: this.binding.variables["name"],
            version: this.binding.variables["version"] != null ? this.binding.variables["version"] : ""
        )
        registerDefBuilder(infoDefBuilder)
    }

    NomicConfig getCfg() {
        return this.binding['config']
    }

    void module(String path) {
        ModuleDefBuilder builder = new ModuleDefBuilder(path: path);
        registerDefBuilder(builder)
    }


    void registerDefBuilder(DefBuilder builder) {
        if (this.binding.variables["defProducers"] == null) {
            this.binding.variables["defProducers"] = new LinkedList<DefBuilder>()
        }
        ((List<DefBuilder>)this.binding.variables["defProducers"]).add(builder);
    }

    public String getUser() {
        return this.binding.variables['user']
    }

    public String getAppDir() {
        return this.binding.variables['appDir']
    }

    public String getHomeDir() {
        return this.binding.variables['homeDir']
    }

    public String getDefaultSchema() {
        return this.binding.variables['defaultSchema']
    }

    public String getGroup() {
        return this.binding.variables['group']
    }

    public String getName() {
        return this.binding.variables['name']
    }

    public String getVersion() {
        return this.binding.variables['version']
    }

    public String getWorkingDir() {
        def path = new StringBuilder();
        path.append(getAppDir())

        if (group != null && !group.isEmpty()) {
            path.append("/${group}")
        }

        if (name != null && !name.isEmpty()) {
            path.append("/${name}")
        }

        return path.toString();
    }

}
