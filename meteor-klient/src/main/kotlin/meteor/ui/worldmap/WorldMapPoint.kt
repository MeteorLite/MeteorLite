/*
 * Copyright (c) 2018, Morgan Lewis <https://github.com/MESLewis>
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
package meteor.ui.worldmap

import meteor.plugins.worldmap.FishingSpotLocation
import meteor.plugins.worldmap.MapPoint
import net.runelite.api.Point
import net.runelite.api.coords.WorldPoint
import java.awt.image.BufferedImage


class WorldMapPoint private constructor(builder: Builder) {
    var fishingPoint: FishingSpotLocation
    val type: MapPoint.Type
    val target: WorldPoint
    var image: BufferedImage
    var imagePoint: Point? = null
    var snapToEdge = false
    var currentlyEdgeSnapped = false
    var jumpOnClick = false
    var name: String? = null
    var tooltip: String? = null
    var worldPoint: WorldPoint
    fun onEdgeSnap() {}
    fun onEdgeUnsnap() {}

    init {
        this.fishingPoint = builder.fishingPoint
        this.type = builder.type
        this.target = builder.target
        this.imagePoint = builder.imagePoint
        this.snapToEdge = builder.snapToEdge
        this.currentlyEdgeSnapped = builder.currentlyEdgeSnapped
        this.jumpOnClick = builder.jumpOnClick
        this.name = builder.name
        this.tooltip = builder.tooltip
        this.worldPoint = builder.worldPoint
        this.image = builder.image


    }

    open class Builder {
        lateinit var fishingPoint: FishingSpotLocation
            private set
        lateinit var type: MapPoint.Type
            private set
        lateinit var target: WorldPoint
            private set
        lateinit var imagePoint: Point
            private set
        lateinit var image: BufferedImage
            private set
        var snapToEdge: Boolean = false
            private set
        var currentlyEdgeSnapped: Boolean = false
            private set
        var jumpOnClick = false
            private set
        var name: String? = null
            private set
        var tooltip: String? = null
            private set
        lateinit var worldPoint: WorldPoint
            private set

        open fun type(type: MapPoint.Type) = apply { this.type = type }
        open fun target(target: WorldPoint?) = apply {
            if (target != null) {
                this.target = target
            }
        }

        open fun image(image: BufferedImage?) = apply { this.image = image!! }
        fun imagePoint(point: Point) = apply { this.imagePoint = point }
        fun snapToEdge(snapToEdge: Boolean) = apply { this.snapToEdge = snapToEdge }
        fun currentlyEdgeSnapped(currentlyEdgeSnapped: Boolean) =
            apply { this.currentlyEdgeSnapped = currentlyEdgeSnapped }

        fun jumpOnClick(jumpOnClick: Boolean) = apply { this.jumpOnClick = jumpOnClick }
        fun name(name: String) = apply { this.name = name }
        fun tooltip(tooltip: String?) = apply { this.tooltip = tooltip }
        fun build() = WorldMapPoint(this)
        fun worldPoint(location: WorldPoint?) = apply {
            this.worldPoint = worldPoint
        }

        fun fishingPoint(location: FishingSpotLocation) = apply {
            this.fishingPoint = location
        }
    }

}