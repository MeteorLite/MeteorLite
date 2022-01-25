package dev.hoot.api.example.taskplugin.tasks;

import meteor.PluginTask;
import dev.hoot.api.entities.NPCs;
import dev.hoot.api.items.GrandExchange;
import dev.hoot.api.movement.Movement;

public class OpenGE implements PluginTask {
	@Override
	public boolean validate() {
		return NPCs.getNearest("Grand Exchange Clerk") != null && !GrandExchange.isOpen();
	}

	@Override
	public int execute() {
		if (Movement.isWalking()) {
			return 1000;
		}

		NPCs.getNearest("Grand Exchange Clerk").interact("Exchange");
		return 1000;
	}
}
