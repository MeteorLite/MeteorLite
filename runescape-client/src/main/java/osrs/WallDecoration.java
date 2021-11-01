package osrs;

import net.runelite.mapping.Export;
import net.runelite.mapping.Implements;
import net.runelite.mapping.ObfuscatedGetter;
import net.runelite.mapping.ObfuscatedName;
import net.runelite.mapping.ObfuscatedSignature;

@ObfuscatedName("hk")
@Implements("WallDecoration")
public final class WallDecoration {
	@ObfuscatedName("l")
	@ObfuscatedGetter(
		intValue = -486262227
	)
	@Export("tileHeight")
	int tileHeight;
	@ObfuscatedName("q")
	@ObfuscatedGetter(
		intValue = 1658643165
	)
	@Export("x")
	int x;
	@ObfuscatedName("f")
	@ObfuscatedGetter(
		intValue = -457731185
	)
	@Export("y")
	int y;
	@ObfuscatedName("j")
	@ObfuscatedGetter(
		intValue = 1010602035
	)
	@Export("orientation")
	int orientation;
	@ObfuscatedName("m")
	@ObfuscatedGetter(
		intValue = -1936125561
	)
	@Export("orientation2")
	int orientation2;
	@ObfuscatedName("k")
	@ObfuscatedGetter(
		intValue = -1930286525
	)
	@Export("xOffset")
	int xOffset;
	@ObfuscatedName("t")
	@ObfuscatedGetter(
		intValue = -607759825
	)
	@Export("yOffset")
	int yOffset;
	@ObfuscatedName("a")
	@ObfuscatedSignature(
		descriptor = "Lhi;"
	)
	@Export("renderable1")
	public Renderable renderable1;
	@ObfuscatedName("e")
	@ObfuscatedSignature(
		descriptor = "Lhi;"
	)
	@Export("renderable2")
	public Renderable renderable2;
	@ObfuscatedName("i")
	@ObfuscatedGetter(
		longValue = -1585251643949812861L
	)
	@Export("tag")
	public long tag;
	@ObfuscatedName("y")
	@ObfuscatedGetter(
		intValue = 148336509
	)
	@Export("flags")
	int flags;

	WallDecoration() {
		this.tag = 0L;
		this.flags = 0;
	}

	@ObfuscatedName("l")
	@ObfuscatedSignature(
		descriptor = "(S)[Lin;",
		garbageValue = "180"
	)
	@Export("ServerPacket_values")
	public static ServerPacket[] ServerPacket_values() {
		return new ServerPacket[]{ServerPacket.CAM_SHAKE, ServerPacket.SOUND_AREA, ServerPacket.UPDATE_IGNORELIST, ServerPacket.field2790, ServerPacket.VARCLAN_ENABLE, ServerPacket.SEND_PING, ServerPacket.field2862, ServerPacket.IF_SETANGLE, ServerPacket.MAP_ANIM, ServerPacket.UPDATE_ZONE_PARTIAL_FOLLOWS, ServerPacket.IF_SETNPCHEAD, ServerPacket.LOC_ANIM, ServerPacket.IF_SETTEXT, ServerPacket.IF_SETPLAYERHEAD, ServerPacket.IF_SETOBJECT, ServerPacket.UPDATE_SITESETTINGS, ServerPacket.PLAYER_INFO, ServerPacket.MINIMAP_TOGGLE, ServerPacket.UPDATE_FRIENDLIST, ServerPacket.UPDATE_INV_PARTIAL, ServerPacket.field2807, ServerPacket.RUNCLIENTSCRIPT, ServerPacket.CLANSETTINGS_FULL, ServerPacket.VARCLAN_DISABLE, ServerPacket.MIDI_JINGLE, ServerPacket.NPC_INFO_SMALL, ServerPacket.CAM_RESET, ServerPacket.ENTER_FREECAM, ServerPacket.REBUILD_REGION, ServerPacket.UPDATE_ZONE_PARTIAL_ENCLOSED, ServerPacket.FRIENDLIST_LOADED, ServerPacket.UPDATE_RUNWEIGHT, ServerPacket.MESSAGE_CLANCHANNEL, ServerPacket.IF_OPENSUB, ServerPacket.OBJ_ADD, ServerPacket.RESET_ANIMS, ServerPacket.TRIGGER_ONDIALOGABORT, ServerPacket.IF_SETHIDE, ServerPacket.field2845, ServerPacket.MIDI_SONG, ServerPacket.SET_MAP_FLAG, ServerPacket.UPDATE_FRIENDCHAT_CHANNEL_FULL, ServerPacket.UPDATE_FRIENDCHAT_CHANNEL_SINGLEUSER, ServerPacket.LOC_ADD_CHANGE, ServerPacket.UPDATE_INV_STOP_TRANSMIT, ServerPacket.CAM_LOOKAT, ServerPacket.MESSAGE_CLANCHANNEL_SYSTEM, ServerPacket.IF_CLOSESUB, ServerPacket.VARP_SMALL, ServerPacket.IF_OPENTOP, ServerPacket.CAM_MOVETO, ServerPacket.CHAT_FILTER_SETTINGS, ServerPacket.MESSAGE_PRIVATE_ECHO, ServerPacket.SET_PLAYER_OP, ServerPacket.IF_SETPOSITION, ServerPacket.UPDATE_ZONE_FULL_FOLLOWS, ServerPacket.IF_SETSCROLLPOS, ServerPacket.IF_SETCOLOUR, ServerPacket.MESSAGE_GAME, ServerPacket.MESSAGE_PRIVATE, ServerPacket.TRADING_POST_RESULTS, ServerPacket.OBJ_COUNT, ServerPacket.REBUILD_NORMAL, ServerPacket.IF_SETANIM, ServerPacket.VARP_LARGE, ServerPacket.OBJ_DEL, ServerPacket.UPDATE_STAT, ServerPacket.URL_OPEN, ServerPacket.LOC_DEL, ServerPacket.field2848, ServerPacket.CLANSETTINGS_DELTA, ServerPacket.field2858, ServerPacket.field2859, ServerPacket.NPC_INFO_LARGE, ServerPacket.field2824, ServerPacket.LOGOUT_FULL, ServerPacket.field2863, ServerPacket.MESSAGE_FRIENDCHANNEL, ServerPacket.IF_SETEVENTS, ServerPacket.HINT_ARROW, ServerPacket.IF_SETMODEL, ServerPacket.SYNTH_SOUND, ServerPacket.UPDATE_UID192, ServerPacket.CHAT_FILTER_SETTINGS_PRIVATECHAT, ServerPacket.IF_MOVESUB, ServerPacket.MAP_PROJANIM, ServerPacket.LOGOUT_TRANSFER, ServerPacket.UPDATE_INV_FULL, ServerPacket.UPDATE_RUNENERGY, ServerPacket.UPDATE_REBOOT_TIMER, ServerPacket.REFLECTION_CHECK, ServerPacket.RESET_CLIENT_VARCACHE, ServerPacket.VARCLAN, ServerPacket.CLANCHANNEL_FULL, ServerPacket.CLANCHANNEL_DELTA};
	}

