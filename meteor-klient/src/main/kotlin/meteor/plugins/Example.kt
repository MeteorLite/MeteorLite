package meteor.plugins

import meteor.ui.overlay.Overlay
import java.awt.Dimension
import java.awt.Graphics2D

class ExamplePlugin: Plugin() {
    override var overlay = ExampleOverlay as Overlay?

    override fun onStart() {
    }
}

object ExampleOverlay: Overlay() {
    override fun render(graphics: Graphics2D): Dimension? {
        for (npc in client.npcs) {
            val convexHull = npc.convexHull
            if (convexHull != null)
                graphics.draw(convexHull)
        }
        return null
    }
}