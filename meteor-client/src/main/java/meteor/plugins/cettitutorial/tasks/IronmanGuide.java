package meteor.plugins.cettitutorial.tasks;

import meteor.PluginTask;
import dev.hoot.api.commons.Time;
import dev.hoot.api.entities.NPCs;
import dev.hoot.api.entities.Players;
import dev.hoot.api.game.Game;
import dev.hoot.api.game.GameThread;
import dev.hoot.api.input.Keyboard;
import dev.hoot.api.movement.Movement;
import dev.hoot.api.widgets.Dialog;
import meteor.plugins.cettitutorial.CettiTutorialConfig;
import meteor.plugins.cettitutorial.CettiTutorialPlugin;
import meteor.plugins.cettitutorial.Methods;
import net.runelite.api.NPC;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;

public class IronmanGuide implements PluginTask {

	CettiTutorialConfig config;
	CettiTutorialPlugin plugin;

	public IronmanGuide(CettiTutorialConfig config, CettiTutorialPlugin plugin) {
		super();
		this.config = config;
		this.plugin = plugin;
	}

	@Override
	public boolean validate() {
		return !config.gameMode().toString().equals("Regular") && !CettiTutorialPlugin.ironTypeSet;
	}

	private void talkToGuide() {
		Widget ironManSetup = Game.getClient().getWidget(215, 1);
		Widget bankPinScreen = Game.getClient().getWidget(WidgetInfo.BANK_PIN_CONTAINER);

		if (ironManSetup != null) {
			return;
		}

		if (bankPinScreen != null) {
			return;
		}

		var pinVarb = GameThread.invokeLater(() -> Game.getClient().getVarbitValue(1776));

		if (pinVarb == 1) {
			return;
		}

		if (Dialog.canContinue()) {
			Dialog.continueSpace();
			return;
		}

		if (Dialog.isViewingOptions()) {
			Dialog.chooseOption("Tell me about", "I'd like to change my");
			return;
		}

		if (!Players.getLocal().isIdle()) {
			return;
		}

		NPC guide = NPCs.getNearest("Iron Man tutor");
		WorldArea ironManTutorLocation = new WorldArea(new WorldPoint(3125, 3075, 0), new WorldPoint(3135, 3095, 0));

		if (guide == null) {
			Movement.walk(ironManTutorLocation);
			return;
		}

		guide.interact(0);
	}


	private void setIronmanType() {
		Widget ironManSetup = Game.getClient().getWidget(215, 1);
		Widget modes = Game.getClient().getWidget(215, 14);

		if (ironManSetup == null) {
			return;
		}

		if (modes == null) {
			return;
		}

		Widget soloMode = modes.getChild(0);
		Widget groupMode = modes.getChild(9);

		if (config.groupIron()) {
			if (soloMode.getActions() == null) {
				groupMode.interact("View");
				return;
			}

			Widget groupIronMan = Game.getClient().getWidget(215, 13);
			Widget groupHardcoreIronMan = Game.getClient().getWidget(215, 31);

			if (groupIronMan == null) {
				return;
			}

			if (groupHardcoreIronMan == null) {
				return;
			}

			if (config.gameMode().equals(Methods.GameMode.IRONMAN)) {
				groupIronMan.interact(0);
			}

			if (config.gameMode().equals(Methods.GameMode.HARDCORE_IRONMAN)) {
				groupHardcoreIronMan.interact(0);
			}


			Time.sleep(600);

		} else {
			if (groupMode.getActions() == null) {
				soloMode.interact("View");
				return;
			}

			Widget ironMan = Game.getClient().getWidget(215, 9);
			Widget hardcoreIronMan = Game.getClient().getWidget(215, 10);
			Widget ultimateIronMan = Game.getClient().getWidget(215, 11);

			if (ironMan == null) {
				return;
			}

			if (hardcoreIronMan == null) {
				return;
			}

			if (ultimateIronMan == null) {
				return;
			}

			if (config.gameMode().equals(Methods.GameMode.IRONMAN)) {
				ironMan.interact(0);
			}

			if (config.gameMode().equals(Methods.GameMode.HARDCORE_IRONMAN)) {
				hardcoreIronMan.interact(0);
			}

			if (config.gameMode().equals(Methods.GameMode.ULTIMATE_IRONMAN)) {
				ultimateIronMan.interact(0);
			}

			Time.sleep(600);
		}
	}


	private void setBankPin() {
		if (Dialog.canContinue()) {
			Dialog.continueSpace();
			return;
		}

		if (Dialog.isViewingOptions()) {
			Dialog.chooseOption("Okay, let me set a PIN.");
			return;
		}

		Widget bankPinScreen = Game.getClient().getWidget(WidgetInfo.BANK_PIN_CONTAINER);
		Widget firstPinEntered = Game.getClient().getWidget(WidgetInfo.BANK_PIN_FIRST_ENTERED);
		Widget pinSection = Game.getClient().getWidget(WidgetInfo.BANK_PIN_TOP_LEFT_TEXT);

		if (bankPinScreen == null) {
			return;
		}

		if (firstPinEntered == null) {
			return;
		}

		if (pinSection == null) {
			return;
		}

		if (pinSection.getText().equals("Set new PIN")) {

			for (char pin : config.bankPin().toCharArray()) {
				Keyboard.type(pin);
				Time.sleep(600);
			}
			Time.sleepUntil(() -> pinSection.getText().equals("Confirm new PIN"), 5000);
			return;
		}

		if (pinSection.getText().equals("Confirm new PIN")) {
			Keyboard.type(config.bankPin());
			Time.sleep(1000);
		}

	}

	private void checkIronStatus() {

		var pinVarb = GameThread.invokeLater(() -> Game.getClient().getVarbitValue(1776));
		if (pinVarb == 1) {
			CettiTutorialPlugin.ironTypeSet = true;
		}
	}

	@Override
	public int execute() {
		checkIronStatus();
		setBankPin();
		setIronmanType();
		talkToGuide();
		return 700;
	}
}
