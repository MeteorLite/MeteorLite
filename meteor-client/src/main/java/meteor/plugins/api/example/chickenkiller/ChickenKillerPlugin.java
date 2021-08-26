package meteor.plugins.api.example.chickenkiller;

import com.google.inject.Provides;
import meteor.config.ConfigManager;
import meteor.eventbus.Subscribe;
import meteor.plugins.Plugin;
import meteor.plugins.PluginDescriptor;
import meteor.plugins.api.commons.Time;
import meteor.plugins.api.entities.NPCs;
import meteor.plugins.api.entities.Players;
import meteor.plugins.api.entities.TileItems;
import meteor.plugins.api.entities.TileObjects;
import meteor.plugins.api.game.Combat;
import meteor.plugins.api.items.Bank;
import meteor.plugins.api.items.Equipment;
import meteor.plugins.api.items.Inventory;
import meteor.plugins.api.movement.Movement;
import meteor.plugins.api.movement.Reachable;
import meteor.ui.overlay.OverlayLayer;
import meteor.ui.overlay.OverlayManager;
import meteor.ui.overlay.OverlayPosition;
import meteor.ui.overlay.OverlayPriority;
import net.runelite.api.Item;
import net.runelite.api.NPC;
import net.runelite.api.TileItem;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameTick;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@PluginDescriptor(
        name = "Chicken Killer",
        description = "Weed"
)
@Singleton
public class ChickenKillerPlugin extends Plugin {
    @Inject
    private ScheduledExecutorService executor;

    @Inject
    private ChickenKillerConfig chickenKillerConfig;

    @Inject
    private ChickenKillerOverlay overlay;

    @Inject
    private OverlayManager overlayManager;

    @Override
    public void startup() {
        overlayManager.add(overlay);
        overlay.setPosition(OverlayPosition.DYNAMIC);
        overlay.setLayer(OverlayLayer.ABOVE_WIDGETS);
        overlay.setPriority(OverlayPriority.LOW);

        executor.scheduleAtFixedRate(() -> {
            if (!chickenKillerConfig.enabled()) {
                return;
            }

            try {
                if (Movement.isWalking()) {
                    return;
                }

                if (Bank.isOpen()) {
                    if (!Inventory.contains(995)) {
                        logger.debug("Withdrawing coins");
                        Bank.withdrawAll(995, Bank.WithdrawMode.DEFAULT);
                        return;
                    }

                    if (Inventory.contains("Feather")) {
                        logger.debug("Depositing feathers");
                        Bank.depositAll("Feather");
                        return;
                    }
                }

                Item shield = Equipment.getFirst("Iron kiteshield (t)");
                if (shield != null) {
                    shield.interact("Remove");
                    return;
                }

                Item bones = Inventory.getFirst("Bones");
                if (bones != null) {
                    bones.interact("Bury");
                    return;
                }

                TileItem loot = TileItems.getNearest(x -> x.getName() != null &&
                        (x.getName().equals("Bones") || x.getName().equals("Feather")));
                if (loot != null) {
                    if (!Reachable.isInteractable(loot.getTile())) {
                        Movement.walkTo(loot.getTile().getWorldLocation());
                        return;
                    }

                    loot.pickup();
                    return;
                }

                if (Players.getLocal().getInteracting() != null) {
                    return;
                }

                NPC chicken = Combat.getAttackableNPC(x -> x.getName() != null
                        && x.getName().equals("Chicken") && !x.isDead()
                );
                if (chicken == null) {
                    Movement.walkTo(new WorldPoint(3233, 3293, 0));
                    return;
                }

                overlay.setLocatable(chicken);
                if (!Reachable.isInteractable(chicken)) {
                    Movement.walkTo(chicken.getWorldLocation());
                    return;
                }

                chicken.interact("Attack");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }, 0, 1000, TimeUnit.MILLISECONDS);
    }

    @Provides
    public ChickenKillerConfig getConfig(ConfigManager configManager) {
        return configManager.getConfig(ChickenKillerConfig.class);
    }

    @Override
    public void shutdown() {
        overlayManager.remove(overlay);
    }
}
