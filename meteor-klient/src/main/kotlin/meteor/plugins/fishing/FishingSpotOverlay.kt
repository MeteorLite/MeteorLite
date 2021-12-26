package meteor.plugins.fishing

import meteor.ui.overlay.Overlay
import net.runelite.api.coords.WorldPoint
import java.awt.Color
import java.awt.Dimension
import java.awt.Graphics2D

class FishingSpotOverlay(var plugin: FishingPlugin): Overlay() {
    override fun render(graphics: Graphics2D): Dimension? {

        var previousSpot: FishingSpot? = null
        var previousLocation: WorldPoint? = null
        for (npc in plugin.fishingSpots) {
            val spot: FishingSpot = FishingSpot.findSpot(npc.id) ?: continue

            // This relies on the sort order to keep identical npcs on the same tile adjacent to each other
            if (previousSpot == spot && previousLocation == npc.worldLocation) {
                continue
            }
            val color: Color = Color.CYAN
            val poly = npc.canvasTilePoly
            if (poly != null) {
                renderPolygon(graphics, poly, color.darker())
            }
            val text: String = spot.spotName
            val textLocation = npc.getCanvasTextLocation(graphics, text, npc.logicalHeight + 40)
            if (textLocation != null) {
                renderTextLocation(graphics, textLocation, text, color.darker())
            }
            previousSpot = spot
            previousLocation = npc.worldLocation
        }
        return null
    }

}