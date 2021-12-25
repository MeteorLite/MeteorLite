package meteor.ui

import Main.client
import Main.overlayManager
import meteor.ui.overlay.Overlay
import meteor.ui.overlay.OverlayLayer
import meteor.ui.overlay.OverlayPosition
import net.runelite.api.GameState
import net.runelite.api.Point
import net.runelite.api.widgets.Widget
import net.runelite.api.widgets.WidgetItem
import java.awt.Color
import java.awt.Graphics2D
import java.awt.Rectangle
import java.awt.RenderingHints

object OverlayRenderer {

    fun renderOverlayLayer(graphics: Graphics2D, layer: OverlayLayer) {
        val overlays: Collection<Overlay> = overlayManager.getLayer(layer)
        renderOverlays(graphics, overlays, layer)
    }

    fun renderAfterLayer(graphics: Graphics2D?, layer: Widget,
                         widgetItems: Collection<WidgetItem>) {
        val overlays = overlayManager.getForLayer(layer.id)
        overlayManager.widgetItems = widgetItems
        renderOverlays(graphics!!, overlays, OverlayLayer.ABOVE_WIDGETS)
        overlayManager.widgetItems = emptyList()
    }

    fun setGraphicProperties(graphics: Graphics2D) {
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
    }

    private fun renderOverlays(graphics: Graphics2D, overlays: Collection<Overlay>?,
                               layer: OverlayLayer) {
        if ((overlays == null) || overlays.isEmpty()
                || (client.gameState != GameState.LOGGED_IN)) {
            return
        }
        setGraphicProperties(graphics)

        // Draw snap corners
        if (inOverlayDraggingMode && layer == OverlayLayer.UNDER_WIDGETS && currentManagedOverlay != null && currentManagedOverlay.getPosition() != OverlayPosition.DETACHED) {
            val translatedSnapCorners: OverlayBounds = snapCorners.translated(
                    -meteor.ui.overlay.OverlayRenderer.SNAP_CORNER_SIZE.width,
                    -meteor.ui.overlay.OverlayRenderer.SNAP_CORNER_SIZE.height)
            val previous = graphics.color
            for (corner in translatedSnapCorners.getBounds()) {
                graphics.setColor(
                        if (corner.contains(mousePosition)) meteor.ui.overlay.OverlayRenderer.SNAP_CORNER_ACTIVE_COLOR else meteor.ui.overlay.OverlayRenderer.SNAP_CORNER_COLOR)
                graphics.fill(corner)
            }
            graphics.color = previous
        }

        // Get mouse position
        val mouseCanvasPosition: Point = client.getMouseCanvasPosition()
        val mouse = java.awt.Point(mouseCanvasPosition.x, mouseCanvasPosition.y)

        // Save graphics2d properties so we can restore them later
        val transform = graphics.transform
        val stroke = graphics.stroke
        val composite = graphics.composite
        val paint = graphics.paint
        val renderingHints = graphics.renderingHints
        val background = graphics.background
        for (overlay in overlays) {
            val overlayPosition: OverlayPosition = getCorrectedOverlayPosition(overlay)
            if (overlayPosition == OverlayPosition.DYNAMIC
                    || overlayPosition == OverlayPosition.TOOLTIP) {
                safeRender(client, overlay, layer, graphics, java.awt.Point())

                // Restore graphics2d properties
                graphics.transform = transform
                graphics.stroke = stroke
                graphics.composite = composite
                graphics.paint = paint
                graphics.setRenderingHints(renderingHints)
                graphics.background = background
            } else {
                val bounds: Rectangle = overlay.bounds
                val dimension = bounds.size
                val preferredLocation: java.awt.Point = overlay.preferredLocation
                var location: java.awt.Point

                // If the final position is not modified, layout it
                if (overlayPosition != OverlayPosition.DETACHED && (preferredLocation == null
                                || overlay.preferredPosition != null)) {
                    val snapCorner: Rectangle = snapCorners.forPosition(overlayPosition)
                    val translation: java.awt.Point = meteor.ui.overlay.OverlayUtil.transformPosition(overlayPosition,
                            dimension) // offset from corner
                    // Target x/y to draw the overlay
                    val destX = snapCorner.getX().toInt() + translation.x
                    val destY = snapCorner.getY().toInt() + translation.y
                    // Clamp the target position to ensure it is on screen or within parent bounds
                    location = clampOverlayLocation(destX, destY, dimension.width, dimension.height, overlay)
                    // Diff final position to target position in order to add it to the snap corner padding. The
                    // overlay effectively takes up the difference of (clamped location - target location) in
                    // addition to its normal dimensions.
                    val dX = location.x - destX
                    val dY = location.y - destY
                    val padding: java.awt.Point = meteor.ui.overlay.OverlayUtil.padPosition(overlayPosition, dimension,
                            meteor.ui.overlay.OverlayRenderer.PADDING) // overlay size + fixed padding
                    // translate corner for padding and any difference due to the position clamping
                    snapCorner.translate(padding.x + dX, padding.y + dY)
                } else {
                    location = preferredLocation ?: bounds.location

                    // Clamp the overlay position to ensure it is on screen or within parent bounds
                    location = clampOverlayLocation(location.x, location.y, dimension.width, dimension.height,
                            overlay)
                }
                if (overlay.preferredSize != null) {
                    bounds.size = overlay.preferredSize
                }
                safeRender(client, overlay, layer, graphics, location)

                // Restore graphics2d properties prior to drawing bounds
                graphics.transform = transform
                graphics.stroke = stroke
                graphics.composite = composite
                graphics.paint = paint
                graphics.setRenderingHints(renderingHints)
                graphics.background = background
                if (!bounds.isEmpty) {
                    if (inOverlayManagingMode) {
                        var boundsColor: Color
                        if (inOverlayResizingMode && currentManagedOverlay === overlay) {
                            boundsColor = meteor.ui.overlay.OverlayRenderer.MOVING_OVERLAY_RESIZING_COLOR
                        } else if (inOverlayDraggingMode && currentManagedOverlay === overlay) {
                            boundsColor = meteor.ui.overlay.OverlayRenderer.MOVING_OVERLAY_ACTIVE_COLOR
                        } else if (inOverlayDraggingMode && overlay.isDragTargetable()
                                && currentManagedOverlay.isDragTargetable()
                                && currentManagedOverlay.getBounds().intersects(bounds)) {
                            boundsColor = meteor.ui.overlay.OverlayRenderer.MOVING_OVERLAY_TARGET_COLOR
                            assert(currentManagedOverlay !== overlay)
                            dragTargetOverlay = overlay
                        } else {
                            boundsColor = meteor.ui.overlay.OverlayRenderer.MOVING_OVERLAY_COLOR
                        }
                        graphics.color = boundsColor
                        graphics.draw(bounds)
                        graphics.paint = paint
                    }
                    if (!client.isMenuOpen() && !client.getSpellSelected() && bounds.contains(mouse)) {
                        if (menuEntries == null) {
                            menuEntries = createRightClickMenuEntries(overlay)
                        }
                        if (focusedOverlay == null) {
                            focusedOverlay = overlay
                            if (focusedOverlay !== prevFocusedOverlay) {
                                if (prevFocusedOverlay != null) {
                                    prevFocusedOverlay.onMouseExit()
                                }
                                overlay.onMouseEnter()
                            }
                        }
                        overlay.onMouseOver()
                    }
                }
            }
        }
    }
}