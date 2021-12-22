package meteor

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalContext
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.ComposePanel
import androidx.compose.ui.awt.ComposeWindow
import androidx.compose.ui.awt.SwingPanel
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.FrameWindowScope
import meteor.ui.ToolbarState
import java.awt.BorderLayout
import java.awt.Dimension
import javax.swing.JPanel

object UI {
    private val toolbarPosition = mutableStateOf(ToolbarState.TOP)

    @Composable
    fun Window(): (@Composable FrameWindowScope.() -> Unit) {
        return {
            MaterialTheme(colors = darkThemeColors) {
                BoxWithConstraints(modifier = Modifier.fillMaxSize()){
                    Column {
                        if (toolbarPosition.value == ToolbarState.TOP)
                        { Toolbar(toolbarPosition) }

                        OSRSApplet(this@BoxWithConstraints)

                        if (toolbarPosition.value == ToolbarState.BOTTOM)
                        { Toolbar(toolbarPosition) }
                    }
                }
            }
        }
    }

    @Composable
    fun OSRSApplet(box: BoxWithConstraintsScope) {
        SwingPanel(Color.Black,
                modifier = Modifier.fillMaxWidth().height(box.maxHeight - 30.dp).background(Color.Black),
                factory = {
                    JPanel().apply {
                        layout = BorderLayout()
                        add(Applet.applet, BorderLayout.CENTER)
                        Applet.applet.init()
                        Applet.applet.start()
                    }
                })
    }

    @Composable
    fun Toolbar(toolbarState: MutableState<ToolbarState>){
        TopAppBar(
                title = { Text("MeteorLite")},
                contentColor = Color.Cyan,
                actions = {
            IconButton(onClick = {
                when (toolbarState.value) {
                    ToolbarState.TOP -> toolbarState.value = ToolbarState.BOTTOM
                    ToolbarState.BOTTOM -> toolbarState.value = ToolbarState.TOP
                    else -> {}
                }}) {
                if (toolbarPosition.value == ToolbarState.TOP)
                    Icon(Icons.Filled.KeyboardArrowDown, null)
                if (toolbarPosition.value == ToolbarState.BOTTOM)
                    Icon(Icons.Filled.KeyboardArrowUp, null)
            }
            IconButton(onClick = {/* Do Something*/ }) {
                Icon(Icons.Filled.Menu, null)
            }
        }, modifier = Modifier.fillMaxWidth().height(30.dp))
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