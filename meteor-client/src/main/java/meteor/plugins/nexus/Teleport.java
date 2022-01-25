package meteor.plugins.nexus;

/**
 * Represents a teleport option on the Nexus menu
 *
 * @author Antipixel
 */
public class Teleport {
    private final String name;
    private final int childIndex;
    private final String keyShortcut;
    private final boolean alternate;

    /**
     * Creates a new teleport instance
     *
     * @param name       the teleport name
     * @param childIndex the child index of the widget
     *                   for this teleport option
     * @param key        the keyboard shortcut
     * @param alt        true if this teleport is an alternate type,
     *                   for example the Grand Exhange is an alternate of Varrock
     */
    public Teleport(String name, int childIndex, String key, boolean alt) {
        this.name = name;
        this.childIndex = childIndex;
        this.keyShortcut = key;
        this.alternate = alt;
    }

    /**
     * Gets the child index of this teleports widget
     *
     * @return this teleports widget child index
     */
    public int getChildIndex() {
        return this.childIndex;
    }

    /**
     * Checks whether or not the teleport is an
     * alternative teleport location, such as the
     * Grand Exchange on the Varrock teleport spell
     *
     * @return true if is alternate location, otherwise false
     */
    public boolean isAlt() {
        return this.alternate;
    }


    /**
     * The keyboard shortcut key used to activate this teleport
     *
     * @return the shortcut key
     */
    public String getKeyShortcut() {
        return this.keyShortcut;
    }
}
