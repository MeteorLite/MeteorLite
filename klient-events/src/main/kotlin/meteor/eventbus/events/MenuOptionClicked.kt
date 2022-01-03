/*
 * Copyright (c) 2017, Robin <robin.weymans@gmail.com>
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
package meteor.eventbus.events

import meteor.Event
import net.runelite.api.MenuAction
import net.runelite.api.MenuEntry

/**
 * An event where a menu option has been clicked.
 *
 *
 * This event does not only trigger when clicking an option in a
 * right-clicked menu. The event will trigger for any left click
 * action performed (ie. clicking an item, walking to a tile, etc) as
 * well as any right-click menu option.
 *
 *
 * By default, when there is no action performed when left-clicking,
 * it seems that this event still triggers with the "Cancel" action.
 */
class MenuOptionClicked(var actionParam: Int, var menuOption: String?, var menuTarget: String, var menuAction: MenuAction,
                        var id: Int,var selectedItemIndex: Int, var canvasX: Int, var canvasY: Int) : Event() {


    private val consumed: Boolean
        get() = isConsumed

    /**
     * Action parameter 1. Its value depends on the menuAction.
     */
    @get:Deprecated("")
    @set:Deprecated("")
    var widgetId = 0


    /**
     * Whether or not the event has been consumed by a subscriber.
     */
    var isConsumed = false

    /**
     * Marks the event as having been consumed.
     *
     *
     * Setting this state indicates that a plugin has processed the menu
     * option being clicked and that the event will not be passed on
     * for handling by vanilla client code.
     */
    fun consume() {
        isConsumed = true
    }

    fun setMenuEntry(entry: MenuEntry) {
        this.menuOption = (entry.option)
        this.menuTarget = (entry.target)
        this.id = (entry.id)
        this.menuAction = (MenuAction.of(entry.opcode))
        this.canvasX = (entry.param0)
        this.canvasY = (entry.param1)
    }

    val isAutomated = false
}