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

    open fun onGameStateChanged() : ((Any) -> Unit)? = stub()
    open fun onInteractingChanged() : ((Any) -> Unit)? = stub()
    open fun onNPCSpawned() : ((Any) -> Unit)? = stub()
    open fun onNPCDespawned() : ((Any) -> Unit)? = stub()
    open fun onGameObjectSpawned() : ((Any) -> Unit)? = stub()
    open fun onGameObjectChanged() : ((Any) -> Unit)? = stub()
    open fun onGameObjectDespawned(): ((Any) -> Unit)? = stub()
    open fun onGroundObjectSpawned(): ((Any) -> Unit)? = stub()
    open fun onGroundObjectChanged(): ((Any) -> Unit)? = stub()
    open fun onGroundObjectDespawned(): ((Any) -> Unit)? = stub()
    open fun onWallObjectSpawned(): ((Any) -> Unit)? = stub()
    open fun onWallObjectChanged(): ((Any) -> Unit)? = stub()
    open fun onWallObjectDespawned(): ((Any) -> Unit)? = stub()
    open fun onDecorativeObjectSpawned(): ((Any) -> Unit)? = stub()
    open fun onDecorativeObjectChanged(): ((Any) -> Unit)? = stub()
    open fun onDecorativeObjectDespawned(): ((Any) -> Unit)? = stub()
    open fun onItemSpawned(): ((Any) -> Unit)? = stub()
    open fun onItemDespawned(): ((Any) -> Unit)? = stub()
    open fun onNpcSpawned(): ((Any) -> Unit)? = stub()
    open fun onNpcChanged(): ((Any) -> Unit)? = stub()
    open fun onNpcDespawned(): ((Any) -> Unit)? = stub()
    open fun onProjectileMoved(): ((Any) -> Unit)? = stub()
    open fun onConfigChanged(): ((Any) -> Unit)? = stub()
    
    private fun stub(): ((Any) -> Unit)? {
        return null
    }

    companion object {
        private fun registerSubscriber(type: Any, execution: ((Any) -> Unit)?) {
            execution?.also { EventBus.subscribe(type, execution) }
        }
    }
}