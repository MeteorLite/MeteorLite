package meteor.plugins.fishing

import meteor.eventbus.EventBus
import meteor.eventbus.events.GameStateChanged
import meteor.eventbus.events.InteractingChanged
import meteor.eventbus.events.NpcDespawned
import meteor.eventbus.events.NpcSpawned
import meteor.plugins.Plugin
import meteor.ui.overlay.Overlay
import net.runelite.api.Actor
import net.runelite.api.GameState
import net.runelite.api.NPC

class FishingPlugin: Plugin() {
    override var overlay = FishingSpotOverlay(this) as Overlay?
    val fishingSpots: ArrayList<NPC> = ArrayList()
    private var currentSpot: FishingSpot? = null

    init {
        EventBus.subscribe {
            when (it) {
                is GameStateChanged -> onGameStateChanged(it)
                is InteractingChanged -> onInteractingChanged(it)
                is NpcSpawned -> onNPCSpawned(it)
                is NpcDespawned -> onNPCDespawned(it)
            }
        }
    }

    private fun onGameStateChanged(event: GameStateChanged) {
        when (event.new) {
            GameState.CONNECTION_LOST,
            GameState.LOGIN_SCREEN,
            GameState.HOPPING -> {
                fishingSpots.clear()
            }
        }
    }

    private fun onInteractingChanged(event: InteractingChanged) {
        if (event.source != client.localPlayer) {
            return
        }

        val target: Actor = event.target as? NPC ?: return

        val npc = target as NPC
        val spot: FishingSpot = FishingSpot.findSpot(npc.id) ?: return

        currentSpot = spot
    }

    private fun onNPCSpawned(event: NpcSpawned) {
        if (FishingSpot.findSpot(event.npc.id) == null) {
            return
        }

        fishingSpots.add(event.npc)
    }

    private fun onNPCDespawned(event: NpcDespawned) {
        if (FishingSpot.findSpot(event.npc.id) == null) {
            return
        }

        fishingSpots.remove(event.npc)
    }
}