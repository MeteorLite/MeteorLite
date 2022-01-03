/*
 * Copyright (c) 2018, Kamiel
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

import net.runelite.api.MenuEntry

/**
 * An event where a menu has been opened.
 */
class MenuOpened(var modified: Boolean = false, var menuEntries: Array<MenuEntry>) : Iterable<MenuEntry?> {

    /**
     * Gets the entry that will be displayed first in the menu.
     *
     * @return the first entry
     */
    val firstEntry: MenuEntry?
        get() = if (menuEntries.size > 0) {
            menuEntries[menuEntries.size - 1]
        } else null

    fun setModified() {
        modified = true
    }

    override fun iterator(): MutableIterator<MenuEntry> {
        return object : MutableIterator<MenuEntry> {
            var index = 0
            override fun hasNext(): Boolean {
                return index < menuEntries.size
            }

            override fun next(): MenuEntry {
                return menuEntries[index++]
            }

            override fun remove() {
                //TODO("Not yet implemented")
            }
        }
    }
}