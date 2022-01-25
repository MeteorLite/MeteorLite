package meteor.plugins.nexus;

import com.google.gson.Gson;
import com.google.inject.Provides;
import meteor.callback.ClientThread;
import meteor.config.ConfigManager;
import meteor.eventbus.Subscribe;
import meteor.game.SpriteManager;
import meteor.plugins.Plugin;
import meteor.plugins.PluginDescriptor;
import meteor.plugins.nexus.definition.IconDefinition;
import meteor.plugins.nexus.definition.RegionDefinition;
import meteor.plugins.nexus.definition.TeleportDefinition;
import meteor.plugins.nexus.sprites.SpriteDefinition;
import meteor.plugins.nexus.ui.UIButton;
import meteor.plugins.nexus.ui.UICheckBox;
import meteor.plugins.nexus.ui.UIComponent;
import meteor.plugins.nexus.ui.UIFadeButton;
import meteor.plugins.nexus.ui.UIGraphic;
import meteor.plugins.nexus.ui.UIPage;
import net.runelite.api.Client;
import net.runelite.api.SoundEffectID;
import net.runelite.api.SpriteID;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.events.VarbitChanged;
import net.runelite.api.events.WidgetLoaded;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetType;

import javax.inject.Inject;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@PluginDescriptor(
        name = "Nexus Menu Map",
        description = "Replaces the player owned house teleport Nexus menu",
        tags = {"poh", "portal", "teleport", "nexus"}
)
public class NexusMapPlugin extends Plugin {
    /* Packed Widget IDs */
    private static final int GROUP_NEXUS_PORTAL = 17;
    private static final int ID_PORTAL_WINDOW = 0x110001;
    private static final int ID_PORTAL_PANEL = 0x110002;
    private static final int ID_PORTAL_MODEL = 0x110003;
    private static final int ID_SCRY_TEXT = 0x110004;
    private static final int ID_SCRY_SELECT = 0x110005;
    private static final int ID_KEYEVENTS_ALTERNATE = 0x110007;
    private static final int ID_KEYEVENTS_PRIMARY = 0x110008;
    private static final int ID_SCROLLBOX_BORDER = 0x110009;
    private static final int ID_SCRY_RADIO_PANE = 0x11000A;
    private static final int ID_TELEPORT_LIST = 0x11000B;
    private static final int ID_LOC_LABELS_PRIMARY = 0x11000C;
    private static final int ID_SCROLLBAR = 0x11000E;
    private static final int ID_LOC_LABELS_ALTERNATE = 0x110010;

    /* Widget dimensions and positions */
    private static final int TELE_ICON_SIZE = 24;
    private static final int MAP_SPRITE_POS_X = 39;
    private static final int MAP_SPRITE_POS_Y = 53;
    private static final int INDEX_MAP_SPRITE_WIDTH = 400;
    private static final int INDEX_MAP_SPRITE_HEIGHT = 214;
    private static final int REGION_MAP_SPRITE_WIDTH = 478;
    private static final int REGION_MAP_SPRITE_HEIGHT = 272;
    private static final int MAP_ICON_WIDTH = 50;
    private static final int MAP_ICON_HEIGHT = 41;

    /* Script, Sprite IDs */
    private static final int SCRIPT_TRIGGER_KEY = 1437;
    private static final int REGION_MAP_MAIN = 2721;
    private static final int VARBIT_NEXUS_MODE = 6671;

    /* Menu actions */
    private static final String ACTION_TEXT_TELE = "Teleport";
    private static final String ACTION_TEXT_SCRY = "Scry";
    private static final String ACTION_TEXT_SELECT = "Select";
    private static final String ACTION_TEXT_BACK = "Back";
    private static final String NAME_TEXT_TOGGLE = "Map Mode";

    /* Configuration Group & Keys */
    private static final String CFG_GROUP = "nexusMapCFG";
    private static final String CFG_KEY_STATE = "prevState";

    /* Definition JSON files */
    private static final String DEF_FILE_REGIONS = "RegionDef.json";
    private static final String DEF_FILE_SPRITES = "SpriteDef.json";

