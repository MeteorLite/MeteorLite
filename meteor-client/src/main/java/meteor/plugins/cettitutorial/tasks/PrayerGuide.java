package meteor.plugins.cettitutorial.tasks;

import meteor.PluginTask;
import dev.hoot.api.entities.NPCs;
import dev.hoot.api.entities.Players;
import dev.hoot.api.entities.TileObjects;
import dev.hoot.api.game.Game;
import dev.hoot.api.movement.Movement;
import dev.hoot.api.widgets.Dialog;
import net.runelite.api.NPC;
import net.runelite.api.TileObject;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.widgets.Widget;

public class PrayerGuide implements PluginTask {

	@Override
	public boolean validate() {
		return Game.getClient().getVarpValue(281) < 620;
	}

	private void talkToGuide() {
		if (Dialog.canContinue()) {
			Dialog.continueSpace();
			return;
		}
		if (!Players.getLocal().isIdle()) {
			return;
		}

		NPC guide = NPCs.getNearest("Brother Brace");
		WorldArea church = new WorldArea(new WorldPoint(3122,3105,0), new WorldPoint(3128,3107,0));

		if (guide == null) {
			Movement.walk(church);
			return;
		}

		guide.interact(0);
	}

	private void leaveArea() {
		if (!Players.getLocal().isIdle()) {
			return;
		}

		TileObject door = TileObjects.getNearest(9723);

		if (door == null) {
			return;
		}

		door.interact(0);
	}

	private void openPrayer() {
		if (Dialog.canContinue()) {
			Dialog.continueSpace();
			return;
		}
		Widget prayer = Game.getClient().getWidget(164, 56);

		if (prayer == null) {
			return;
		}

		prayer.interact("Prayer");
	}

	private void openFriendsList() {
		if (Dialog.canContinue()) {
			Dialog.continueSpace();
			return;
		}
		Widget friends = Game.getClient().getWidget(164, 39);

		if (friends == null) {
			return;
		}

		friends.interact("Friends List");
	}

	@Override
	public int execute() {
		switch (Game.getClient().getVarpValue(281)) {
			case 550, 570, 600 -> talkToGuide();
			case 560 -> openPrayer();
			case 580 -> openFriendsList();
			case 610 -> leaveArea();
		}
		return 700;
	}
}
