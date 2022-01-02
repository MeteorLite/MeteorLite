package meteor.plugins

import meteor.eventbus.EventBus
import meteor.eventbus.events.GameStateChanged
import meteor.ui.overlay.Overlay
import net.runelite.api.GameState
import java.awt.Dimension
import java.awt.Graphics2D
@PluginDescriptor("Example")
class ExamplePlugin: Plugin() {
    var exampleOverlay = overlay<ExampleOverlay>(ExampleOverlay)

    override fun onStart() {
        EventBus.subscribe(GameStateChanged::class.java) { it as GameStateChanged
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