    private static final String TELE_NAME_PATTERN = "<col=ffffff>(\\S+)</col> :  (.+)";

    @Inject
    private Client client;

    @Inject
    private ClientThread clientThread;

    @Inject
    private NexusConfig config;

    @Inject
    private ConfigManager configManager;

    @Inject
    private SpriteManager spriteManager;

    private RegionDefinition[] regionDefinitions;
    private SpriteDefinition[] spriteDefinitions;

    private boolean mapEnabled;
    private boolean switchingModes;
    private String teleportAction;

    private Map<String, Teleport> availableTeleports;

    /* Widgets */
    private List<Integer> hiddenWidgetIDs;
    private UIGraphic mapGraphic;
    private UIGraphic[] indexRegionGraphics;
    private UIButton[] indexRegionIcons;
    private UICheckBox mapToggleCheckbox;

    private UIPage indexPage;
    private List<UIPage> mapPages;

    @Override
    public void startup() {
        this.loadDefinitions();
        this.createHiddenWidgetList();

        // Add the custom sprites to the sprite manager
        this.spriteManager.addSpriteOverrides(spriteDefinitions);
    }

    @Provides
    public NexusConfig getConfig(ConfigManager configManager) {
        return configManager.getConfig(NexusConfig.class);
    }

    @Override
    public void shutdown() {
        this.regionDefinitions = null;
        this.hiddenWidgetIDs.clear();

        // Remove the custom sprites
        this.spriteManager.removeSpriteOverrides(spriteDefinitions);
    }

    /**
     * Loads the definition files
     */
    private void loadDefinitions() {
        // Construct an instance of GSON
        Gson gson = new Gson();

        // Load the definitions files for the regions and sprite override
        this.regionDefinitions = loadDefinitionResource(RegionDefinition[].class, DEF_FILE_REGIONS, gson);
        this.spriteDefinitions = loadDefinitionResource(SpriteDefinition[].class, DEF_FILE_SPRITES, gson);
    }

    /**
     * Loads a definition resource from a JSON file
     *
     * @param classType the class into which the data contained in the JSON file will be read into
     * @param resource  the name of the resource (file name)
     * @param gson      a reference to the GSON object
     * @param <T>       the class type
     * @return the data read from the JSON definition file
     */
    private <T> T loadDefinitionResource(Class<T> classType, String resource, Gson gson) {
        // Load the resource as a stream and wrap it in a reader
        InputStream resourceStream = classType.getResourceAsStream(resource);
        InputStreamReader definitionReader = new InputStreamReader(resourceStream);

        // Load the objects from the JSON file
        return gson.fromJson(definitionReader, classType);
    }

    /**
     * Creates a list of widgets that the plugin does not require
     * in order to function, for them to be hidden and shown as required
     */
    private void createHiddenWidgetList() {
        this.hiddenWidgetIDs = new ArrayList<>();

        this.hiddenWidgetIDs.add(ID_PORTAL_MODEL);
        this.hiddenWidgetIDs.add(ID_SCRY_TEXT);
        this.hiddenWidgetIDs.add(ID_SCRY_SELECT);
        this.hiddenWidgetIDs.add(ID_SCROLLBOX_BORDER);
        this.hiddenWidgetIDs.add(ID_TELEPORT_LIST);
        this.hiddenWidgetIDs.add(ID_SCROLLBAR);
    }

    @Subscribe
    public void onMenuOptionClicked(MenuOptionClicked e) {
        // When switching between Teleport and Scry mode in the standard
        // Nexus menu, the entire interface is reloaded, triggering the
        // WidgetLoaded event. By listening out for a menu option click
        // event on either of the radio buttons, we can set a flag indicating
        // that the widget reload was triggered by the switching of the mode
		if (e.getWidgetId() == ID_SCRY_RADIO_PANE) {
			this.switchingModes = true;
		}
    }

    @Subscribe
    public void onVarbitChanged(VarbitChanged e) {
        // Update the action text in the menu
        this.teleportAction = this.getModeAction();
    }

