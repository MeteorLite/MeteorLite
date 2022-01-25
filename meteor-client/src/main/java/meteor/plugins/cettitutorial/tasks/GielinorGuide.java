package meteor.plugins.cettitutorial.tasks;

import meteor.PluginTask;
import dev.hoot.api.entities.NPCs;
import dev.hoot.api.entities.Players;
import dev.hoot.api.entities.TileObjects;
import dev.hoot.api.game.Game;
import dev.hoot.api.widgets.Dialog;
import net.runelite.api.NPC;
import net.runelite.api.TileObject;
import net.runelite.api.widgets.Widget;

public class GielinorGuide implements PluginTask {

	@Override
	public boolean validate() {
		return Game.getClient().getVarpValue(281) < 20;
	}

	private void talkToGuide() {
		if (Dialog.canContinue()) {
			Dialog.continueSpace();
			return;
		}

		if (Dialog.isViewingOptions()) {
			Dialog.chooseOption("I am an experienced player.");
			return;
		}

		if (Players.getLocal().isMoving()) {
			return;
		}

		NPC guide = NPCs.getNearest("Gielinor Guide");
		guide.interact("Talk-to");
	}

	private void openSettings() {
		if (Dialog.canContinue()) {
			Dialog.continueSpace();
		}

		Widget settings = Game.getClient().getWidget(164, 40);

		if (settings == null) {
			return;
		}

		settings.interact("Settings");
	}

	private void leaveArea() {
		if (Players.getLocal().isMoving()) {
			return;
		}

		TileObject door = TileObjects.getNearest(9398);
		if (door == null) {
			return;
		}

		door.interact(0);
	}

	@Override
	public int execute() {
		switch (Game.getClient().getVarpValue(281)) {
			case 2, 7 -> {
				talkToGuide();
			}
			case 3 -> openSettings();
			case 10 -> leaveArea();
		}
		return 700;
	}
}
