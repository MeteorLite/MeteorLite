package meteor.ui

import Main.client
import Main.fontManager
import Main.overlayManager
import com.google.common.base.MoreObjects
import com.google.common.primitives.Ints
import meteor.Event
import meteor.eventbus.EventBus
import meteor.ui.overlay.*
import meteor.util.JagexColors
import net.runelite.api.*
import net.runelite.api.widgets.Widget
import net.runelite.api.widgets.WidgetInfo
import net.runelite.api.widgets.WidgetItem
import java.awt.*
import java.awt.Point

object OverlayRenderer {

    init {
        EventBus.subscribe(onEvent())
    }

    private fun onEvent(): (Event) -> Unit {
        return {
            if (it is meteor.eventbus.events.BeforeRender) {
                onBeforeRender()
            }
        }
    }

    private const val BORDER = 5
    private const val BORDER_TOP = BORDER + 15
    private const val PADDING = 2
    private const val OVERLAY_RESIZE_TOLERANCE = 5
    private val SNAP_CORNER_SIZE = Dimension(80, 80)
    private val SNAP_CORNER_COLOR = Color(0, 255, 255, 50)
    private val SNAP_CORNER_ACTIVE_COLOR = Color(0, 255, 0, 100)
    private val MOVING_OVERLAY_COLOR = Color(255, 255, 0, 100)
    private val MOVING_OVERLAY_ACTIVE_COLOR = Color(255, 255, 0, 200)
    private val MOVING_OVERLAY_TARGET_COLOR = Color.RED
    private val MOVING_OVERLAY_RESIZING_COLOR = Color(255, 0, 255, 200)

    // Overlay movement variables
    private val overlayOffset = java.awt.Point()
    private val mousePosition = java.awt.Point()
    private val currentManagedOverlay: Overlay? = null
    private var dragTargetOverlay: Overlay? = null
    private val currentManagedBounds: Rectangle? = null
    private const val inOverlayManagingMode = false
    private const val inOverlayResizingMode = false
    private const val inOverlayDraggingMode = false
    private const val startedMovingOverlay = false
    private var menuEntries: Array<MenuEntry>? = null

    // Overlay state validation
    private var viewportBounds: Rectangle? = null
    private var chatboxBounds: Rectangle? = null
    private var chatboxHidden = false
    private var isResizeable = false
    private var emptySnapCorners: OverlayBounds? = null
    private var snapCorners: OverlayBounds? = null

    // focused overlay
    private var focusedOverlay: Overlay? = null
    private var prevFocusedOverlay: Overlay? = null

    private fun getViewportLayer(): Widget? {
        return if (client.isResized) {
            if (client.getVar(Varbits.SIDE_PANELS) == 1) {
                client.getWidget(WidgetInfo.RESIZABLE_VIEWPORT_BOTTOM_LINE)
            } else {
                client.getWidget(WidgetInfo.RESIZABLE_VIEWPORT_OLD_SCHOOL_BOX)
            }
        } else client.getWidget(WidgetInfo.FIXED_VIEWPORT)
    }

    private fun shouldInvalidateBounds(): Boolean {
        val chatbox = client.getWidget(WidgetInfo.CHATBOX)
        val resizeableChanged = isResizeable != client.isResized
        var changed = false
        if (resizeableChanged) {
            isResizeable = client.isResized
            changed = true
        }
        val chatboxBoundsChanged = chatbox == null || chatbox.bounds != chatboxBounds
        if (chatboxBoundsChanged) {
            chatboxBounds = if (chatbox != null) chatbox.bounds else Rectangle()
            changed = true
        }
        val chatboxHiddenChanged = chatboxHidden != (chatbox == null || chatbox.isHidden)
        if (chatboxHiddenChanged) {
            chatboxHidden = chatbox == null || chatbox.isHidden
            changed = true
        }
        val viewportWidget: Widget? = getViewportLayer()
        val viewport = if (viewportWidget != null) viewportWidget.bounds else Rectangle()
        val viewportChanged = viewport != viewportBounds
        if (viewportChanged) {
            viewportBounds = viewport
            changed = true
        }
        return changed
    }


