import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import meteor.*
import meteor.Event
import meteor.eventbus.EventBus
import meteor.eventbus.events.GameStateChanged
import meteor.eventbus.events.GameTick
import meteor.plugins.PluginManager
import meteor.rs.Applet
import meteor.rs.AppletConfiguration
import meteor.ui.OverlayManager
import meteor.ui.OverlayRenderer
import meteor.ui.Toolbar
import meteor.ui.UI
import meteor.ui.overlay.Overlay
import meteor.ui.overlay.OverlayLayer
import net.runelite.api.Client
import net.runelite.api.hooks.Callbacks
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.context.startKoin
import themes.MeteorliteTheme
import java.awt.*

object Main: KoinComponent {
    lateinit var client: Client
    private lateinit var callbacks: Callbacks
    val overlayManager = OverlayManager
    val overlayRenderer = OverlayRenderer
    val fontManager = FontManager

    @JvmStatic
    fun main(args: Array<String>) = application {
        startKoin { modules(Module.CLIENT_MODULE) }
        callbacks = get()
        MeteorliteTheme.install()
        EventBus.subscribe(onEvent())
        AppletConfiguration.init()
        Applet().init()
        Window(
                onCloseRequest = ::exitApplication,
                title = "Meteor",
                state = rememberWindowState(width = 1280.dp, height = 720.dp),
                content = UI.Window()
        )
        client = Applet.asClient(Applet.applet)
        client.callbacks = callbacks
        client.gameDrawingMode = 2
        overlayManager.add(TestOverlay)
        PluginManager.startPlugins()
    }

    private fun onEvent(): (Event) -> Unit {
        return {
            if (it is GameTick) {
                println("Game Tick")
            }
            if (it is GameStateChanged) {
                println("GameStateChanged: ${it.new}")
            }
        }
    }
}

object TestOverlay : Overlay() {
    override fun render(graphics: Graphics2D): Dimension? {
        graphics.color = Color.CYAN
        if (UI.toolbarPosition.value == Toolbar.Position.TOP)
        graphics.drawString("Meteor Klient rendering overlays!", 0, 10)
        return null
    }
}