package meteor.plugins.agility

import com.google.common.eventbus.Subscribe

import meteor.plugins.Plugin
import meteor.plugins.PluginDescriptor
import meteor.ui.OverlayManager
import meteor.ui.overlay.OverlayLayer
import net.runelite.api.GameState
import net.runelite.api.ItemID
import net.runelite.api.Tile
import net.runelite.api.TileObject
import net.runelite.api.events.*
import java.util.ArrayList
import javax.inject.Inject

@Suppress("UnstableApiUsage")
@PluginDescriptor(
    name = "Agility",
    description = "Show helpful information about agility courses and obstacles",
    enabledByDefault = true,

)
class AgilityPlugin : Plugin() {
    val obstacles: MutableMap<TileObject, Obstacle> = HashMap()
    var marks: MutableList<Tile> = ArrayList()

    @Inject
     lateinit var om: OverlayManager

    @Inject
    lateinit var agilityoverlay: AgilityOverlay
    fun startup() {
        agilityoverlay.layer = OverlayLayer.ABOVE_SCENE
        om.add(agilityoverlay)
    }

    @Subscribe
    fun onGameStateChanged(event: GameStateChanged) {
        if (event.gameState == GameState.LOADING) {
            marks.clear()
            obstacles.clear()
        }
    }

    @Subscribe
    fun onGameObjectSpawned(event: GameObjectSpawned) {
        onTileObject(event.tile, null, event.gameObject)
    }

    @Subscribe
    fun onGameObjectChanged(event: GameObjectChanged) {
        onTileObject(event.tile, event.oldObject, event.newObject)
    }

    @Subscribe
    fun onGameObjectDespawned(event: GameObjectDespawned) {
        onTileObject(event.tile, event.gameObject, null)
    }

    @Subscribe
    fun onGroundObjectSpawned(event: GroundObjectSpawned) {
        onTileObject(event.tile, null, event.groundObject)
    }

    @Subscribe
    fun onGroundObjectChanged(event: GroundObjectChanged) {
        onTileObject(event.tile, event.previous, event.groundObject)
    }

    @Subscribe
    fun onGroundObjectDespawned(event: GroundObjectDespawned) {
        onTileObject(event.tile, event.groundObject, null)
    }

    @Subscribe
    fun onWallObjectSpawned(event: WallObjectSpawned) {
        onTileObject(event.tile, null, event.wallObject)
    }

    @Subscribe
    fun onWallObjectChanged(event: WallObjectChanged) {
        onTileObject(event.tile, event.previous, event.wallObject)
    }

    @Subscribe
    fun onWallObjectDespawned(event: WallObjectDespawned) {
        onTileObject(event.tile, event.wallObject, null)
    }

    @Subscribe
    fun onDecorativeObjectSpawned(event: DecorativeObjectSpawned) {
        onTileObject(event.tile, null, event.decorativeObject)
    }

    @Subscribe
    fun onDecorativeObjectChanged(event: DecorativeObjectChanged) {
        onTileObject(event.tile, event.previous, event.decorativeObject)
    }

    @Subscribe
    fun onDecorativeObjectDespawned(event: DecorativeObjectDespawned) {
        onTileObject(event.tile, event.decorativeObject, null)
    }

    @Subscribe
    fun onItemSpawned(event: ItemSpawned) {
        if (event.item.id == ItemID.MARK_OF_GRACE) {
            marks.add(event.tile)
        }
    }

    @Subscribe
    fun onItemDespawned(event: ItemDespawned) {
        if (event.item.id == ItemID.MARK_OF_GRACE) {
            marks.remove(event.tile)
        }
    }

    private fun onTileObject(tile: Tile, oldObject: TileObject?, newObject: TileObject?) {
        obstacles.remove(oldObject)
        if (newObject == null) {
            return
        }
        if (Obstacles.OBSTACLE_IDS.contains(newObject.id) ||
            Obstacles.PORTAL_OBSTACLE_IDS.contains(newObject.id) ||
            (Obstacles.TRAP_OBSTACLE_IDS.contains(newObject.id)
                    && Obstacles.TRAP_OBSTACLE_REGIONS.contains(newObject.worldLocation.regionID))
            ||
            Obstacles.SEPULCHRE_OBSTACLE_IDS.contains(newObject.id) ||
            Obstacles.SEPULCHRE_SKILL_OBSTACLE_IDS.contains(newObject.id)
        ) {
            obstacles[newObject] = Obstacle(tile, null)
        }
        if (Obstacles.SHORTCUT_OBSTACLE_IDS!!.containsKey(newObject.id)) {
            var closestShortcut: AgilityShortcut? = null
            var distance = -1

            // Find the closest shortcut to this object
            for (shortcut in Obstacles.SHORTCUT_OBSTACLE_IDS!![newObject.id]) {
                if (!shortcut!!.matches(newObject)) {
                    continue
                }
                if (shortcut.worldLocation == null) {
                    closestShortcut = shortcut
                    break
                } else {
                    val newDistance: Int = shortcut.worldLocation.distanceTo2D(newObject.worldLocation)
                    if (closestShortcut == null || newDistance < distance) {
                        closestShortcut = shortcut
                        distance = newDistance
                    }
                }
            }
            if (closestShortcut != null) {
                obstacles[newObject] = Obstacle(tile, closestShortcut)
            }
        }
    }
}