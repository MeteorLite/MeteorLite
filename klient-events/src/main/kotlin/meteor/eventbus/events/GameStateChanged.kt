package meteor.eventbus.events

import meteor.Event
import net.runelite.api.GameState

class GameStateChanged(val old: GameState? = GameState.UNKNOWN, val new: GameState): Event()