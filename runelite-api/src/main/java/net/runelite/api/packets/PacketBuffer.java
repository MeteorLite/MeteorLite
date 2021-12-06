package net.runelite.api.packets;

import net.runelite.api.Buffer;

public interface PacketBuffer extends Buffer {
    IsaacCipher getIsaacCipher();

    void write2(int i);

    void writeByteB0(int i);

    void writeByteB(int i);

    void write1(int i);

    void writeByteA(int i);

    void writeStringCp1252NullTerminated(String s);

    void writeByteC(int i);

    void writeIntME3(int i);

    void writeIntME2(int i);

    void writeIntME(int i);
}
