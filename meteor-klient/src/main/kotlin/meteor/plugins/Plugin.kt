package meteor.plugins

import meteor.ui.overlay.Overlay
import meteor.ui.overlay.OverlayLayer
import meteor.ui.overlay.OverlayPosition

open class Plugin {
    val client = Main.client
    val overlayManager = Main.overlayManager
    open var overlay: Overlay? = null
    var enabled = false

    fun start() {
        enabled = true
        if (overlay != null) {
            overlay!!.layer = OverlayLayer.ALWAYS_ON_TOP
            overlay!!.position = OverlayPosition.TOP_LEFT
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