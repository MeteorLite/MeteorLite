package meteor.plugins

import meteor.config.ConfigManager
import meteor.config.legacy.Config
import meteor.ui.overlay.Overlay

open class Plugin(val configuration: Config? = null) {
    val client = Main.client
    val overlayManager = Main.overlayManager
    open var overlay: Overlay? = null
    var enabled = false
    open var config: Config? = null

    init {
        if (configuration != null)
            config = ConfigManager.getConfig(configuration.javaClass)
    }
    fun start() {
        enabled = true
        if (overlay != null) {
            overlayManager.add(overlay!!)
        }
    }

    fun stop() {
        enabled = false
    }

    open fun onStart() {

    }

    open fun onStop() {

    }
}