import meteor.eventbus.EventBus
import meteor.eventbus.events.GameStateChanged
import meteor.plugins.PluginManager
import meteor.plugins.gpu.GpuPlugin

object GPUEventAdapter {
    fun registerEvents() {
        val gpuPlugin = PluginManager.getPlugin<GpuPlugin>()!!
        EventBus.subscribe(GameStateChanged::class.java) { gpuPlugin.onGameState(it as GameStateChanged) }
    }
}