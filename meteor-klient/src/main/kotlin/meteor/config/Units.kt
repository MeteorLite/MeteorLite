
package meteor.config

/**
 * Used with ConfigItem, defines what units are shown to the side of the box.
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER)
@MustBeDocumented
annotation class Units(val value: String) {
    companion object {
        var MILLISECONDS = "ms"
        var MINUTES = " mins"
        var PERCENT = "%"
        var PIXELS = "px"
        var POINTS = "pt"
        var SECONDS = "s"
        var TICKS = " ticks"
        var LEVELS = " lvls"
        var FPS = " fps"
        var GP = " GP"
    }
}