    @Subscribe
    public void onWidgetLoaded(WidgetLoaded e) {
        if (e.getGroupId() == GROUP_NEXUS_PORTAL) {
            // The main window layer
            Widget window = this.client.getWidget(ID_PORTAL_WINDOW);

            // Builds a list of teleports that are
            // actually available to the player
            this.buildAvailableTeleportList();
            this.updateDisplayedMenu();

            // Create the page objects, onto which the UI
            // components will be placed
            this.createMenuPages();

            // Create the custom widgets
            this.createIndexMenu(window);
            this.createMapGraphic(window);
            this.createBackButton(window);
            this.createTeleportWidgets(window);
            this.createToggleCheckbox(window);

            this.updateMapState(window);
        }
    }

    /**
     * Shows or hides the default menu widgets
     *
     * @param visible the desired visibility state of the widgets,
     *                true to set them to visible, false for hidden
     */
    private void setDefaultWidgetVisibility(boolean visible) {
        // Iterate though each of the non essential widgets
        for (Integer packedID : this.hiddenWidgetIDs) {
            // Update their visibility
            this.client.getWidget(packedID).setHidden(!visible);
        }
    }

    /**
     * Constructs the list of teleports available for the player to use
     */
    private void buildAvailableTeleportList() {
        this.availableTeleports = new HashMap<>();

        // Compile the pattern that will match the teleport label
        // and place the hotkey and teleport name into groups
        Pattern labelPattern = Pattern.compile(TELE_NAME_PATTERN);

        // Get the parent widgets containing the label list, for both
        // the primary type teleports and alternate type
        Widget primaryParent = this.client.getWidget(ID_LOC_LABELS_PRIMARY);
        Widget alternateParent = this.client.getWidget(ID_LOC_LABELS_ALTERNATE);

        // Fetch all teleports for both the primary and alternate teleport widgets,
        // appending the results of both to the available teleports maps
        this.availableTeleports.putAll(this.getTeleportsFromLabelWidget(primaryParent, false, labelPattern));
        this.availableTeleports.putAll(this.getTeleportsFromLabelWidget(alternateParent, true, labelPattern));
    }

    /**
     * Updates which menu is being displayed
     */
    private void updateDisplayedMenu() {
        // If the menu is currently switching modes, don't update
        if (this.switchingModes) {
            // Reset the flag
            this.switchingModes = false;
        } else {
            // Set the initial map state from config
            this.mapEnabled = this.getInitialMapState();
        }
    }

    /**
     * Gets the preferred initial menu to display upon
     * first opening the Nexus menu, as set in the config
     *
     * @return true if the initial menu should be the map,
     * or false if the original menu should be displayed
     */
    private boolean getInitialMapState() {
        switch (config.initialMode()) {
            case NEXUS_MAP:
                return true;
            case DEFAULT_MENU:
                return false;
            case REMEMBER_PREVIOUS:
                return this.getPreviousDisplayMode();
        }
        return false;
    }

    /**
     * Extracts information from a nexus portals teleport list and returns the information as a Teleport list,
     * containing the name, index, shortcut key and type of teleport (either primary or alternate)
     *
     * @param labelParent the widget containing a teleport list
     * @param alt         true if this widget contains alternate teleports, false if primary
     * @param pattern     a compiled pattern for matching the text contained in the list item widgets
     * @return a list containing all available teleports for the provided widget
     */
    private Map<String, Teleport> getTeleportsFromLabelWidget(Widget labelParent, boolean alt, Pattern pattern) {
        // Grab the children of the widget, each of which have a text
        // attribute containing the teleport location name and key shortcut
        Widget[] labelWidgets = labelParent.getDynamicChildren();

        // Create a map in which to place the available teleport options
        Map<String, Teleport> teleports = new HashMap<>();

        for (Widget child : labelWidgets) {
            // Create a pattern matcher with the widgets text content
            Matcher matcher = pattern.matcher(child.getText());

            // If the text doesn't match the pattern, skip onto the next
			if (!matcher.matches()) {
				continue;
			}

            // Extract the pertinent information
            String shortcutKey = matcher.group(1);
            String teleportName = matcher.group(2);

            // Construct a new teleport object for us to add to the map of available teleports
            teleports.put(teleportName, new Teleport(teleportName, child.getIndex(), shortcutKey, alt));
        }

        return teleports;
    }

