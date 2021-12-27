package meteor.plugins.fishing

import meteor.config.legacy.Config
import meteor.config.legacy.ConfigGroup
import meteor.config.legacy.ConfigItem
import meteor.config.legacy.ExpandResizeType
import net.runelite.api.Point
import net.runelite.api.coords.WorldPoint
import java.awt.Color
import java.awt.Dimension
import java.awt.Rectangle
import java.time.Instant

@ConfigGroup("fishing")
@JvmDefaultWithoutCompatibility
interface FishingConfig: Config {
    @ConfigItem(position = 0,
            keyName = "onlyCurrent",
            name = "Display only currently fished fish",
            description = "Configures whether only current fished fish's fishing spots are displayed")
    fun onlyCurrentSpot(): Boolean {
        return true
    }

    @ConfigItem(position = 1,
            keyName = "ColorTest",
            name = "Display only currently fished fish",
            description = "Configures whether only current fished fish's fishing spots are displayed")
    fun colorTest(): Color {
        return Color.CYAN
    }

    @ConfigItem(position = 2,
            keyName = "StringTest",
            name = "Display only currently fished fish",
            description = "Configures whether only current fished fish's fishing spots are displayed")
    fun stringTest(): String {
        return "Should work"
    }

    @ConfigItem(position = 3,
            keyName = "EnumTest",
            name = "Display only currently fished fish",
            description = "Configures whether only current fished fish's fishing spots are displayed")
    fun enumTest(): ExpandResizeType {
        return ExpandResizeType.KEEP_GAME_SIZE
    }

    @ConfigItem(position = 4,
            keyName = "DimensionTest",
            name = "Display only currently fished fish",
            description = "Configures whether only current fished fish's fishing spots are displayed")
    fun dimensionTest(): Dimension {
        return Dimension(4, 20)
    }

    @ConfigItem(position = 4,
            keyName = "pointTest",
            name = "Display only currently fished fish",
            description = "Configures whether only current fished fish's fishing spots are displayed")
    fun pointTest(): Point {
        return Point(4, 20)
    }

    @ConfigItem(position = 4,
            keyName = "rectangleTest",
            name = "Display only currently fished fish",
            description = "Configures whether only current fished fish's fishing spots are displayed")
    fun rectangleTest(): Rectangle {
        return Rectangle(4, 20)
    }

    @ConfigItem(position = 4,
            keyName = "instantTest",
            name = "Display only currently fished fish",
            description = "Configures whether only current fished fish's fishing spots are displayed")
    fun instantTest(): Instant {
        return Instant.now()
    }

    @ConfigItem(position = 4,
            keyName = "worldPointTest",
            name = "Display only currently fished fish",
            description = "Configures whether only current fished fish's fishing spots are displayed")
    fun worldPointTest(): WorldPoint {
        return WorldPoint(0, 4, 20)
    }
}