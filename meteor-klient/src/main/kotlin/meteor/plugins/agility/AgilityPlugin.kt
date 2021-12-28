package meteor.plugins.agility

import com.google.common.eventbus.Subscribe
import meteor.eventbus.EventBus
import meteor.eventbus.events.*

import meteor.plugins.Plugin
import meteor.plugins.PluginDescriptor
import meteor.ui.overlay.Overlay
import net.runelite.api.GameState
import net.runelite.api.ItemID
import net.runelite.api.Tile
import net.runelite.api.TileObject
import java.util.ArrayList

@Suppress("UnstableApiUsage")
@PluginDescriptor(
    name = "Agility",
    description = "Show helpful information about agility courses and obstacles",
    enabledByDefault = true,

)
class AgilityPlugin() : Plugin() {
    override var overlay = AgilityOverlay(this) as Overlay?
    val obstacles: MutableMap<TileObject, Obstacle> = HashMap()
    var marks: MutableList<Tile> = ArrayList()

    init {
        EventBus.subscribe {
            when (it) {
                is GameStateChanged -> onGameStateChanged(it)
                is GameObjectSpawned -> onGameObjectSpawned(it)
                is GameObjectChanged -> onGameObjectChanged(it)
                is GameObjectDespawned -> onGameObjectDespawned(it)
                is GroundObjectSpawned -> onGroundObjectSpawned(it)
                is GroundObjectChanged -> onGroundObjectChanged(it)
                is GroundObjectDespawned -> onGroundObjectDespawned(it)
                is WallObjectSpawned -> onWallObjectSpawned(it)
                is WallObjectChanged -> onWallObjectChanged(it)
                is WallObjectDespawned -> onWallObjectDespawned(it)
                is DecorativeObjectSpawned -> onDecorativeObjectSpawned(it)
                is DecorativeObjectChanged -> onDecorativeObjectChanged(it)
                is DecorativeObjectDespawned -> onDecorativeObjectDespawned(it)
                is ItemSpawned -> onItemSpawned(it)
                is ItemDespawned -> onItemDespawned(it)
            }
        }
    }

    fun onGameStateChanged(event: GameStateChanged) {
        if (event.new == GameState.LOADING) {
            marks.clear()
            obstacles.clear()
        }
    }

    fun onGameObjectSpawned(event: GameObjectSpawned) {
        onTileObject(event.tile, null, event.gameObject)
    }

    fun onGameObjectChanged(event: GameObjectChanged) {
        onTileObject(event.tile, event.oldObject, event.newObject)
    }

    fun onGameObjectDespawned(event: GameObjectDespawned) {
        onTileObject(event.tile, event.gameObject, null)
    }

    fun onGroundObjectSpawned(event: GroundObjectSpawned) {
        onTileObject(event.tile, null, event.groundObject)
    }

    fun onGroundObjectChanged(event: GroundObjectChanged) {
        onTileObject(event.tile, event.previous, event.groundObject)
    }

    fun onGroundObjectDespawned(event: GroundObjectDespawned) {
        onTileObject(event.tile, event.groundObject, null)
    }

    fun onWallObjectSpawned(event: WallObjectSpawned) {
        onTileObject(event.tile, null, event.wallObject)
    }

    fun onWallObjectChanged(event: WallObjectChanged) {
        onTileObject(event.tile, event.previous, event.wallObject)
    }

    fun onWallObjectDespawned(event: WallObjectDespawned) {
        onTileObject(event.tile, event.wallObject, null)
    }

    fun onDecorativeObjectSpawned(event: DecorativeObjectSpawned) {
        onTileObject(event.tile, null, event.decorativeObject)
    }

    fun onDecorativeObjectChanged(event: DecorativeObjectChanged) {
        onTileObject(event.tile, event.previous, event.decorativeObject)
    }

    fun onDecorativeObjectDespawned(event: DecorativeObjectDespawned) {
        onTileObject(event.tile, event.decorativeObject, null)
    }

    fun onItemSpawned(event: ItemSpawned) {
        if (event.item.id == ItemID.MARK_OF_GRACE) {
            marks.add(event.tile)
        }
    }

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