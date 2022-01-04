package meteor.eventbus

import meteor.ItemManager.onPostItemComposition
import meteor.eventbus.events.*

open class EventSubscriber {
    fun registerSubscribers() {
        registerSubscriber(ActorDeath::class.java, onActorDeath())
        registerSubscriber(AnimationChanged::class.java, onAnimationChanged())
        registerSubscriber(AreaSoundEffectPlayed::class.java, onAreaSoundEffectPlayed())
        registerSubscriber(BeforeMenuRender::class.java, onBeforeMenuRender())
        registerSubscriber(CanvasSizeChanged::class.java, onCanvasSizeChanged())
        registerSubscriber(ChatMessage::class.java, onChatMessage())
        registerSubscriber(ClanChannelChanged::class.java, onClanChannelChanged())
        registerSubscriber(ClanMemberJoined::class.java, onClanMemberJoined())
        registerSubscriber(ClanMemberLeft::class.java, onClanMemberLeft())
        registerSubscriber(ClientLoaded::class.java, onClientLoaded())
        registerSubscriber(ClientTick::class.java, onClientTick())
        registerSubscriber(ConfigChanged::class.java, onConfigChanged())
        registerSubscriber(DecorativeObjectChanged::class.java, onDecorativeObjectChanged())
        registerSubscriber(DecorativeObjectDespawned::class.java, onDecorativeObjectDespawned())
        registerSubscriber(DecorativeObjectSpawned::class.java, onDecorativeObjectSpawned())
        registerSubscriber(DialogProcessed::class.java, onDialogProcessed())
        registerSubscriber(DynamicObjectAnimationChanged::class.java, onDynamicObjectAnimationChanged())
        registerSubscriber(ExperienceGained::class.java, onExperienceGained())
        registerSubscriber(FocusChanged::class.java, onFocusChanged())
        registerSubscriber(GameObjectChanged::class.java, onGameObjectChanged())
        registerSubscriber(GameObjectDespawned::class.java, onGameObjectDespawned())
        registerSubscriber(GameObjectSpawned::class.java, onGameObjectSpawned())
        registerSubscriber(GameStateChanged::class.java, onGameStateChanged())
        registerSubscriber(GameTick::class.java, onGameTick())
        registerSubscriber(GrandExchangeOfferChanged::class.java, onGrandExchangeOfferChanged())
        registerSubscriber(GraphicsObjectCreated::class.java, onGraphicsObjectCreated())
        registerSubscriber(GroundObjectChanged::class.java, onGroundObjectChanged())
        registerSubscriber(GroundObjectDespawned::class.java, onGroundObjectDespawned())
        registerSubscriber(GroundObjectSpawned::class.java, onGroundObjectSpawned())
        registerSubscriber(HealthBarUpdated::class.java, onHealthBarUpdated())
        registerSubscriber(InteractingChanged::class.java, onInteractingChanged())
        registerSubscriber(InventoryChanged::class.java, onInventoryChanged())
        registerSubscriber(InvokeMenuAction::class.java, onInvokeMenuAction())
        registerSubscriber(ItemContainerChanged::class.java, onItemContainerChanged())
        registerSubscriber(ItemDespawned::class.java, onItemDespawned())
        registerSubscriber(ItemObtained::class.java, onItemObtained())
        registerSubscriber(ItemQuantityChanged::class.java, onItemQuantityChanged())
        registerSubscriber(ItemSpawned::class.java, onItemSpawned())
        registerSubscriber(LoginStateChanged::class.java, onLoginStateChanged())
        registerSubscriber(MenuEntryAdded::class.java, onMenuEntryAdded())
        registerSubscriber(MenuOpened::class.java, onMenuOpened())
        registerSubscriber(MenuOptionClicked::class.java, onMenuOptionClicked())
        registerSubscriber(NpcActionChanged::class.java, onNpcActionChanged())
        registerSubscriber(NpcChanged::class.java, onNpcChanged())
        registerSubscriber(NpcDespawned::class.java, onNpcDespawned())
        registerSubscriber(NpcSpawned::class.java, onNpcSpawned())
        registerSubscriber(PacketSent::class.java, onPacketSent())
        registerSubscriber(OverheadPrayerChanged::class.java, onOverheadPrayerChanged())
        registerSubscriber(PlayerDespawned::class.java, onPlayerDespawned())
        registerSubscriber(PlayerMenuOptionsChanged::class.java, onPlayerMenuOptionsChanged())
        registerSubscriber(PlayerSkullChanged::class.java, onPlayerSkullChanged())
        registerSubscriber(PlayerSpawned::class.java, onPlayerSpawned())
        registerSubscriber(PostItemComposition::class.java, onPostItemComposition())
        registerSubscriber(ProjectileMoved::class.java, onProjectileMoved())
        registerSubscriber(ProjectileSpawned::class.java, onProjetileSpawned())
        registerSubscriber(ResizeableChanged::class.java, onResizeableChanged())
        registerSubscriber(ScriptCallbackEvent::class.java, onScriptCallbackEvent())
        registerSubscriber(ScriptPostFired::class.java, onScriptPostFired())
        registerSubscriber(ScriptPreFired::class.java, onScriptPreFired())
        registerSubscriber(SoundEffectPlayed::class.java, onSoundEffectPlayed())
        registerSubscriber(StatChanged::class.java, onStatChanged())
        registerSubscriber(VarbitChanged::class.java, onVarbitChanged())
        registerSubscriber(WallObjectChanged::class.java, onWallObjectChanged())
        registerSubscriber(WallObjectDespawned::class.java, onWallObjectDespawned())
        registerSubscriber(WallObjectSpawned::class.java, onWallObjectSpawned())
        registerSubscriber(WidgetHiddenChanged::class.java, onWidgetHiddenChanged())
        registerSubscriber(WidgetLoaded::class.java, onWidgetLoaded())
        registerSubscriber(WidgetPositioned::class.java, onWidgetPositioned())
        registerSubscriber(WidgetPressed::class.java, onWidgetPressed())
        registerSubscriber(WorldListLoad::class.java, onWorldListLoad())
    }

