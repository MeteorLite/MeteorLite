package dev.hoot.api.packets;

import dev.hoot.api.game.Game;
import net.runelite.api.*;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.packets.ClientPacket;
import net.runelite.api.packets.PacketBufferNode;
import net.runelite.api.widgets.WidgetInfo;

import java.util.List;

public class ItemPackets {
	public static void itemAction(Item item, String action) {
		List<String> actions = item.getActions();
		int index = actions.indexOf(action);
		switch (index) {
			case 0 -> itemFirstOption(item);
			case 1 -> itemSecondOption(item);
			case 2 -> itemThirdOption(item);
			case 3 -> itemFourthOption(item);
			case 4 -> itemFifthOption(item);
			default ->{
				WidgetPackets.widgetAction(Game.getClient().getWidget(item.getWidgetId()),action);
			}
		}
	}

	public static void itemFirstOption(Item item) {
		queueItemActionPacket(item.getWidgetId(), item.getId(), item.getSlot());
	}

	public static void itemSecondOption(Item item) {
		queueItemAction2Packet(item.getWidgetId(), item.getId(), item.getSlot());
	}
	public static void itemThirdOption(Item item) {
		queueItemAction3Packet(item.getWidgetId(), item.getId(), item.getSlot());
	}
	public static void itemFourthOption(Item item) {
		queueItemAction4Packet(item.getWidgetId(), item.getId(), item.getSlot());
	}

	public static void itemFifthOption(Item item) {
		queueItemAction5Packet(item.getWidgetId(), item.getId(), item.getSlot());
	}

	public static void useItemOnItem(Item item, Item item2) {
		if (item.getType().getInventoryID() != InventoryID.INVENTORY
						|| item2.getType().getInventoryID() != InventoryID.INVENTORY) {
			return;
		}

		queueItemOnItemPacket(item.getId(), item.getSlot(), item2.getId(), item2.getSlot());
	}

	public static void useItemOnTileObject(Item item, TileObject object) {
		Point p = object.menuPoint();
		LocalPoint lp = new LocalPoint(p.getX(),p.getY());
		WorldPoint wp = WorldPoint.fromScene(Game.getClient(),lp.getX(),lp.getY(),object.getPlane());
		queueItemUseOnTileObjectPacket(object.getId(), wp.getX(), wp.getY(), item.getSlot(), item.getId(), item.getWidgetId(), 0);
	}
	public static void queueBankItemActionPacket(int inventoryID, int itemID, int itemSlot){
		Client client = Game.getClient();
		ClientPacket clientPacket = Game.getClientPacket();
		PacketBufferNode var10 = Game.getClient().preparePacket(clientPacket.IF_BUTTON9(), client.getPacketWriter().getIsaacCipher());
		var10.getPacketBuffer().writeInt(inventoryID);
		var10.getPacketBuffer().writeShort(itemSlot);
		var10.getPacketBuffer().writeShort(itemID);
		client.getPacketWriter().queuePacket(var10);
	}
	public static void queueItemOnNpcPacket(int npcIndex,int itemWidgetID,int itemID,int itemSlot,int ctrlDown){
		Client client = Game.getClient();
		ClientPacket clientPacket = Game.getClientPacket();
		PacketBufferNode var9 = Game.getClient().preparePacket(clientPacket.OPNPCU(), client.getPacketWriter().getIsaacCipher());
		var9.getPacketBuffer().write2(ctrlDown);
		var9.getPacketBuffer().writeByteA(itemID);
		var9.getPacketBuffer().writeByteB0(itemSlot);
		var9.getPacketBuffer().writeIntME3(itemWidgetID);
		var9.getPacketBuffer().writeByteC(npcIndex);
		client.getPacketWriter().queuePacket(var9);
	}
	public static void queueItemUseOnTileObjectPacket(int objectID, int objectX, int objectY, int itemSlot, int itemID, int itemWidgetID, int ctrlDown){
		Client client = Game.getClient();
		ClientPacket clientPacket = Game.getClientPacket();
		PacketBufferNode var8 = Game.getClient().preparePacket(clientPacket.OPLOCU(), client.getPacketWriter().getIsaacCipher());
		var8.getPacketBuffer().writeByteB0(itemSlot);
		var8.getPacketBuffer().writeByteB0(objectY);
		var8.getPacketBuffer().writeIntME2(itemWidgetID);
		var8.getPacketBuffer().writeByteA(objectX);
		var8.getPacketBuffer().writeByteB0(objectID);
		var8.getPacketBuffer().writeByteC(itemID);
		var8.getPacketBuffer().writeByteB(ctrlDown);
		client.getPacketWriter().queuePacket(var8);
	}
	public static void queueItemActionPacket(int inventoryID, int itemID, int itemSlot) {
		Client client = Game.getClient();
		ClientPacket clientPacket = Game.getClientPacket();
		PacketBufferNode var8 = Game.getClient().preparePacket(clientPacket.OPHELD1(), client.getPacketWriter().getIsaacCipher());
		var8.getPacketBuffer().writeByteC(itemID);
		var8.getPacketBuffer().writeByteB0(itemSlot);
		var8.getPacketBuffer().writeIntME(inventoryID);
		client.getPacketWriter().queuePacket(var8);
	}
	public static void queueItemAction2Packet(int inventoryID, int itemID, int itemSlot) {
		Client client = Game.getClient();
		PacketBufferNode var8 = Game.getClient().preparePacket(Game.getClient().getItemAction2Packet(), client.getPacketWriter().getIsaacCipher());
		var8.getPacketBuffer().writeByteA(itemID);
		var8.getPacketBuffer().writeByteA(itemSlot);
		var8.getPacketBuffer().writeIntME(inventoryID);
		client.getPacketWriter().queuePacket(var8);
	}
	
