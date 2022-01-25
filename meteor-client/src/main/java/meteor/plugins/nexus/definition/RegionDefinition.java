package meteor.plugins.nexus.definition;

import lombok.Getter;

/**
 * Contains data that defines a game region on the menu.
 * This information is loaded directly from a JSON file.
 *
 * @author Antipixel
 */
@Getter
public class RegionDefinition {
    private int id;
    private String name;
    private IconDefinition icon;
    private int indexSprite;
    private int mapSprite;

    private TeleportDefinition[] teleportDefinitions;

    /**
     * Gets the teleport definitions for this region
     *
     * @return the teleport definitions
     */
    public TeleportDefinition[] getTeleports() {
        return this.teleportDefinitions;
    }

    /**
     * Checks if this region has teleports defined for it
     *
     * @return true if teleports are defined, otherwise false
     */
    public boolean hasTeleports() {
        return this.teleportDefinitions.length > 0;
    }
}
