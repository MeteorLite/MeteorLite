package meteor.plugins.cettitutorial.tasks;

import meteor.PluginTask;
import dev.hoot.api.commons.Time;
import dev.hoot.api.entities.NPCs;
import dev.hoot.api.entities.Players;
import dev.hoot.api.entities.TileObjects;
import dev.hoot.api.game.Game;
import dev.hoot.api.items.Inventory;
import dev.hoot.api.widgets.Dialog;
import net.runelite.api.*;

public class CookingGuide implements PluginTask {

	@Override
	public boolean validate() {
		return Game.getClient().getVarpValue(281) < 200;
	}

	private void talkToGuide() {
		if (Dialog.canContinue()) {
			Dialog.continueSpace();
			return;
		}

		if (!Players.getLocal().isIdle()) {
			return;
		}

		NPC chef = NPCs.getNearest("Master Chef");
		if (chef == null) {
			return;
		}

		chef.interact("Talk-to");
	}

	private void enterArea() {
		if (!Players.getLocal().isIdle()) {
			return;
		}

		TileObject door = TileObjects.getNearest(9709);
		if (door == null) {
			return;
		}
		door.interact(0);
	}

	private void leaveArea() {
		if (!Players.getLocal().isIdle()) {
			return;
		}

		TileObject door = TileObjects.getNearest(9710);
		if (door == null) {
			return;
		}
		door.interact(0);
	}

	private void makeDough() {
		if (Players.getLocal().isAnimating()) {
			return;
		}

		Item flour = Inventory.getFirst(2516);
		Item water = Inventory.getFirst(ItemID.BUCKET_OF_WATER);

		flour.useOn(water);
		Time.sleepUntil(() -> Inventory.contains(ItemID.BREAD_DOUGH), 3000);
	}

	private void cookFood() {
		if (!Players.getLocal().isIdle()) {
			return;
		}

		Item dough = Inventory.getFirst(ItemID.BREAD_DOUGH);
		TileObject range = TileObjects.getNearest(9736);

		dough.useOn(range);
		Time.sleepUntil(() -> Inventory.contains(ItemID.BREAD), 3000);
	}

	@Override
	public int execute() {
		switch (Game.getClient().getVarpValue(281)) {
			case 130 -> enterArea();
			case 140 -> talkToGuide();
			case 150 -> makeDough();
			case 160 -> cookFood();
			case 170 -> leaveArea();
		}
		return 700;
	}
}
