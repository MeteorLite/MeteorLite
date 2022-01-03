package meteor.plugins

import meteor.config.ConfigManager
import meteor.plugins.agility.AgilityPlugin
import meteor.plugins.fishing.FishingPlugin
import meteor.plugins.stretchedmode.StretchedModePlugin
import rs117.hd.GpuHDPlugin
import java.lang.Boolean
import kotlin.String

object PluginManager {
    var plugins = ArrayList<Plugin>()
    init {
        plugins.add(ExamplePlugin())
        plugins.add(FishingPlugin())
        plugins.add(AgilityPlugin())
        //plugins.add(GpuHDPlugin())
        //plugins.add(StretchedModePlugin())
    }

    fun startPlugins() {
        for (plugin in plugins) {
            val enabledConfig: String? = ConfigManager.getConfiguration(plugin.javaClass.simpleName, "pluginEnabled")
            val descriptor: PluginDescriptor? = plugin.javaClass.getAnnotation(PluginDescriptor::class.java)
            if (enabledConfig == null) {
                if (descriptor != null) {
                    val enabledByDefault = descriptor.enabledByDefault || descriptor.cantDisable
                    ConfigManager.setConfiguration(plugin.javaClass.simpleName, "pluginEnabled", enabledByDefault)
                }
            }

            if (enabledConfig != null && descriptor!!.disabledOnStartup) {
                ConfigManager.setConfiguration(plugin.javaClass.simpleName, "pluginEnabled", false)
            }

            var shouldEnable = false

            if (Boolean.parseBoolean(ConfigManager.getConfiguration(plugin.javaClass.simpleName, "pluginEnabled")))
                shouldEnable = true else if (plugin.javaClass.getAnnotation(PluginDescriptor::class.java).cantDisable) shouldEnable = true

            if (shouldEnable) {
                Thread {
                    plugin.start()
                    plugin.onStart()
                }.start()
            }
        }
    }

    inline fun <reified T : Plugin> getPlugin(): T? {
        for (plugin in plugins) {
            if (plugin is T)
                return plugin
        }
        return null
    }
}