package meteor.plugins.prayerFlicker;

import meteor.config.Config;
import meteor.config.ConfigGroup;
import meteor.config.ConfigItem;
import meteor.config.Keybind;

@ConfigGroup("PrayerFlicker")
public interface PrayerFlickerConfig extends Config {
    @ConfigItem(
            keyName = "Toggle",
            name = "",
            description = ""
    )
    default Keybind toggle() {
        return Keybind.NOT_SET;
    }
}
