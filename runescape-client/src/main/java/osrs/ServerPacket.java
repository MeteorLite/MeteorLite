package osrs;

import net.runelite.mapping.Export;
import net.runelite.mapping.Implements;
import net.runelite.mapping.ObfuscatedGetter;
import net.runelite.mapping.ObfuscatedName;
import net.runelite.mapping.ObfuscatedSignature;

@ObfuscatedName("in")
@Implements("ServerPacket")
public class ServerPacket {
	@ObfuscatedName("l")
	@ObfuscatedSignature(
		descriptor = "Lin;"
	)
	public static final ServerPacket CAM_SHAKE;
	@ObfuscatedName("q")
	@ObfuscatedSignature(
		descriptor = "Lin;"
	)
	public static final ServerPacket SOUND_AREA;
	@ObfuscatedName("f")
	@ObfuscatedSignature(
		descriptor = "Lin;"
	)
	public static final ServerPacket UPDATE_IGNORELIST;
	@ObfuscatedName("j")
	@ObfuscatedSignature(
		descriptor = "Lin;"
	)
	public static final ServerPacket field2790;
	@ObfuscatedName("m")
	@ObfuscatedSignature(
		descriptor = "Lin;"
	)
	public static final ServerPacket VARCLAN_ENABLE;
	@ObfuscatedName("k")
	@ObfuscatedSignature(
		descriptor = "Lin;"
	)
	public static final ServerPacket SEND_PING;
	@ObfuscatedName("t")
	@ObfuscatedSignature(
		descriptor = "Lin;"
	)
	public static final ServerPacket field2862;
	@ObfuscatedName("a")
	@ObfuscatedSignature(
		descriptor = "Lin;"
	)
	public static final ServerPacket IF_SETANGLE;
	@ObfuscatedName("e")
	@ObfuscatedSignature(
		descriptor = "Lin;"
	)
	public static final ServerPacket MAP_ANIM;
	@ObfuscatedName("i")
	@ObfuscatedSignature(
		descriptor = "Lin;"
	)
	public static final ServerPacket UPDATE_ZONE_PARTIAL_FOLLOWS;
	@ObfuscatedName("y")
	@ObfuscatedSignature(
		descriptor = "Lin;"
	)
	public static final ServerPacket IF_SETNPCHEAD;
	@ObfuscatedName("w")
	@ObfuscatedSignature(
		descriptor = "Lin;"
	)
	public static final ServerPacket LOC_ANIM;
	@ObfuscatedName("g")
	@ObfuscatedSignature(
		descriptor = "Lin;"
	)
	public static final ServerPacket IF_SETTEXT;
	@ObfuscatedName("v")
	@ObfuscatedSignature(
		descriptor = "Lin;"
	)
	public static final ServerPacket IF_SETPLAYERHEAD;
	@ObfuscatedName("s")
	@ObfuscatedSignature(
		descriptor = "Lin;"
	)
	public static final ServerPacket IF_SETOBJECT;
	@ObfuscatedName("c")
	@ObfuscatedSignature(
		descriptor = "Lin;"
	)
	public static final ServerPacket UPDATE_SITESETTINGS;
	@ObfuscatedName("b")
	@ObfuscatedSignature(
		descriptor = "Lin;"
	)
	public static final ServerPacket PLAYER_INFO;
	@ObfuscatedName("x")
	@ObfuscatedSignature(
		descriptor = "Lin;"
	)
	public static final ServerPacket MINIMAP_TOGGLE;
	@ObfuscatedName("p")
	@ObfuscatedSignature(
		descriptor = "Lin;"
	)
	public static final ServerPacket UPDATE_FRIENDLIST;
	@ObfuscatedName("z")
	@ObfuscatedSignature(
		descriptor = "Lin;"
	)
	public static final ServerPacket UPDATE_INV_PARTIAL;
	@ObfuscatedName("h")
	@ObfuscatedSignature(
		descriptor = "Lin;"
	)
	public static final ServerPacket field2807;
	@ObfuscatedName("o")
	@ObfuscatedSignature(
		descriptor = "Lin;"
	)
	public static final ServerPacket RUNCLIENTSCRIPT;
	@ObfuscatedName("r")
	@ObfuscatedSignature(
		descriptor = "Lin;"
	)
	public static final ServerPacket CLANSETTINGS_FULL;
	@ObfuscatedName("n")
	@ObfuscatedSignature(
		descriptor = "Lin;"
	)
	public static final ServerPacket VARCLAN_DISABLE;
	@ObfuscatedName("u")
	@ObfuscatedSignature(
		descriptor = "Lin;"
	)
	public static final ServerPacket MIDI_JINGLE;
	@ObfuscatedName("d")
	@ObfuscatedSignature(
		descriptor = "Lin;"
	)
	public static final ServerPacket NPC_INFO_SMALL;
	@ObfuscatedName("ab")
	@ObfuscatedSignature(
		descriptor = "Lin;"
	)
	public static final ServerPacket CAM_RESET;
	@ObfuscatedName("ad")
	@ObfuscatedSignature(
		descriptor = "Lin;"
	)
	public static final ServerPacket ENTER_FREECAM;
	@ObfuscatedName("as")
	@ObfuscatedSignature(
		descriptor = "Lin;"
	)
	public static final ServerPacket REBUILD_REGION;
	@ObfuscatedName("ak")
	@ObfuscatedSignature(
		descriptor = "Lin;"
	)
	public static final ServerPacket UPDATE_ZONE_PARTIAL_ENCLOSED;
	@ObfuscatedName("ah")
	@ObfuscatedSignature(
		descriptor = "Lin;"
	)
	public static final ServerPacket FRIENDLIST_LOADED;
	@ObfuscatedName("ay")
	@ObfuscatedSignature(
		descriptor = "Lin;"
	)
	public static final ServerPacket UPDATE_RUNWEIGHT;
	@ObfuscatedName("al")
	@ObfuscatedSignature(
		descriptor = "Lin;"
	)
	public static final ServerPacket MESSAGE_CLANCHANNEL;
	@ObfuscatedName("ao")
	@ObfuscatedSignature(
		descriptor = "Lin;"
	)
	public static final ServerPacket IF_OPENSUB;
	@ObfuscatedName("au")
	@ObfuscatedSignature(
		descriptor = "Lin;"
	)
	public static final ServerPacket OBJ_ADD;
	@ObfuscatedName("aa")
	@ObfuscatedSignature(
		descriptor = "Lin;"
	)
	public static final ServerPacket RESET_ANIMS;
	@ObfuscatedName("ag")
	@ObfuscatedSignature(
		descriptor = "Lin;"
	)
	public static final ServerPacket TRIGGER_ONDIALOGABORT;
	@ObfuscatedName("aq")
	@ObfuscatedSignature(
		descriptor = "Lin;"
	)
	public static final ServerPacket IF_SETHIDE;
	@ObfuscatedName("ar")
	@ObfuscatedSignature(
		descriptor = "Lin;"
	)
	public static final ServerPacket field2845;
	@ObfuscatedName("ac")
	@ObfuscatedSignature(
		descriptor = "Lin;"
	)
	public static final ServerPacket MIDI_SONG;
	@ObfuscatedName("at")
	@ObfuscatedSignature(
		descriptor = "Lin;"
	)
	public static final ServerPacket SET_MAP_FLAG;
	@ObfuscatedName("ax")
	@ObfuscatedSignature(
		descriptor = "Lin;"
	)
	public static final ServerPacket UPDATE_FRIENDCHAT_CHANNEL_FULL;
	@ObfuscatedName("an")
	@ObfuscatedSignature(
		descriptor = "Lin;"
	)
	public static final ServerPacket UPDATE_FRIENDCHAT_CHANNEL_SINGLEUSER;
	@ObfuscatedName("av")
	@ObfuscatedSignature(
		descriptor = "Lin;"
	)
	public static final ServerPacket LOC_ADD_CHANGE;
	@ObfuscatedName("ap")
	@ObfuscatedSignature(
		descriptor = "Lin;"
	)
	public static final ServerPacket UPDATE_INV_STOP_TRANSMIT;
	@ObfuscatedName("aj")
	@ObfuscatedSignature(
		descriptor = "Lin;"
	)
	public static final ServerPacket CAM_LOOKAT;
	@ObfuscatedName("am")
	@ObfuscatedSignature(
		descriptor = "Lin;"
	)
	public static final ServerPacket MESSAGE_CLANCHANNEL_SYSTEM;
	@ObfuscatedName("aw")
	@ObfuscatedSignature(
		descriptor = "Lin;"
	)
	public static final ServerPacket IF_CLOSESUB;
	@ObfuscatedName("az")
	@ObfuscatedSignature(
		descriptor = "Lin;"
	)
	public static final ServerPacket VARP_SMALL;
	@ObfuscatedName("ae")
	@ObfuscatedSignature(
		descriptor = "Lin;"
	)
	public static final ServerPacket IF_OPENTOP;
	@ObfuscatedName("ai")
	@ObfuscatedSignature(
		descriptor = "Lin;"
	)
	public static final ServerPacket CAM_MOVETO;
	@ObfuscatedName("af")
	@ObfuscatedSignature(
		descriptor = "Lin;"
	)
	public static final ServerPacket CHAT_FILTER_SETTINGS;
	@ObfuscatedName("bz")
	@ObfuscatedSignature(
		descriptor = "Lin;"
	)
	public static final ServerPacket MESSAGE_PRIVATE_ECHO;
	@ObfuscatedName("bt")
	@ObfuscatedSignature(
		descriptor = "Lin;"
	)
	public static final ServerPacket SET_PLAYER_OP;
	@ObfuscatedName("bg")
	@ObfuscatedSignature(
		descriptor = "Lin;"
	)
	public static final ServerPacket IF_SETPOSITION;
	@ObfuscatedName("bf")
	@ObfuscatedSignature(
		descriptor = "Lin;"
	)
	public static final ServerPacket UPDATE_ZONE_FULL_FOLLOWS;
	@ObfuscatedName("bj")
	@ObfuscatedSignature(
		descriptor = "Lin;"
	)
	public static final ServerPacket IF_SETSCROLLPOS;
	@ObfuscatedName("bo")
	@ObfuscatedSignature(
		descriptor = "Lin;"
	)
	public static final ServerPacket IF_SETCOLOUR;
	@ObfuscatedName("bx")
	@ObfuscatedSignature(
		descriptor = "Lin;"
	)
	public static final ServerPacket MESSAGE_GAME;
	@ObfuscatedName("bu")
	@ObfuscatedSignature(
		descriptor = "Lin;"
	)
	public static final ServerPacket MESSAGE_PRIVATE;
	@ObfuscatedName("bc")
	@ObfuscatedSignature(
		descriptor = "Lin;"
	)
	public static final ServerPacket TRADING_POST_RESULTS;
	@ObfuscatedName("ba")
	@ObfuscatedSignature(
		descriptor = "Lin;"
	)
	public static final ServerPacket OBJ_COUNT;
	@ObfuscatedName("bm")
	@ObfuscatedSignature(
		descriptor = "Lin;"
	)
	public static final ServerPacket REBUILD_NORMAL;
	@ObfuscatedName("bv")
	@ObfuscatedSignature(
		descriptor = "Lin;"
	)
	public static final ServerPacket IF_SETANIM;
	@ObfuscatedName("bs")
	@ObfuscatedSignature(
		descriptor = "Lin;"
	)
	public static final ServerPacket VARP_LARGE;
	@ObfuscatedName("bd")
	@ObfuscatedSignature(
		descriptor = "Lin;"
	)
	public static final ServerPacket OBJ_DEL;
	@ObfuscatedName("bb")
	@ObfuscatedSignature(
		descriptor = "Lin;"
	)
	public static final ServerPacket UPDATE_STAT;
	@ObfuscatedName("bh")
	@ObfuscatedSignature(
		descriptor = "Lin;"
	)
	public static final ServerPacket URL_OPEN;
	@ObfuscatedName("bi")
	@ObfuscatedSignature(
		descriptor = "Lin;"
	)
	public static final ServerPacket LOC_DEL;
	@ObfuscatedName("bq")
	@ObfuscatedSignature(
		descriptor = "Lin;"
	)
	public static final ServerPacket field2848;
	@ObfuscatedName("by")
	@ObfuscatedSignature(
		descriptor = "Lin;"
	)
	public static final ServerPacket CLANSETTINGS_DELTA;
	@ObfuscatedName("be")
	@ObfuscatedSignature(
		descriptor = "Lin;"
	)
	public static final ServerPacket field2858;
	@ObfuscatedName("bw")
	@ObfuscatedSignature(
		descriptor = "Lin;"
	)
	public static final ServerPacket field2859;
	@ObfuscatedName("bn")
	@ObfuscatedSignature(
		descriptor = "Lin;"
	)
	public static final ServerPacket NPC_INFO_LARGE;
	@ObfuscatedName("bl")
	@ObfuscatedSignature(
		descriptor = "Lin;"
	)
	public static final ServerPacket field2824;
	@ObfuscatedName("bk")
	@ObfuscatedSignature(
		descriptor = "Lin;"
	)
	public static final ServerPacket LOGOUT_FULL;
	@ObfuscatedName("bp")
	@ObfuscatedSignature(
		descriptor = "Lin;"
	)
	public static final ServerPacket field2863;
	@ObfuscatedName("br")
	@ObfuscatedSignature(
		descriptor = "Lin;"
	)
	public static final ServerPacket MESSAGE_FRIENDCHANNEL;
	@ObfuscatedName("ca")
	@ObfuscatedSignature(
		descriptor = "Lin;"
	)
	public static final ServerPacket IF_SETEVENTS;
	@ObfuscatedName("ct")
	@ObfuscatedSignature(
		descriptor = "Lin;"
	)
	public static final ServerPacket HINT_ARROW;
	@ObfuscatedName("cy")
	@ObfuscatedSignature(
		descriptor = "Lin;"
	)
	public static final ServerPacket IF_SETMODEL;
	@ObfuscatedName("cg")
	@ObfuscatedSignature(
		descriptor = "Lin;"
	)
	public static final ServerPacket SYNTH_SOUND;
	@ObfuscatedName("ci")
	@ObfuscatedSignature(
		descriptor = "Lin;"
	)
	public static final ServerPacket UPDATE_UID192;
	@ObfuscatedName("cx")
	@ObfuscatedSignature(
		descriptor = "Lin;"
	)
	public static final ServerPacket CHAT_FILTER_SETTINGS_PRIVATECHAT;
	@ObfuscatedName("cd")
	@ObfuscatedSignature(
		descriptor = "Lin;"
	)
	public static final ServerPacket IF_MOVESUB;
	@ObfuscatedName("cp")
	@ObfuscatedSignature(
		descriptor = "Lin;"
	)
	public static final ServerPacket MAP_PROJANIM;
	@ObfuscatedName("cl")
	@ObfuscatedSignature(
		descriptor = "Lin;"
	)
	public static final ServerPacket LOGOUT_TRANSFER;
	@ObfuscatedName("cr")
	@ObfuscatedSignature(
		descriptor = "Lin;"
	)
	public static final ServerPacket UPDATE_INV_FULL;
	@ObfuscatedName("cq")
	@ObfuscatedSignature(
		descriptor = "Lin;"
	)
	public static final ServerPacket UPDATE_RUNENERGY;
	@ObfuscatedName("cs")
	@ObfuscatedSignature(
		descriptor = "Lin;"
	)
	public static final ServerPacket UPDATE_REBOOT_TIMER;
	@ObfuscatedName("cw")
	@ObfuscatedSignature(
		descriptor = "Lin;"
	)
	public static final ServerPacket REFLECTION_CHECK;
	@ObfuscatedName("ce")
	@ObfuscatedSignature(
		descriptor = "Lin;"
	)
	public static final ServerPacket RESET_CLIENT_VARCACHE;
	@ObfuscatedName("cz")
	@ObfuscatedSignature(
		descriptor = "Lin;"
	)
	public static final ServerPacket VARCLAN;
	@ObfuscatedName("cu")
	@ObfuscatedSignature(
		descriptor = "Lin;"
	)
	public static final ServerPacket CLANCHANNEL_FULL;
	@ObfuscatedName("cv")
	@ObfuscatedSignature(
		descriptor = "Lin;"
	)
	public static final ServerPacket CLANCHANNEL_DELTA;
	@ObfuscatedName("cn")
	@ObfuscatedGetter(
		intValue = -215332077
	)
	@Export("id")
	public final int id;
	@ObfuscatedName("cf")
	@ObfuscatedGetter(
		intValue = -387107353
	)
	@Export("length")
	public final int length;

