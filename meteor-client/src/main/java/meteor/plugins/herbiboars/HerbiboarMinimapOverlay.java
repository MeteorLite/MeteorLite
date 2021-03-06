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
package meteor.plugins.herbiboars;

import com.google.inject.Inject;
import meteor.ui.overlay.Overlay;
import meteor.ui.overlay.OverlayLayer;
import meteor.ui.overlay.OverlayPosition;
import meteor.ui.overlay.OverlayUtil;
import net.runelite.api.Point;
import net.runelite.api.TileObject;

import java.awt.*;
import java.util.Set;

class HerbiboarMinimapOverlay extends Overlay
{
	private final HerbiboarPlugin plugin;
	private final HerbiboarConfig config;

	@Inject
	public HerbiboarMinimapOverlay(HerbiboarPlugin plugin, HerbiboarConfig config)
	{
		setPosition(OverlayPosition.DYNAMIC);
		setLayer(OverlayLayer.ABOVE_WIDGETS);
		this.plugin = plugin;
		this.config = config;
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		if (!config.isTrailShown() || !plugin.isInHerbiboarArea())
		{
			return null;
		}

		TrailToSpot nextTrail = plugin.getNextTrail();
		int finishId = plugin.getFinishId();
		Set<Integer> shownTrailIds = plugin.getShownTrails();

		for (TileObject tileObject : plugin.getTrails().values())
		{
			int id = tileObject.getId();
			Point minimapLocation = tileObject.getMinimapLocation();

			if (minimapLocation == null)
			{
				continue;
			}

			if (shownTrailIds.contains(id) && (finishId > 0 || nextTrail != null && !nextTrail.getFootprintIds().contains(id)))
			{
				OverlayUtil.renderMinimapLocation(graphics, minimapLocation, config.getTrailColor());
			}
		}
		return null;
	}
}
