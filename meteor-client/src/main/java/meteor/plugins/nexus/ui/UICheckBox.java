package meteor.plugins.nexus.ui;

import lombok.Getter;
import lombok.Setter;
import net.runelite.api.widgets.Widget;

/**
 * This class wraps two widgets, one a sprite and one text, and gives them the
 * functionality of a checkbox component. A listener can be added to the checkbox
 * which triggers upon the switching of the checkbox state between check and unchecked.
 *
 * @author Antipixel
 */
public class UICheckBox extends UIComponent {
    private static final int LABEL_COL_DEFAULT = 0xFF981F;
    private static final int LABEL_COL_HOVER = 0xFFFFFF;
    private static final int LABEL_WIDTH = 89;
    private static final int LABEL_HEIGHT = 18;

    private static final int SPRITEID_CHECKBOX_UNCHECKED_STD = 1215;
    private static final int SPRITEID_CHECKBOX_CHECKED_STD = 1217;
    private static final int SPRITEID_CHECKBOX_UNCHECKED_HOV = 1218;
    private static final int SPRITEID_CHECKBOX_CHECKED_HOV = 1220;
    private static final int CHECKBOX_SIZE = 16;

    private static final String OPTION_TOGGLE = "Toggle";

    private final UILabel label;

    @Getter
    private boolean enabled;
    private boolean hovering;

    @Setter
    private ComponentEventListener toggleListener;

    /**
     * Constructs a new checkbox component
     *
     * @param boxIcon     the checkbox graphic widget
     * @param labelWidget the checkbox label widget
     */
    public UICheckBox(Widget boxIcon, Widget labelWidget) {
        super(boxIcon);

        this.enabled = false;
        this.hovering = false;

        // Wrap the widget in a label component, set the
        // dimension, colour and attach the listeners
        this.label = new UILabel(labelWidget);
        this.label.setSize(LABEL_WIDTH, LABEL_HEIGHT);
        this.label.setColour(LABEL_COL_DEFAULT);
        this.label.setOnHoverListener(this::onLabelHover);
        this.label.setOnLeaveListener(this::onLabelLeave);
        this.label.addAction(OPTION_TOGGLE, this::onCheckboxToggled);

        // Resize and update the sprite for the checkbox widget
        this.updateCheckboxSprite();
        this.setSize(CHECKBOX_SIZE, CHECKBOX_SIZE);
    }

    /**
     * Called upon the mouse hovering over the label component
     *
     * @param src the label component
     */
    private void onLabelHover(UIComponent src) {
        // Update the hovering state
        this.hovering = true;

        // Update the label colour
        UILabel label = (UILabel) src;
        label.setColour(LABEL_COL_HOVER);

        // Make sure the checkbox sprite is up to date
        this.updateCheckboxSprite();
    }

    /**
     * Called upon the mouse leaving the label component
     *
     * @param src the label component
     */
    private void onLabelLeave(UIComponent src) {
        // Update the hovering state
        this.hovering = false;

        // Update the label colour
        UILabel label = (UILabel) src;
        label.setColour(LABEL_COL_DEFAULT);

        // Make sure the checkbox sprite is up to date
        this.updateCheckboxSprite();
    }

    @Override
    public void setX(int x) {
        // Match the position of the label
        // to that of the checkbox
        super.setX(x);
        this.label.setX(x);
    }

    @Override
    public void setY(int y) {
        // Match the position of the label
        // to that of the checkbox
        super.setY(y);
        this.label.setY(y);
    }

    @Override
    public void setName(String name) {
        // Set the name of the label instead
        this.label.setName(name);
    }

    /**
     * Called upon the toggle menu action being selected on the checkbox component
     */
    private void onCheckboxToggled() {
        // Switch the checked state
        this.setEnabled(!enabled);

        // If there's a toggle listener registered, call it
		if (this.toggleListener != null) {
			this.toggleListener.onComponentEvent(this);
		}
    }

    /**
     * Updates the sprite for the checkbox, depending on whether the checkbox
     * is enabled or disabled, or if the mouse is hovering over it
     */
    private void updateCheckboxSprite() {
        // Get both standard and hovering sprites for the current enable state
        int stdSprite = enabled ? SPRITEID_CHECKBOX_CHECKED_STD : SPRITEID_CHECKBOX_UNCHECKED_STD;
        int hovSprite = enabled ? SPRITEID_CHECKBOX_CHECKED_HOV : SPRITEID_CHECKBOX_UNCHECKED_HOV;

        // Apply it to the checkbox widget, depending on whether the mouse is hovering
        this.getWidget().setSpriteId(this.hovering ? hovSprite : stdSprite);
    }

    /**
     * Sets the state of the checkbox, checked or unchecked
     *
     * @param enabled true for checked, false for unchecked
     */
    public void setEnabled(boolean enabled) {
        // Set the new state
        this.enabled = enabled;

        // Update the sprite
        this.updateCheckboxSprite();
    }

    /**
     * Sets the text of the checkbox label
     *
     * @param text the label text
     */
    public void setText(String text) {
        this.label.setText(text);
    }
}
