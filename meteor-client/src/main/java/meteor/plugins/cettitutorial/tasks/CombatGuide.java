package meteor.plugins.cettitutorial.tasks;

import meteor.PluginTask;
import dev.hoot.api.commons.Time;
import dev.hoot.api.entities.NPCs;
import dev.hoot.api.entities.Players;
import dev.hoot.api.entities.TileObjects;
import dev.hoot.api.game.Game;
import dev.hoot.api.items.Equipment;
import dev.hoot.api.items.Inventory;
import dev.hoot.api.movement.Movement;
import dev.hoot.api.movement.Reachable;
import dev.hoot.api.widgets.Dialog;
import net.runelite.api.*;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.widgets.Widget;

public class CombatGuide implements PluginTask {

	@Override
	public boolean validate() {
		return Game.getClient().getVarpValue(281) < 510;
	}

	private void talkToGuide() {
		if (Dialog.canContinue()) {
			Dialog.continueSpace();
			return;
		}

		if (Players.getLocal().isMoving()) {
			return;
		}

		NPC instructor = NPCs.getNearest("Combat Instructor");

		if (instructor == null) {
			return;
		}

		instructor.interact("Talk-to");

	}

	private void enterArea() {
		if (!Players.getLocal().isIdle()) {
			return;
		}

		TileObject gate = TileObjects.getNearest(9719);

		if (gate == null) {
			return;
		}

		gate.interact(0);
	}

	private void leaveArea() {
		if (!Players.getLocal().isIdle()) {
			return;
		}

		TileObject ladder = TileObjects.getNearest(9727);

		if (ladder == null) {
			return;
		}

		ladder.interact(0);
	}

	private void openEquipment() {
		if (Dialog.canContinue()) {
			Dialog.continueSpace();
			return;
		}

		Widget equipment = Game.getClient().getWidget(164, 55);

		if (equipment == null) {
			return;
		}

		equipment.interact("Worn Equipment");
	}

	private void viewEquipment() {
		if (Dialog.canContinue()) {
			Dialog.continueSpace();
			return;
		}

		Widget viewEquipment = Game.getClient().getWidget(387, 1);

		if (viewEquipment == null) {
			return;
		}

		viewEquipment.interact("View equipment stats");
	}

	private void openCombat() {
		if (Dialog.canContinue()) {
			Dialog.continueSpace();
			return;
		}
		Widget combat = Game.getClient().getWidget(164, 51);

		if (combat == null) {
			return;
		}

		combat.interact("Combat Options");
	}

	private void equipEquipItems(int... items) {
		// When inside equipment stats interface
		Widget eq_items = Game.getClient().getWidget(85, 0);

		if (eq_items == null) {
			return;
		}

		if (eq_items.getChildren() == null) {
			return;
		}

		for (Widget eq_item : eq_items.getChildren()) {
			for (int item : items) {
				if (eq_item.getItemId() == item) {
					eq_item.interact("Equip");
				}
			}
		}
	}

	private void equipItems(int... items) {
		for (Item invItem : Inventory.getAll()) {
			for (int item : items)
				if (invItem.getId() == item) {
					invItem.interact("Wield");
				}
		}
	}

	private void killRat(boolean ranged) {
		if (!Players.getLocal().isIdle()) {
			return;
		}

		if (ranged) {
			WorldArea rangedSpot = new WorldArea(new WorldPoint(3106,9519, 0), new WorldPoint(3112,9525, 0));
			if (!rangedSpot.contains(Players.getLocal().getWorldLocation())) {
				Movement.walk(rangedSpot);
				return;
			}
		}

		NPC rat = NPCs.getNearest("Giant rat");
		rat.interact("Attack");
		Time.sleepUntil(rat::isDead, 5000);
	}

	@Override
	public int execute() {
		switch (Game.getClient().getVarpValue(281)) {
			case 370, 410 -> talkToGuide();
			case 390 -> openEquipment();
			case 400 -> viewEquipment();
			case 405 -> equipEquipItems(1205);
			case 420 -> equipItems(ItemID.BRONZE_SWORD, ItemID.WOODEN_SHIELD);
			case 430 -> openCombat();
			case 440 -> enterArea();
			case 450, 460 -> killRat(false);
			case 470 -> {
				if (Reachable.isWalkable(NPCs.getNearest("Combat Instructor").getWorldLocation())) {
					talkToGuide();
				} else {
					enterArea();
				}
			}
			case 480, 490 -> {
				if (!Equipment.contains(ItemID.SHORTBOW) || !Equipment.contains(ItemID.BRONZE_ARROW)) {
					equipItems(ItemID.SHORTBOW, ItemID.BRONZE_ARROW);
				} else {
					killRat(true);
				}
			}
			case 500 -> leaveArea();
		}
		return 700;
	}
}