    fun onBeforeRender() {
        menuEntries = null
        if (focusedOverlay == null && prevFocusedOverlay != null) {
            prevFocusedOverlay!!.onMouseExit()
        }
        prevFocusedOverlay = focusedOverlay
        focusedOverlay = null
        if (client.gameState == GameState.LOGGED_IN) {
            if (shouldInvalidateBounds()) {
                emptySnapCorners = buildSnapCorners()
            }

            // Create copy of snap corners because overlays will modify them
            snapCorners = OverlayBounds(other = emptySnapCorners!!)
        }
    }

    private fun buildSnapCorners(): OverlayBounds? {
        val topLeftPoint = java.awt.Point(
                viewportBounds!!.x + BORDER,
                viewportBounds!!.y + BORDER_TOP)
        val topCenterPoint = java.awt.Point(
                viewportBounds!!.x + viewportBounds!!.width / 2,
                viewportBounds!!.y + BORDER
        )
        val topRightPoint = java.awt.Point(
                viewportBounds!!.x + viewportBounds!!.width - BORDER,
                topCenterPoint.y)
        val bottomLeftPoint = java.awt.Point(
                topLeftPoint.x,
                viewportBounds!!.y + viewportBounds!!.height - BORDER)
        val bottomRightPoint = java.awt.Point(
                topRightPoint.x,
                bottomLeftPoint.y)

        // Check to see if chat box is minimized
        if (isResizeable && chatboxHidden) {
            bottomLeftPoint.y += chatboxBounds!!.height
        }
        val rightChatboxPoint = if (isResizeable) java.awt.Point(
                viewportBounds!!.x + chatboxBounds!!.width - BORDER,
                bottomLeftPoint.y) else bottomRightPoint
        val canvasTopRightPoint = if (isResizeable) java.awt.Point(client.realDimensions.getWidth().toInt(),
                0) else topRightPoint
        return OverlayBounds(
                Rectangle(topLeftPoint, SNAP_CORNER_SIZE),
                Rectangle(topCenterPoint, SNAP_CORNER_SIZE),
                Rectangle(topRightPoint, SNAP_CORNER_SIZE),
                Rectangle(bottomLeftPoint, SNAP_CORNER_SIZE),
                Rectangle(bottomRightPoint, SNAP_CORNER_SIZE),
                Rectangle(rightChatboxPoint, SNAP_CORNER_SIZE),
                Rectangle(canvasTopRightPoint, SNAP_CORNER_SIZE))
    }


    fun renderOverlayLayer(graphics: Graphics2D, layer: OverlayLayer) {
        val overlays: Collection<Overlay> = overlayManager.getLayer(layer)
        renderOverlays(graphics, overlays, layer)
    }

    fun renderAfterLayer(graphics: Graphics2D, layer: Widget,
                         widgetItems: Collection<WidgetItem>) {
        val overlays = overlayManager.getForLayer(layer.id)
        overlayManager.widgetItems = widgetItems
        renderOverlays(graphics!!, overlays, OverlayLayer.ABOVE_WIDGETS)
        overlayManager.widgetItems = emptyList()
    }

    fun setGraphicProperties(graphics: Graphics2D) {
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
    }

    private fun getCorrectedOverlayPosition(overlay: Overlay): OverlayPosition {
        var overlayPosition: OverlayPosition = overlay.position
        if (overlay.preferredPosition != null) {
            overlayPosition = overlay.preferredPosition!!
        }
        if (!isResizeable) {
            // On fixed mode, ABOVE_CHATBOX_RIGHT is in the same location as
            // BOTTOM_RIGHT and CANVAS_TOP_RIGHT is same as TOP_RIGHT.
            // Just use BOTTOM_RIGHT and TOP_RIGHT to prevent overlays from
            // drawing over each other.
            when (overlayPosition) {
                OverlayPosition.CANVAS_TOP_RIGHT -> overlayPosition = OverlayPosition.TOP_RIGHT
                OverlayPosition.ABOVE_CHATBOX_RIGHT -> overlayPosition = OverlayPosition.BOTTOM_RIGHT
            }
        }
        return overlayPosition
    }

