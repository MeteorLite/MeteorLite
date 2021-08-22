package meteor.plugins.api.debug.walking;

import com.google.inject.Provides;
import meteor.config.ConfigManager;
import meteor.eventbus.Subscribe;
import meteor.plugins.Plugin;
import meteor.plugins.PluginDescriptor;
import meteor.plugins.api.movement.Movement;
import meteor.ui.overlay.OverlayLayer;
import meteor.ui.overlay.OverlayManager;
import meteor.ui.overlay.OverlayPosition;
import meteor.ui.overlay.OverlayPriority;
import net.runelite.api.GameState;
import net.runelite.api.MenuAction;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.MenuOptionClicked;
import org.sponge.util.Logger;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.concurrent.ScheduledExecutorService;

@PluginDescriptor(
        name = "Walker debug",
        description = "Weed"
)
@Singleton
public class WalkerDebugPlugin extends Plugin {
    private final Logger logger = new Logger("WalkerDebugPlugin");

    @Inject
    private ScheduledExecutorService executorService;

    @Inject
    private OverlayManager overlayManager;

    @Inject
    private WalkerDebugOverlay overlay;

    @Inject
    private WalkerDebugConfig config;

    @Provides
    public WalkerDebugConfig getConfig(ConfigManager configManager) {
        return configManager.getConfig(WalkerDebugConfig.class);
    }

    @Override
    public void startup() {
        overlay.setPosition(OverlayPosition.DYNAMIC);
        overlay.setLayer(OverlayLayer.ABOVE_WIDGETS);
        overlay.setPriority(OverlayPriority.LOW);

        overlayManager.add(overlay);
    }

    @Override
    public void shutdown() {
    }

    @Subscribe
    public void onGameTick(GameTick e) {
        if (Movement.isWalking()) {
            return;
        }

        if (config.walk()) {
            WorldPoint walkPoint = new WorldPoint(config.x(), config.y(), client.getPlane());
            logger.debug("Destination is {} {}", walkPoint.getX(), walkPoint.getY());
            overlay.setTile(walkPoint);
            executorService.execute(() -> Movement.walkTo(walkPoint));
        }
    }

    @Subscribe
    public void onMenuOptionClicked(MenuOptionClicked e) {
        if (e.getMenuAction() == MenuAction.WALK && config.walk()) {
            e.consume();
        }
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged e) {
        if (e.getGameState() == GameState.LOGIN_SCREEN) {
            client.setUsername("");
            client.setPassword("");
        }
    }
}