	@ObfuscatedName("l")
	@ObfuscatedSignature(
		descriptor = "([BIII)Ljava/lang/String;",
		garbageValue = "-1531705117"
	)
	public static String method4580(byte[] var0, int var1, int var2) {
		StringBuilder var3 = new StringBuilder();

		for (int var4 = var1; var4 < var2 + var1; var4 += 3) {
			int var5 = var0[var4] & 255;
			var3.append(class317.field3869[var5 >>> 2]);
			if (var4 < var2 - 1) {
				int var6 = var0[var4 + 1] & 255;
				var3.append(class317.field3869[(var5 & 3) << 4 | var6 >>> 4]);
				if (var4 < var2 - 2) {
					int var7 = var0[var4 + 2] & 255;
					var3.append(class317.field3869[(var6 & 15) << 2 | var7 >>> 6]).append(class317.field3869[var7 & 63]);
				} else {
					var3.append(class317.field3869[(var6 & 15) << 2]).append("=");
				}
			} else {
				var3.append(class317.field3869[(var5 & 3) << 4]).append("==");
			}
		}

		return var3.toString();
	}

	@ObfuscatedName("av")
	@ObfuscatedSignature(
		descriptor = "(ILbe;ZB)I",
		garbageValue = "-20"
	)
	static int method4581(int var0, Script var1, boolean var2) {
		if (var0 == 6800) {
			Interpreter.Interpreter_stringStack[++class54.Interpreter_stringStackSize - 1] = "";
			return 1;
		} else if (var0 != 6801 && var0 != 6802) {
			if (var0 == 6850) {
				Interpreter.Interpreter_stringStack[++class54.Interpreter_stringStackSize - 1] = "";
				return 1;
			} else if (var0 != 6851 && var0 != 6852) {
				if (var0 == 6853) {
					Interpreter.Interpreter_intStack[++AbstractByteArrayCopier.Interpreter_intStackSize - 1] = 0;
					return 1;
				} else {
					return 2;
				}
			} else {
				Interpreter.Interpreter_intStack[++AbstractByteArrayCopier.Interpreter_intStackSize - 1] = -1;
				return 1;
			}
		} else {
			Interpreter.Interpreter_intStack[++AbstractByteArrayCopier.Interpreter_intStackSize - 1] = -1;
			return 1;
		}
	}
}