	static {
		CAM_SHAKE = new ServerPacket(0, 4);
		SOUND_AREA = new ServerPacket(1, 5);
		UPDATE_IGNORELIST = new ServerPacket(2, -2);
		field2790 = new ServerPacket(3, 20);
		VARCLAN_ENABLE = new ServerPacket(4, 0);
		SEND_PING = new ServerPacket(5, 8);
		field2862 = new ServerPacket(6, 4);
		IF_SETANGLE = new ServerPacket(7, 10);
		MAP_ANIM = new ServerPacket(8, 6);
		UPDATE_ZONE_PARTIAL_FOLLOWS = new ServerPacket(9, 2);
		IF_SETNPCHEAD = new ServerPacket(10, 6);
		LOC_ANIM = new ServerPacket(11, 4);
		IF_SETTEXT = new ServerPacket(12, -2);
		IF_SETPLAYERHEAD = new ServerPacket(13, 4);
		IF_SETOBJECT = new ServerPacket(14, 10);
		UPDATE_SITESETTINGS = new ServerPacket(15, -1);
		PLAYER_INFO = new ServerPacket(16, -2);
		MINIMAP_TOGGLE = new ServerPacket(17, 1);
		UPDATE_FRIENDLIST = new ServerPacket(18, -2);
		UPDATE_INV_PARTIAL = new ServerPacket(19, -2);
		field2807 = new ServerPacket(20, 1);
		RUNCLIENTSCRIPT = new ServerPacket(21, -2);
		CLANSETTINGS_FULL = new ServerPacket(22, -2);
		VARCLAN_DISABLE = new ServerPacket(23, 0);
		MIDI_JINGLE = new ServerPacket(24, 5);
		NPC_INFO_SMALL = new ServerPacket(25, -2);
		CAM_RESET = new ServerPacket(26, 0);
		ENTER_FREECAM = new ServerPacket(27, 1);
		REBUILD_REGION = new ServerPacket(28, -2);
		UPDATE_ZONE_PARTIAL_ENCLOSED = new ServerPacket(29, -2);
		FRIENDLIST_LOADED = new ServerPacket(30, 0);
		UPDATE_RUNWEIGHT = new ServerPacket(31, 2);
		MESSAGE_CLANCHANNEL = new ServerPacket(32, -1);
		IF_OPENSUB = new ServerPacket(33, 7);
		OBJ_ADD = new ServerPacket(34, 5);
		RESET_ANIMS = new ServerPacket(35, 0);
		TRIGGER_ONDIALOGABORT = new ServerPacket(36, 0);
		IF_SETHIDE = new ServerPacket(37, 5);
		field2845 = new ServerPacket(38, 14);
		MIDI_SONG = new ServerPacket(39, 2);
		SET_MAP_FLAG = new ServerPacket(40, 2);
		UPDATE_FRIENDCHAT_CHANNEL_FULL = new ServerPacket(41, -2);
		UPDATE_FRIENDCHAT_CHANNEL_SINGLEUSER = new ServerPacket(42, -1);
		LOC_ADD_CHANGE = new ServerPacket(43, 4);
		UPDATE_INV_STOP_TRANSMIT = new ServerPacket(44, 2);
		CAM_LOOKAT = new ServerPacket(45, 6);
		MESSAGE_CLANCHANNEL_SYSTEM = new ServerPacket(46, -1);
		IF_CLOSESUB = new ServerPacket(47, 4);
		VARP_SMALL = new ServerPacket(48, 3);
		IF_OPENTOP = new ServerPacket(49, 2);
		CAM_MOVETO = new ServerPacket(50, 6);
		CHAT_FILTER_SETTINGS = new ServerPacket(51, 2);
		MESSAGE_PRIVATE_ECHO = new ServerPacket(52, -2);
		SET_PLAYER_OP = new ServerPacket(53, -1);
		IF_SETPOSITION = new ServerPacket(54, 8);
		UPDATE_ZONE_FULL_FOLLOWS = new ServerPacket(55, 2);
		IF_SETSCROLLPOS = new ServerPacket(56, 6);
		IF_SETCOLOUR = new ServerPacket(57, 6);
		MESSAGE_GAME = new ServerPacket(58, -1);
		MESSAGE_PRIVATE = new ServerPacket(59, -2);
		TRADING_POST_RESULTS = new ServerPacket(60, -2);
		OBJ_COUNT = new ServerPacket(61, 7);
		REBUILD_NORMAL = new ServerPacket(62, -2);
		IF_SETANIM = new ServerPacket(63, 6);
		VARP_LARGE = new ServerPacket(64, 6);
		OBJ_DEL = new ServerPacket(65, 3);
		UPDATE_STAT = new ServerPacket(66, 6);
		URL_OPEN = new ServerPacket(67, -2);
		LOC_DEL = new ServerPacket(68, 2);
		field2848 = new ServerPacket(69, -2);
		CLANSETTINGS_DELTA = new ServerPacket(70, -2);
		field2858 = new ServerPacket(71, 4);
		field2859 = new ServerPacket(72, 0);
		NPC_INFO_LARGE = new ServerPacket(73, -2);
		field2824 = new ServerPacket(74, 1);
		LOGOUT_FULL = new ServerPacket(75, 0);
		field2863 = new ServerPacket(76, 8);
		MESSAGE_FRIENDCHANNEL = new ServerPacket(77, -1);
		IF_SETEVENTS = new ServerPacket(78, 12);
		HINT_ARROW = new ServerPacket(79, 6);
		IF_SETMODEL = new ServerPacket(80, 6);
		SYNTH_SOUND = new ServerPacket(81, 5);
		UPDATE_UID192 = new ServerPacket(82, 28);
		CHAT_FILTER_SETTINGS_PRIVATECHAT = new ServerPacket(83, 1);
		IF_MOVESUB = new ServerPacket(84, 8);
		MAP_PROJANIM = new ServerPacket(85, 15);
		LOGOUT_TRANSFER = new ServerPacket(86, -1);
		UPDATE_INV_FULL = new ServerPacket(87, -2);
		UPDATE_RUNENERGY = new ServerPacket(88, 1);
		UPDATE_REBOOT_TIMER = new ServerPacket(89, 2);
		REFLECTION_CHECK = new ServerPacket(90, -2);
		RESET_CLIENT_VARCACHE = new ServerPacket(91, 0);
		VARCLAN = new ServerPacket(92, -1);
		CLANCHANNEL_FULL = new ServerPacket(93, -2);
		CLANCHANNEL_DELTA = new ServerPacket(94, -2);
	}

