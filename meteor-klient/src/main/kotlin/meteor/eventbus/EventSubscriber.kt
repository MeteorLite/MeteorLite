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

    open fun onGameStateChanged() : ((Event) -> Unit)? = stub()
    open fun onInteractingChanged() : ((Event) -> Unit)? = stub()
    open fun onNPCSpawned() : ((Event) -> Unit)? = stub()
    open fun onNPCDespawned() : ((Event) -> Unit)? = stub()
    open fun onGameObjectSpawned() : ((Event) -> Unit)? = stub()
    open fun onGameObjectChanged() : ((Event) -> Unit)? = stub()
    open fun onGameObjectDespawned(): ((Event) -> Unit)? = stub()
    open fun onGroundObjectSpawned(): ((Event) -> Unit)? = stub()
    open fun onGroundObjectChanged(): ((Event) -> Unit)? = stub()
    open fun onGroundObjectDespawned(): ((Event) -> Unit)? = stub()
    open fun onWallObjectSpawned(): ((Event) -> Unit)? = stub()
    open fun onWallObjectChanged(): ((Event) -> Unit)? = stub()
    open fun onWallObjectDespawned(): ((Event) -> Unit)? = stub()
    open fun onDecorativeObjectSpawned(): ((Event) -> Unit)? = stub()
    open fun onDecorativeObjectChanged(): ((Event) -> Unit)? = stub()
    open fun onDecorativeObjectDespawned(): ((Event) -> Unit)? = stub()
    open fun onItemSpawned(): ((Event) -> Unit)? = stub()
    open fun onItemDespawned(): ((Event) -> Unit)? = stub()
    open fun onNpcSpawned(): ((Event) -> Unit)? = stub()
    open fun onNpcChanged(): ((Event) -> Unit)? = stub()
    open fun onNpcDespawned(): ((Event) -> Unit)? = stub()
    open fun onProjectileMoved(): ((Event) -> Unit)? = stub()
    open fun onConfigChanged(): ((Event) -> Unit)? = stub()
    
    private fun stub(): ((Event) -> Unit)? {
        return null
    }

    companion object {
        private fun registerSubscriber(type: Class<out Event>, execution: ((Event) -> Unit)?) {
            execution?.also { EventBus.subscribe(type, execution) }
        }
    }
}