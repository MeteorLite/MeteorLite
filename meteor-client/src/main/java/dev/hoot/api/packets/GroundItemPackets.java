package dev.hoot.api.packets;

import dev.hoot.api.game.Game;
import net.runelite.api.TileItem;
import osrs.Client;
import osrs.ClientPacket;

import java.util.List;

import static osrs.Client.packetWriter;

public class GroundItemPackets {
    public static void queueGroundItemActionPacket(int itemID,int worldPointX,int worldPointY,boolean ctrlDown){
        osrs.PacketBufferNode var8 = (osrs.PacketBufferNode) Game.getClient().preparePacket(ClientPacket.groundItem1, packetWriter.isaacCipher);
        var8.packetBuffer.writeByteA(worldPointX);
        var8.packetBuffer.writeShort(itemID);
        var8.packetBuffer.writeShort(worldPointY);
        var8.packetBuffer.write2(ctrlDown ? 1 : 0);
        Client.packetWriter.addNode(var8);
    }
    public static void queueGroundItemAction2Packet(int itemID,int worldPointX,int worldPointY,boolean ctrlDown){
        osrs.PacketBufferNode var8 = (osrs.PacketBufferNode) Game.getClient().preparePacket(ClientPacket.groundItem2, packetWriter.isaacCipher);
        var8.packetBuffer.writeByteC(worldPointY);
        var8.packetBuffer.write2(ctrlDown ? 1 : 0);
        var8.packetBuffer.writeByteB0(itemID);
        var8.packetBuffer.writeShort(worldPointX);
        Client.packetWriter.addNode(var8);
    }
    public static void queueGroundItemAction3Packet(int itemID,int worldPointX,int worldPointY,boolean ctrlDown){
        osrs.PacketBufferNode var8 = (osrs.PacketBufferNode) Game.getClient().preparePacket(ClientPacket.groundItem3, packetWriter.isaacCipher);
        var8.packetBuffer.writeByteA(worldPointY);
        var8.packetBuffer.write2(ctrlDown ? 1 : 0);
        var8.packetBuffer.writeByteA(worldPointX);
        var8.packetBuffer.writeByteA(itemID);
        Client.packetWriter.addNode(var8);
    }
    public static void queueGroundItemAction4Packet(int itemID,int worldPointX,int worldPointY,boolean ctrlDown){
        osrs.PacketBufferNode var8 = (osrs.PacketBufferNode) Game.getClient().preparePacket(ClientPacket.groundItem4, packetWriter.isaacCipher);
        var8.packetBuffer.writeByteA(worldPointY);
        var8.packetBuffer.writeShort(itemID);
        var8.packetBuffer.writeByteC(worldPointX);
        var8.packetBuffer.write2(ctrlDown ? 1 : 0);
        Client.packetWriter.addNode(var8);
    }
    public static void queueGroundItemAction5Packet(int itemID,int worldPointX,int worldPointY,boolean ctrlDown){
        osrs.PacketBufferNode var8 = (osrs.PacketBufferNode) Game.getClient().preparePacket(ClientPacket.groundItem5, packetWriter.isaacCipher);
        var8.packetBuffer.write1(ctrlDown ? 1 : 0);
        var8.packetBuffer.writeByteA(worldPointX);
        var8.packetBuffer.writeByteC(itemID);
        var8.packetBuffer.writeShort(worldPointY);
        Client.packetWriter.addNode(var8);
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