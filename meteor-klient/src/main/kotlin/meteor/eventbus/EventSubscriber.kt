package meteor.eventbus

import meteor.Event
import meteor.eventbus.events.*

open class EventSubscriber {

    fun registerSubscribers() {
        registerSubscriber(GameStateChanged::class.java, onGameStateChanged())
        registerSubscriber(InteractingChanged::class.java, onInteractingChanged())
        registerSubscriber(NpcSpawned::class.java, onNPCSpawned())
        registerSubscriber(NpcDespawned::class.java, onNPCDespawned())
        registerSubscriber(GameObjectSpawned::class.java, onGameObjectSpawned())
        registerSubscriber(GameObjectChanged::class.java, onGameObjectChanged())
        registerSubscriber(GameObjectDespawned::class.java, onGameObjectDespawned())
        registerSubscriber(GroundObjectSpawned::class.java, onGroundObjectSpawned())
        registerSubscriber(GroundObjectChanged::class.java, onGroundObjectChanged())
        registerSubscriber(GroundObjectDespawned::class.java, onGroundObjectDespawned())
        registerSubscriber(WallObjectSpawned::class.java, onWallObjectSpawned())
        registerSubscriber(WallObjectChanged::class.java, onWallObjectChanged())
        registerSubscriber(WallObjectDespawned::class.java, onWallObjectDespawned())
        registerSubscriber(DecorativeObjectSpawned::class.java, onDecorativeObjectSpawned())
        registerSubscriber(DecorativeObjectChanged::class.java, onDecorativeObjectChanged())
        registerSubscriber(DecorativeObjectDespawned::class.java, onDecorativeObjectDespawned())
        registerSubscriber(ItemSpawned::class.java, onItemSpawned())
        registerSubscriber(ItemDespawned::class.java, onItemDespawned())
        registerSubscriber(NpcSpawned::class.java, onNpcSpawned())
        registerSubscriber(NpcDespawned::class.java, onNpcDespawned())
        registerSubscriber(NpcChanged::class.java, onNpcChanged())
        registerSubscriber(ConfigChanged::class.java, onConfigChanged())
    }

    open fun onGameStateChanged() = stub()
    open fun onInteractingChanged() = stub()
    open fun onNPCSpawned() = stub()
    open fun onNPCDespawned() = stub()
    open fun onGameObjectSpawned() = stub()
    open fun onGameObjectChanged() = stub()
    open fun onGameObjectDespawned() = stub()
    open fun onGroundObjectSpawned() = stub()
    open fun onGroundObjectChanged() = stub()
    open fun onGroundObjectDespawned() = stub()
    open fun onWallObjectSpawned() = stub()
    open fun onWallObjectChanged() = stub()
    open fun onWallObjectDespawned() = stub()
    open fun onDecorativeObjectSpawned() = stub()
    open fun onDecorativeObjectChanged() = stub()
    open fun onDecorativeObjectDespawned() = stub()
    open fun onItemSpawned() = stub()
    open fun onItemDespawned() = stub()
    open fun onNpcSpawned() = stub()
    open fun onNpcChanged() = stub()
    open fun onNpcDespawned() = stub()
    open fun onProjectileMoved() = stub()
    open fun onConfigChanged() = stub()
    
    private fun stub(): ((Any) -> Unit)? {
        return null
    }

    companion object {
        private fun registerSubscriber(type: Any, execution: ((Any) -> Unit)?) {
            execution?.also { EventBus.subscribe(type, execution) }
        }
    }
}