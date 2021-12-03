package meteor

import meteor.eventbus.EventBus
import meteor.events.AppletLoaded
import net.runelite.api.Client
import themes.MeteorliteTheme
import java.applet.Applet
import java.applet.AppletContext
import java.applet.AppletStub
import java.applet.AudioClip
import java.awt.Desktop
import java.awt.Dimension
import java.awt.Image
import java.io.InputStream
import java.net.URL
import java.util.*
import javax.swing.ImageIcon
import javax.swing.JFrame

class UI: AppletStub, AppletContext {
    companion object {
        lateinit var applet: Applet

        fun getClient(applet: Applet?): Client {
            return applet as Client
        }
    }
    var icon = ImageIcon(UI::class.java.getResource("/Meteor_icon.png")).image
    private var properties: Map<String, String> = AppletConfiguration.properties
    private var parameters: Map<String, String> = AppletConfiguration.parameters
    private var frame: JFrame = JFrame("Meteor Klient")


    fun init() {

        applet = configureApplet()
        frame.add(applet)
        frame.iconImage = icon
        applet.size = applet.minimumSize
        frame.size = applet.minimumSize
        frame.isVisible = true
        applet.init()
        applet.start()
        EventBus.post(AppletLoaded())
    }

    private fun configureApplet(): Applet {
        val applet = ClassLoader.getSystemClassLoader().loadClass("osrs.Client").newInstance() as Applet
        applet.setStub(this)
        applet.maximumSize = appletMaxSize()
        applet.minimumSize = appletMinSize()
        applet.preferredSize = applet.minimumSize
        return applet
    }

    private fun appletMinSize(): Dimension {
        return Dimension(properties["applet_minwidth"]!!.toInt(), properties["applet_minheight"]!!.toInt())
    }

    private fun appletMaxSize(): Dimension {
        return Dimension(properties["applet_maxwidth"]!!.toInt(), properties["applet_maxheight"]!!.toInt())
    }
    override fun isActive(): Boolean {
        return true
    }

    override fun getDocumentBase(): URL {
        return codeBase
    }

    override fun getCodeBase(): URL {
        return try {
            URL(properties["codebase"])
        } catch (e: Exception) {
            throw RuntimeException("Invalid Codebase")
        }
    }

    override fun getParameter(name: String?): String {
        if (!parameters.containsKey(name))
            return ""
        return parameters[name]!!
    }

    override fun appletResize(width: Int, height: Int) {
    }

    override fun showDocument(url: URL?) {
        try {
            Desktop.getDesktop().browse(url!!.toURI())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun showDocument(url: URL?, target: String?) {
        try {
            Desktop.getDesktop().browse(url!!.toURI())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun getAppletContext(): AppletContext {
        return this
    }

    override fun getAudioClip(url: URL): AudioClip {
      throw UnsupportedOperationException()
    }

    override fun getImage(url: URL): Image {
        throw UnsupportedOperationException()
    }

    override fun getApplet(name: String): Applet {
        throw UnsupportedOperationException()
    }

    override fun getApplets(): Enumeration<Applet> {
        throw UnsupportedOperationException()
    }

    override fun showStatus(status: String) {
        throw UnsupportedOperationException()
    }

    override fun setStream(key: String, stream: InputStream) {
        throw UnsupportedOperationException()
    }

    override fun getStream(key: String): InputStream {
        throw UnsupportedOperationException()
    }

    override fun getStreamKeys(): MutableIterator<String> {
        throw UnsupportedOperationException()
    }
}