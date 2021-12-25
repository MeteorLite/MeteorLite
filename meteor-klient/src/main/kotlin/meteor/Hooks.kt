package meteor

import Main.client
import Main.overlayRenderer
import meteor.eventbus.EventBus
import meteor.eventbus.events.GameStateChanged
import meteor.eventbus.events.GameTick
import meteor.input.KeyManager
import meteor.input.MouseManager
import meteor.util.RSTimeUnit
import net.runelite.api.BufferProvider
import net.runelite.api.GameState
import net.runelite.api.MainBufferProvider
import net.runelite.api.Renderable
import net.runelite.api.hooks.Callbacks
import net.runelite.api.hooks.DrawCallbacks
import net.runelite.api.widgets.Widget
import net.runelite.api.widgets.WidgetItem
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.event.KeyEvent
import java.awt.event.MouseEvent
import java.awt.event.MouseWheelEvent

class Hooks : Callbacks {
    private var lastCheck: Long = 0
    private val CHECK: Long = RSTimeUnit.GAME_TICKS.duration
            .toNanos()
    private val GAME_TICK = GameTick()
    private var shouldProcessGameTick = false
    private var ignoreNextNpcUpdate = false
    private var lastMainBufferProvider: MainBufferProvider? = null
    private var lastGraphics: Graphics2D? = null

    init {
        EventBus.subscribe(onEvent())
    }

    override fun post(obj: Any?) {
        if (obj is Event)
            EventBus.post(obj)
    }

    override fun postDeferred(event: Any?) {
        TODO("Not yet implemented")
    }

    override fun tick() {
        if (shouldProcessGameTick) {
            shouldProcessGameTick = false
            EventBus.post(GAME_TICK as Event)
        }

        val now = System.nanoTime()

        if (now - lastCheck < CHECK) {
            return
        }

        lastCheck = now
    }

    override fun frame() {
        TODO("Not yet implemented")
    }

    override fun updateNpcs() {
        if (ignoreNextNpcUpdate) {
            ignoreNextNpcUpdate = false
        } else {
            shouldProcessGameTick = true
        }
    }

    private fun onEvent(): (Event) -> Unit {
        return {
            if (it is GameStateChanged)
                when (it.new) {
                    GameState.LOGGING_IN, GameState.HOPPING -> {
                        ignoreNextNpcUpdate = true
                    }
                }
        }
    }

    override fun drawScene() {
        TODO("Not yet implemented")
    }

    override fun drawAboveOverheads() {
        TODO("Not yet implemented")
    }

    override fun draw(mainBufferProvider: MainBufferProvider?, graphics: Graphics?, x: Int, y: Int) {
        TODO("Not yet implemented")
    }

    override fun drawInterface(interfaceId: Int, widgetItems: MutableList<WidgetItem>?) {
        TODO("Not yet implemented")
    }

    override fun drawLayer(layer: Widget?, widgetItems: MutableList<WidgetItem>?) {
        val bufferProvider = client.bufferProvider as MainBufferProvider
        val graphics2d: Graphics2D = getGraphics(bufferProvider)

        try {
            overlayRenderer.renderAfterLayer(graphics2d, layer, widgetItems)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    private fun getGraphics(mainBufferProvider: MainBufferProvider): Graphics2D {
        if (lastGraphics == null || lastMainBufferProvider !== mainBufferProvider) {
            lastGraphics?.dispose()
            lastMainBufferProvider = mainBufferProvider
            lastGraphics = mainBufferProvider.image.graphics as Graphics2D
        }
        return lastGraphics as Graphics2D
    }

    override fun mousePressed(mouseEvent: MouseEvent): MouseEvent {
        return MouseManager.processMousePressed(mouseEvent)
    }

    override fun mouseReleased(mouseEvent: MouseEvent): MouseEvent {
        return MouseManager.processMouseReleased(mouseEvent)
    }

    override fun mouseClicked(mouseEvent: MouseEvent): MouseEvent {
        return MouseManager.processMouseClicked(mouseEvent)
    }

    override fun mouseEntered(mouseEvent: MouseEvent): MouseEvent {
        return MouseManager.processMouseEntered(mouseEvent)
    }

    override fun mouseExited(mouseEvent: MouseEvent): MouseEvent {
        return MouseManager.processMouseExited(mouseEvent)
    }

    override fun mouseDragged(mouseEvent: MouseEvent): MouseEvent {
        return MouseManager.processMouseDragged(mouseEvent)
    }

    override fun mouseMoved(mouseEvent: MouseEvent): MouseEvent {
        return MouseManager.processMouseMoved(mouseEvent)
    }

    override fun mouseWheelMoved(mouseEvent: MouseWheelEvent): MouseWheelEvent {
        return MouseManager.processMouseWheelMoved(mouseEvent)
    }

    override fun keyPressed(keyEvent: KeyEvent) {
        return KeyManager.processKeyPressed(keyEvent)
    }

    override fun keyReleased(keyEvent: KeyEvent) {
        return KeyManager.processKeyReleased(keyEvent)
    }

    override fun keyTyped(keyEvent: KeyEvent) {
        return KeyManager.processKeyTyped(keyEvent)
    }

    companion object {
        @JvmStatic
        fun clearColorBuffer(x: Int, y: Int, width: Int, height: Int, color: Int) {
            val bp: BufferProvider = client.bufferProvider
            val canvasWidth = bp.width
            val pixels = bp.pixels
            var pixelPos = y * canvasWidth + x
            val pixelJump = canvasWidth - width
            for (cy in y until y + height) {
                for (cx in x until x + width) {
                    pixels[pixelPos++] = 0
                }
                pixelPos += pixelJump
            }
        }

        @JvmStatic
        fun renderDraw(renderable: Renderable, orientation: Int, pitchSin: Int, pitchCos: Int,
                       yawSin: Int, yawCos: Int, x: Int, y: Int, z: Int, hash: Long) {
            val drawCallbacks: DrawCallbacks? = client.drawCallbacks
            if (drawCallbacks != null) {
                drawCallbacks
                        .draw(renderable, orientation, pitchSin, pitchCos, yawSin, yawCos, x, y, z, hash)
            } else {
                renderable.`draw$api`(orientation, pitchSin, pitchCos, yawSin, yawCos, x, y, z, hash)
            }
        }

        @JvmStatic
        fun drawMenu(): Boolean {
            val event = meteor.eventbus.events.BeforeMenuRender()
            client.callbacks.post(event)
            return event.consumed
        }
    }
}