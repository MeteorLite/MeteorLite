import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import meteor.*
import meteor.Event
import meteor.config.ConfigManager
import meteor.eventbus.EventBus
import meteor.eventbus.events.GameStateChanged
import meteor.eventbus.events.GameTick
import meteor.plugins.PluginManager
import meteor.plugins.gpu.GpuPlugin
import meteor.rs.Applet
import meteor.rs.AppletConfiguration
import meteor.ui.Components
import meteor.ui.OverlayManager
import meteor.ui.UI
import meteor.ui.overlay.Overlay
import meteor.ui.overlay.OverlayLayer
import meteor.ui.themes.MeteorliteTheme
import net.runelite.api.Client
import net.runelite.api.GameState
import net.runelite.api.hooks.Callbacks
import okhttp3.OkHttpClient
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.context.startKoin
import rs117.hd.GpuHDPlugin
import java.awt.*

object Main: KoinComponent {
    lateinit var client: Client
    lateinit var callbacks: Callbacks
    val httpClient = OkHttpClient()
    val overlayManager = OverlayManager
    val fontManager = FontManager
    val itemManager = ItemManager
    var gpuLoaded = false

    @JvmStatic
    fun main(args: Array<String>) = application {
        startKoin { modules(Module.CLIENT_MODULE) }
        callbacks = get()
        MeteorliteTheme.install()
        EventBus.subscribe(GameTick::class.java) {
            if (client.gameDrawingMode != 2)
                client.gameDrawingMode = 2
        }
        gpuHDSceneFux()
        AppletConfiguration.init()
        Applet().init()
        Window(
            onCloseRequest = ::exitApplication,
            title = "Meteor",
            icon = painterResource("Meteor_icon.png"),
            state = rememberWindowState(width = 1280.dp, height = 720.dp),
            content = UI.Window()
        )
    }

    fun finishStartup() {
        client = Applet.asClient(Applet.applet)
        client.callbacks = callbacks
        ConfigManager.loadSavedProperties()
        PluginManager.startPlugins()
    }

    fun gpuHDSceneFux() {
        EventBus.subscribe(GameStateChanged::class.java) { it as GameStateChanged
            PluginManager.getPlugin<GpuHDPlugin>()!!.onGameStateChanged(it)
        }
    }
}

object TestOverlay : Overlay(layer = OverlayLayer.ABOVE_SCENE) {
    override fun render(graphics: Graphics2D): Dimension? {
        graphics.color = Color.CYAN
        val s = "Meteor Klient rendering overlays!"
        val fm = graphics.fontMetrics
        if (UI.toolbarPosition.value == Components.Toolbar.Position.TOP)
        graphics.drawString(s, 0, fm.getStringBounds(s, graphics).height.toInt())
        return null
    }
}