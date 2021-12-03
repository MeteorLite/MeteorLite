package meteor

import meteor.Module.CLIENT_MODULE
import meteor.UI.Companion.applet
import meteor.eventbus.Event
import meteor.eventbus.EventBus
import meteor.events.AppletLoaded
import meteor.events.ClientLoaded
import net.runelite.api.Client
import net.runelite.api.hooks.Callbacks
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.component.inject
import org.koin.core.context.startKoin
import themes.MeteorliteTheme

object Main: KoinComponent {
    private val ui: UI by inject()
    lateinit var client: Client
    var startMs = System.currentTimeMillis()

    private lateinit var callbacks: Callbacks

    private fun start() {
        EventBus.subscribe(onEvent())
        EventBus.subscribe(onAppletLoaded()) {
            it is AppletLoaded
        }
        MeteorliteTheme.install()
        AppletConfiguration.init()
        ui.init()
        client = UI.getClient(applet)
        client.callbacks = Hooks()
    }

    @JvmStatic
    fun main(args: Array<String>) {
        startKoin { modules(CLIENT_MODULE) }
        callbacks = get()
        this.start()
        val clientLoaded = ClientLoaded()
        clientLoaded.msToStart = (System.currentTimeMillis() - startMs)
        callbacks.post(clientLoaded)
    }

    private fun onAppletLoaded(): (Event) -> Unit {
        return {
            println("Applet Loaded (Filtered)")
        }
    }

    private fun onEvent(): (Event) -> Unit {
        return {
            if (it is AppletLoaded)
                println("Applet Loaded (Global)")
            if (it is ClientLoaded)
                println("Client Loaded in " + it.msToStart + " ms")
        }
    }
}
