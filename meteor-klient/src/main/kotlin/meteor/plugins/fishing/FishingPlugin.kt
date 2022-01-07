package meteor.plugins.fishing

import meteor.eventbus.events.GameStateChanged
import meteor.eventbus.events.InteractingChanged
import meteor.eventbus.events.NpcDespawned
import meteor.eventbus.events.NpcSpawned
import meteor.plugins.Plugin
import meteor.plugins.PluginDescriptor
import net.runelite.api.Actor
import net.runelite.api.GameState
import net.runelite.api.NPC

@PluginDescriptor("Fishing")
class FishingPlugin: Plugin() {
    val config = configuration<FishingConfig>()
    val spotsOverlay = overlay<FishingSpotOverlay>(FishingSpotOverlay(this, config))

    val fishingSpots: ArrayList<NPC> = ArrayList()
    val minnowSpots: HashMap<Int, MinnowSpot> = HashMap()
    var currentSpot: FishingSpot? = null

    override fun onGameStateChanged(): ((Any) -> Unit) = { it as GameStateChanged
        when (it.new) {
            GameState.CONNECTION_LOST,
            GameState.LOGIN_SCREEN,
            GameState.HOPPING -> {
                fishingSpots.clear()
                minnowSpots.clear()
            }
        }
    }

    override fun onInteractingChanged(): ((Any) -> Unit) = { it as InteractingChanged
        if (it.source == client.localPlayer) {
            if (it.target is NPC) {
                val target: Actor = it.target as NPC
                target as NPC
                val spot: FishingSpot? = FishingSpot.findSpot(target.id)

                spot.also { currentSpot = spot }
            }
        }
    }

    override fun onNPCSpawned(): ((Any) -> Unit) =  { it as NpcSpawned
        if (FishingSpot.findSpot(it.npc.id) != null) {
            fishingSpots.add(it.npc)
        }
    }

    override fun onNPCDespawned(): ((Any) -> Unit) =  { it as NpcDespawned
        if (FishingSpot.findSpot(it.npc.id) != null) {
            fishingSpots.remove(it.npc)
        }
    }
}