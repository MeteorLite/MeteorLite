import meteor.*
import meteor.Event
import meteor.config.ConfigManager
import meteor.eventbus.EventBus
import meteor.eventbus.events.GameStateChanged
import meteor.plugins.PluginManager
import meteor.rs.Applet
import meteor.rs.AppletConfiguration
import meteor.ui.Components
import meteor.ui.OverlayManager
import meteor.ui.UI
import meteor.ui.overlay.Overlay
import meteor.ui.overlay.OverlayLayer
import net.runelite.api.Client
import net.runelite.api.hooks.Callbacks
import okhttp3.OkHttpClient
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.context.startKoin
import java.awt.*

object Main: KoinComponent {
    lateinit var client: Client
    lateinit var callbacks: Callbacks
    val httpClient = OkHttpClient()
    val overlayManager = OverlayManager
    val fontManager = FontManager
    val itemManager = ItemManager

    @JvmStatic
    fun main(args: Array<String>) {
        startKoin { modules(Module.CLIENT_MODULE) }
        callbacks = get()
        //MeteorliteTheme.install()
        EventBus.subscribe(GameStateChanged::class.java, onEvent())
        AppletConfiguration.init()
        Applet().init()
        /*       Window(
                       onCloseRequest = ::exitApplication,
                       title = "Meteor",
                       icon = painterResource("Meteor_icon.png"),
                       state = rememberWindowState(width = 1280.dp, height = 720.dp),
                       content = UI.Window()
               )*/
    }

    fun finishStartup() {
        overlayManager.add(TestOverlay)
        ConfigManager.loadSavedProperties()
        PluginManager.startPlugins()
    }

    private fun onEvent(): (Event) -> Unit  = {
        it as GameStateChanged
        println("GameStateChanged: ${it.new}")
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