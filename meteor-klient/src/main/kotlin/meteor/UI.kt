package meteor

import java.applet.Applet
import java.applet.AppletContext
import java.applet.AppletStub
import java.awt.Desktop
import java.awt.Dimension
import java.lang.Exception
import java.lang.RuntimeException
import java.net.URL
import javax.swing.JFrame
import java.applet.AudioClip
import java.awt.Image
import java.io.InputStream
import java.util.*

class UI: AppletStub, AppletContext {
    private var properties: Map<String, String> = AppletConfiguration.properties!!
    private var parameters: Map<String, String> = AppletConfiguration.parameters!!
    private var frame: JFrame = JFrame("Meteor Klient")
    private lateinit var applet: Applet

    fun init() {
        applet = configureApplet()
        frame.add(applet)
        applet.size = applet.minimumSize
        frame.size = applet.minimumSize
        frame.isVisible = true
        applet.init()
        applet.start()
        EventBus.post(Event.APPLET_LOADED)
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

    override fun getAudioClip(url: URL?): AudioClip {
        TODO("Not yet implemented")
    }

    override fun getImage(url: URL?): Image {
        TODO("Not yet implemented")
    }

    override fun getApplet(name: String?): Applet {
        TODO("Not yet implemented")
    }

    override fun getApplets(): Enumeration<Applet> {
        TODO("Not yet implemented")
    }

    override fun showStatus(status: String?) {
        TODO("Not yet implemented")
    }

    override fun setStream(key: String?, stream: InputStream?) {
        TODO("Not yet implemented")
    }

    override fun getStream(key: String?): InputStream {
        TODO("Not yet implemented")
    }

    override fun getStreamKeys(): MutableIterator<String> {
        TODO("Not yet implemented")
    }
}