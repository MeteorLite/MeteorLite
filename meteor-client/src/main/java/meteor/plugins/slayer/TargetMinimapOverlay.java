/*
 * Copyright (c) 2018, James Swindle <wilingua@gmail.com>
 * Copyright (c) 2018, Adam <Adam@sigterm.info>
 * Copyright (c) 2018, Shaun Dreclin <shaundreclin@gmail.com>
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
package meteor.plugins.slayer;

import meteor.ui.overlay.Overlay;
import meteor.ui.overlay.OverlayLayer;
import meteor.ui.overlay.OverlayPosition;
import meteor.ui.overlay.OverlayUtil;
import net.runelite.api.NPC;
import net.runelite.api.Point;

import javax.inject.Inject;
import java.awt.*;
import java.util.List;

public class TargetMinimapOverlay extends Overlay
{
	private final SlayerConfig config;
	private final SlayerPlugin plugin;

	@Inject
	TargetMinimapOverlay(SlayerConfig config, SlayerPlugin plugin)
	{
		this.config = config;
		this.plugin = plugin;
		setPosition(OverlayPosition.DYNAMIC);
		setLayer(OverlayLayer.ABOVE_WIDGETS);
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		if (!config.highlightTargets())
		{
			return null;
		}

		List<NPC> targets = plugin.getHighlightedTargets();
		for (NPC target : targets)
		{
			renderTargetOverlay(graphics, target, config.getTargetColor());
		}

		return null;
	}

	private void renderTargetOverlay(Graphics2D graphics, NPC actor, Color color)
	{
		Point minimapLocation = actor.getMinimapLocation();
		if (minimapLocation != null)
		{
			OverlayUtil.renderMinimapLocation(graphics, minimapLocation, color);
		}
	}
}
