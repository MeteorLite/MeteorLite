/*
 * Copyright (c) 2018, GeChallengeM <https://github.com/GeChallengeM>
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
package meteor.plugins.npcstatus;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.util.Random;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import javax.inject.Singleton;

import dev.hoot.api.widgets.Prayers;
import meteor.plugins.npcstatus.NpcStatusConfig.PrayStyle;
import meteor.ui.overlay.Overlay;
import meteor.ui.overlay.OverlayLayer;
import meteor.ui.overlay.OverlayPosition;
import meteor.ui.overlay.OverlayUtil;
import net.runelite.api.Client;
import net.runelite.api.Point;
import net.runelite.api.Prayer;

@Singleton
public class NpcStatusOverlay extends Overlay
{
	private final Client client;
	private final NpcStatusPlugin plugin;
	private int lastTicksLeft = 0;
	private boolean isPraying;
	private Random random = new Random();

	@Inject
	private NpcStatusConfig config;

	@Inject
	private ScheduledExecutorService executorService;

	@Inject
	NpcStatusOverlay(final Client client, final NpcStatusPlugin plugin)
	{
		this.client = client;
		this.plugin = plugin;
		setPosition(OverlayPosition.DYNAMIC);
		setLayer(OverlayLayer.ABOVE_SCENE);
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		if (config.autoPray())
			if (client.getLocalPlayer().isIdle())
				if (isPraying)
					queueDisablePrayers(random.nextInt(300));
		for (MemorizedNPC npc : plugin.getMemorizedNPCs())
		{
			if (npc.getNpc().getInteracting() == client.getLocalPlayer() || client.getLocalPlayer().getInteracting() == npc.getNpc())
			{
				int flickOn = 40 + random.nextInt(70);
				int flickOff = 600 - flickOn + random.nextInt(200);
				switch (npc.getStatus())
				{
					case FLINCHING:
						npc.setTimeLeft(Math.max(0, npc.getFlinchTimerEnd() - client.getTickCount()));
						break;
					case IN_COMBAT_DELAY:
						if (npc.getNpc().getInteracting() == client.getLocalPlayer()) {
							int ticksLeft = Math.max(0, npc.getCombatTimerEnd() - client.getTickCount() - 7);
							if (lastTicksLeft != ticksLeft)
								if (config.autoPray())
									if (ticksLeft == 1)
										enablePrayers(flickOn, flickOff);
							lastTicksLeft = ticksLeft;
						}
						npc.setTimeLeft(lastTicksLeft);
						npc.setTimeLeft(Math.max(0, npc.getCombatTimerEnd() - client.getTickCount() - 7));
						break;
					case IN_COMBAT:
						if (npc.getNpc().getInteracting() == client.getLocalPlayer()) {
							int ticksLeft = Math.max(0, npc.getCombatTimerEnd() - client.getTickCount());
							if (lastTicksLeft != ticksLeft)
								if (config.autoPray())
									if (ticksLeft == 1)
										enablePrayers(flickOn, flickOff);
						lastTicksLeft = ticksLeft;
						}
						npc.setTimeLeft(lastTicksLeft);
						break;
					case OUT_OF_COMBAT:
					default:
						npc.setTimeLeft(0);
						break;
				}

				Point textLocation = npc.getNpc().getCanvasTextLocation(graphics, Integer.toString(npc.getTimeLeft()), npc.getNpc().getLogicalHeight() + 40);

				if (textLocation != null)
				{
					OverlayUtil.renderTextLocation(graphics, textLocation, Integer.toString(npc.getTimeLeft()), npc.getStatus().getColor());
				}
			}
		}
		return null;
	}

	private void enablePrayers(int flickOn, int flickOff) {
		executorService.schedule(() -> {
			if (config.prayStyle() == PrayStyle.MELEE) {
				if (!Prayers.isEnabled(Prayer.PROTECT_FROM_MELEE))
					Prayers.toggle(Prayer.PROTECT_FROM_MELEE);
			}
			else if (config.prayStyle() == PrayStyle.RANGED) {
				if (!Prayers.isEnabled(Prayer.PROTECT_FROM_MISSILES))
					Prayers.toggle(Prayer.PROTECT_FROM_MISSILES);
			}
			else if (config.prayStyle() == PrayStyle.MAGIC) {
				if (!Prayers.isEnabled(Prayer.PROTECT_FROM_MAGIC))
					Prayers.toggle(Prayer.PROTECT_FROM_MAGIC);
			}
			queueDisablePrayers(flickOff);
			isPraying = true;
		}, flickOn, TimeUnit.MILLISECONDS);
	}

	private void queueDisablePrayers(int flickOff) {
		executorService.schedule(() -> {
			if (!Prayers.isEnabled(Prayer.PROTECT_FROM_MELEE))
				if (!Prayers.isEnabled(Prayer.PROTECT_FROM_MELEE))
					if (!Prayers.isEnabled(Prayer.PROTECT_FROM_MELEE))
						return;

			if (config.prayStyle() == PrayStyle.MELEE) {
				if (Prayers.isEnabled(Prayer.PROTECT_FROM_MELEE))
					Prayers.toggle(Prayer.PROTECT_FROM_MELEE);
			}
			else if (config.prayStyle() == PrayStyle.RANGED) {
				if (Prayers.isEnabled(Prayer.PROTECT_FROM_MISSILES))
					Prayers.toggle(Prayer.PROTECT_FROM_MISSILES);
			}
			else if (config.prayStyle() == PrayStyle.MAGIC) {
				if (Prayers.isEnabled(Prayer.PROTECT_FROM_MAGIC))
					Prayers.toggle(Prayer.PROTECT_FROM_MAGIC);
			}
			isPraying = false;
		}, flickOff, TimeUnit.MILLISECONDS);
	}
}
