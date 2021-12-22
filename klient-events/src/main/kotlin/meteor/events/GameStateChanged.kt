package meteor.events

import meteor.Event
import net.runelite.api.GameState

class GameStateChanged(old: GameState?, new: GameState): Event() {
    val old: GameState
    val new: GameState
    init {
        if (old == null)
            this.old = GameState.UNKNOWN
        else
            this.old = old
        this.new = new
    }
}