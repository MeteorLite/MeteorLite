package meteor.plugins.regions;

import com.google.inject.Provides;
import dev.hoot.api.game.Game;
import dev.hoot.api.movement.pathfinder.GlobalCollisionMap;
import dev.hoot.api.movement.pathfinder.RegionManager;
import lombok.Getter;
import meteor.config.ConfigManager;
import meteor.eventbus.Subscribe;
import meteor.events.PlaneChanged;
import meteor.plugins.Plugin;
import meteor.plugins.PluginDescriptor;
import meteor.ui.overlay.OverlayManager;
import net.runelite.api.GameState;
import net.runelite.api.MenuEntry;
import net.runelite.api.events.ClientTick;
import net.runelite.api.events.ConfigButtonClicked;
import net.runelite.api.events.GameStateChanged;

import javax.inject.Inject;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.zip.GZIPInputStream;

@PluginDescriptor(name = "Regions")
public class RegionPlugin extends Plugin {
    @Inject
    private OverlayManager overlayManager;

    @Inject
    private RegionOverlay overlay;

    @Inject
    private AddTransportDialog transportDialog;

    @Inject
    private RegionManager regionManager;

    @Inject
    private GlobalCollisionMap collisionMap;

    public static boolean selectingSourceTile = false;
    public static boolean selectingDestinationTile = false;
    public static boolean selectingObject = false;

    @Override
    public void startup() {
        eventBus.register(transportDialog);

        updateCollisionMap();

        overlayManager.add(overlay);
    }

    @Override
    public void shutdown() {
        overlayManager.remove(overlay);
        eventBus.unregister(transportDialog);
    }

    @Subscribe
    public void onClientTick(ClientTick e) {
        if (selectingSourceTile) {
            client.setLeftClickMenuEntry(new MenuEntry("Set", "<col=00ff00>Source tile", TileSelection.SOURCE.id, -1, -1,
                    -1, false));
            return;
        }

        if (selectingDestinationTile) {
            client.setLeftClickMenuEntry(new MenuEntry("Set", "<col=00ff00>Destination tile",
                    TileSelection.DESTINATION.id, -1, -1, -1, false));
            return;
        }

        if (selectingObject) {
            client.setLeftClickMenuEntry(new MenuEntry("Select", "<col=00ff00>Transport object",
                    TileSelection.OBJECT.id, -1, -1, -1, false));
        }
    }

    @Subscribe
    public void onConfigButtonClicked(ConfigButtonClicked e) {
        if (!e.getGroup().equals("regions")) {
            return;
        }

        switch (e.getKey()) {
            case "download":
                updateCollisionMap();
                break;
            case "transport":
                if (transportDialog == null) {
                    logger.error("Add transport UI was not loaded somehow");
                    return;
                }

                transportDialog.display();
                break;
        }
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged e) {
        if (e.getGameState() != GameState.LOGGED_IN) {
            return;
        }

        regionManager.sendRegion();
    }

    @Subscribe
    public void onPlaneChanged(PlaneChanged e) {
        if (Game.getState() != GameState.LOGGED_IN) {
            return;
        }

        regionManager.sendRegion();
    }

    private void updateCollisionMap() {
        try (InputStream is = new URL(RegionManager.API_URL + "/regions").openStream()) {
            collisionMap.overwrite(new GlobalCollisionMap(readGzip(is.readAllBytes())));
        } catch (IOException e) {
            logger.error("Error downloading collision data: {}", e.getMessage());
        }
    }

    private byte[] readGzip(byte[] input) throws IOException {
        return new GZIPInputStream(new ByteArrayInputStream(input)).readAllBytes();
    }

    @Override
    @Provides
    public RegionConfig getConfig(ConfigManager configManager) {
        return configManager.getConfig(RegionConfig.class);
    }

    enum TileSelection {
        SOURCE(-420),
        DESTINATION(-421),
        OBJECT(-422);

        @Getter
        private final int id;

        TileSelection(int id) {
            this.id = id;
        }
    }
}
