package meteor.plugins.fishing

import meteor.ItemManager
import meteor.ui.overlay.Overlay
import meteor.ui.overlay.OverlayLayer
import meteor.util.ImageUtil
import net.runelite.api.Point
import net.runelite.api.coords.WorldPoint
import java.awt.Color
import java.awt.Dimension
import java.awt.Graphics2D
import java.awt.image.BufferedImage

class FishingSpotOverlay(var plugin: FishingPlugin): Overlay(layer = OverlayLayer.ABOVE_SCENE) {
    private val ONE_TICK_AERIAL_FISHING = 3
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

            var fishImage: BufferedImage? = ItemManager.getImage(spot.fishSpriteId)
            if (spot == FishingSpot.COMMON_TENCH
                    && npc.worldLocation.distanceTo2D(client.localPlayer!!.worldLocation)
                    <= ONE_TICK_AERIAL_FISHING) {
                fishImage = outlineImage(ItemManager.getImage(spot.fishSpriteId)!!, color)
            }
            if (fishImage != null) {
                val imageLocation: Point? = npc.getCanvasImageLocation(fishImage, npc.logicalHeight)
                if (imageLocation != null) {
                    renderImageLocation(graphics, imageLocation, fishImage)
                }
            }
        }
        return null
    }

}