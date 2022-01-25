package meteor.plugins.nexus.ui;

import java.util.ArrayList;
import java.util.List;

/**
 * A group of UIComponents
 *
 * @author Antipixel
 */
public class UIPage {
    private final List<UIComponent> components;

    /**
     * Constructs a new UI page
     */
    public UIPage() {
        this.components = new ArrayList<>();
    }

    /**
     * Sets the visibility for all the components in the page
     *
     * @param visibility true for visible, false for hidden
     */
    public void setVisibility(boolean visibility) {
        // Update the visibility for each of the components
        this.components.forEach(c -> c.setVisibility(visibility));
    }

    /**
     * Adds a UI component to the page
     *
     * @param component the component to add
     */
    public void add(UIComponent component) {
        this.components.add(component);
    }

    /**
     * Removes a UI component from the page
     *
     * @param component the component to remove
     */
    public void remove(UIComponent component) {
        this.components.remove(component);
    }
}
