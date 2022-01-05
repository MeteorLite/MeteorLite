package meteor.plugins

import meteor.Configuration
import meteor.config.ConfigManager
import meteor.plugins.agility.AgilityPlugin
import meteor.plugins.fishing.FishingPlugin
import meteor.plugins.stretchedmode.StretchedModePlugin
import meteor.plugins.worldmap.WorldMapPlugin
import rs117.hd.GpuHDEventAdapter
import rs117.hd.GpuHDPlugin
import java.lang.RuntimeException

object PluginManager {
    var plugins = ArrayList<Plugin>()
    init {
        initPlugin(ExamplePlugin())
        initPlugin(FishingPlugin())
        initPlugin(AgilityPlugin())
        if (Configuration.allowGPU) {
            initPlugin(GpuHDPlugin())
            GpuHDEventAdapter.registerEvents()
        }
        initPlugin(StretchedModePlugin())
        initPlugin(WorldMapPlugin())
    }

    fun initPlugin(plugin: Plugin) {
        for (p in plugins)
            if (p::class == plugin::class)
                throw RuntimeException("Duplicate plugin ${p::class.simpleName} not allowed")
        plugins.add(plugin)
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

        if (ConfigManager.getConfiguration(plugin.javaClass.simpleName, "pluginEnabled").toBoolean())
            shouldEnable = true else if (plugin.javaClass.getAnnotation(PluginDescriptor::class.java).cantDisable) shouldEnable = true

        if (shouldEnable) {
            Thread {
                plugin.start()
                plugin.onStart()
            }.start()
        }

        if (!plugins.contains(plugin))
            plugins.add(plugin)
    }

    inline fun <reified T : Plugin> getPlugin(): T? {
        for (plugin in plugins) {
            if (plugin is T)
                return plugin
        }
        return null
    }

    inline fun <reified T : Plugin> restartPlugin(): T? {
        for (plugin in plugins) {
            if (plugin is T) {
                plugin.onStop()
                plugin.onStart()
            }
        }
        return null
    }
}