    /**
     * Creates the pages for the nexus menu, which are used to group the
     * various UI components in order to conveniently switch between them
     */
    private void createMenuPages() {
        this.indexPage = new UIPage();
        this.mapPages = new ArrayList<>(regionDefinitions.length);

        // Add a page for each region
		for (int i = 0; i < regionDefinitions.length; i++) {
			this.mapPages.add(new UIPage());
		}
    }

    /**
     * Creates the widgets and components required for the index menu,
     * such as the index maps and the region icons
     *
     * @param window the layer on which to create the widgets
     */
    private void createIndexMenu(Widget window) {
        // Create a graphic widget for the background image of the index page
        Widget backingWidget = window.createChild(-1, WidgetType.GRAPHIC);

        // Wrap in a UIGraphic, set dimensions, position and sprite
        UIGraphic indexBackingGraphic = new UIGraphic(backingWidget);
        indexBackingGraphic.setPosition(MAP_SPRITE_POS_X, MAP_SPRITE_POS_Y);
        indexBackingGraphic.setSize(INDEX_MAP_SPRITE_WIDTH, INDEX_MAP_SPRITE_HEIGHT);
        indexBackingGraphic.setSprite(REGION_MAP_MAIN);

        // Initialise the arrays for the map graphics and icons
        this.indexRegionGraphics = new UIGraphic[regionDefinitions.length];
        this.indexRegionIcons = new UIButton[regionDefinitions.length];

        // Add the backing graphic to the index page
        this.indexPage.add(indexBackingGraphic);

        for (int i = 0; i < regionDefinitions.length; i++) {
            // Get definition for the region
            RegionDefinition regionDef = this.regionDefinitions[i];

            // Create a widget for the region sprite graphic
            Widget regionGraphic = window.createChild(-1, WidgetType.GRAPHIC);

            // Wrap in UIGraphic, update the size and position to match that of
            // the backing graphic. Set the sprite to that of the current region
            this.indexRegionGraphics[i] = new UIGraphic(regionGraphic);
            this.indexRegionGraphics[i].setPosition(MAP_SPRITE_POS_X, MAP_SPRITE_POS_Y);
            this.indexRegionGraphics[i].setSize(INDEX_MAP_SPRITE_WIDTH, INDEX_MAP_SPRITE_HEIGHT);
            this.indexRegionGraphics[i].setSprite(regionDef.getIndexSprite());

            // Add the component to the index page
            this.indexPage.add(this.indexRegionGraphics[i]);

            // If there's no teleports defined for this region, skip onto the next
            // before the icon widget is created and has its listeners attached
			if (!regionDef.hasTeleports()) {
				continue;
			}

            // Create the widget for the regions icon
            Widget regionIcon = window.createChild(-1, WidgetType.GRAPHIC);

            // Get the definition for the regions icon
            IconDefinition iconDef = regionDef.getIcon();

            // Wrap in UIBUtton, position the component. attach listeners, etc.
            this.indexRegionIcons[i] = new UIButton(regionIcon);
            this.indexRegionIcons[i].setName(regionDef.getName());
            this.indexRegionIcons[i].setPosition(iconDef.getX(), iconDef.getY());
            this.indexRegionIcons[i].setSize(MAP_ICON_WIDTH, MAP_ICON_HEIGHT);
            this.indexRegionIcons[i].setSprites(iconDef.getSpriteStandard(), iconDef.getSpriteHover());
            this.indexRegionIcons[i].setOnHoverListener((c) -> onIconHover(regionDef.getId()));
            this.indexRegionIcons[i].setOnLeaveListener((c) -> onIconLeave(regionDef.getId()));
            this.indexRegionIcons[i].addAction(ACTION_TEXT_SELECT, () -> onIconClicked(regionDef.getId()));

            // Add to the index page
            this.indexPage.add(this.indexRegionIcons[i]);
        }
    }

