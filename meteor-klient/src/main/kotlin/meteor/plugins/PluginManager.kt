package meteor.plugins

object PluginManager {
    var plugins = ArrayList<Plugin>()
    init {
        plugins.add(ExamplePlugin())
    }

    fun startPlugins() {
        for (it in plugins) {
            it.start()
            it.onStart()
        }
    }
}