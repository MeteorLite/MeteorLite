package meteor.plugins.cettitutorial.tasks;

import meteor.PluginTask;
import dev.hoot.api.commons.Time;
import dev.hoot.api.entities.NPCs;
import dev.hoot.api.entities.Players;
import dev.hoot.api.entities.TileObjects;
import dev.hoot.api.game.Game;
import dev.hoot.api.items.Inventory;
import dev.hoot.api.movement.Movement;
import dev.hoot.api.widgets.Dialog;
import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;

public class MiningGuide implements PluginTask {

	@Override
	public boolean validate() {
		return Game.getClient().getVarpValue(281) < 370;
	}

	private void talkToGuide() {
		if (Dialog.canContinue()) {
			Dialog.continueSpace();
			return;
		}

		if (!Players.getLocal().isIdle()) {
			return;
		}

		NPC guide = NPCs.getNearest("Mining Instructor");
		WorldPoint miningArea = new WorldPoint(3080, 9505, 0);

		if (guide == null) {
			Movement.walkTo(miningArea, 10);

			return;
		}

		guide.interact(0);
	}

	private void leaveArea() {
		if (Players.getLocal().isMoving()) {
			return;
		}

		TileObject gate = TileObjects.getNearest(9717);

		if (gate == null) {
			return;
		}

		gate.interact(0);
	}

	private void mineOre(int ore) {
		if (!Players.getLocal().isIdle()) {
			return;
		}

		TileObject mineOre = TileObjects.getNearest(ore);

		if (mineOre == null) {
			return;
		}

		mineOre.interact(0);

		if (ore == 10080) {
			Time.sleepUntil(() -> Inventory.contains(ItemID.TIN_ORE), 5000);
		} else {
			Time.sleepUntil(() -> Inventory.contains(ItemID.COPPER_ORE), 5000);
		}

	}

	private void smeltOre() {
		if (!Players.getLocal().isIdle()) {
			return;
		}

		TileObject furnace = TileObjects.getNearest(10082);

		if (furnace == null) {
			return;
		}

		furnace.interact(0);
		Time.sleepUntil(() -> Inventory.contains(ItemID.BRONZE_BAR), 5000);

	}

	private void useAnvil() {
		if (!Players.getLocal().isIdle()) {
			return;
		}

		Widget smithDagger = Game.getClient().getWidget(WidgetInfo.SMITHING_ANVIL_DAGGER);
		TileObject anvil = TileObjects.getNearest(2097);

		if (smithDagger == null) {
			anvil.interact(0);
			return;
		}

		smithDagger.interact("Smith");
	}

	@Override
	public int execute() {
		switch (Game.getClient().getVarpValue(281)) {
			case 260, 330 -> talkToGuide();
			case 270 -> mineOre(10080);
			case 310 -> mineOre(10079);
			case 320 -> smeltOre();
			case 340, 350 -> useAnvil();
			case 360 -> leaveArea();
		}
		return 700;
	}
}
