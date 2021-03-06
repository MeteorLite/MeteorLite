/*
 * Copyright (c) 2018, Seth <Sethtroll3@gmail.com>
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
package meteor.plugins.blastfurnace;

import java.awt.Dimension;
import java.awt.Graphics2D;
import javax.inject.Inject;

import meteor.ui.overlay.OverlayMenuEntry;
import meteor.ui.overlay.OverlayPanel;
import meteor.ui.overlay.OverlayPosition;
import meteor.ui.overlay.components.LineComponent;
import meteor.util.QuantityFormatter;
import net.runelite.api.Client;

import static meteor.ui.overlay.OverlayManager.OPTION_CONFIGURE;
import static net.runelite.api.MenuAction.RUNELITE_OVERLAY_CONFIG;
import static net.runelite.api.Varbits.BLAST_FURNACE_COFFER;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import static org.apache.commons.lang3.time.DurationFormatUtils.formatDuration;

class BlastFurnaceCofferOverlay extends OverlayPanel
{
	private static final float COST_PER_HOUR = 72000.0f;

	private final Client client;
	private final BlastFurnacePlugin plugin;
	private final BlastFurnaceConfig config;

	@Inject
	private BlastFurnaceCofferOverlay(Client client, BlastFurnacePlugin plugin, BlastFurnaceConfig config)
	{
		super(plugin);
		setPosition(OverlayPosition.TOP_LEFT);
		this.client = client;
		this.plugin = plugin;
		this.config = config;
		getMenuEntries().add(new OverlayMenuEntry(RUNELITE_OVERLAY_CONFIG, OPTION_CONFIGURE, "Coffer overlay"));
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		if (plugin.getConveyorBelt() == null)
		{
			return null;
		}

		Widget sack = client.getWidget(WidgetInfo.BLAST_FURNACE_COFFER);

		if (sack != null)
		{
			final int coffer = client.getVar(BLAST_FURNACE_COFFER);

			sack.setHidden(true);

			panelComponent.getChildren().add(LineComponent.builder()
				.left("Coffer:")
				.right(QuantityFormatter.quantityToStackSize(coffer) + " gp")
				.build());

			if (config.showCofferTime())
			{
				final long millis = (long) (coffer / COST_PER_HOUR * 60 * 60 * 1000);

				panelComponent.getChildren().add(LineComponent.builder()
					.left("Time:")
					.right(formatDuration(millis, "H'h' m'm' s's'", true))
					.build());
			}
		}

		return super.render(graphics);
	}
}
