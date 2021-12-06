package dev.hoot.api.packets;

import dev.hoot.api.game.Game;
import net.runelite.api.Client;
import net.runelite.api.TileItem;
import net.runelite.api.packets.ClientPacket;
import net.runelite.api.packets.PacketBufferNode;

import java.util.List;

public class GroundItemPackets {
    public static void queueGroundItemActionPacket(int itemID,int worldPointX,int worldPointY,boolean ctrlDown){
        net.runelite.api.Client client = Game.getClient();
        net.runelite.api.packets.ClientPacket clientPacket = Game.getClientPacket();
        PacketBufferNode var8 = Game.getClient().preparePacket(clientPacket.grounditem1(), client.getPacketWriter().getIsaacCipher());
        var8.getPacketBuffer().writeByteA(worldPointX);
        var8.getPacketBuffer().writeShort(itemID);
        var8.getPacketBuffer().writeShort(worldPointY);
        var8.getPacketBuffer().write2(ctrlDown ? 1 : 0);
        client.getPacketWriter().queuePacket(var8);
    }
    public static void queueGroundItemAction2Packet(int itemID,int worldPointX,int worldPointY,boolean ctrlDown){
        Client client = Game.getClient();
        ClientPacket clientPacket = Game.getClientPacket();
        PacketBufferNode var8 = Game.getClient().preparePacket(clientPacket.grounditem2(), client.getPacketWriter().getIsaacCipher());
        var8.getPacketBuffer().writeByteC(worldPointY);
        var8.getPacketBuffer().write2(ctrlDown ? 1 : 0);
        var8.getPacketBuffer().writeByteB0(itemID);
        var8.getPacketBuffer().writeShort(worldPointX);
        client.getPacketWriter().queuePacket(var8);
    }
    public static void queueGroundItemAction3Packet(int itemID,int worldPointX,int worldPointY,boolean ctrlDown){
        Client client = Game.getClient();
        ClientPacket clientPacket = Game.getClientPacket();
        PacketBufferNode var8 = Game.getClient().preparePacket(clientPacket.grounditem3(), client.getPacketWriter().getIsaacCipher());
        var8.getPacketBuffer().writeByteA(worldPointY);
        var8.getPacketBuffer().write2(ctrlDown ? 1 : 0);
        var8.getPacketBuffer().writeByteA(worldPointX);
        var8.getPacketBuffer().writeByteA(itemID);
        client.getPacketWriter().queuePacket(var8);
    }
    public static void queueGroundItemAction4Packet(int itemID,int worldPointX,int worldPointY,boolean ctrlDown){
        Client client = Game.getClient();
        ClientPacket clientPacket = Game.getClientPacket();
        PacketBufferNode var8 = Game.getClient().preparePacket(clientPacket.grounditem4(), client.getPacketWriter().getIsaacCipher());
        var8.getPacketBuffer().writeByteA(worldPointY);
        var8.getPacketBuffer().writeShort(itemID);
        var8.getPacketBuffer().writeByteC(worldPointX);
        var8.getPacketBuffer().write2(ctrlDown ? 1 : 0);
        client.getPacketWriter().queuePacket(var8);
    }
    public static void queueGroundItemAction5Packet(int itemID,int worldPointX,int worldPointY,boolean ctrlDown){
        Client client = Game.getClient();
        ClientPacket clientPacket = Game.getClientPacket();
        PacketBufferNode var8 = Game.getClient().preparePacket(clientPacket.grounditem5(), client.getPacketWriter().getIsaacCipher());
        var8.getPacketBuffer().write1(ctrlDown ? 1 : 0);
        var8.getPacketBuffer().writeByteA(worldPointX);
        var8.getPacketBuffer().writeByteC(itemID);
        var8.getPacketBuffer().writeShort(worldPointY);
        client.getPacketWriter().queuePacket(var8);
    }

    public static void groundItemFirstOption(TileItem item, boolean runEnabled){
        queueGroundItemActionPacket(item.getId(),item.getWorldLocation().getX(),item.getWorldLocation().getY(),runEnabled);
    }
    public static void groundItemSecondOption(TileItem item,boolean runEnabled){
        queueGroundItemAction2Packet(item.getId(),item.getWorldLocation().getX(),item.getWorldLocation().getY(),runEnabled);
    }
    public static void groundItemThirdOption(TileItem item,boolean runEnabled){
        queueGroundItemAction3Packet(item.getId(),item.getWorldLocation().getX(),item.getWorldLocation().getY(),runEnabled);
    }
    public static void groundItemFourthOption(TileItem item,boolean runEnabled){
        queueGroundItemAction4Packet(item.getId(),item.getWorldLocation().getX(),item.getWorldLocation().getY(),runEnabled);
    }
    public static void groundItemFifthOption(TileItem item,boolean runEnabled){
        queueGroundItemAction5Packet(item.getId(),item.getWorldLocation().getX(),item.getWorldLocation().getY(),runEnabled);
    }
    public static void groundItemAction(TileItem item, String action,boolean runEnabled) {
        List<String> actions = item.getActions();
        int index = actions.indexOf(action);
        switch (index) {
            case 0 -> groundItemFirstOption(item,runEnabled);
            case 1 -> groundItemSecondOption(item,runEnabled);
            case 2 -> groundItemThirdOption(item,runEnabled);
            case 3 -> groundItemFourthOption(item,runEnabled);
            case 4 -> groundItemFifthOption(item,runEnabled);
        }
    }
}
