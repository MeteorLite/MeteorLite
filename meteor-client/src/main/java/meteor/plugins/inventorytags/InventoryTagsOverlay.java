/*
 * Copyright (c) 2018 kulers
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
package meteor.plugins.inventorytags;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import net.runelite.api.widgets.WidgetItem;
import meteor.game.ItemManager;
import meteor.ui.overlay.WidgetItemOverlay;
import meteor.util.ColorUtil;
import meteor.util.ImageUtil;

import javax.inject.Inject;
import java.awt.*;
import java.awt.image.BufferedImage;

public class InventoryTagsOverlay extends WidgetItemOverlay
{
	private final ItemManager itemManager;
	private final InventoryTagsPlugin plugin;
	private final InventoryTagsConfig config;
	private final Cache<Long, Image> fillCache;

	@Inject
	private InventoryTagsOverlay(ItemManager itemManager, InventoryTagsPlugin plugin, InventoryTagsConfig config)
	{
		this.itemManager = itemManager;
		this.plugin = plugin;
		this.config = config;
		showOnEquipment();
		showOnInventory();
		fillCache = CacheBuilder.newBuilder()
			.concurrencyLevel(1)
			.maximumSize(32)
			.build();
	}

	@Override
	public void renderItemOverlay(Graphics2D graphics, int itemId, WidgetItem widgetItem)
	{
		final String group = plugin.getTag(itemId);
		if (group != null)
		{
			final Color color = plugin.getGroupNameColor(group);
			if (color != null)
			{
				Rectangle bounds = widgetItem.getCanvasBounds();
				if (config.showTagOutline())
				{
					final BufferedImage outline = itemManager.getItemOutline(itemId, widgetItem.getQuantity(), color);
					graphics.drawImage(outline, (int) bounds.getX(), (int) bounds.getY(), null);
				}

				if (config.showTagFill())
				{
					final Image image = getFillImage(color, widgetItem.getId(), widgetItem.getQuantity());
					graphics.drawImage(image, (int) bounds.getX(), (int) bounds.getY(), null);
				}

				if (config.showTagUnderline())
				{
					int heightOffSet = (int) bounds.getY() + (int) bounds.getHeight() + 2;
					graphics.setColor(color);
					graphics.drawLine((int) bounds.getX(), heightOffSet, (int) bounds.getX() + (int) bounds.getWidth(), heightOffSet);
				}
			}
		}
	}

	private Image getFillImage(Color color, int itemId, int qty)
	{
		long key = (((long) itemId) << 32) | qty;
		Image image = fillCache.getIfPresent(key);
		if (image == null)
		{
			final Color fillColor = ColorUtil.colorWithAlpha(color, config.fillOpacity());
			image = ImageUtil.fillImage(itemManager.getImage(itemId, qty, false), fillColor);
			fillCache.put(key, image);
		}
		return image;
	}

	void invalidateCache()
	{
		fillCache.invalidateAll();
	}
}
