/*
 * Copyright (c) 2019, Adam <Adam@sigterm.info>
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
package net.runelite.api;

import lombok.Data;
import net.runelite.api.widgets.*;

import java.util.Arrays;
import java.util.List;

import net.runelite.api.widgets.WidgetItem;

@Data
public class Item implements Interactable {
	private final int id;
	private final int quantity;

	private Client client;
	private int slot;

	// Interaction
	private int actionParam;
	private int widgetId;

	public String getName() {
		return client.getItemComposition(getId()).getName();
	}

	@Override
	public String[] getActions() {
		if (WidgetInfo.TO_GROUP(widgetId) == WidgetID.INVENTORY_GROUP_ID) {
			return client.getItemComposition(getId()).getInventoryActions();
		}

		Widget widget = client.getWidget(widgetId);
		if (widget != null) {
			return widget.getActions();
		}

		return null;
	}

	@Override
	public int getActionId(int action) {
		switch (action) {
			case 0:
				if (getActions()[0] == null) {
					return MenuAction.ITEM_USE.getId();
				}

				return MenuAction.ITEM_FIRST_OPTION.getId();
			case 1:
				return MenuAction.ITEM_SECOND_OPTION.getId();
			case 2:
				return MenuAction.ITEM_THIRD_OPTION.getId();
			case 3:
				return MenuAction.ITEM_FOURTH_OPTION.getId();
			case 4:
				return MenuAction.ITEM_FIFTH_OPTION.getId();
			default:
				throw new IllegalArgumentException("action = " + action);
		}
	}

	@Override
	public List<String> actions() {
		return Arrays.asList(getActions());
	}

	@Override
	public void interact(String action) {
		interact(actions().indexOf(action));
	}

	@Override
	public void interact(int index) {
		switch (getType()) {
			case TRADE, TRADE_INVENTORY -> {
				Widget itemWidget = client.getWidget(widgetId);
				if (itemWidget == null) {
					return;
				}
				itemWidget.interact(index);
			}
			case EQUIPMENT -> interact(index, index > 4 ? MenuAction.CC_OP_LOW_PRIORITY.getId()
							: MenuAction.CC_OP.getId());
			case BANK, BANK_INVENTORY -> interact(index, MenuAction.CC_OP.getId());
			case INVENTORY -> interact(getId(), getActionId(index));
			case UNKNOWN -> throw new IllegalStateException("Couldn't detect Item type for itemId: " + getId());
		}
	}

	public void interact(int index, int menuAction) {
		switch (getType()) {
			case TRADE, TRADE_INVENTORY -> {
				Widget itemWidget = client.getWidget(widgetId);
				if (itemWidget == null) {
					return;
				}
				itemWidget.interact(index, menuAction);
			}
			case EQUIPMENT -> interact(index + 1, menuAction, actionParam, widgetId);
			case BANK -> interact(index, menuAction, getSlot(), WidgetInfo.BANK_ITEM_CONTAINER.getPackedId());
			case BANK_INVENTORY -> interact(index, menuAction, getSlot(), WidgetInfo.BANK_INVENTORY_ITEMS_CONTAINER.getPackedId());
			case INVENTORY -> interact(getId(), menuAction, actionParam, widgetId);
			case UNKNOWN -> throw new IllegalStateException("Couldn't detect Item type for itemId: " + getId());
		}
	}

	@Override
	public void interact(int identifier, int opcode, int param0, int param1) {
		client.interact(identifier, opcode, param0, param1);
	}

	public void useOn(TileObject object) {
		client.setSelectedItemWidget(widgetId);
		client.setSelectedItemSlot(getSlot());
		client.setSelectedItemID(getId());
		object.interact(0, MenuAction.ITEM_USE_ON_GAME_OBJECT.getId());
	}

	public void useOn(Item item) {
		client.setSelectedItemWidget(widgetId);
		client.setSelectedItemSlot(item.getSlot());
		client.setSelectedItemID(item.getId());
		item.interact(0, MenuAction.ITEM_USE_ON_WIDGET_ITEM.getId());
	}

	public void useOn(Actor actor) {
		MenuAction menuAction = actor instanceof NPC ? MenuAction.ITEM_USE_ON_NPC : MenuAction.ITEM_USE_ON_PLAYER;
		client.setSelectedItemWidget(widgetId);
		client.setSelectedItemSlot(getSlot());
		client.setSelectedItemID(getId());
		actor.interact(0, menuAction.getId());
	}

	public void useOn(Widget widget) {
		client.setSelectedItemWidget(widgetId);
		client.setSelectedItemSlot(getSlot());
		client.setSelectedItemID(getId());
		widget.interact(0, MenuAction.ITEM_USE_ON_WIDGET.getId());
	}

	public Type getType() {
		return Type.get(widgetId);
	}

	public int calculateWidgetId(WidgetInfo containerInfo) {
		return calculateWidgetId(client.getWidget(containerInfo));
	}

	public int calculateWidgetId(Widget containerWidget) {
		if (containerWidget == null) {
			return -1;
		}

		Widget[] children = containerWidget.getChildren();
		if (children == null) {
			return -1;
		}

		return Arrays.stream(children)
						.filter(x -> x.getItemId() == getId()).findFirst()
						.map(Widget::getId)
						.orElse(-1);
	}

	public enum Type {
		INVENTORY, EQUIPMENT, BANK, BANK_INVENTORY, TRADE, TRADE_INVENTORY, UNKNOWN;

		private static Type get(int widgetId) {
			return switch (WidgetInfo.TO_GROUP(widgetId)) {
				case WidgetID.PLAYER_TRADE_SCREEN_GROUP_ID -> TRADE;
				case WidgetID.PLAYER_TRADE_INVENTORY_GROUP_ID -> TRADE_INVENTORY;
				case WidgetID.EQUIPMENT_GROUP_ID -> EQUIPMENT;
				case WidgetID.BANK_GROUP_ID -> BANK;
				case WidgetID.BANK_INVENTORY_GROUP_ID -> BANK_INVENTORY;
				case WidgetID.INVENTORY_GROUP_ID -> INVENTORY;
				default -> UNKNOWN;
			};
		}
	}
}
