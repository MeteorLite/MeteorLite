package meteor.config;

import lombok.Getter;
import meteor.plugins.meteorlite.interaction.InteractType;
import meteor.ui.ContainableFrame;
import meteor.ui.overlay.components.ComponentConstants;
import meteor.util.OSType;
import net.runelite.api.Constants;

import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

@ConfigGroup(MeteorLiteConfig.GROUP_NAME)
public interface MeteorLiteConfig extends Config {
    String GROUP_NAME = "MeteorLite";
    /*
      Client settings
     */

    @ConfigItem(
            keyName = "toolbarPosition",
            name = "Toolbar Position",
            description = "Where the toolbar is located.",
            section = clientSettings,
            position = 0
    )
    default ToolbarPosition toolbarPosition() {
        return ToolbarPosition.EAST;
    }

    @ConfigSection(
            name = "Client settings",
            description = "Client settings",
            closedByDefault = true,
            position = 0
    )
    String clientSettings = "Client settings";

    @ConfigItem(
            keyName = "pluginPanelResize",
            name = "Plugin Panel Resizes Game",
            description = "Whether toggling the plugin panel resizes the game client.",
            section = clientSettings,
            position = 0
    )
    default boolean resizeGame() {
        return false;
    }

    /*
      Debugging/Logger
     */

    @ConfigSection(
            name = "Debug/Logger",
            description = "Debugging settings",
            closedByDefault = true,
            position = 1
    )
    String debugLogger = "Debug/Logger";

    @ConfigItem(
            keyName = "debugEnabled",
            name = "Enable debug logs",
            description = "Enables printing of debug logs",
            section = debugLogger,
            position = 0
    )
    default boolean debugEnabled() {
        return false;
    }

    /*
      Externals
     */

    @ConfigSection(
            name = "External manager",
            description = "External plugins",
            closedByDefault = true,
            position = 2
    )
    String externalManager = "External manager";

    @ConfigItem(
            keyName = "externalsLoadOnStartup",
            name = "Load on startup",
            description = "Load externals on startup",
            section = externalManager,
            position = 0
    )
    default boolean externalsLoadOnStartup() {
        return false;
    }

    @Icon
    @ConfigItem(
            keyName = "reloadExternals",
            name = "(Re-)Load Externals",
            description = "Reload external plugins from externals folder",
            section = externalManager,
            position = 1
    )
    default Button reloadExternals() {
        return new Button();
    }

    @ConfigSection(
            name = "Interaction manager",
            description = "Interaction settings",
            closedByDefault = true,
            position = 3
    )
    String interactionManager = "Interaction manager";

    @ConfigItem(
            keyName = "mouseEvents",
            name = "Mouse events",
            description = "Sends mouse events before interaction",
            section = interactionManager,
            position = 0
    )
    default boolean mouseEvents() {
        return false;
    }

    @ConfigItem(
            keyName = "interactType",
            name = "Interact click type",
            description = "Type of clicks to send to the server",
            section = interactionManager,
            position = 0
    )
    default InteractType interactType() {
        return InteractType.CLICKBOXES;
    }

    @ConfigItem(
            keyName = "drawMouse",
            name = "Draw mouse events",
            description = "Draws the sent mouse events on screen",
            section = interactionManager,
            position = 1
    )
    default boolean drawMouse() {
        return false;
    }

    @ConfigItem(
            keyName = "debugInteractions",
            name = "Debug interactions",
            description = "Prints interactions to console",
            section = interactionManager,
            position = 2
    )
    default boolean debugInteractions() {
        return false;
    }

    @ConfigItem(
            keyName = "debugDialogs",
            name = "Debug dialogs",
            description = "Prints dialog actions to console",
            section = interactionManager,
            position = 3
    )
    default boolean debugDialogs() {
        return false;
    }

    @ConfigSection(
            name = "Visuals",
            description = "Client visual settings",
            closedByDefault = true,
            position = 4
    )
    String visualSettings = "Visuals";

    @ConfigItem(
            keyName = "meteorLoginScreen",
            name = "MeteorLite Login screen",
            description = "Sets the custom MeteorLite Login screen",
            section = visualSettings,
            position = 0
    )
    default boolean meteorLoginScreen() {
        return true;
    }


    @ConfigSection(
            name = "Window Settings",
            description = "Settings relating to the client's window and frame",
            position = 4,
            closedByDefault = true
    )
    String windowSettings = "Window Settings";

    @ConfigSection(
            name = "Notification Settings",
            description = "Settings relating to notifications",
            position = 5,
            closedByDefault = true
    )
    String notificationSettings = "Notification Settings";

