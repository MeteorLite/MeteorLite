package dev.hoot.api.example.taskplugin.tasks;

import meteor.PluginTask;
import dev.hoot.api.commons.Rand;
import dev.hoot.api.entities.Players;
import dev.hoot.api.game.Game;
import dev.hoot.api.movement.Movement;
import net.runelite.api.coords.WorldPoint;

public class WalkToGE implements PluginTask {
	private static final WorldPoint GE_LOCATION = new WorldPoint(3164, 3485, 0);

	@Override
	public boolean validate() {
		return GE_LOCATION.distanceTo(Players.getLocal()) > 15;
	}

	@Override
	public int execute() {
		if (Movement.isWalking()) {
			if (!Movement.isRunEnabled() && Game.getClient().getEnergy() > Rand.nextInt(5, 15)) {
				Movement.toggleRun();
			}

			return 1000;
		}

		Movement.walkTo(GE_LOCATION);
		return 1500;
	}
}
