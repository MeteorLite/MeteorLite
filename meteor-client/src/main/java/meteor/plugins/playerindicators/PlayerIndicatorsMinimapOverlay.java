/*
 * Copyright (c) 2018, Tomas Slusny <slusnucky@gmail.com>
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
package meteor.plugins.playerindicators;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.List;
import javax.inject.Inject;
import net.runelite.api.Player;
import net.runelite.api.Point;
import net.runelite.api.SkullIcon;
import meteor.ui.overlay.Overlay;
import meteor.ui.overlay.OverlayLayer;
import meteor.ui.overlay.OverlayPosition;
import meteor.ui.overlay.OverlayPriority;
import meteor.ui.overlay.OverlayUtil;
import meteor.util.ImageUtil;

public class PlayerIndicatorsMinimapOverlay extends Overlay
{
	private final PlayerIndicatorsService playerIndicatorsExtendedService;
	private final PlayerIndicatorsPlugin plugin;
	private final PlayerIndicatorsConfig config;
	private final BufferedImage skullIcon = ImageUtil.loadImageResource(
      PlayerIndicatorsPlugin.class,
		"skull.png");

	@Inject
	private PlayerIndicatorsMinimapOverlay(final PlayerIndicatorsPlugin plugin, final PlayerIndicatorsConfig config, final PlayerIndicatorsService playerIndicatorsExtendedService)
	{
		this.plugin = plugin;
		this.config = config;
		this.playerIndicatorsExtendedService = playerIndicatorsExtendedService;
		setLayer(OverlayLayer.ABOVE_WIDGETS);
		setPosition(OverlayPosition.DYNAMIC);
		setPriority(OverlayPriority.HIGH);
	}

	private void renderMinimapOverlays(Graphics2D graphics, Player actor, PlayerIndicatorsPlugin.PlayerRelation relation)
	{
		if (!plugin.getLocationHashMap().containsKey(relation) || actor.getName() == null)
		{
			return;
		}

		final List indicationLocations = Arrays.asList(plugin.getLocationHashMap().get(relation));
		final Color color = plugin.getRelationColorHashMap().get(relation);

		if (indicationLocations.contains(
        PlayerIndicatorsPlugin.PlayerIndicationLocation.MINIMAP))
		{
			String name = actor.getName().replace('\u00A0', ' ');

			Point minimapLocation = actor.getMinimapLocation();

			if (minimapLocation != null)
			{
				if (config.showCombatLevel())
				{
					name += "-(" + actor.getCombatLevel() + ")";
				}
				if (actor.getSkullIcon() != null && config.playerSkull() && actor.getSkullIcon() == SkullIcon.SKULL)
				{
					final int width = graphics.getFontMetrics().stringWidth(name);
					final int height = graphics.getFontMetrics().getHeight();
					if (config.skullLocation().equals(
              PlayerIndicatorsPlugin.MinimapSkullLocations.AFTER_NAME))
					{
						OverlayUtil.renderImageLocation(graphics, new Point(minimapLocation.getX()
								+ width, minimapLocation.getY() - height),
							ImageUtil.resizeImage(skullIcon, height, height));
					}
					else
					{
						OverlayUtil.renderImageLocation(graphics, new Point(minimapLocation.getX(),
								minimapLocation.getY() - height),
							ImageUtil.resizeImage(skullIcon, height, height));
						minimapLocation = new Point(minimapLocation.getX() + skullIcon.getWidth(), minimapLocation.getY());
					}
				}
				OverlayUtil.renderTextLocation(graphics, minimapLocation, name, color);
			}

		}

	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		playerIndicatorsExtendedService.forEachPlayer((player, playerRelation) -> renderMinimapOverlays(graphics, player, playerRelation));
		return null;
	}
}
