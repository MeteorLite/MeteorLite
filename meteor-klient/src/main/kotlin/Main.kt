import androidx.compose.material.*
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import meteor.*
import meteor.eventbus.EventBus
import meteor.events.GameStateChanged
import meteor.events.GameTick
import net.runelite.api.Client
import net.runelite.api.hooks.Callbacks
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.context.startKoin
import themes.MeteorliteTheme
import javax.swing.JPanel

object Main: KoinComponent {
    lateinit var client: Client
    private lateinit var callbacks: Callbacks
    var toolbar: JPanel? = null

    @JvmStatic
    fun main(args: Array<String>) = application {
        val scaffoldState = rememberScaffoldState()
        val scope = rememberCoroutineScope()

        startKoin { modules(Module.CLIENT_MODULE) }
        callbacks = get()
        MeteorliteTheme.install()
        EventBus.subscribe(onEvent())
        AppletConfiguration.init()
        Applet().init()
        Window(
                onCloseRequest = ::exitApplication,
                title = "Meteor Klient",
                state = rememberWindowState(width = 1280.dp, height = 720.dp),
                content = UI.Window()
        )
        client = Applet.asClient(Applet.applet)
        client.callbacks = callbacks
        client.gameDrawingMode = 2
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
