
package meteor.config

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon

/**
 * Used with ConfigItem, defines what units are shown to the side of the box.
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER)
@MustBeDocumented
annotation class Icon(
    val value: FontAwesomeIcon = FontAwesomeIcon.PLUG,
    val stop: FontAwesomeIcon = FontAwesomeIcon.STOP,
    val start: FontAwesomeIcon = FontAwesomeIcon.PLAY,
    val canToggle: Boolean = false
)