    @ConfigSection(
            name = "Overlay Settings",
            description = "Settings relating to fonts",
            position = 6,
            closedByDefault = true
    )
    String overlaySettings = "Overlay Settings";

    @ConfigItem(
            keyName = "gameSize",
            name = "Game size",
            description = "The game will resize to this resolution upon starting the client",
            position = 10,
            section = windowSettings
    )
    default Dimension gameSize() {
        return Constants.GAME_FIXED_SIZE;
    }

    @ConfigItem(
            keyName = "automaticResizeType",
            name = "Resize type",
            description = "Choose how the window should resize when opening and closing panels",
            position = 11,
            section = windowSettings
    )
    default ExpandResizeType automaticResizeType() {
        return ExpandResizeType.KEEP_GAME_SIZE;
    }

    @ConfigItem(
            keyName = "lockWindowSize",
            name = "Lock window size",
            description = "Determines if the window resizing is allowed or not",
            position = 12,
            section = windowSettings
    )
    default boolean lockWindowSize() {
        return false;
    }

    @ConfigItem(
            keyName = "containInScreen2",
            name = "Contain in screen",
            description = "Makes the client stay contained in the screen when attempted to move out of it.<br>Note: 'Always' only works if custom chrome is enabled.",
            position = 13,
            section = windowSettings
    )
    default ContainableFrame.Mode containInScreen() {
        return ContainableFrame.Mode.RESIZING;
    }

    @ConfigItem(
            keyName = "rememberScreenBounds",
            name = "Remember client position",
            description = "Save the position and size of the client after exiting",
            position = 14,
            section = windowSettings
    )
    default boolean rememberScreenBounds() {
        return true;
    }

    @ConfigItem(
            keyName = "uiEnableCustomChrome",
            name = "Enable custom window chrome",
            description = "Use RuneLite's custom window title and borders.",
            warning = "Please restart your client after changing this setting",
            position = 15,
            section = windowSettings
    )
    default boolean enableCustomChrome() {
        return OSType.getOSType() == OSType.Windows;
    }

    @Range(
            min = 10,
            max = 100
    )
    @ConfigItem(
            keyName = "uiWindowOpacity",
            name = "Window opacity",
            description = "Set the windows opacity. Requires \"Enable custom window chrome\" to be enabled.",
            position = 16,
            section = windowSettings
    )
    default int windowOpacity() {
        return 100;
    }

    @ConfigItem(
            keyName = "gameAlwaysOnTop",
            name = "Always on top",
            description = "The game will always be on the top of the screen",
            position = 17,
            section = windowSettings
    )
    default boolean gameAlwaysOnTop() {
        return false;
    }

    @ConfigItem(
            keyName = "warningOnExit",
            name = "Exit warning",
            description = "Shows a warning popup when trying to exit the client",
            position = 18,
            section = windowSettings
    )
    default WarningOnExit warningOnExit() {
        return WarningOnExit.LOGGED_IN;
    }

    @ConfigItem(
            keyName = "usernameInTitle",
            name = "Show display name in title",
            description = "Toggles displaying of local player's display name in client title",
            position = 19,
            section = windowSettings
    )
    default boolean usernameInTitle() {
        return true;
    }

    @ConfigItem(
            keyName = "notificationTray",
            name = "Enable tray notifications",
            description = "Enables tray notifications",
            position = 20,
            section = notificationSettings
    )
    default boolean enableTrayNotifications() {
        return true;
    }

    @ConfigItem(
            keyName = "notificationRequestFocus",
            name = "Request focus",
            description = "Configures the window focus request type on notification",
            position = 21,
            section = notificationSettings
    )
    default RequestFocusType notificationRequestFocus() {
        return RequestFocusType.OFF;
    }

    @ConfigItem(
            keyName = "notificationGameMessage",
            name = "Game message notifications",
            description = "Adds a notification message to the chatbox",
            position = 23,
            section = notificationSettings
    )
    default boolean enableGameMessageNotification() {
        return false;
    }

    @ConfigItem(
            keyName = "flashNotification",
            name = "Flash",
            description = "Flashes the game frame as a notification",
            position = 24,
            section = notificationSettings
    )
    default FlashNotification flashNotification() {
        return FlashNotification.DISABLED;
    }

    @ConfigItem(
            keyName = "notificationFocused",
            name = "Send notifications when focused",
            description = "Toggles all notifications for when the client is focused",
            position = 25,
            section = notificationSettings
    )
    default boolean sendNotificationsWhenFocused() {
        return false;
    }

    @Alpha
    @ConfigItem(
            keyName = "notificationFlashColor",
            name = "Notification Flash",
            description = "Sets the color of the notification flashes.",
            position = 26,
            section = notificationSettings
    )
    default Color notificationFlashColor() {
        return new Color(255, 0, 0, 70);
    }