    fun transformPosition(position: OverlayPosition?, dimension: Dimension): java.awt.Point {
        val result = Point()
        when (position) {
            OverlayPosition.DYNAMIC, OverlayPosition.TOOLTIP, OverlayPosition.TOP_LEFT -> {}
            OverlayPosition.TOP_CENTER -> result.x = -dimension.width / 2
            OverlayPosition.BOTTOM_LEFT -> result.y = -dimension.height
            OverlayPosition.BOTTOM_RIGHT, OverlayPosition.ABOVE_CHATBOX_RIGHT -> {
                result.y = -dimension.height
                result.x = -dimension.width
            }
            OverlayPosition.CANVAS_TOP_RIGHT, OverlayPosition.TOP_RIGHT -> result.x = -dimension.width
        }
        return result
    }

    fun padPosition(position: OverlayPosition, dimension: Dimension,
                    padding: Int): java.awt.Point {
        val result = Point()
        when (position) {
            OverlayPosition.DYNAMIC, OverlayPosition.TOOLTIP -> {}
            OverlayPosition.BOTTOM_LEFT -> result.x += dimension.width + (if (dimension.width == 0) 0 else padding)
            OverlayPosition.BOTTOM_RIGHT -> result.x -= dimension.width + (if (dimension.width == 0) 0 else padding)
            OverlayPosition.TOP_LEFT, OverlayPosition.TOP_CENTER, OverlayPosition.CANVAS_TOP_RIGHT, OverlayPosition.TOP_RIGHT -> result.y += dimension.height + (if (dimension.height == 0) 0 else padding)
            OverlayPosition.ABOVE_CHATBOX_RIGHT -> result.y -= dimension.height + (if (dimension.height == 0) 0 else padding)
        }
        return result
    }

    private fun clampOverlayLocation(overlayX: Int, overlayY: Int, overlayWidth: Int,
                                     overlayHeight: Int, overlay: Overlay): java.awt.Point {
        var parentBounds: Rectangle? = overlay.parentBounds
        if (parentBounds == null || parentBounds.isEmpty) {
            // If no bounds are set, use the full client bounds
            val dim = client.realDimensions
            parentBounds = Rectangle(0, 0, dim.width, dim.height)
        }

        // Constrain overlay position to be within the parent bounds
        return java.awt.Point(
                Ints.constrainToRange(overlayX, parentBounds.x,
                        Math.max(parentBounds.x, parentBounds.width - overlayWidth)),
                Ints.constrainToRange(overlayY, parentBounds.y,
                        Math.max(parentBounds.y, parentBounds.height - overlayHeight))
        )
    }

    private fun createRightClickMenuEntries(overlay: Overlay): Array<MenuEntry>? {
        val menuEntries: List<OverlayMenuEntry> = overlay.menuEntries
        if (menuEntries.isEmpty()) {
            return null
        }
        val entries = ArrayList<MenuEntry>(menuEntries.size)

        // Add in reverse order so they display correctly in the right-click menu
        for (i in menuEntries.indices.reversed()) {
            val overlayMenuEntry = menuEntries[i]
            val entry = MenuEntry()
            entry.option = overlayMenuEntry.option
            entry.target = meteor.util.ColorUtil.wrapWithColorTag(overlayMenuEntry.target, JagexColors.MENU_TARGET)
            entry.type = overlayMenuEntry.menuAction!!.id
            entry.identifier = overlayManager.overlays.indexOf(overlay) // overlay id
            entries[i] = entry
        }
        return entries.toArray() as Array<MenuEntry>?
    }

    fun renderAfterInterface(graphics: Graphics2D, interfaceId: Int,
                             widgetItems: Collection<WidgetItem>) {
        val overlays = overlayManager.getForInterface(interfaceId)
        overlayManager.widgetItems = widgetItems
        renderOverlays(graphics, overlays, OverlayLayer.ABOVE_WIDGETS)
        overlayManager.widgetItems = emptyList()
    }

