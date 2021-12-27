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
import meteor.ui.Components.OSRSApplet
import meteor.ui.Modifiers.toolbarDragListener
import meteor.ui.Toolbar.LeftRightToolbar
import meteor.ui.Toolbar.Position
import meteor.ui.Toolbar.TopBottomToolbar
import java.awt.Dimension

object UI {
    val toolbarPosition = mutableStateOf(Position.TOP)
    var loaded = false
    const val TOOLBAR_WIDTH = 40
    lateinit var contentSize: Dimension

    fun Window(): (@Composable FrameWindowScope.() -> Unit) {
        return {

            MaterialTheme(colors = darkThemeColors) {
                BoxWithConstraints(modifier = Modifier.fillMaxSize().then(toolbarDragListener())) {
                    contentSize = Dimension(this.constraints.maxWidth, this.constraints.maxHeight)
                    when (val position = toolbarPosition.value) {
                        Position.LEFT -> {
                            Row {
                                LeftRightToolbar(toolbarPosition)
                                OSRSApplet(this@BoxWithConstraints.constraints)
                            }
                        }
                        Position.RIGHT -> {
                            Row {
                                OSRSApplet(this@BoxWithConstraints.constraints)
                                LeftRightToolbar(toolbarPosition)
                            }
                        }
                        else -> Column {
                            if (position == Position.TOP)
                                TopBottomToolbar(toolbarPosition)
                            if (position == Position.TOP || position == Position.BOTTOM)
                                OSRSApplet(this@BoxWithConstraints.constraints)
                            if (position == Position.BOTTOM) {
                                TopBottomToolbar(toolbarPosition)
                            }
                        }
                    }
                }
            }
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