	ServerPacket(int var1, int var2) {
		this.id = var1;
		this.length = var2;
	}

	@ObfuscatedName("hi")
	@ObfuscatedSignature(
		descriptor = "(IIIIIIII)V",
		garbageValue = "1488688652"
	)
	@Export("addPendingSpawnToScene")
	static final void addPendingSpawnToScene(int var0, int var1, int var2, int var3, int var4, int var5, int var6) {
		if (var2 >= 1 && var3 >= 1 && var2 <= 102 && var3 <= 102) {
			if (Client.isLowDetail && var0 != VertexNormal.Client_plane) {
				return;
			}

			long var7 = 0L;
			boolean var9 = true;
			boolean var10 = false;
			boolean var11 = false;
			if (var1 == 0) {
				var7 = PlayerComposition.scene.getBoundaryObjectTag(var0, var2, var3);
			}

			if (var1 == 1) {
				var7 = PlayerComposition.scene.getWallDecorationTag(var0, var2, var3);
			}

			if (var1 == 2) {
				var7 = PlayerComposition.scene.getGameObjectTag(var0, var2, var3);
			}

			if (var1 == 3) {
				var7 = PlayerComposition.scene.getFloorDecorationTag(var0, var2, var3);
			}

			int var12;
			if (0L != var7) {
				var12 = PlayerComposition.scene.getObjectFlags(var0, var2, var3, var7);
				int var14 = InterfaceParent.Entity_unpackID(var7);
				int var15 = var12 & 31;
				int var16 = var12 >> 6 & 3;
				ObjectComposition var13;
				if (var1 == 0) {
					PlayerComposition.scene.removeBoundaryObject(var0, var2, var3);
					var13 = class245.getObjectDefinition(var14);
					if (var13.interactType != 0) {
						Client.collisionMaps[var0].method3388(var2, var3, var15, var16, var13.boolean1);
					}
				}

				if (var1 == 1) {
					PlayerComposition.scene.removeWallDecoration(var0, var2, var3);
				}

				if (var1 == 2) {
					PlayerComposition.scene.removeGameObject(var0, var2, var3);
					var13 = class245.getObjectDefinition(var14);
					if (var2 + var13.sizeX > 103 || var3 + var13.sizeX > 103 || var2 + var13.sizeY > 103 || var3 + var13.sizeY > 103) {
						return;
					}

					if (var13.interactType != 0) {
						Client.collisionMaps[var0].setFlagOffNonSquare(var2, var3, var13.sizeX, var13.sizeY, var16, var13.boolean1);
					}
				}

				if (var1 == 3) {
					PlayerComposition.scene.removeFloorDecoration(var0, var2, var3);
					var13 = class245.getObjectDefinition(var14);
					if (var13.interactType == 1) {
						Client.collisionMaps[var0].method3391(var2, var3);
					}
				}
			}

			if (var4 >= 0) {
				var12 = var0;
				if (var0 < 3 && (Tiles.Tiles_renderFlags[1][var2][var3] & 2) == 2) {
					var12 = var0 + 1;
				}

				class115.method2495(var0, var12, var2, var3, var4, var5, var6, PlayerComposition.scene, Client.collisionMaps[var0]);
			}
		}

	}
}