    private fun renderOverlays(graphics: Graphics2D, overlays: Collection<Overlay>?,
                               layer: OverlayLayer) {
        if ((overlays == null) || overlays.isEmpty()
                || (client.gameState != GameState.LOGGED_IN)) {
            return
        }
        setGraphicProperties(graphics)

        // Draw snap corners
        if (inOverlayDraggingMode && layer == OverlayLayer.UNDER_WIDGETS && currentManagedOverlay != null && currentManagedOverlay.position != OverlayPosition.DETACHED) {
            val translatedSnapCorners: OverlayBounds = snapCorners!!.translated(
                    -SNAP_CORNER_SIZE.width,
                    -SNAP_CORNER_SIZE.height)
            val previous = graphics.color
            for (corner in translatedSnapCorners.bounds) {
                graphics.setColor(
                        if (corner.contains(mousePosition)) SNAP_CORNER_ACTIVE_COLOR else SNAP_CORNER_COLOR)
                graphics.fill(corner)
            }
            graphics.color = previous
        }

        // Get mouse position
        val mouseCanvasPosition: net.runelite.api.Point = client.mouseCanvasPosition
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
                safeRender(client, overlay, layer, graphics, Point())

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
                if (overlayPosition != OverlayPosition.DETACHED && snapCorners != null) {
                    val snapCorner: Rectangle = snapCorners!!.forPosition(overlayPosition)
                    val translation: java.awt.Point = transformPosition(overlayPosition,
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
                    val padding: java.awt.Point = padPosition(overlayPosition, dimension,
                            PADDING) // overlay size + fixed padding
                    // translate corner for padding and any difference due to the position clamping
                    snapCorner.translate(padding.x + dX, padding.y + dY)
                } else {
                    location = preferredLocation ?: bounds.location

                    // Clamp the overlay position to ensure it is on screen or within parent bounds
                    location = clampOverlayLocation(location.x, location.y, dimension.width, dimension.height,
                            overlay)
                }
                bounds.size = overlay.preferredSize
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
                            boundsColor = MOVING_OVERLAY_RESIZING_COLOR
                        } else if (inOverlayDraggingMode && currentManagedOverlay === overlay) {
                            boundsColor = MOVING_OVERLAY_ACTIVE_COLOR
                        } else if (inOverlayDraggingMode && overlay.dragTargetable
                                && currentManagedOverlay!!.dragTargetable
                                && currentManagedOverlay.bounds.intersects(bounds)) {
                            boundsColor = MOVING_OVERLAY_TARGET_COLOR
                            assert(currentManagedOverlay !== overlay)
                            dragTargetOverlay = overlay
                        } else {
                            boundsColor = MOVING_OVERLAY_COLOR
                        }
                        graphics.color = boundsColor
                        graphics.draw(bounds)
                        graphics.paint = paint
                    }
                    if (!client.isMenuOpen && !client.spellSelected && bounds.contains(mouse)) {
                        if (menuEntries == null) {
                            menuEntries = createRightClickMenuEntries(overlay)
                        }
                        if (focusedOverlay == null) {
                            focusedOverlay = overlay
                            if (focusedOverlay !== prevFocusedOverlay) {
                                prevFocusedOverlay?.onMouseExit()
                                overlay.onMouseEnter()
                            }
                        }
                        overlay.onMouseOver()
                    }
                }
            }
        }
    }
    private fun safeRender(client: Client, overlay: Overlay, layer: OverlayLayer, graphics: Graphics2D,
                           point: java.awt.Point) {
        if (!isResizeable && (layer == OverlayLayer.ABOVE_SCENE
                        || layer == OverlayLayer.UNDER_WIDGETS)) {
            graphics.setClip(client.viewportXOffset,
                    client.viewportYOffset,
                    client.viewportWidth,
                    client.viewportHeight)
        } else {
            graphics.setClip(0, 0, client.canvasWidth, client.canvasHeight)
        }
        val position: OverlayPosition = overlay.position

        // FIXME
        // Set font based on configuration
        graphics.font = fontManager.runescapeSmallFont
        graphics.translate(point.x, point.y)
        overlay.bounds.location = point
        val overlayDimension: Dimension? = try {
            overlay.render(graphics)
        } catch (ex: Exception) {
            ex.printStackTrace()
            return
        }
        val dimension = MoreObjects.firstNonNull(overlayDimension, Dimension())
        overlay.bounds.size = dimension
    }
}