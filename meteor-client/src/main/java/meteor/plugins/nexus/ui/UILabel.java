package meteor.plugins.nexus.ui;

import net.runelite.api.FontID;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetTextAlignment;

import java.awt.*;

/**
 * This class wraps a game widget and gives it the functionality of
 * a text label component, with basic control over text colour and font.
 *
 * @author Antipixel
 */
public class UILabel extends UIComponent {
    /**
     * Constructs a new label component
     *
     * @param labelWidget the underlying widget
     */
    public UILabel(Widget labelWidget) {
        super(labelWidget);

        // Set default font and text colour
        this.setFont(FontID.PLAIN_11);
        this.setColour(Color.WHITE.getRGB());

        // Set the alignment to centre and enable text shadowing
        labelWidget.setXTextAlignment(WidgetTextAlignment.CENTER);
        labelWidget.setYTextAlignment(WidgetTextAlignment.CENTER);
        labelWidget.setTextShadowed(true);
    }

    /**
     * Sets the display text of the label
     *
     * @param text the display text
     */
    public void setText(String text) {
        this.getWidget().setText(text);
    }

    /**
     * Sets the font of the label
     *
     * @param fontID the font ID, specified in {@link FontID}
     */
    public void setFont(int fontID) {
        this.getWidget().setFontId(fontID);
    }

    /**
     * Sets the colour of the label text
     *
     * @param colour the RGB colour
     */
    public void setColour(int colour) {
        this.getWidget().setTextColor(colour);
    }
}