    /**
     * Creates the graphic used to display the custom map sprite on each of the map pages
     *
     * @param window the layer on which to create the widget
     */
    private void createMapGraphic(Widget window) {
        // Create the widget for the map graphic
        Widget mapWidget = window.createChild(-1, WidgetType.GRAPHIC);

        // Wrap the widget in a UIGraphic
        this.mapGraphic = new UIGraphic(mapWidget);
        this.mapGraphic.setPosition(7, 35);
        this.mapGraphic.setSize(REGION_MAP_SPRITE_WIDTH, REGION_MAP_SPRITE_HEIGHT);

        // Add the map graphic to each of the map pages
        this.mapPages.forEach(page -> page.add(this.mapGraphic));
    }

    /**
     * Creates the back arrow, used to return to the index page
     *
     * @param window the layer on which to create the widget
     */
    private void createBackButton(Widget window) {
        // Create the widget for the button
        Widget backArrowWidget = window.createChild(-1, WidgetType.GRAPHIC);

        // Wrap as a button, set the position, sprite, etc.
        UIButton backArrowButton = new UIFadeButton(backArrowWidget);
        backArrowButton.setSprites(SpriteID.GE_BACK_ARROW_BUTTON);
        backArrowButton.setPosition(13, 41);
        backArrowButton.setSize(30, 23);

        // Assign the callback for the button
        backArrowButton.addAction(ACTION_TEXT_BACK, this::onBackButtonPressed);

        // Add the back arrow to each map page
        this.mapPages.forEach(page -> page.add(backArrowButton));
    }

    /**
     * Creates the teleport icon widgets and places them
     * in their correct position on the nexus widget pane
     *
     * @param window the layer on which to create the widget
     */
    private void createTeleportWidgets(Widget window) {
        // Iterate through each of the map regions
        for (int i = 0; i < regionDefinitions.length; i++) {
            // Current map region
            RegionDefinition regionDef = this.regionDefinitions[i];

            // Get the definitions for the teleports within this map region
            TeleportDefinition[] teleportDefs = regionDef.getTeleports();

            // Iterate through each of the *defined* teleports, not just
            // the teleports that are available to the player
            for (TeleportDefinition teleportDef : teleportDefs) {
                // Create the teleport icon widget
                Widget teleportWidget = window.createChild(-1, WidgetType.GRAPHIC);

                // Create a button wrapper for the teleport widget. Set the dimensions,
                // the position and the visibility to hidden
                UIButton teleportButton = new UIButton(teleportWidget);
                teleportButton.setSize(TELE_ICON_SIZE, TELE_ICON_SIZE);
                teleportButton.setX(teleportDef.getSpriteX());
                teleportButton.setY(teleportDef.getSpriteY());
                teleportButton.setVisibility(false);

                // Add the teleport button to this regions map page
                this.mapPages.get(i).add(teleportButton);

                // Check that the teleport is available to the player
                if (this.isTeleportAvailable(teleportDef)) {
                    // Grab the teleport from the list of available teleports
                    Teleport teleport = this.getAvailableTeleport(teleportDef);

                    // Set the sprite to the active icon for this spell
                    teleportButton.setSprites(teleportDef.getEnabledSprite());

                    // Get the teleport name, formatted with alias
                    String teleportName = getFormattedLocationName(teleportDef);

                    // If enabled in the config, prepend the shortcut key for this
                    // teleport to the beginning of the teleport name
					if (this.config.displayShortcuts()) {
						teleportName = this.prependShortcutKey(teleportName, teleport.getKeyShortcut());
					}

                    // Assign the teleport name
                    teleportButton.setName(teleportName);

                    // Set the teleport action type, which will either be Teleport
                    // or Scry, depending on the value of the VarBit
                    this.teleportAction = this.getModeAction();

                    // Add the menu options and listener, activate listeners
                    teleportButton.addAction(teleportAction, () -> triggerTeleport(teleport));
                } else {
                    // If the spell isn't available to the player, display the
                    // deactivated spell icon instead
                    teleportButton.setSprites(teleportDef.getDisabledSprite());
                }
            }
        }
    }

