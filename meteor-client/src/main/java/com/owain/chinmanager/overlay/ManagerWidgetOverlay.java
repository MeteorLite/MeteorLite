package com.owain.chinmanager.overlay;

import com.google.inject.Inject;
import com.owain.chinmanager.ChinManager;
import com.owain.chinmanager.ChinManagerPlugin;
import com.owain.chinmanager.ui.plugins.options.OptionsConfig;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import net.runelite.api.widgets.WidgetItem;
import meteor.game.ItemManager;
import meteor.ui.overlay.Overlay;
import meteor.ui.overlay.OverlayLayer;
import meteor.ui.overlay.OverlayPosition;
import meteor.ui.overlay.OverlayUtil;

public class ManagerWidgetOverlay extends Overlay
{
	private final ItemManager itemManager;
	private final ChinManager chinManager;
	private final OptionsConfig optionsConfig;

	@Inject
	public ManagerWidgetOverlay(ItemManager itemManager, ChinManager chinManager, OptionsConfig optionsConfig)
	{
		this.itemManager = itemManager;
		this.chinManager = chinManager;
		this.optionsConfig = optionsConfig;

		setPosition(OverlayPosition.DYNAMIC);
		setLayer(OverlayLayer.ABOVE_WIDGETS);
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		if (chinManager.getActivePlugins().size() == 0 || !optionsConfig.showOverlays())
		{
			return null;
		}

		if (ChinManagerPlugin.getHighlightWidget() != null)
		{
			if (!ChinManagerPlugin.getHighlightWidget().isHidden() && !ChinManagerPlugin.getHighlightWidget().isSelfHidden())
			{
				OverlayUtil.renderPolygon(graphics, rectangleToPolygon(ChinManagerPlugin.getHighlightWidget().getBounds()), new Color(255, 100, 100));
			}
		}

		if (!ChinManagerPlugin.getHighlightWidgetItem().isEmpty())
		{
			for (WidgetItem widgetItem : ChinManagerPlugin.getHighlightWidgetItem())
			{
				if (widgetItem == null || widgetItem.getWidget().isHidden() || widgetItem.getWidget().isSelfHidden())
				{
					continue;
				}

				Rectangle bounds = widgetItem.getCanvasBounds();

				final BufferedImage outline = itemManager.getItemOutline(widgetItem.getId(), widgetItem.getQuantity(), new Color(255, 100, 100));
				graphics.drawImage(outline, (int) bounds.getX(), (int) bounds.getY(), null);
			}
		}

		return null;
	}

	public static Polygon rectangleToPolygon(Rectangle rect)
	{
		int[] xpoints = {rect.x, rect.x + rect.width, rect.x + rect.width, rect.x};
		int[] ypoints = {rect.y, rect.y, rect.y + rect.height, rect.y + rect.height};
		return new Polygon(xpoints, ypoints, 4);
	}
}
