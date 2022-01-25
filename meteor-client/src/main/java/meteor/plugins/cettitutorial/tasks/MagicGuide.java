package meteor.plugins.cettitutorial.tasks;

import meteor.PluginTask;
import dev.hoot.api.entities.NPCs;
import dev.hoot.api.entities.Players;
import dev.hoot.api.game.Game;
import dev.hoot.api.magic.Magic;
import dev.hoot.api.magic.Regular;
import dev.hoot.api.movement.Movement;
import dev.hoot.api.widgets.Dialog;
import net.runelite.api.NPC;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.widgets.Widget;

public class MagicGuide implements PluginTask {

	@Override
	public boolean validate() {
		return Game.getClient().getVarpValue(281) < 670;
	}

	private void talkToGuide() {
		if (Dialog.canContinue()) {
			Dialog.continueSpace();
			return;
		}

		if (!Players.getLocal().isIdle()) {
			return;
		}

		NPC guide = NPCs.getNearest("Magic Instructor");
		WorldArea magicHouse = new WorldArea(new WorldPoint(3140,3088, 0), new WorldPoint(3142,3090, 0));

		if (guide == null) {
			Movement.walk(magicHouse);
			return;
		}

		guide.interact(0);
	}

	private void openSpellBook() {
		if (Dialog.canContinue()) {
			Dialog.continueSpace();
			return;
		}
		Widget magic = Game.getClient().getWidget(164, 57);

		if (magic == null) {
			return;
		}

		magic.interact("Magic");
	}


	private void killChicken() {
		if (!Players.getLocal().isIdle()) {
			return;
		}

		WorldArea magicHouse = new WorldArea(new WorldPoint(3140,3088, 0), new WorldPoint(3142,3090, 0));

		if (!magicHouse.contains(Players.getLocal().getWorldLocation())) {
			Movement.walk(magicHouse);
		}

		Magic.cast(Regular.WIND_STRIKE, NPCs.getNearest("Chicken"));
	}

	@Override
	public int execute() {
		switch (Game.getClient().getVarpValue(281)) {
			case 620, 640, 670 -> talkToGuide();
			case 630 -> openSpellBook();
			case 650 -> killChicken();
		}
		return 700;
	}
}
