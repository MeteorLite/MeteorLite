package meteor.plugins.hootagility;

import com.google.inject.Inject;
import com.google.inject.Provides;
import meteor.config.ConfigManager;
import meteor.eventbus.Subscribe;
import meteor.plugins.Plugin;
import meteor.plugins.PluginDescriptor;
import dev.hoot.api.commons.Rand;
import dev.hoot.api.entities.Players;
import dev.hoot.api.entities.TileItems;
import dev.hoot.api.entities.TileObjects;
import dev.hoot.api.movement.Movement;
import dev.hoot.api.widgets.Dialog;
import net.runelite.api.Client;
import net.runelite.api.Player;
import net.runelite.api.TileItem;
import net.runelite.api.TileObject;
import net.runelite.api.events.GameTick;

@PluginDescriptor(
				name = "Hoot Agility",
				enabledByDefault = false
)
public class HootAgilityPlugin extends Plugin {
	@Inject
	private HootAgilityConfig hootAgilityConfig;

	@Inject
	private Client client;
	private Course course;

	@Override
	public void startup() {
		if (course == Course.NEAREST) {
			course = Course.getNearest();
		} else {
			course = hootAgilityConfig.course();
		}
	}

	@SuppressWarnings("unused")
	@Subscribe
	private void onGameTick(GameTick e) {
		if (Dialog.canContinue()) {
			Dialog.continueSpace();
			return;
		}

		Player local = Players.getLocal();

		Obstacle obstacle = course.getNext(local);
		if (obstacle == null) {
			logger.error("No obstacle detected");
			return;
		}

		TileObject obs = obstacle.getId() != 0 ? TileObjects.getNearest(obstacle.getId())
						: TileObjects.getNearest(x -> x.hasAction(obstacle.getAction()) && x.getName().equals(obstacle.getName()));

		if (client.getEnergy() > Rand.nextInt(5, 55) && !Movement.isRunEnabled()) {
			Movement.toggleRun();
			return;
		}

		TileItem mark = TileItems.getNearest("Mark of grace");
		if (mark != null && obstacle.getArea().contains(mark.getTile())) {
			mark.pickup();
			return;
		}

		if (obs != null) {
			if (local.getAnimation() != -1 || local.isMoving()) {
				return;
			}

			obs.interact(obstacle.getAction());
			return;
		}

		logger.error("Obstacle was null");
	}

	@Provides
	public HootAgilityConfig getConfig(ConfigManager configManager) {
		return configManager.getConfig(HootAgilityConfig.class);
	}
}
