/*
 * Copyright (c) 2018, Kamiel
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package meteor.plugins.raids;

import lombok.Getter;
import meteor.game.WorldService;
import meteor.plugins.raids.solver.Room;
import meteor.ui.overlay.OverlayMenuEntry;
import meteor.ui.overlay.OverlayPanel;
import meteor.ui.overlay.OverlayPosition;
import meteor.ui.overlay.OverlayPriority;
import meteor.ui.overlay.components.ComponentConstants;
import meteor.ui.overlay.components.LineComponent;
import meteor.ui.overlay.components.TitleComponent;
import net.runelite.api.Client;
import net.runelite.api.FriendsChatManager;
import net.runelite.api.Varbits;
import net.runelite.http.api.worlds.World;
import net.runelite.http.api.worlds.WorldRegion;
import net.runelite.http.api.worlds.WorldResult;

import javax.inject.Inject;
import java.awt.*;

import static meteor.ui.overlay.OverlayManager.OPTION_CONFIGURE;
import static net.runelite.api.MenuAction.RUNELITE_OVERLAY;
import static net.runelite.api.MenuAction.RUNELITE_OVERLAY_CONFIG;

class RaidsOverlay extends OverlayPanel
{
	private static final int OLM_PLANE = 0;
	static final String BROADCAST_ACTION = "Broadcast layout";
	static final String SCREENSHOT_ACTION = "Screenshot";

	private final Client client;
	private final RaidsPlugin plugin;
	private final RaidsConfig config;
	private final WorldService worldService;

	@Getter
	private boolean scoutOverlayShown = false;

	@Inject
	private RaidsOverlay(Client client, RaidsPlugin plugin, RaidsConfig config, WorldService worldService)
	{
		super(plugin);
		setPosition(OverlayPosition.TOP_LEFT);
		setPriority(OverlayPriority.LOW);
		this.client = client;
		this.plugin = plugin;
		this.config = config;
		this.worldService = worldService;
		getMenuEntries().add(new OverlayMenuEntry(RUNELITE_OVERLAY_CONFIG, OPTION_CONFIGURE, "Raids overlay"));
		getMenuEntries().add(new OverlayMenuEntry(RUNELITE_OVERLAY, BROADCAST_ACTION, "Raids overlay"));
		getMenuEntries().add(new OverlayMenuEntry(RUNELITE_OVERLAY, SCREENSHOT_ACTION, "Raids overlay"));
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		scoutOverlayShown = shouldShowOverlay();
		if (!scoutOverlayShown)
		{
			return null;
		}

		Color color = Color.WHITE;
		String layout = plugin.getRaid().getLayout().toCodeString();

		if (config.enableLayoutWhitelist() && !plugin.getLayoutWhitelist().contains(layout.toLowerCase()))
		{
			color = Color.RED;
		}

		panelComponent.getChildren().add(TitleComponent.builder()
			.text(layout)
			.color(color)
			.build());

		if (config.fcDisplay())
		{
			color = Color.RED;
			FriendsChatManager friendsChatManager = client.getFriendsChatManager();
			FontMetrics metrics = graphics.getFontMetrics();

			String worldString = "W" + client.getWorld();
			WorldResult worldResult = worldService.getWorlds();
			if (worldResult != null)
			{
				World world = worldResult.findWorld(client.getWorld());
				WorldRegion region = world.getRegion();
				if (region != null)
				{
					String countryCode = region.getAlpha2();
					worldString += " (" + countryCode + ")";
				}
			}

			String owner = "Join a FC";
			if (friendsChatManager != null)
			{
				owner = friendsChatManager.getOwner();
				color = Color.ORANGE;
			}

			panelComponent.setPreferredSize(new Dimension(Math.max(ComponentConstants.STANDARD_WIDTH, metrics.stringWidth(worldString) + metrics.stringWidth(owner) + 14), 0));
			panelComponent.getChildren().add(LineComponent.builder()
				.left(worldString)
				.right(owner)
				.leftColor(Color.ORANGE)
				.rightColor(color)
				.build());
		}

		for (Room layoutRoom : plugin.getRaid().getLayout().getRooms())
		{
			int position = layoutRoom.getPosition();
			RaidRoom room = plugin.getRaid().getRoom(position);

			if (room == null)
			{
				continue;
			}

			color = Color.WHITE;

			switch (room.getType())
			{
				case COMBAT:
					if (plugin.getRoomWhitelist().contains(room.getName().toLowerCase()))
					{
						color = Color.GREEN;
					}
					else if (plugin.getRoomBlacklist().contains(room.getName().toLowerCase())
							|| config.enableRotationWhitelist() && !plugin.getRotationMatches())
					{
						color = Color.RED;
					}

					String name = room == RaidRoom.UNKNOWN_COMBAT ? "Unknown" : room.getName();

					panelComponent.getChildren().add(LineComponent.builder()
						.left(room.getType().getName())
						.right(name)
						.rightColor(color)
						.build());

					break;

				case PUZZLE:
					if (plugin.getRoomWhitelist().contains(room.getName().toLowerCase()))
					{
						color = Color.GREEN;
					}
					else if (plugin.getRoomBlacklist().contains(room.getName().toLowerCase()))
					{
						color = Color.RED;
					}

					name = room == RaidRoom.UNKNOWN_PUZZLE ? "Unknown" : room.getName();

					panelComponent.getChildren().add(LineComponent.builder()
						.left(room.getType().getName())
						.right(name)
						.rightColor(color)
						.build());
					break;
			}
		}

		return super.render(graphics);
	}

	private boolean shouldShowOverlay()
	{
		if (plugin.getRaid() == null
			|| plugin.getRaid().getLayout() == null
			|| !config.scoutOverlay())
		{
			return false;
		}

		if (plugin.isInRaidChambers())
		{
			// If the raid has started
			if (client.getVar(Varbits.RAID_STATE) > 0)
			{
				if (client.getPlane() == OLM_PLANE)
				{
					return false;
				}

				return config.scoutOverlayInRaid();
			}
			else
			{
				return true;
			}
		}

		return plugin.getRaidPartyID() != -1 && config.scoutOverlayAtBank();
	}
}
