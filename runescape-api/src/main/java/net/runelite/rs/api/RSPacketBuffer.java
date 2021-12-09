package net.runelite.rs.api;

import net.runelite.api.packets.PacketBuffer;
import net.runelite.mapping.Import;

public interface RSPacketBuffer extends RSBuffer, PacketBuffer {
    @Import("isaacCipher")
    @Override
    RSIsaacCipher getIsaacCipher();

    @Import("write2")
    @Override
    void write2(int i);

    @Import("writeByteB0")
    @Override
    void writeByteB0(int i);

    @Import("writeByteB")
    @Override
    void writeByteB(int i);

    @Import("write1")
    @Override
    void write1(int i);

    @Import("writeByteA")
    @Override
    void writeByteA(int i);

    @Import("writeStringCp1252NullTerminated")
    @Override
    void writeStringCp1252NullTerminated(String s);

    @Import("writeByteC")
    @Override
    void writeByteC(int i);

    @Import("writeIntME3")
    @Override
    void writeIntME3(int i);

    @Import("writeIntME2")
    @Override
    void writeIntME2(int i);

    @Import("writeIntME")
    @Override
    void writeIntME(int i);
}
