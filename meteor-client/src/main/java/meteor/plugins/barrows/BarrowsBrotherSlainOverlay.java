/*
 * Copyright (c) 2018, Seth <http://github.com/sethtroll>
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
package meteor.plugins.barrows;

import net.runelite.api.Client;
import net.runelite.api.Varbits;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import meteor.ui.FontManager;
import meteor.ui.overlay.OverlayMenuEntry;
import meteor.ui.overlay.OverlayPanel;
import meteor.ui.overlay.OverlayPosition;
import meteor.ui.overlay.OverlayPriority;
import meteor.ui.overlay.components.LineComponent;

import javax.inject.Inject;
import java.awt.*;
import java.text.DecimalFormat;

import static net.runelite.api.MenuAction.RUNELITE_OVERLAY_CONFIG;
import static meteor.ui.overlay.OverlayManager.OPTION_CONFIGURE;

public class BarrowsBrotherSlainOverlay extends OverlayPanel
{
	private static final DecimalFormat REWARD_POTENTIAL_FORMATTER = new DecimalFormat("##0.00%");

	private final Client client;

	@Inject
	private BarrowsBrotherSlainOverlay(BarrowsPlugin plugin, Client client)
	{
		super(plugin);
		setPosition(OverlayPosition.TOP_LEFT);
		setPriority(OverlayPriority.LOW);
		this.client = client;
		getMenuEntries().add(new OverlayMenuEntry(RUNELITE_OVERLAY_CONFIG, OPTION_CONFIGURE, "Barrows overlay"));
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		// Do not display overlay if potential is null/hidden
		final Widget potential = client.getWidget(WidgetInfo.BARROWS_POTENTIAL);
		if (potential == null || potential.isHidden())
		{
			return null;
		}

		// Hide original overlay
		final Widget barrowsBrothers = client.getWidget(WidgetInfo.BARROWS_BROTHERS);
		if (barrowsBrothers != null)
		{
			barrowsBrothers.setHidden(true);
			potential.setHidden(true);
		}

		for (BarrowsBrothers brother : BarrowsBrothers.values())
		{
			final boolean brotherSlain = client.getVar(brother.getKilledVarbit()) > 0;
			String slain = brotherSlain ? "\u2713" : "\u2717";
			panelComponent.getChildren().add(LineComponent.builder()
				.left(brother.getName())
				.right(slain)
				.rightFont(FontManager.getDefaultFont())
				.rightColor(brotherSlain ? Color.GREEN : Color.RED)
				.build());
		}

		final int rewardPotential = rewardPotential();
		panelComponent.getChildren().add(LineComponent.builder()
			.left("Potential")
			.right(REWARD_POTENTIAL_FORMATTER.format(rewardPotential / 1012f))
			.rightColor(rewardPotential >= 756 && rewardPotential < 881 ? Color.GREEN : rewardPotential < 631 ? Color.WHITE : Color.YELLOW)
			.build());

		return super.render(graphics);
	}

	/**
	 * Compute the barrows reward potential. Potential rewards are based off of the amount of
	 * potential.
	 * <p>
	 * The reward potential thresholds are as follows:
	 * Mind rune - 381
	 * Chaos rune - 506
	 * Death rune - 631
	 * Blood rune - 756
	 * Bolt rack - 881
	 * Half key - 1006
	 * Dragon med - 1012
	 *
	 * @return potential, 0-1012 inclusive
	 * @see <a href="https://twitter.com/jagexkieren/status/705428283509366785?lang=en">source</a>
	 */
	private int rewardPotential()
	{
		// this is from [proc,barrows_overlay_reward]
		int brothers = client.getVar(Varbits.BARROWS_KILLED_AHRIM)
			+ client.getVar(Varbits.BARROWS_KILLED_DHAROK)
			+ client.getVar(Varbits.BARROWS_KILLED_GUTHAN)
			+ client.getVar(Varbits.BARROWS_KILLED_KARIL)
			+ client.getVar(Varbits.BARROWS_KILLED_TORAG)
			+ client.getVar(Varbits.BARROWS_KILLED_VERAC);
		return client.getVar(Varbits.BARROWS_REWARD_POTENTIAL) + brothers * 2;
	}
}