    /**
     * Creates the checkbox for toggling the state of the map
     *
     * @param window the layer on which to create the widget
     */
    private void createToggleCheckbox(Widget window) {
        // Create the graphic widget for the checkbox
        Widget toggleWidget = window.createChild(-1, WidgetType.GRAPHIC);
        Widget labelWidget = window.createChild(-1, WidgetType.TEXT);

        // Wrap in checkbox, set size, position, etc.
        UICheckBox mapToggle = new UICheckBox(toggleWidget, labelWidget);
        mapToggle.setPosition(10, 10);
        mapToggle.setName(NAME_TEXT_TOGGLE);
        mapToggle.setEnabled(this.mapEnabled);
        mapToggle.setText("Show Map");
        mapToggle.setToggleListener(this::onMapStateToggled);
    }

    /**
     * Updates the state of the widgets depending on map state
     *
     * @param window the window layer
     */
    private void updateMapState(Widget window) {
        // If the map is enabled, display the custom widgets
        if (this.mapEnabled) {
            // Hide the default widgets and display the map index map
            this.setDefaultWidgetVisibility(false);
            this.displayIndexPage();
        } else {
            // Hide all custom widgets and show the default widgets
            this.indexPage.setVisibility(false);
            this.mapPages.forEach(page -> page.setVisibility(false));
            this.setDefaultWidgetVisibility(true);
        }

        // Save the new map mode to the config
        this.setPreviousDisplayMode(mapEnabled);
    }

    /**
     * Displays the index page and makes sure
     * that each of the map pages are hidden
     */
    private void displayIndexPage() {
        this.indexPage.setVisibility(true);
        this.mapPages.forEach(page -> page.setVisibility(false));
    }

    /**
     * Displays the map page for the given region ID
     *
     * @param regionID the region ID to display
     */
    private void displayMapPage(int regionID) {
        // Hide the index page
        this.indexPage.setVisibility(false);

        // Make sure all other map pages a hidden
        this.mapPages.forEach(page -> page.setVisibility(false));
        this.mapPages.get(regionID).setVisibility(true);

        // Set the sprite to that of the specified region
        this.mapGraphic.setSprite(regionDefinitions[regionID].getMapSprite());
    }

    /**
     * Called when the map state checkbox is toggled
     *
     * @param src the checkbox component
     */
    private void onMapStateToggled(UIComponent src) {
        // The checkbox component
        UICheckBox toggleCheckbox = (UICheckBox) src;

        // Update the map enabled flag
        this.mapEnabled = toggleCheckbox.isEnabled();

        // Update the map state
        this.updateMapState(client.getWidget(ID_PORTAL_PANEL));

        // *Boop*
        this.client.playSoundEffect(SoundEffectID.UI_BOOP);
    }

    /**
     * Called when the mouse enters the icon
     *
     * @param regionID the ID of the region represented by the icon
     */
    private void onIconHover(int regionID) {
        // Move the map sprite for this region up by 2 pixels, and
        // set the opacity to 75% opaque
        this.indexRegionGraphics[regionID].setY(MAP_SPRITE_POS_Y - 2);
        this.indexRegionGraphics[regionID].setOpacity(.75f);
        this.indexRegionGraphics[regionID].getWidget().revalidate();
    }

    /**
     * Called when the mouse exits the icon
     *
     * @param regionID the ID of the region represented by the icon
     */
    private void onIconLeave(int regionID) {
        // Restore the original position and set back to fully opaque
        this.indexRegionGraphics[regionID].setY(MAP_SPRITE_POS_Y);
        this.indexRegionGraphics[regionID].setOpacity(1.0f);
        this.indexRegionGraphics[regionID].getWidget().revalidate();
    }

    /**
     * Called when a region icon is selected
     *
     * @param regionID the ID of the region represented by the icon
     */
    private void onIconClicked(int regionID) {
        // Display the map page for the region
        this.displayMapPage(regionID);

        // *Boop*
        this.client.playSoundEffect(SoundEffectID.UI_BOOP);
    }

