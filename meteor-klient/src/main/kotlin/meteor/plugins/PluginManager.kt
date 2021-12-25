package meteor.plugins

object PluginManager {
    var plugins = ArrayList<Plugin>()
    init {
        plugins.add(OverlayTest())
    }

    fun startPlugins() {
        for (it in plugins) {
            it.start()
            it.onStart()
        }
    }
}