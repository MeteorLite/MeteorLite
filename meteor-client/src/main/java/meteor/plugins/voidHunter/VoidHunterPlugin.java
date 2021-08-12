package meteor.plugins.voidHunter;

import static net.runelite.api.ItemID.BLACK_SALAMANDER;
import static net.runelite.api.ItemID.ROPE;
import static net.runelite.api.ItemID.SMALL_FISHING_NET;
import static net.runelite.api.ObjectID.NET_TRAP_8996;
import static net.runelite.api.ObjectID.NET_TRAP_9002;
import static net.runelite.api.ObjectID.YOUNG_TREE_9000;

import com.google.inject.Provides;
import java.util.List;
import javax.inject.Inject;
import meteor.config.ConfigManager;
import meteor.eventbus.Subscribe;
import meteor.plugins.Plugin;
import meteor.plugins.PluginDescriptor;
import meteor.plugins.voidutils.OSRSUtils;
import meteor.ui.overlay.OverlayManager;
import net.runelite.api.GameObject;
import net.runelite.api.Item;
import net.runelite.api.Skill;
import net.runelite.api.TileItem;
import net.runelite.api.events.ConfigButtonClicked;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.api.events.StatChanged;
import net.runelite.api.widgets.WidgetItem;

@PluginDescriptor(
    name = "Void Hunter"
)
public class VoidHunterPlugin extends Plugin {

  public static boolean enabled = false;
  @Inject
  OSRSUtils osrs;
  @Inject
  VoidHunterOverlay overlay;
  @Inject
  OverlayManager overlayManager;
  private long lastDelayedAction;
  private String lastStateExecuted;
  private int prevSalamanderCount = 0;
  private int caught = 0;
  private int startXP = 0;
  private int gainedXP = 0;

  @Provides
  public VoidHunterConfig getConfig(ConfigManager configManager) {
    return configManager.getConfig(VoidHunterConfig.class);
  }

  @Subscribe
  public void onGameTick(GameTick event) {
    if (!enabled) {
      return;
    }

    if (client.getLocalPlayer() == null) {
      return;
    }

    if (!client.getLocalPlayer().idle()) {
      return;
    }

    if (nearestItemPickupExecute()) {
      return;
    }

    if (releaseSalamandersExecute()) {
      return;
    }

    // Longer delays for less repetitive actions
    if ((System.currentTimeMillis() - lastDelayedAction) < 1200) {
      return;
    }

    // Check but only pickup if there are 3 active traps
    if (checkCaughtTrapHighPriorityExecute()) {
      return;
    }

    if (setupEmptyTrapExecute()) {
      return;
    }

    checkCaughtTrapLowPriorityExecute();
  }

  @Subscribe
  private void onStatChanged(StatChanged event) {
    if (enabled) {
      if (event.getSkill() == Skill.HUNTER) {
        gainedXP = event.getXp() - startXP;
      }
    }
  }

  @Subscribe
  private void onItemContainerChanged(ItemContainerChanged event) {
    int salamanderCount = 0;
    for (Item item : event.getItemContainer().getItems()) {
      if (item.getId() == BLACK_SALAMANDER) {
        salamanderCount++;
      }
    }

    if (salamanderCount > prevSalamanderCount) {
      caught++;
    }

    prevSalamanderCount = salamanderCount;
  }

  private boolean setupEmptyTrapExecute() {
    if (activeTraps() != null) {
      if (activeTraps().size() >= 5) {
        return false;
      }
    }
    lastStateExecuted = "Setup Trap";
    GameObject nearestEmptyTrap = nearestEmptyTrap();
    if (nearestEmptyTrap != null) {
      nearestEmptyTrap.interact("Set-trap");
      lastDelayedAction = System.currentTimeMillis();
      return true;
    }
    return false;
  }

  private boolean checkCaughtTrapLowPriorityExecute() {
    lastStateExecuted = "Check Trap";
    GameObject nearestCaughtTrap = nearestCaughtTrap();

    if (nearestCaughtTrap != null) {
      nearestCaughtTrap.interact("Check");
      lastDelayedAction = System.currentTimeMillis();
      return true;
    }
    return false;
  }

  private boolean checkCaughtTrapHighPriorityExecute() {
    lastStateExecuted = "Check Trap";
    if (activeTraps() != null) {
      if (activeTraps().size() >= 3) {
        checkCaughtTrapLowPriorityExecute();
      }
    }
    return false;
  }

  private boolean releaseSalamandersExecute() {
    lastStateExecuted = "Release";
    List<WidgetItem> items = osrs.items(BLACK_SALAMANDER);
    if (items != null) {
      WidgetItem salamanderToDrop = items.get(0);
      salamanderToDrop.interact("Release");
      return true;
    }
    return false;
  }

  private boolean nearestItemPickupExecute() {
    lastStateExecuted = "Pickup";
    TileItem nearestItemToPickup = nearestItemToPickup();
    if (nearestItemToPickup != null) {
      nearestItemToPickup.pickup();
      return true;
    }
    return false;
  }

  public List<GameObject> activeTraps() {
    return osrs.objects(NET_TRAP_9002, NET_TRAP_8996);
  }

  public List<GameObject> emptyTraps() {
    return osrs.objects(YOUNG_TREE_9000);
  }

  public GameObject nearestCaughtTrap() {
    return osrs.nearestObject(NET_TRAP_8996);
  }

  public GameObject nearestEmptyTrap() {
    return osrs.nearestObject(YOUNG_TREE_9000);
  }

  public TileItem nearestItemToPickup() {
    return osrs.nearestLoot(ROPE, SMALL_FISHING_NET);
  }

  @Subscribe
  public void onConfigButtonClicked(ConfigButtonClicked event) {
    if (event.getGroup().equals("voidHunter")) {
      if (event.getKey().equals("startStop")) {
        enabled = !enabled;
      }
    }

    if (enabled) {
      overlay.instanceTimer.reset();
      startXP = client.getSkillExperience(Skill.HUNTER);
    }
  }

  public void startup() {
    overlayManager.add(overlay);
  }

  public void shutdown() {
    overlayManager.remove(overlay);
  }

  public void updateConfig() {

  }

  public String getState() {
    return lastStateExecuted;
  }

  public int getCaught() {
    return caught;
  }

  public int getGainedXP() {
    return gainedXP;
  }
}