    /**
     * Called when the back arrow has been pressed
     */
    private void onBackButtonPressed() {
        // Go back to the index page
        this.displayIndexPage();

        // *Boop*
        this.client.playSoundEffect(SoundEffectID.UI_BOOP);
    }

    /**
     * Teleports the player to the specified teleport location
     *
     * @param teleport the teleport location
     */
    private void triggerTeleport(Teleport teleport) {
        // Get the appropriate widget parent for the teleport, depending
        // on whether the teleport is of primary or alternate type
        int packedID = teleport.isAlt() ? ID_KEYEVENTS_ALTERNATE : ID_KEYEVENTS_PRIMARY;

        // Get the child index of the teleport
        final int widgetIndex = teleport.getChildIndex();

        // Call a CS2 script which will trigger the widget's keypress event.
        // Credit to Abex for discovering this clever trick.
        this.clientThread.invokeLater(() -> client.runScript(SCRIPT_TRIGGER_KEY, packedID, widgetIndex));
    }

    /**
     * Checks if there's a teleport available for a given teleport definition
     *
     * @param teleportDefinition the teleport definition
     * @return true, if the teleport is available to the player, otherwise false
     */
    private boolean isTeleportAvailable(TeleportDefinition teleportDefinition) {
        return this.availableTeleports.containsKey(teleportDefinition.getName());
    }

    /**
     * Gets the teleport corresponding to the specified teleport definition
     *
     * @param teleportDefinition the teleport definition
     * @return the teleport option
     */
    private Teleport getAvailableTeleport(TeleportDefinition teleportDefinition) {
        return this.availableTeleports.get(teleportDefinition.getName());
    }

    /**
     * Gets the mode the menu was in at last use, this value is
     * stored in the config manager and persists between sessions
     *
     * @return true if the last used mode was the map mode, false
     * if it was in the default nexus menu mode
     */
    private boolean getPreviousDisplayMode() {
        // Get the stored previous display mode
        Boolean mode = this.configManager.getConfiguration(CFG_GROUP, CFG_KEY_STATE, Boolean.class);

        // If the mode has yet to be defined, return false
		if (mode == null) {
			return false;
		}

        // Otherwise return the value pulled from config
        return mode;
    }

    /**
     * Stores the display mode the menu was last in into the config
     *
     * @param mode true for map mode, false for the standard menu
     */
    private void setPreviousDisplayMode(boolean mode) {
        this.configManager.setConfiguration(CFG_GROUP, CFG_KEY_STATE, mode);
    }

    /**
     * Gets the menu action appropriate for the current nexus mode
     *
     * @return the action string
     */
    private String getModeAction() {
        // Get the current mode for the nexus. 0 = Teleport, 1 = Scry
        int mode = this.client.getVarbitValue(VARBIT_NEXUS_MODE);

        // Return "Teleport" or "Scry", depending on the mode
        return (mode == 1) ? ACTION_TEXT_SCRY : ACTION_TEXT_TELE;
    }

    /**
     * Prepends the shortcut key to the teleport name
     *
     * @param name the teleport name
     * @param key  the shortcut key
     * @return the teleport name with the shortcut key prepended
     */
    private String prependShortcutKey(String name, String key) {
        return String.format("[%s] %s", key, name);
    }

    /**
     * Creates a formatted string which is to be used as the name
     * for the teleport icons. The string contains the base name of
     * the teleport, and the alias name of the teleport, if is applicable.
     *
     * @param teleportDefinition the teleport definition
     * @return the formatted string
     */
    private String getFormattedLocationName(TeleportDefinition teleportDefinition) {
        // Create the base name
        String name = teleportDefinition.getName();

        // If this location has an alias, append it to the
        // end of the name string, enclosed in parenthesis
		if (teleportDefinition.hasAlias()) {
			name += String.format(" (%s)", teleportDefinition.getAlias());
		}

        return name;
    }
}
