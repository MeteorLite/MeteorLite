package meteor.plugins.agility

import meteor.Event
import meteor.eventbus.events.*

import meteor.plugins.Plugin
import meteor.plugins.PluginDescriptor
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
class AgilityPlugin : Plugin() {
    var overlay = overlay<AgilityOverlay>(AgilityOverlay(this))
    val obstacles: MutableMap<TileObject, Obstacle> = HashMap()
    var marks: MutableList<Tile> = ArrayList()

    init {
        registerSubscribers()
    }

    override fun onGameStateChanged(): ((Any) -> Unit) =  { it as GameStateChanged
        if (it.new == GameState.LOADING) {
            marks.clear()
            obstacles.clear()
        }
    }

    override fun onGameObjectSpawned(): ((Any) -> Unit) =  { it as GameObjectSpawned
        onTileObject(it.tile, null, it.gameObject)
    }

    override fun onGameObjectChanged(): ((Any) -> Unit) =  { it as GameObjectChanged
        onTileObject(it.tile, it.oldObject, it.newObject)
    }

    override fun onGameObjectDespawned(): ((Any) -> Unit) =  { it as GameObjectDespawned
        onTileObject(it.tile, it.gameObject, null)
    }

    override fun onGroundObjectSpawned(): ((Any) -> Unit) =  { it as GroundObjectSpawned
        onTileObject(it.tile, null, it.groundObject)
    }

    override fun onGroundObjectChanged(): ((Any) -> Unit) =  { it as GroundObjectChanged
        onTileObject(it.tile, it.previous, it.groundObject)
    }

    override fun onGroundObjectDespawned(): ((Any) -> Unit) =  { it as GroundObjectDespawned
        onTileObject(it.tile, it.groundObject, null)
    }

    override fun onWallObjectSpawned(): ((Any) -> Unit) =  { it as WallObjectSpawned
        onTileObject(it.tile, null, it.wallObject)
    }

    override fun onWallObjectChanged(): ((Any) -> Unit) =  { it as WallObjectChanged
        onTileObject(it.tile, it.previous, it.wallObject)
    }

    override fun onWallObjectDespawned(): ((Any) -> Unit) =  { it as WallObjectDespawned
        onTileObject(it.tile, it.wallObject, null)
    }

    override fun onDecorativeObjectSpawned(): ((Any) -> Unit) =  { it as DecorativeObjectSpawned
        onTileObject(it.tile, null, it.decorativeObject)
    }

    override fun onDecorativeObjectChanged(): ((Any) -> Unit) =  { it as DecorativeObjectChanged
        onTileObject(it.tile, it.previous, it.decorativeObject)
    }

    override fun onDecorativeObjectDespawned(): ((Any) -> Unit) =  { it as DecorativeObjectDespawned
        onTileObject(it.tile, it.decorativeObject, null)
    }

    override fun onItemSpawned(): ((Any) -> Unit) =  { it as ItemSpawned
        if (it.item.id == ItemID.MARK_OF_GRACE) {
            marks.add(it.tile)
        }
    }

    override fun onItemDespawned(): ((Any) -> Unit) =  { it as ItemDespawned
        if (it.item.id == ItemID.MARK_OF_GRACE) {
            marks.remove(it.tile)
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