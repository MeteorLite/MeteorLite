package dev.hoot.api.example.deathevent;

import meteor.eventbus.Subscribe;
import meteor.plugins.Plugin;
import meteor.plugins.PluginDescriptor;
import dev.hoot.api.entities.NPCs;
import dev.hoot.api.entities.Players;
import dev.hoot.api.entities.TileObjects;
import dev.hoot.api.game.Game;
import dev.hoot.api.game.GameThread;
import dev.hoot.api.widgets.Dialog;
import net.runelite.api.NPC;
import net.runelite.api.TileObject;
import net.runelite.api.events.GameTick;
import net.runelite.api.widgets.Widget;

import java.util.List;
import java.util.stream.Collectors;

@PluginDescriptor(
				name = "Death Event",
				description = "Completes the Death random event for first deaths",
				enabledByDefault = false
)
public class DeathEventPlugin extends Plugin {
	@SuppressWarnings("unused")
	@Subscribe
	public void onGameTick(GameTick e) {
		if (!Game.getClient().isInInstancedRegion()) {
			return;
		}

		NPC death = NPCs.getNearest("Death");
		if (death == null) {
			return;
		}

		if (Players.getLocal().isMoving()) {
			return;
		}

		if (!Dialog.isOpen()) {
			death.interact("Talk-to");
			return;
		}

		if (Dialog.canContinue()) {
			Dialog.continueSpace();
			return;
		}

		if (Dialog.isViewingOptions()) {
			List<Widget> completedDialogs = Dialog.getOptions().stream()
							.filter(x -> x.getText() != null && x.getText().contains("<str>"))
							.collect(Collectors.toList());
			if (completedDialogs.size() >= 4) {
				TileObject portal = TileObjects.getNearest("Portal");
				if (portal != null) {
					portal.interact("Use");
					return;
				}
			}

			Widget incompleteDialog = Dialog.getOptions().stream()
							.filter(x -> !completedDialogs.contains(x))
							.findFirst()
							.orElse(null);
			if (incompleteDialog != null && !GameThread.invokeLater(incompleteDialog::isHidden)) {
				Dialog.chooseOption(Dialog.getOptions().indexOf(incompleteDialog) + 1);
			}
		}
	}
}
