package meteor.plugins

import meteor.eventbus.EventBus
import meteor.eventbus.events.GameStateChanged
import meteor.ui.overlay.Overlay
import net.runelite.api.GameState
import java.awt.Dimension
import java.awt.Graphics2D

class ExamplePlugin: Plugin() {
    override var overlay = ExampleOverlay as Overlay?

    override fun onStart() {
        EventBus.subscribe {
            if (it is GameStateChanged)
                when (it.new) {
                    GameState.LOGGING_IN, GameState.HOPPING -> {
                        //println("Shits pretty slick")
                    }
                }
        }
    }
}

object ExampleOverlay: Overlay() {
    override fun render(graphics: Graphics2D): Dimension? {
        return null
    }
}