    open fun onWorldListLoad() = stub()
    open fun onWidgetPressed() = stub()
    open fun onWidgetPositioned() = stub()
    open fun onWidgetLoaded() = stub()
    open fun onWidgetHiddenChanged() = stub()
    open fun onVarbitChanged() = stub()
    open fun onSoundEffectPlayed() = stub()
    open fun onScriptPreFired() = stub()
    open fun onScriptPostFired() = stub()
    open fun onScriptCallbackEvent() = stub()
    open fun onResizeableChanged() = stub()
    open fun onProjetileSpawned() = stub()
    open fun onStatChanged() = stub()
    open fun onPlayerSpawned() = stub()
    open fun onPlayerSkullChanged() = stub()
    open fun onPlayerMenuOptionsChanged() = stub()
    open fun onPlayerDespawned() = stub()
    open fun onOverheadPrayerChanged() = stub()
    open fun onPacketSent() = stub()
    open fun onNpcActionChanged() = stub()
    open fun onMenuOptionClicked() = stub()
    open fun onMenuOpened() = stub()
    open fun onMenuEntryAdded() = stub()
    open fun onLoginStateChanged() = stub()
    open fun onItemQuantityChanged() = stub()
    open fun onItemObtained() = stub()
    open fun onItemContainerChanged() = stub()
    open fun onInvokeMenuAction() = stub()
    open fun onInventoryChanged() = stub()
    open fun onHealthBarUpdated() = stub()
    open fun onGraphicsObjectCreated() = stub()
    open fun onGrandExchangeOfferChanged() = stub()
    open fun onGameTick() = stub()
    open fun onFocusChanged() = stub()
    open fun onExperienceGained() = stub()
    open fun onDynamicObjectAnimationChanged() = stub()
    open fun onDialogProcessed() = stub()
    open fun onClientTick() = stub()
    open fun onClientLoaded() = stub()
    open fun onClanMemberLeft() = stub()
    open fun onClanMemberJoined() = stub()
    open fun onClanChannelChanged() = stub()
    open fun onChatMessage() = stub()
    open fun onCanvasSizeChanged() = stub()
    open fun onBeforeMenuRender() = stub()
    open fun onAreaSoundEffectPlayed() = stub()
    open fun onAnimationChanged() = stub()
    open fun onActorDeath() = stub()
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
    
    private fun stub(): ((Any) -> Unit)? { return null }

    companion object {
        private fun registerSubscriber(type: Any, execution: ((Any) -> Unit)?) {
            execution?.also { EventBus.subscribe(type, execution) }
        }
    }
}