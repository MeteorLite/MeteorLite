package meteor.plugins.cettitutorial.tasks;

import meteor.PluginTask;
import dev.hoot.api.entities.NPCs;
import dev.hoot.api.entities.Players;
import dev.hoot.api.entities.TileObjects;
import dev.hoot.api.game.Game;
import dev.hoot.api.game.Vars;
import dev.hoot.api.items.Inventory;
import dev.hoot.api.movement.Movement;
import dev.hoot.api.widgets.Dialog;
import meteor.plugins.cettitutorial.Methods;
import net.runelite.api.Item;
import net.runelite.api.ItemID;
import net.runelite.api.NPC;
import net.runelite.api.TileObject;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.widgets.Widget;

public class SurvivalGuide implements PluginTask {

	@Override
	public boolean validate() {
		return Vars.getVarp(281) < 130;
	}

	private void talkToGuide() {
		if (Dialog.canContinue()) {
			Dialog.continueSpace();
			return;
		}

		if (Players.getLocal().isMoving()) {
			return;
		}

		NPC expert = NPCs.getNearest("Survival Expert");
		if (expert == null) {
			return;
		}

		expert.interact("Talk-to");
	}

	private void openInventory() {
		if (Dialog.canContinue()) {
			Dialog.continueSpace();
		}

		Widget inventory = Game.getClient().getWidget(164, 54);

		if (inventory == null) {
			return;
		}

		inventory.interact("Inventory");
	}

	private void openSkills() {
		if (Dialog.canContinue()) {
			Dialog.continueSpace();
		}

		Widget skills = Game.getClient().getWidget(164, 52);

		if (skills == null) {
			return;
		}

		skills.interact("Skills");
	}

	private void leaveArea() {
		if (Players.getLocal().isMoving()) {
			return;
		}

		TileObject gate = TileObjects.getNearest(9708);
		if (gate == null) {
			return;
		}

		gate.interact(0);
	}

	private void fishPond() {
		if (Players.getLocal().isMoving() || Players.getLocal().isAnimating()) {
			return;
		}

		NPC pond = NPCs.getNearest(3317);
		if (pond == null) {
			return;
		}

		pond.interact("Net");
	}

	private void chopTree() {
		if (Dialog.canContinue()) {
			Dialog.continueSpace();
		}

		if (Players.getLocal().isMoving() || Players.getLocal().isAnimating()) {
			return;
		}

		TileObject tree = TileObjects.getNearest(9730);
		if (tree == null) {
			return;
		}

		tree.interact("Chop down");
	}

	private void lightFire() {
		if (!Players.getLocal().isIdle()) {
			return;
		}

		Item tinderbox = Inventory.getFirst(ItemID.TINDERBOX);
		Item logs = Inventory.getFirst(2511);
		TileObject nearestObject = TileObjects.getNearest(x -> x.getName() != null && !x.getName().equals("null"));

		if (tinderbox == null || logs == null) {
			return;
		}

		if (Players.getLocal().getWorldLocation().equals(nearestObject.getWorldLocation())) {
			WorldPoint newWalkPoint = Methods.getRandomPoint(Players.getLocal().getWorldLocation(), 3, true);
			Movement.walk(newWalkPoint);
			return;
		}

		tinderbox.useOn(logs);
	}

	private void cookFood() {
		if (Players.getLocal().isAnimating()) {
			return;
		}

		Item food = Inventory.getFirst(2514);
		TileObject fire = TileObjects.getNearest(26185);
		if (food == null || fire == null) {
			return;
		}

		food.useOn(fire);
	}

	@Override
	public int execute() {
		switch (Game.getClient().getVarpValue(281)) {
			case 20, 60 -> talkToGuide();
			case 30 -> openInventory();
			case 40 -> fishPond();
			case 50 -> openSkills();
			case 70 -> chopTree();
			case 80 -> lightFire();
			case 90 -> cookFood();
			case 120 -> leaveArea();
		}
		return 1000;
	}
}
