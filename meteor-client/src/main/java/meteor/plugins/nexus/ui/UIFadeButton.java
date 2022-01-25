package meteor.plugins.nexus.ui;

import net.runelite.api.ScriptEvent;
import net.runelite.api.widgets.Widget;

/**
 * Functionally identical to the UI Button, except the opacity
 * of the widget is dimmed when the mouse moves over the component
 *
 * @author Antipixel
 */
public class UIFadeButton extends UIButton {
    private static final float FADE_OPACITY = 0.75f;
    private static final float DEFAULT_OPACITY = 1.0f;

    public UIFadeButton(Widget widget) {
        super(widget);
    }

    @Override
    protected void onMouseHover(ScriptEvent e) {
        super.onMouseHover(e);

        // Fade the widget
        this.setOpacity(FADE_OPACITY);
    }

    @Override
    protected void onMouseLeave(ScriptEvent e) {
        super.onMouseLeave(e);

        // Set the widget back to full opacity
        this.setOpacity(DEFAULT_OPACITY);
    }
}