	public static void queueItemAction3Packet(int inventoryID, int itemID, int itemSlot){
		Client client = Game.getClient();
		PacketBufferNode var8 = Game.getClient().preparePacket(Game.getClient().getItemAction3Packet(), client.getPacketWriter().getIsaacCipher());
		var8.getPacketBuffer().writeByteC(itemID);
		var8.getPacketBuffer().writeByteA(itemSlot);
		var8.getPacketBuffer().writeIntME(inventoryID);
		client.getPacketWriter().queuePacket(var8);
	}
	public static void queueItemAction4Packet(int inventoryID, int itemID, int itemSlot){
		Client client = Game.getClient();
		ClientPacket clientPacket = Game.getClientPacket();
		PacketBufferNode var8 = Game.getClient().preparePacket(clientPacket.OPHELD4(), client.getPacketWriter().getIsaacCipher());
		var8.getPacketBuffer().writeByteC(itemID);
		var8.getPacketBuffer().writeByteB0(itemSlot);
		var8.getPacketBuffer().writeIntME3(inventoryID);
		client.getPacketWriter().queuePacket(var8);
	}
	
	public static void queueItemAction5Packet(int inventoryID, int itemID, int itemSlot) {
		Client client = Game.getClient();
		ClientPacket clientPacket = Game.getClientPacket();
		PacketBufferNode var8 = Game.getClient().preparePacket(clientPacket.OPHELD5(), client.getPacketWriter().getIsaacCipher());
		var8.getPacketBuffer().writeByteA(itemSlot);
		var8.getPacketBuffer().writeShort(itemID);
		var8.getPacketBuffer().writeIntME2(inventoryID);
		client.getPacketWriter().queuePacket(var8);
	}

	public static void queueItemOnItemPacket(int itemId1, int slot1, int itemId2, int slot2) {
		Client client = Game.getClient();
		ClientPacket clientPacket = Game.getClientPacket();
		PacketBufferNode var8 = Game.getClient().preparePacket(clientPacket.OPHELDU(), client.getPacketWriter().getIsaacCipher());
		var8.getPacketBuffer().writeByteA(itemId2);
		var8.getPacketBuffer().writeIntME2(WidgetInfo.INVENTORY.getPackedId());
		var8.getPacketBuffer().writeShort(slot2);
		var8.getPacketBuffer().writeIntME2(WidgetInfo.INVENTORY.getPackedId());
		var8.getPacketBuffer().writeByteC(itemId1);
		var8.getPacketBuffer().writeByteA(slot1);
		client.getPacketWriter().queuePacket(var8);
	}
}
