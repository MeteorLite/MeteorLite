package meteor.plugins

import meteor.plugins.fishing.FishingPlugin

object PluginManager {
    var plugins = ArrayList<Plugin>()
    init {
        plugins.add(ExamplePlugin())
        plugins.add(FishingPlugin())
    }

    fun startPlugins() {
        for (it in plugins) {
            it.start()
            it.onStart()
        }
    }
}