/*
 * Copyright (c) 2016-2017, Adam <Adam@sigterm.info>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package meteor.ui.overlay

import Main
import com.google.common.base.Strings
import meteor.plugins.Plugin
import meteor.ui.components.LayoutableRenderableEntity
import meteor.util.ColorUtil
import net.runelite.api.widgets.WidgetInfo
import java.awt.*

abstract class Overlay(plugin: Plugin? = null,
                       var layer: OverlayLayer = OverlayLayer.ABOVE_SCENE)
    : LayoutableRenderableEntity {
    val client = Main.client
    val drawHooks: MutableList<Int> = ArrayList()
    val menuEntries: List<OverlayMenuEntry> = ArrayList<OverlayMenuEntry>()
    override var preferredLocation: Point = Point(0,0)
    override var preferredSize: Dimension = Dimension(50,50)
    var preferredPosition= OverlayPosition.TOP_LEFT
    override val bounds = Rectangle()
    var position = OverlayPosition.TOP_LEFT
    var priority: OverlayPriority = OverlayPriority.NONE
    var resizable = false
    private val minimumSize = 32
    private val resettable = true

    /**
     * Whether this overlay can be dragged onto other overlays &amp; have other overlays dragged onto
     * it.
     */
    val dragTargetable = false

    /**
     * Overlay name, used for saving the overlay, needs to be unique
     *
     * @return overlay name
     */
    open val name: String
        get() = this.javaClass.simpleName

    protected fun drawAfterInterface(interfaceId: Int) {
        drawHooks.add(interfaceId shl 16 or 0xffff)
    }

    protected fun drawAfterLayer(layer: WidgetInfo) {
        drawHooks.add(layer.packedId)
    }

    fun onMouseOver() {}
    fun onMouseEnter() {}
    fun onMouseExit() {}

    fun onDrag(other: Overlay?): Boolean {
        return false
    }

    open var parentBounds = Rectangle(0,0,1920,1080)

    open fun renderPolygon(graphics: Graphics2D, poly: Shape, color: Color) {
        renderPolygon(graphics, poly, color, BasicStroke(2F))
    }

    open fun renderPolygon(graphics: Graphics2D, poly: Shape, color: Color,
                           borderStroke: Stroke) {
        graphics.color = color
        val originalStroke = graphics.stroke
        graphics.stroke = borderStroke
        graphics.draw(poly)
        graphics.color = Color(0, 0, 0, 50)
        graphics.fill(poly)
        graphics.stroke = originalStroke
    }

    open fun renderTextLocation(graphics: Graphics2D, txtString: String, fontSize: Int,
                                fontStyle: Int, fontColor: Color, canvasPoint: net.runelite.api.Point, shadows: Boolean, yOffset: Int) {
        graphics.font = Font("Arial", fontStyle, fontSize)
        val canvasCenterPoint = net.runelite.api.Point(
                canvasPoint.x,
                canvasPoint.y + yOffset)
        val canvasCenterPoint_shadow = net.runelite.api.Point(
                canvasPoint.x + 1,
                canvasPoint.y + 1 + yOffset)
        if (shadows) {
            renderTextLocation(graphics, canvasCenterPoint_shadow, txtString, Color.BLACK)
        }
        renderTextLocation(graphics, canvasCenterPoint, txtString, fontColor)
    }

    open fun renderTextLocation(graphics: Graphics2D, txtLoc: net.runelite.api.Point, text: String,
                                color: Color) {
        if (Strings.isNullOrEmpty(text)) {
            return
        }
        val x = txtLoc.x
        val y = txtLoc.y
        graphics.color = Color.BLACK
        graphics.drawString(text, x + 1, y + 1)
        graphics.color = ColorUtil.colorWithAlpha(color!!, 0xFF)
        graphics.drawString(text, x, y)
    }

}