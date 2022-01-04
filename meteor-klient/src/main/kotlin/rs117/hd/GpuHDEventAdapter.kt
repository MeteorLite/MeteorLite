package rs117.hd

import meteor.eventbus.EventBus
import meteor.eventbus.events.*
import meteor.plugins.PluginManager

// This allows java plugins to interface with the new eventbus
object GpuHDEventAdapter {
    fun registerEvents() {
        val hdPlugin = PluginManager.getPlugin<GpuHDPlugin>()!!
        EventBus.subscribe(GameStateChanged::class.java) { hdPlugin.onGameStateChanged(it as GameStateChanged) }
        EventBus.subscribe(ConfigChanged::class.java) { hdPlugin.onConfigChanged(it as ConfigChanged) }
        EventBus.subscribe(ProjectileMoved::class.java) { hdPlugin.onProjectileMoved(it as ProjectileMoved) }
        EventBus.subscribe(NpcSpawned::class.java) { hdPlugin.onNpcSpawned(it as NpcSpawned) }
        EventBus.subscribe(NpcDespawned::class.java) { hdPlugin.onNpcDespawned(it as NpcDespawned) }
        EventBus.subscribe(NpcChanged::class.java) { hdPlugin.onNpcChanged(it as NpcChanged) }
        EventBus.subscribe(GameObjectSpawned::class.java) { hdPlugin.onGameObjectSpawned(it as GameObjectSpawned) }
        EventBus.subscribe(GameObjectChanged::class.java) { hdPlugin.onGameObjectChanged(it as GameObjectChanged) }
        EventBus.subscribe(GameObjectDespawned::class.java) { hdPlugin.onGameObjectDespawned(it as GameObjectDespawned) }
        EventBus.subscribe(WallObjectSpawned::class.java) { hdPlugin.onWallObjectSpawned(it as WallObjectSpawned) }
        EventBus.subscribe(WallObjectChanged::class.java) { hdPlugin.onWallObjectChanged(it as WallObjectChanged) }
        EventBus.subscribe(WallObjectDespawned::class.java) { hdPlugin.onWallObjectDespawned(it as WallObjectDespawned) }
        EventBus.subscribe(DecorativeObjectSpawned::class.java) { hdPlugin.onDecorativeObjectSpawned(it as DecorativeObjectSpawned) }
        EventBus.subscribe(DecorativeObjectChanged::class.java) { hdPlugin.onDecorativeObjectChanged(it as DecorativeObjectChanged) }
        EventBus.subscribe(DecorativeObjectDespawned::class.java) { hdPlugin.onDecorativeObjectDespawned(it as DecorativeObjectDespawned) }
        EventBus.subscribe(GroundObjectSpawned::class.java) { hdPlugin.onGroundObjectSpawned(it as GroundObjectSpawned) }
        EventBus.subscribe(GroundObjectChanged::class.java) { hdPlugin.onGroundObjectChanged(it as GroundObjectChanged) }
        EventBus.subscribe(GroundObjectDespawned::class.java) { hdPlugin.onGroundObjectDespawned(it as GroundObjectDespawned) }
        EventBus.subscribe(ItemSpawned::class.java) { hdPlugin.onItemSpawned(it as ItemSpawned) }
        EventBus.subscribe(ItemDespawned::class.java) { hdPlugin.onItemDespawned(it as ItemDespawned) }
    }
}