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

import meteor.plugins.Plugin
import meteor.ui.components.LayoutableRenderableEntity
import net.runelite.api.widgets.WidgetInfo
import java.awt.Dimension
import java.awt.Point
import java.awt.Rectangle
import java.util.ArrayList

abstract class Overlay : LayoutableRenderableEntity {
    private val plugin: Plugin?
    val drawHooks: MutableList<Int> = ArrayList()
    private val menuEntries: List<OverlayMenuEntry> = ArrayList<OverlayMenuEntry>()
    val preferredLocation: Point? = null
    val preferredSize: Dimension? = null
    var preferredPosition: OverlayPosition? = null
    override val bounds = Rectangle()
    var position = OverlayPosition.TOP_LEFT
    var priority: OverlayPriority = OverlayPriority.NONE
    var layer: OverlayLayer = OverlayLayer.UNDER_WIDGETS
    var resizable = false
    private val minimumSize = 32
    private val resettable = true

    /**
     * Whether this overlay can be dragged onto other overlays &amp; have other overlays dragged onto
     * it.
     */
    private val dragTargetable = false

    protected constructor() {
        plugin = null
    }

    protected constructor(plugin: Plugin?) {
        this.plugin = plugin
    }

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

    private val parentBounds: Rectangle?
        get() = null
}