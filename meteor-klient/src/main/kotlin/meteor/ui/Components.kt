package meteor.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.SwingPanel
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.loadImageBitmap
import androidx.compose.ui.res.useResource
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import meteor.rs.Applet
import java.awt.BorderLayout
import javax.swing.JPanel
import meteor.ui.Components.Toolbar.Position.*
import javax.swing.JFrame

object Components {
    var jpanel: JPanel? = null
    @Composable
    fun BrandBadge() {
        val bitmap: ImageBitmap = useResource("brand/badge.png") { loadImageBitmap(it) }
        Image(bitmap = bitmap, contentDescription = "Brand Badge", filterQuality = FilterQuality.High)
    }

    @Composable
    fun OSRSApplet(constraints: Constraints) {
        val mod = when (UI.toolbarPosition.value) {
            TOP -> {
                Modifier.fillMaxWidth().fillMaxHeight()
            }
            BOTTOM -> {
                Modifier.fillMaxWidth().height(constraints.maxHeight.dp - UI.TOOLBAR_WIDTH.dp).background(Color.Black)
            }
            LEFT, RIGHT -> {
                Modifier.fillMaxHeight().width(UI.contentSize.width.dp - UI.TOOLBAR_WIDTH.dp).background(Color.Black)
            }
        }
        SwingPanel(Color.Black,
                modifier = mod,
                factory = {
                    JPanel().apply {
                        jpanel = this
                        layout = BorderLayout()
                        add(Applet.applet)
                        if (!UI.loaded) {
                            Applet.applet.init()
                            Applet.applet.start()
                            UI.loaded = true
                        }
                    }
                })
    }

    fun awtFrame() {
        val frame = JFrame("Meteor")
        frame.setSize(1920, 1080)
        val jpanel = JPanel()
        jpanel.setSize(1920, 1080)
        jpanel.layout = BorderLayout()
        jpanel.add(Applet.applet)
        frame.add(jpanel)
        frame.isVisible = true
        Applet.applet.init()
        Applet.applet.start()
    }

    object Toolbar {
        @Composable
        fun LeftRightToolbar(position: MutableState<Position>) {
            return Column(verticalArrangement = Arrangement.Top) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Top,
                        modifier = Modifier.width(UI.TOOLBAR_WIDTH.dp).fillMaxHeight(.5f).background(UI.darkThemeColors.background)) {
                    MaterialTheme(colors = UI.darkThemeColors) {
                    }
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Bottom,
                        modifier = Modifier.width(UI.TOOLBAR_WIDTH.dp).fillMaxHeight().background(UI.darkThemeColors.background)) {
                    MaterialTheme(colors = UI.darkThemeColors) {
                        BrandBadge()
                    }
                }
            }
        }

        @Composable
        fun TopBottomToolbar(position: MutableState<Position>) {
            return Row(horizontalArrangement = Arrangement.Center) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Start,
                        modifier = Modifier.height(UI.TOOLBAR_WIDTH.dp).fillMaxWidth(.5f).background(UI.darkThemeColors.background)) {
                    MaterialTheme(colors = UI.darkThemeColors) {
                    }
                }
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.End,
                        modifier = Modifier.height(UI.TOOLBAR_WIDTH.dp).fillMaxWidth().background(UI.darkThemeColors.background)) {
                    MaterialTheme(colors = UI.darkThemeColors) {
                        BrandBadge()
                    }
                }
            }
        }

        enum class Position(position: String) {
            TOP("Top"),
            RIGHT("Right"),
            LEFT("Left"),
            BOTTOM("Bottom")
        }
    }
}