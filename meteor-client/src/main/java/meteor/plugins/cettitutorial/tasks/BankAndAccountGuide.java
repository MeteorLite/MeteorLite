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

public class BankAndAccountGuide implements PluginTask {

	@Override
	public boolean validate() {
		return Game.getClient().getVarpValue(281) < 550;
	}

	private void talkToGuide() {
		if (Dialog.canContinue()) {
			Dialog.continueSpace();
			return;
		}

		if (!Players.getLocal().isIdle()) {
			return;
		}

		NPC guide = NPCs.getNearest("Account Guide");

		if (guide == null) {
			return;
		}

		guide.interact("Talk-to");

	}

	private void enterArea() {
		if (!Players.getLocal().isIdle()) {
			return;
		}

		TileObject door =  TileObjects.getNearest(9721);

		if (door == null) {
			return;
		}

		door.interact(0);
	}

	private void leaveArea() {
		if (!Players.getLocal().isIdle()) {
			return;
		}

		TileObject door =  TileObjects.getNearest(9722);

		if (door == null) {
			return;
		}

		door.interact(0);
	}

	private void openAccountManagement() {
		if (Dialog.canContinue()) {
			Dialog.continueSpace();
			return;
		}
		Widget account = Game.getClient().getWidget(164, 38);

		if (account == null) {
			return;
		}

		account.interact("Account Management");
	}

	private void useBank() {
		if (!Players.getLocal().isIdle()) {
			return;
		}

		TileObject bank = TileObjects.getNearest(10083);

		if (bank == null) {
			return;
		}

		bank.interact("Use");
	}

	private void usePollingBooth() {
		if (Dialog.canContinue()) {
			Dialog.continueSpace();
			return;
		}

		if (!Players.getLocal().isIdle()) {
			return;
		}

		TileObject booth = TileObjects.getNearest(26815);

		if (booth == null) {
			return;
		}

		booth.interact("Use");
	}

	@Override
	public int execute() {
		switch (Game.getClient().getVarpValue(281)) {
			case 510 -> useBank();
			case 520 -> usePollingBooth();
			case 525 -> enterArea();
			case 530, 532 -> talkToGuide();
			case 531 -> openAccountManagement();
			case 540 -> leaveArea();
		}
		return 1000;
	}
}
