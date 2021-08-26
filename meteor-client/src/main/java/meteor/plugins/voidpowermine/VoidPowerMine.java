package meteor.plugins.voidpowermine;

import com.google.inject.Provides;
import meteor.config.ConfigManager;
import meteor.eventbus.Subscribe;
import meteor.plugins.Plugin;
import meteor.plugins.PluginDescriptor;
import meteor.plugins.api.entities.Players;
import meteor.plugins.api.entities.TileObjects;
import meteor.plugins.api.game.Game;
import meteor.plugins.api.items.Inventory;
import net.runelite.api.GameObject;
import net.runelite.api.ObjectID;
import net.runelite.api.Player;
import net.runelite.api.events.ConfigButtonClicked;
import net.runelite.api.events.GameTick;

@PluginDescriptor(
    name = "Void 3t Fishing",
        enabledByDefault = false
)
public class VoidPowerMine extends Plugin {

  public static boolean enabled = false;

  int[] ironRocks = new int[] {
      ObjectID.ROCKS_11364,
      ObjectID.ROCKS_11365
  };

  @Provides
  public VoidPowermineConfig getConfig(ConfigManager configManager) {
    return configManager.getConfig(VoidPowermineConfig.class);
  }

  @Subscribe
  public void onGameTick(GameTick event) {
    if (!enabled) {
      return;
    }

    if (client.getLocalPlayer() == null) {
      return;
    }

    for (Player p : Game.getClient().getPlayers())
      if (p.getInteracting() != null)
        if (p.getInteracting() == Game.getClient().getLocalPlayer()) {
          GameObject escape = (GameObject) TileObjects.getNearest(17385);
          if (escape != null)
            escape.interact("Climb-up");
        }


    if (!Players.getLocal().isIdle())
      return;

    if (Inventory.getFirst(440) != null)
    Inventory.getFirst(440).interact("Drop");

    GameObject nearestRock = (GameObject) TileObjects.getNearest(ironRocks);
    if (nearestRock != null) {
      if (nearestRock.getDistanceFromLocalPlayer() < 300)
        nearestRock.interact("Mine");
    }
  }

  @Subscribe
  public void onConfigButtonClicked(ConfigButtonClicked event) {
    if (event.getGroup().equals("voidPowermine")) {
      if (event.getKey().equals("startStop")) {
        enabled = !enabled;
      }
    }
  }


}
