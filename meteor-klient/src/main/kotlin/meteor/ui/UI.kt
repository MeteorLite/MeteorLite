package meteor.ui

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.window.FrameWindowScope
import meteor.Logger
import meteor.plugins.PluginManager
import meteor.ui.Components.OSRSApplet
import meteor.ui.Components.Toolbar.LeftRightToolbar
import meteor.ui.Modifiers.toolbarDragListener
import java.awt.Dimension
import meteor.ui.Components.Toolbar.Position.*
import meteor.ui.Components.Toolbar.TopBottomToolbar
import rs117.hd.GpuHDPlugin

object UI {
    val toolbarPosition = mutableStateOf(TOP)
    var loaded = false
    const val TOOLBAR_WIDTH = 40
    lateinit var contentSize: Dimension
    var window: FrameWindowScope? = null
    var log = Logger(UI::class.java.name)
    var lastPosition: Components.Toolbar.Position? = null
    fun Window(): (@Composable FrameWindowScope.() -> Unit) {
        return {
            log.info("Creating ${window.renderApi} Compose window")
            MaterialTheme(colors = darkThemeColors) {
                BoxWithConstraints(modifier = Modifier.fillMaxSize().then(toolbarDragListener())) {
                    contentSize = Dimension(this.constraints.maxWidth, this.constraints.maxHeight)

                    if (lastPosition != null && lastPosition != toolbarPosition.value) {
                        PluginManager.restartPlugin<GpuHDPlugin>()
                    }
                    when (val position = toolbarPosition.value) {
                        LEFT -> {
                            Row {
                                LeftRightToolbar(toolbarPosition)
                                OSRSApplet(this@BoxWithConstraints.constraints)
                                lastPosition = toolbarPosition.value
                            }
                        }
                        RIGHT -> {
                            Row {
                                OSRSApplet(this@BoxWithConstraints.constraints)
                                LeftRightToolbar(toolbarPosition)
                                lastPosition = toolbarPosition.value
                            }
                        }
                        else -> Column {
                            if (position == TOP)
                                TopBottomToolbar(toolbarPosition)
                            if (position == TOP || position == BOTTOM)
                                OSRSApplet(this@BoxWithConstraints.constraints)
                            if (position == BOTTOM) {
                                TopBottomToolbar(toolbarPosition)
                            }
                            lastPosition = toolbarPosition.value
                        }
                    }
                }
            }
            this@UI.window = this
        }
    }


    val darkThemeColors = darkColors(
            primary = Color.Cyan,
            primaryVariant = Color(0xFF3E2723),
            secondary = Color(0xFF03DAC5),
            background = Color(0xFF121212),
            surface = Color.Black,
            error = Color(0xFFCF6679),
            onPrimary = Color.White,
            onSecondary = Color.White,
            onBackground = Color.White,
            onSurface = Color.White,
            onError = Color.Black
    )
}