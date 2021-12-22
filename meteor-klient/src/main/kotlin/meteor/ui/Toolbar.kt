package meteor.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

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
                    Components.BrandBadge()
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
                    Components.BrandBadge()
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