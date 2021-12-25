package meteor.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.SwingPanel
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.loadImageBitmap
import androidx.compose.ui.res.useResource
import androidx.compose.ui.unit.dp
import meteor.rs.Applet
import java.awt.BorderLayout
import javax.swing.JPanel

object Components {
    @Composable
    fun BrandBadge() {
        val bitmap: ImageBitmap = useResource("brand/badge.png") { loadImageBitmap(it) }
        Image(bitmap = bitmap, contentDescription = "Brand Badge", filterQuality = FilterQuality.High)
    }

    @Composable
    fun OSRSApplet() {
        val mod = when (UI.toolbarPosition.value) {
            Toolbar.Position.TOP, Toolbar.Position.BOTTOM -> {
                Modifier.fillMaxWidth().height(UI.contentSize.height.dp - UI.TOOLBAR_WIDTH.dp + 10.dp).background(Color.Black)
            }
            Toolbar.Position.LEFT, Toolbar.Position.RIGHT -> {
                Modifier.fillMaxHeight().width(UI.contentSize.width.dp - UI.TOOLBAR_WIDTH.dp).background(Color.Black)
            }
        }
        SwingPanel(Color.Black,
                modifier = mod,
                factory = {
                    JPanel().apply {
                        layout = BorderLayout()
                        add(Applet.applet, BorderLayout.CENTER)
                        if (!UI.loaded) {
                            Applet.applet.init()
                            Applet.applet.start()
                            UI.loaded = true
                        }
                    }
                })
    }
}