    @ConfigItem(
            keyName = "fontType",
            name = "Dynamic Overlay Font",
            description = "Configures what font type is used for in-game overlays such as player name, ground items, etc.",
            position = 30,
            section = overlaySettings
    )
    default FontType fontType() {
        return FontType.SMALL;
    }

    @ConfigItem(
            keyName = "tooltipFontType",
            name = "Tooltip Font",
            description = "Configures what font type is used for in-game tooltips such as food stats, NPC names, etc.",
            position = 31,
            section = overlaySettings
    )
    default FontType tooltipFontType() {
        return FontType.SMALL;
    }

    @ConfigItem(
            keyName = "interfaceFontType",
            name = "Interface Font",
            description = "Configures what font type is used for in-game interface overlays such as panels, opponent info, clue scrolls etc.",
            position = 32,
            section = overlaySettings
    )
    default FontType interfaceFontType() {
        return FontType.REGULAR;
    }

    @ConfigItem(
            keyName = "menuEntryShift",
            name = "Require Shift for overlay menu",
            description = "Overlay right-click menu will require shift to be added",
            position = 33,
            section = overlaySettings
    )
    default boolean menuEntryShift() {
        return true;
    }

    @ConfigItem(
            keyName = "tooltipPosition",
            name = "Tooltip Position",
            description = "Configures whether to show the tooltip above or under the cursor",
            position = 35,
            section = overlaySettings
    )
    default TooltipPositionType tooltipPosition() {
        return TooltipPositionType.UNDER_CURSOR;
    }

    @ConfigItem(
            keyName = "infoBoxVertical",
            name = "Display infoboxes vertically",
            description = "Toggles the infoboxes to display vertically",
            position = 40,
            section = overlaySettings,
            hidden = true
    )
    default boolean infoBoxVertical() {
        return false;
    }

    @ConfigItem(
            keyName = "infoBoxSize",
            name = "Infobox size",
            description = "Configures the size of each infobox in pixels",
            position = 42,
            section = overlaySettings
    )
    @Units(Units.PIXELS)
    default int infoBoxSize() {
        return 35;
    }

    @ConfigItem(
            keyName = "infoBoxTextOutline",
            name = "Outline infobox text",
            description = "Draw a full outline instead of a simple shadow for infobox text",
            position = 43,
            section = overlaySettings
    )
    default boolean infoBoxTextOutline() {
        return false;
    }

    @Alpha
    @ConfigItem(
            keyName = "overlayBackgroundColor",
            name = "Overlay Color",
            description = "Configures the background color of infoboxes and overlays",
            position = 44,
            section = overlaySettings
    )
    default Color overlayBackgroundColor() {
        return ComponentConstants.STANDARD_BACKGROUND_COLOR;
    }

    @ConfigItem(
            keyName = "sidebarToggleKey",
            name = "Sidebar Toggle Key",
            description = "The key that will toggle the sidebar (accepts modifiers)",
            position = 45,
            section = windowSettings
    )
    default Keybind sidebarToggleKey() {
        return new Keybind(KeyEvent.VK_F11, InputEvent.CTRL_DOWN_MASK);
    }

    @ConfigItem(
            keyName = "panelToggleKey",
            name = "Plugin Panel Toggle Key",
            description = "The key that will toggle the current or last opened plugin panel (accepts modifiers)",
            position = 46,
            section = windowSettings
    )
    default Keybind panelToggleKey() {
        return new Keybind(KeyEvent.VK_F12, InputEvent.CTRL_DOWN_MASK);
    }

    @ConfigSection(
            name = "Other",
            description = "Other settings",
            closedByDefault = true,
            position = 55
    )
    String other = "Other";

    @ConfigItem(
            keyName = "blockExtraMouseButtons",
            name = "Block extra mouse buttons",
            description = "Blocks extra mouse buttons (4 and above)",
            position = 50,
            section = other
    )
    default boolean blockExtraMouseButtons() {
        return true;
    }

    @ConfigItem(
            keyName = "useWikiItemPrices",
            name = "Use actively traded price",
            description = "Use actively traded prices, sourced from the RuneScape wiki, for item prices",
            position = 51,
            section = other
    )
    default boolean useWikiItemPrices() {
        return true;
    }

    @Getter
    enum ToolbarPosition {
        NORTH(BorderLayout.NORTH),
        SOUTH(BorderLayout.SOUTH),
        EAST(BorderLayout.EAST),
        WEST(BorderLayout.WEST);

        final String position;

        ToolbarPosition(String position) {
            this.position = position;
        }
    }
}
