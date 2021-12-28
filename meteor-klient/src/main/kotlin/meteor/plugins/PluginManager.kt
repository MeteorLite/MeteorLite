package meteor.plugins

import meteor.config.ConfigManager
import meteor.config.legacy.Config
import meteor.plugins.agility.AgilityPlugin
import meteor.plugins.fishing.FishingPlugin
import java.lang.Boolean
import kotlin.String

object PluginManager {
    var plugins = ArrayList<Plugin>()
    init {
        plugins.add(ExamplePlugin())
        plugins.add(FishingPlugin())
        plugins.add(AgilityPlugin())
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

            val config: Config? = plugin.config
            if (config != null) {
                ConfigManager.setDefaultConfiguration(config, false)
            }

            var shouldEnable = false

            if (Boolean.parseBoolean(ConfigManager.getConfiguration(plugin.javaClass.simpleName, "pluginEnabled")))
                shouldEnable = true else if (plugin.javaClass.getAnnotation(PluginDescriptor::class.java).cantDisable) shouldEnable = true

            if (shouldEnable) {
                plugin.start()
                plugin.onStart()
            }
        }
    }
}