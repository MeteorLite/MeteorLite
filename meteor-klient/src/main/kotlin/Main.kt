import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import meteor.*
import meteor.config.ConfigManager
import meteor.eventbus.EventBus
import meteor.eventbus.events.GameTick
import meteor.plugins.PluginManager
import meteor.rs.Applet
import meteor.rs.AppletConfiguration
import meteor.ui.OverlayManager
import meteor.ui.UI
import meteor.ui.themes.MeteorliteTheme
import meteor.util.ExecutorServiceExceptionLogger
import net.runelite.api.Client
import net.runelite.api.hooks.Callbacks
import okhttp3.OkHttpClient
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.context.startKoin
import java.util.concurrent.Executors

object Main: KoinComponent {
    lateinit var client: Client
    lateinit var callbacks: Callbacks
    val httpClient = OkHttpClient()
    val overlayManager = OverlayManager
    val fontManager = FontManager
    val itemManager = ItemManager
    val executor = ExecutorServiceExceptionLogger(Executors.newSingleThreadScheduledExecutor())

    @JvmStatic
    fun main(args: Array<String>) = application {
        processArguments(args)
        startKoin { modules(Module.CLIENT_MODULE) }
        callbacks = get()
        MeteorliteTheme.install()
        EventBus.subscribe(GameTick::class.java) {
            if (client.gameDrawingMode != 2)
                client.gameDrawingMode = 2
        }
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
        PluginManager
    }

    fun processArguments(args: Array<String>) {
        for(arg in args) {
            when (arg.lowercase()) {
                "disableGPU".lowercase() -> {
                    Configuration.allowGPU = false
                }
            }
        }
    }
}