package meteor.plugins.nexus.definition;

import lombok.Getter;

/**
 * Contains data that defines a region icon.
 * This information is loaded directly from a JSON file.
 *
 * @author Antipixel
 */
@Getter
public class IconDefinition {
    private int x;
    private int y;
    private int spriteStandard;
    private int spriteHover;
}
