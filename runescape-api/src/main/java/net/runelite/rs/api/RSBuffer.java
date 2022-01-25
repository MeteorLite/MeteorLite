package net.runelite.rs.api;

import net.runelite.api.Buffer;
import net.runelite.mapping.Import;

public interface RSBuffer extends Buffer, RSNode {

  @Import("array")
  byte[] getPayload();

  @Import("offset")
  int getOffset();

  @Import("writeByte0A1")
  @Override
  void writeByte0A1$api(int var1);

  @Import("writeByte2")
  @Override
  void writeByte2$api(int var1);

  @Import("writeByte01A")
  @Override
  void writeByte01A$api(int var1);

  @Import("write1")
  @Override
  void write1$api(int var1);

  @Import("write2")
  @Override
  void write2$api(int var1);

  @Import("writeIntME2")
  @Override
  void writeIntME2$api(int var1);

  @Import("writeIntME3")
  @Override
  void writeIntME3$api(int var1);

  @Import("writeByteB0")
  @Override
  void writeByteB0$api(int var1);

  @Import("writeByte")
  @Override
  void writeByte$api(int var1);

  @Import("writeByteA")
  @Override
  void writeByteA$api(int var1);

  @Import("writeByteC")
  @Override
  void writeByteC$api(int var1);

  @Import("writeByte01")
  @Override
  void writeByte01$api(int var1);

  @Import("writeShort")
  @Override
  void writeShort$api(int var1);

  @Import("writeShort01")
  @Override
  void writeShort01$api(int var1);

  @Import("writeInt0123")
  @Override
  void writeInt0123$api(int var1);

  @Import("writeShortA")
  @Override
  void writeShortA$api(int var1);

  @Import("writeInt2")
  @Override
  void writeInt2$api(int var1);

  @Import("writeIntME")
  @Override
  void writeIntME$api(int var1);

  @Import("writeShort01A")
  @Override
  void writeShort01A$api(int var1);

  @Import("writeMedium")
  @Override
  void writeMedium$api(int var1);

  @Import("writeInt")
  @Override
  void writeInt$api(int var1);

  @Import("writeLong")
  @Override
  void writeLong$api(long var1);

  @Import("writeStringCp1252NullTerminated")
  @Override
  void writeStringCp1252NullTerminated$api(String string);

  @Import("offset")
  void setOffset(int offset);

  @Import("readUnsignedByte")
  int readUnsignedByte$api();

  @Import("readByte")
  byte readByte$api();

  @Import("readUnsignedShort")
  int readUnsignedShort$api();

  @Import("readShort")
  int readShort$api();

  @Import("readInt")
  int readInt$api();

  @Import("readStringCp1252NullTerminated")
  String readStringCp1252NullTerminated$api();

  @Import("writeByteB")
  @Override
  void writeByteB$api(int i);

  @Import("writeShortLE")
  @Override
  void writeShortLE$api(int i);
}
