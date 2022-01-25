package meteor.plugins.nexus.ui;

import net.runelite.api.widgets.Widget;

/**
 * This class wraps a game widget and gives it the functionality
 * of a graphic display component, effectively just a game sprite.
 *
 * @author Antipixel
 */
public class UIGraphic extends UIComponent {
    /**
     * Constructs a new graphic component
     *
     * @param widget the underlying widget
     */
    public UIGraphic(Widget widget) {
        super(widget);
    }

    /**
     * Sets the sprite to display on the component
     *
     * @param spriteID the sprite ID
     */
    public void setSprite(int spriteID) {
        this.getWidget().setSpriteId(spriteID);
    }
}
