package dev.hoot.api.packets;

import dev.hoot.api.game.Game;
import net.runelite.api.Client;
import net.runelite.api.Point;
import net.runelite.api.TileObject;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.packets.ClientPacket;
import net.runelite.api.packets.PacketBufferNode;

import java.util.List;

public class TileObjectPackets {

    public static void queueTileObjectActionPacket(int objectID, int worldPointX, int worldPointY, int ctrlDown) {
        Client client = Game.getClient();
        ClientPacket clientPacket = Game.getClientPacket();
        PacketBufferNode var8 = Game.getClient().preparePacket(clientPacket.OPLOC1(), client.getPacketWriter().getIsaacCipher());
        var8.getPacketBuffer().writeByteB0$api(worldPointX);
        var8.getPacketBuffer().writeShort(worldPointY);
        var8.getPacketBuffer().writeShortLE$api(objectID);
        var8.getPacketBuffer().writeByte(ctrlDown);
        client.getPacketWriter().queuePacket(var8);
    }

    public static void queueTileObjectAction2Packet(int objectID, int worldPointX, int worldPointY, int ctrlDown){
        Client client = Game.getClient();
        ClientPacket clientPacket = Game.getClientPacket();
        PacketBufferNode var8 =  Game.getClient().preparePacket(clientPacket.OPLOC2(), client.getPacketWriter().getIsaacCipher());
        var8.getPacketBuffer().writeByteB0$api(worldPointX);
        var8.getPacketBuffer().writeByte(ctrlDown);
        var8.getPacketBuffer().writeShort(objectID);
        var8.getPacketBuffer().writeByteC$api(worldPointY);
        client.getPacketWriter().queuePacket(var8);
    }

    public static void queueTileObjectAction3Packet(int objectID, int worldPointX, int worldPointY, int ctrlDown) {
        Client client = Game.getClient();
        ClientPacket clientPacket = Game.getClientPacket();
        PacketBufferNode var8 = Game.getClient().preparePacket(clientPacket.OPLOC3(), client.getPacketWriter().getIsaacCipher());
        var8.getPacketBuffer().writeShort(worldPointX);
        var8.getPacketBuffer().writeByte(ctrlDown);
        var8.getPacketBuffer().writeShort(worldPointY);
        var8.getPacketBuffer().writeShortLE$api(objectID);
        client.getPacketWriter().queuePacket(var8);
    }
    public static void queueTileObjectAction4Packet(int objectID, int worldPointX, int worldPointY, int ctrlDown) {
        Client client = Game.getClient();
        ClientPacket clientPacket = Game.getClientPacket();
        PacketBufferNode var8 = Game.getClient().preparePacket(clientPacket.OPLOC4(), client.getPacketWriter().getIsaacCipher());
        var8.getPacketBuffer().writeByteC$api(objectID);
        var8.getPacketBuffer().writeShort(worldPointY);
        var8.getPacketBuffer().writeByteC$api(worldPointX);
        var8.getPacketBuffer().write1$api(ctrlDown);
        client.getPacketWriter().queuePacket(var8);
    }
    public static void queueTileObjectAction5Packet(int objectID, int worldPointX, int worldPointY, int ctrlDown) {
        Client client = Game.getClient();
        ClientPacket clientPacket = Game.getClientPacket();
        PacketBufferNode var8 = Game.getClient().preparePacket(clientPacket.OPLOC5(), client.getPacketWriter().getIsaacCipher());
        var8.getPacketBuffer().writeShortLE$api(worldPointX);
        var8.getPacketBuffer().writeByteC$api(objectID);
        var8.getPacketBuffer().writeByteC$api(worldPointY);
        var8.getPacketBuffer().writeByteB$api(ctrlDown);
        client.getPacketWriter().queuePacket(var8);
    }
    public static void tileObjectFirstOption(TileObject object, int ctrlDown){
        Point p = object.menuPoint();
        LocalPoint lp = new LocalPoint(p.getX(),p.getY());
        WorldPoint wp = WorldPoint.fromScene(Game.getClient(),lp.getX(),lp.getY(),object.getPlane());
        queueTileObjectActionPacket(object.getId(),wp.getX(),wp.getY(),ctrlDown);
    }
    public static void tileObjectSecondOption(TileObject object,int ctrlDown){
        Point p = object.menuPoint();
        LocalPoint lp = new LocalPoint(p.getX(),p.getY());
        WorldPoint wp = WorldPoint.fromScene(Game.getClient(),lp.getX(),lp.getY(),object.getPlane());
        queueTileObjectAction2Packet(object.getId(),wp.getX(),wp.getY(),ctrlDown);
    }
    public static void tileObjectThirdOption(TileObject object,int ctrlDown){
        Point p = object.menuPoint();
        LocalPoint lp = new LocalPoint(p.getX(),p.getY());
        WorldPoint wp = WorldPoint.fromScene(Game.getClient(),lp.getX(),lp.getY(),object.getPlane());
        queueTileObjectAction3Packet(object.getId(),wp.getX(),wp.getY(),ctrlDown);
    }
    public static void tileObjectFourthOption(TileObject object,int ctrlDown){
        Point p = object.menuPoint();
        LocalPoint lp = new LocalPoint(p.getX(),p.getY());
        WorldPoint wp = WorldPoint.fromScene(Game.getClient(),lp.getX(),lp.getY(),object.getPlane());
        queueTileObjectAction4Packet(object.getId(),wp.getX(),wp.getY(),ctrlDown);
    }
    public static void tileObjectFifthOption(TileObject object,int ctrlDown){
        Point p = object.menuPoint();
        LocalPoint lp = new LocalPoint(p.getX(),p.getY());
        WorldPoint wp = WorldPoint.fromScene(Game.getClient(),lp.getX(),lp.getY(),object.getPlane());
        queueTileObjectAction5Packet(object.getId(),wp.getX(),wp.getY(),ctrlDown);
    }
    public static void tileObjectAction(TileObject object, String action, int ctrlDown) {
        List<String> actions = object.getActions();
        int index = actions.indexOf(action);
        switch (index) {
            case 0 -> tileObjectFirstOption(object,ctrlDown);
            case 1 -> tileObjectSecondOption(object,ctrlDown);
            case 2 -> tileObjectThirdOption(object,ctrlDown);
            case 3 -> tileObjectFourthOption(object,ctrlDown);
            case 4 -> tileObjectFifthOption(object,ctrlDown);
        }
    }
}
