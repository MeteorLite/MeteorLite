package meteor.plugins

import meteor.ui.overlay.Overlay

open class Plugin {
    val client = Main.client
    val overlayManager = Main.overlayManager
    open var overlay: Overlay? = null
    var enabled = false

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