package osrs;

import net.runelite.mapping.*;

import java.io.IOException;

@ObfuscatedName("iu")
@Implements("ClientPacket")
public class ClientPacket implements class251 {
	// Allows us to create instance and provide in module
	public ClientPacket() {
		id = -1;
		length = -1;
	}

	@ObfuscatedName("c")
	@ObfuscatedSignature(
		descriptor = "Liu;"
	)
	public static final ClientPacket CHAT_SETMODE; //CHAT_SETFILTER
	@ObfuscatedName("b")
	@ObfuscatedSignature(
		descriptor = "Liu;"
	)
	public static final ClientPacket IF1_BUTTON4; //WIDGET_FOURTH_OPTION
	@ObfuscatedName("p")
	@ObfuscatedSignature(
		descriptor = "Liu;"
	)
	public static final ClientPacket OPHELDU; //ITEM_USE_ON_WIDGET_ITEM
	@ObfuscatedName("m")
	@ObfuscatedSignature(
		descriptor = "Liu;"
	)
	public static final ClientPacket IGNORELIST_DEL; //REMOVE FROM IGNORE LIST
	@ObfuscatedName("t")
	@ObfuscatedSignature(
		descriptor = "Liu;"
	)
	static final ClientPacket field2806; //unused
	@ObfuscatedName("s")
	@ObfuscatedSignature(
		descriptor = "Liu;"
	)
	public static final ClientPacket OPHELD4; //ITEM_FOURTH_OPTION
	@ObfuscatedName("j")
	@ObfuscatedSignature(
		descriptor = "Liu;"
	)
	public static final ClientPacket RESUME_P_OBJDIALOG; //RESUME_OBJDIALOG
	@ObfuscatedName("w")
	@ObfuscatedSignature(
		descriptor = "Liu;"
	)
	public static final ClientPacket EVENT_APPLET_FOCUS; //FOCUS CHANGE
	@ObfuscatedName("n")
	@ObfuscatedSignature(
		descriptor = "Liu;"
	)
	public static final ClientPacket IF_BUTTON10; //widgetDefaultMenuAction_WIDGET_OPTION_10
	@ObfuscatedName("r")
	@ObfuscatedSignature(
		descriptor = "Liu;"
	)
	public static final ClientPacket EVENT_MOUSE_IDLE; //lOGOUT
	@ObfuscatedName("o")
	@ObfuscatedSignature(
		descriptor = "Liu;"
	)
	public static final ClientPacket IF_BUTTON9; //widgetDefaultMenuAction_WIDGET_OPTION_9
	@ObfuscatedName("v")
	@ObfuscatedSignature(
		descriptor = "Liu;"
	)
	public static final ClientPacket IF_BUTTON2; //widgetDefaultMenuAction_WIDGET_OPTION_2
	@ObfuscatedName("d")
	@ObfuscatedSignature(
		descriptor = "Liu;"
	)
	public static final ClientPacket NO_TIMEOUT; //idk
	@ObfuscatedName("h")
	@ObfuscatedSignature(
		descriptor = "Liu;"
	)
	public static final ClientPacket OPLOC6; //examine object
	@ObfuscatedName("g")
	@ObfuscatedSignature(
		descriptor = "Liu;"
	)
	public static final ClientPacket OPHELDD; //WIDGET DRAG
	@ObfuscatedName("e")
	@ObfuscatedSignature(
		descriptor = "Liu;"
	)
	public static final ClientPacket IF_BUTTONT; //SPELL_CAST_ON_WIDGET
	@ObfuscatedName("a")
	@ObfuscatedSignature(
		descriptor = "Liu;"
	)
	public static final ClientPacket IF_BUTTON1; //widgetDefaultMenuAction_WIDGET_OPTION_1
	@ObfuscatedName("u")
	@ObfuscatedSignature(
		descriptor = "Liu;"
	)
	public static final ClientPacket CLICKWORLDMAP_TELEPORT; //jmod teleport probably
	@ObfuscatedName("k")
	@ObfuscatedSignature(
		descriptor = "Liu;"
	)
	public static final ClientPacket OPPLAYERT; //SPELL_CAST_ON_PLAYER
	@ObfuscatedName("f")
	@ObfuscatedSignature(
		descriptor = "Liu;"
	)
	public static final ClientPacket IF_BUTTON4; //widgetDefaultMenuAction_WIDGET_OPTION_4
	@ObfuscatedName("l")
	@ObfuscatedSignature(
		descriptor = "Liu;"
	)
	public static final ClientPacket IF_BUTTON7; //widgetDefaultMenuAction_WIDGET_OPTION_7
	@ObfuscatedName("q")
	@ObfuscatedSignature(
		descriptor = "Liu;"
	)
	public static final ClientPacket OPOBJ4; //GROUND_ITEM_FOURTH_OPTION
	@ObfuscatedName("x")
	@ObfuscatedSignature(
		descriptor = "Liu;"
	)
	public static final ClientPacket OPNPC6; //EXAMINE_NPC
	@ObfuscatedName("z")
	@ObfuscatedSignature(
		descriptor = "Liu;"
	)
	public static final ClientPacket CLICKWORLDMAP; //world map click?
	@ObfuscatedName("i")
	@ObfuscatedSignature(
		descriptor = "Liu;"
	)
	public static final ClientPacket OPOBJ1; //GROUND_ITEM_FIRST_OPTION
	@ObfuscatedName("y")
	@ObfuscatedSignature(
		descriptor = "Liu;"
	)
	public static final ClientPacket RESUME_P_COUNTDIALOG; //RESUME_COUNTDIALOG
	@ObfuscatedName("ah")
	@ObfuscatedSignature(
		descriptor = "Liu;"
	)
	public static final ClientPacket CLAN_KICKUSER; //clanKickUser
	@ObfuscatedName("ao")
	@ObfuscatedSignature(
		descriptor = "Liu;"
	)
	public static final ClientPacket OPOBJ2; //GROUND_ITEM_SECOND_OPTION
	@ObfuscatedName("ab")
	@ObfuscatedSignature(
		descriptor = "Liu;"
	)
	public static final ClientPacket OPLOCU; //ITEM_USE_ON_GAME_OBJECT
	@ObfuscatedName("an")
	@ObfuscatedSignature(
		descriptor = "Liu;"
	)
	public static final ClientPacket IF_BUTTON3; //widgetDefaultMenuAction_WIDGET_OPTION_3
	@ObfuscatedName("ax")
	@ObfuscatedSignature(
		descriptor = "Liu;"
	)
	static final ClientPacket field2832; //unused
	@ObfuscatedName("am")
	@ObfuscatedSignature(
		descriptor = "Liu;"
	)
	public static final ClientPacket OPHELD2; //ITEM_SECOND_OPTION
	@ObfuscatedName("az")
	@ObfuscatedSignature(
		descriptor = "Liu;"
	)
	public static final ClientPacket IF_BUTTOND; //move item
	@ObfuscatedName("au")
	@ObfuscatedSignature(
		descriptor = "Liu;"
	)
	public static final ClientPacket OPOBJ5; //GROUND_ITEM_FIFTH_OPTION
	@ObfuscatedName("av")
	@ObfuscatedSignature(
		descriptor = "Liu;"
	)
	public static final ClientPacket OPOBJT; //SPELL_CAST_ON_GROUND_ITEM
	@ObfuscatedName("ap")
	@ObfuscatedSignature(
		descriptor = "Liu;"
	)
	public static final ClientPacket EVENT_MOUSE_CLICK;
	@ObfuscatedName("ac")
	@ObfuscatedSignature(
		descriptor = "Liu;"
	)
	public static final ClientPacket CLAN_JOINCHAT_LEAVECHAT; //join and leave clan chat
	@ObfuscatedName("aj")
	@ObfuscatedSignature(
		descriptor = "Liu;"
	)
	public static final ClientPacket OPNPC1; //NPC_FIRST_OPTION
	@ObfuscatedName("af")
	@ObfuscatedSignature(
		descriptor = "Liu;"
	)
	public static final ClientPacket OPNPCU; //ITEM_USE_ON_NPC
	@ObfuscatedName("ar")
	@ObfuscatedSignature(
		descriptor = "Liu;"
	)
	public static final ClientPacket OPOBJU; //ITEM_USE_ON_GROUND_ITEM
	@ObfuscatedName("ag")
	@ObfuscatedSignature(
		descriptor = "Liu;"
	)
	public static final ClientPacket WIDGET_TYPE_1; //WIDGET_TYPE_1
	@ObfuscatedName("al")
	@ObfuscatedSignature(
		descriptor = "Liu;"
	)
	public static final ClientPacket IF1_BUTTON5; //WIDGET_FIFTH_OPTION
	@ObfuscatedName("aa")
	@ObfuscatedSignature(
		descriptor = "Liu;"
	)
	public static final ClientPacket IF1_BUTTON1; //WIDGET_FIRST_OPTION
	@ObfuscatedName("as")
	@ObfuscatedSignature(
		descriptor = "Liu;"
	)
	public static final ClientPacket OPHELD5; //ITEM_FIFTH_OPTION
	@ObfuscatedName("at")
	@ObfuscatedSignature(
		descriptor = "Liu;"
	)
	public static final ClientPacket CLIENT_CHEAT; //doCheat
	@ObfuscatedName("ai")
	@ObfuscatedSignature(
		descriptor = "Liu;"
	)
	public static final ClientPacket BUG_REPORT; //BUG_REPORT
	@ObfuscatedName("aq")
	@ObfuscatedSignature(
		descriptor = "Liu;"
	)
	public static final ClientPacket OPPLAYER3; //PLAYER_THIRD_OPTION
	@ObfuscatedName("aw")
	@ObfuscatedSignature(
		descriptor = "Liu;"
	)
	public static final ClientPacket OPPLAYER4; //PLAYER_FOURTH_OPTION
	@ObfuscatedName("ay")
	@ObfuscatedSignature(
		descriptor = "Liu;"
	)
	public static final ClientPacket WINDOW_STATUS; //WINDOW_STATUS
	@ObfuscatedName("ae")
	@ObfuscatedSignature(
		descriptor = "Liu;"
	)
	public static final ClientPacket PLAYER_REPORT; //CHAT_SENDABUSEREPORT
	@ObfuscatedName("ak")
	@ObfuscatedSignature(
		descriptor = "Liu;"
	)
	public static final ClientPacket REFLECTION_CHECK_REPLY; //reflection check
	@ObfuscatedName("ad")
	@ObfuscatedSignature(
		descriptor = "Liu;"
	)
	public static final ClientPacket RESUME_P_NAMEDIALOG; //RESUME_NAMEDIALOG
	@ObfuscatedName("bp")
	@ObfuscatedSignature(
		descriptor = "Liu;"
	)
	public static final ClientPacket FRIENDLIST_DEL; //removeFriend
	@ObfuscatedName("bd")
	@ObfuscatedSignature(
		descriptor = "Liu;"
	)
	public static final ClientPacket OPLOC5; //GAME_OBJECT_FIFTH_OPTION
	@ObfuscatedName("ba")
	@ObfuscatedSignature(
		descriptor = "Liu;"
	)
	public static final ClientPacket OPLOC3; //GAME_OBJECT_FIRST_OPTION
	@ObfuscatedName("bq")
	@ObfuscatedSignature(
		descriptor = "Liu;"
	)
	public static final ClientPacket MAP_BUILD_COMPLETE; //finish logged in?
	@ObfuscatedName("bg")
	@ObfuscatedSignature(
		descriptor = "Liu;"
	)
	static final ClientPacket field2858; //unused
	@ObfuscatedName("br")
	@ObfuscatedSignature(
		descriptor = "Liu;"
	)
	public static final ClientPacket MOVE_GAMECLICK;
	@ObfuscatedName("bi")
	@ObfuscatedSignature(
		descriptor = "Liu;"
	)
	public static final ClientPacket MOVE_MINIMAP_CLICK; //minimap clicked
	@ObfuscatedName("bm")
	@ObfuscatedSignature(
		descriptor = "Liu;"
	)
	public static final ClientPacket OPNPC3; //NPC_THIRD_OPTION
	@ObfuscatedName("bw")
	@ObfuscatedSignature(
		descriptor = "Liu;"
	)
	public static final ClientPacket MESSAGE_PRIVATE; //CHAT_SENDPRIVATE
	@ObfuscatedName("bl")
	@ObfuscatedSignature(
		descriptor = "Liu;"
	)
	public static final ClientPacket OPPLAYER6; //PLAYER_SIXTH_OPTION
	@ObfuscatedName("bz")
	@ObfuscatedSignature(
		descriptor = "Liu;"
	)
	public static final ClientPacket IF1_BUTTON3; //WIDGET_THIRD_OPTION
	@ObfuscatedName("bu")
	@ObfuscatedSignature(
		descriptor = "Liu;"
	)
	public static final ClientPacket OPNPC2; //NPC_SECOND_OPTION
	@ObfuscatedName("bs")
	@ObfuscatedSignature(
		descriptor = "Liu;"
	)
	public static final ClientPacket IF_BUTTON6; //widgetDefaultMenuAction_WIDGET_OPTION_6
	@ObfuscatedName("bv")
	@ObfuscatedSignature(
		descriptor = "Liu;"
	)
	public static final ClientPacket FREECAM_EXIT; //exit occulus orb
	@ObfuscatedName("bb")
	@ObfuscatedSignature(
		descriptor = "Liu;"
	)
	public static final ClientPacket OPHELD3; //ITEM_THIRD_OPTION
	@ObfuscatedName("bc")
	@ObfuscatedSignature(
		descriptor = "Liu;"
	)
	public static final ClientPacket EVENT_CAMERA_POSITION; //not sure. something with camera
	@ObfuscatedName("bx")
	@ObfuscatedSignature(
		descriptor = "Liu;"
	)
	public static final ClientPacket OPLOC4; //GAME_OBJECT_FOURTH_OPTION
	@ObfuscatedName("bt")
	@ObfuscatedSignature(
		descriptor = "Liu;"
	)
	public static final ClientPacket OPNPCT; //SPELL_CAST_ON_NPC
	@ObfuscatedName("bh")
	@ObfuscatedSignature(
		descriptor = "Liu;"
	)
	public static final ClientPacket OPNPC4; //NPC_FOURTH_OPTION
	@ObfuscatedName("bn")
	@ObfuscatedSignature(
		descriptor = "Liu;"
	)
	public static final ClientPacket RESUME_PAUSEBUTTON; //resumePauseWidget
	@ObfuscatedName("bj")
	@ObfuscatedSignature(
		descriptor = "Liu;"
	)
	public static final ClientPacket OPHELD1; //ITEM_FIRST_OPTION
	@ObfuscatedName("by")
	@ObfuscatedSignature(
		descriptor = "Liu;"
	)
	public static final ClientPacket OPPLAYERU; //ITEM_USE_ON_PLAYER
	@ObfuscatedName("bk")
	@ObfuscatedSignature(
		descriptor = "Liu;"
	)
	public static final ClientPacket OPNPC5; //NPC_FIFTH_OPTION
	@ObfuscatedName("bo")
	@ObfuscatedSignature(
		descriptor = "Liu;"
	)
	public static final ClientPacket IF_BUTTON5; //widgetDefaultMenuAction_WIDGET_OPTION_5
	@ObfuscatedName("bf")
	@ObfuscatedSignature(
		descriptor = "Liu;"
	)
	public static final ClientPacket OPOBJ3; //GROUND_ITEM_THIRD_OPTION
	@ObfuscatedName("be")
	@ObfuscatedSignature(
		descriptor = "Liu;"
	)
	public static final ClientPacket DETECT_MODIFIED_CLIENT; //something about region updates or checking for applet
	@ObfuscatedName("ce")
	@ObfuscatedSignature(
		descriptor = "Liu;"
	)
	public static final ClientPacket RESUME_P_STRINGDIALOG; //RESUME_STRINGDIALOG
	@ObfuscatedName("cl")
	@ObfuscatedSignature(
		descriptor = "Liu;"
	)
	static final ClientPacket field2881; //unused
	@ObfuscatedName("cp")
	@ObfuscatedSignature(
		descriptor = "Liu;"
	)
	public static final ClientPacket OPLOCT; //SPELL_CAST_ON_GAME_OBJECT
	@ObfuscatedName("cd")
	@ObfuscatedSignature(
		descriptor = "Liu;"
	)
	public static final ClientPacket OPLOC1; //GAME_OBJECT_FIRST_OPTION
	@ObfuscatedName("ck")
	@ObfuscatedSignature(
		descriptor = "Liu;"
	)
	public static final ClientPacket IF_BUTTON8; //widgetDefaultMenuAction_WIDGET_OPTION_8
	@ObfuscatedName("cg")
	@ObfuscatedSignature(
		descriptor = "Liu;"
	)
	public static final ClientPacket OPPLAYER8; //PLAYER_EIGTH_OPTION
	@ObfuscatedName("cy")
	@ObfuscatedSignature(
		descriptor = "Liu;"
	)
	public static final ClientPacket OPHELDT; //ITEM_USE_ON_WIDGET
	@ObfuscatedName("cn")
	@ObfuscatedSignature(
		descriptor = "Liu;"
	)
	public static final ClientPacket OPPLAYER1; //PLAYER_FIRST_OPTION
	@ObfuscatedName("cv")
	@ObfuscatedSignature(
		descriptor = "Liu;"
	)
	public static final ClientPacket field2888; //AFFINEDCLANSETTINGS_ADDBANNED_FROMCHANNEL
	@ObfuscatedName("co")
	@ObfuscatedSignature(
		descriptor = "Liu;"
	)
	public static final ClientPacket PING_STATISTICS; //send client info
	@ObfuscatedName("cc")
	@ObfuscatedSignature(
		descriptor = "Liu;"
	)
	public static final ClientPacket IF1_BUTTON2; //WIDGET_SECOND_OPTION
	@ObfuscatedName("cs")
	@ObfuscatedSignature(
		descriptor = "Liu;"
	)
	public static final ClientPacket UPDATE_APPEARANCE; //send player appearance and equipment
	@ObfuscatedName("cr")
	@ObfuscatedSignature(
		descriptor = "Liu;"
	)
	public static final ClientPacket OPPLAYER5; //PLAYER_FIFTH_OPTION
	@ObfuscatedName("cb")
	@ObfuscatedSignature(
		descriptor = "Liu;"
	)
	public static final ClientPacket EVENT_MOUSE_MOVE; //something about mouse position
	@ObfuscatedName("cj")
	@ObfuscatedSignature(
		descriptor = "Liu;"
	)
	public static final ClientPacket OPPLAYER2; //PLAYER_SECOND_OPTION
	@ObfuscatedName("ca")
	@ObfuscatedSignature(
		descriptor = "Liu;"
	)
	public static final ClientPacket FRIENDLIST_ADD; //add friend
	@ObfuscatedName("cz")
	@ObfuscatedSignature(
		descriptor = "Liu;"
	)
	public static final ClientPacket OPLOC2; //GAME_OBJECT_SECOND_OPTION
	@ObfuscatedName("cw")
	@ObfuscatedSignature(
		descriptor = "Liu;"
	)
	public static final ClientPacket MESSAGE_PUBLIC; //CHAT_SENDPUBLIC and CHAT_SENDCLAN
	@ObfuscatedName("ct")
	@ObfuscatedSignature(
		descriptor = "Liu;"
	)
	public static final ClientPacket CLOSE_MODAL; //WIDGET_TYPE_3
	@ObfuscatedName("ci")
	@ObfuscatedSignature(
		descriptor = "Liu;"
	)
	public static final ClientPacket EVENT_KEYBOARD; //keypress maybe
	@ObfuscatedName("ch")
	@ObfuscatedSignature(
		descriptor = "Liu;"
	)
	static final ClientPacket field2900; //unused
	@ObfuscatedName("cq")
	@ObfuscatedSignature(
		descriptor = "Liu;"
	)
	public static final ClientPacket CLAN_SETRANK; //FRIEND_SETRANK
	@ObfuscatedName("cf")
	@ObfuscatedSignature(
		descriptor = "Liu;"
	)
	public static final ClientPacket OPOBJ6; //EXAMINE_ITEM_GROUND
	@ObfuscatedName("cu")
	@ObfuscatedSignature(
		descriptor = "Liu;"
	)
	public static final ClientPacket field2903; //AFFINEDCLANSETTINGS_SETMUTED_FROMCHANNEL
	@ObfuscatedName("cx")
	@ObfuscatedSignature(
		descriptor = "Liu;"
	)
	public static final ClientPacket field2875; //ACTIVECLANCHANNEL_KICKUSER
	@ObfuscatedName("cm")
	@ObfuscatedSignature(
		descriptor = "Liu;"
	)
	public static final ClientPacket IGNORELIST_ADD; //addIgnore
	@ObfuscatedName("dm")
	@ObfuscatedSignature(
		descriptor = "Liu;"
	)
	public static final ClientPacket OPPLAYER7; //PLAYER_SEVENTH_OPTION
	@ObfuscatedName("dr")
	@ObfuscatedSignature(
		descriptor = "Liu;"
	)
	public static final ClientPacket LOGIN_TIMINGS; //something about reflection check maybe
	@ObfuscatedName("ds")
	@ObfuscatedGetter(
		intValue = -94628489
	)
	@Export("id")
	final int id;
	@ObfuscatedName("dv")
	@ObfuscatedGetter(
		intValue = -1390641219
	)
	@Export("length")
	final int length;

	static {
		CHAT_SETMODE = new ClientPacket(0, 3); // L: 5
		IF1_BUTTON4 = new ClientPacket(1, 8); // L: 6
		OPHELDU = new ClientPacket(2, 16); // L: 7
		IGNORELIST_DEL = new ClientPacket(3, -1); // L: 8
		field2806 = new ClientPacket(4, -1); // L: 9
		OPHELD4 = new ClientPacket(5, 8); // L: 10
		RESUME_P_OBJDIALOG = new ClientPacket(6, 2); // L: 11
		EVENT_APPLET_FOCUS = new ClientPacket(7, 1); // L: 12
		IF_BUTTON10 = new ClientPacket(8, 8); // L: 13
		EVENT_MOUSE_IDLE = new ClientPacket(9, 0); // L: 14
		IF_BUTTON9 = new ClientPacket(10, 8); // L: 15
		IF_BUTTON2 = new ClientPacket(11, 8); // L: 16
		NO_TIMEOUT = new ClientPacket(12, 0); // L: 17
		OPLOC6 = new ClientPacket(13, 2); // L: 18
		OPHELDD = new ClientPacket(14, 16); // L: 19
		IF_BUTTONT = new ClientPacket(15, 16); // L: 20
		IF_BUTTON1 = new ClientPacket(16, 8); // L: 21
		CLICKWORLDMAP_TELEPORT = new ClientPacket(17, 9); // L: 22
		OPPLAYERT = new ClientPacket(18, 11); // L: 23
		IF_BUTTON4 = new ClientPacket(19, 8); // L: 24
		IF_BUTTON7 = new ClientPacket(20, 8); // L: 25
		OPOBJ4 = new ClientPacket(21, 7); // L: 26
		OPNPC6 = new ClientPacket(22, 2); // L: 27
		CLICKWORLDMAP = new ClientPacket(23, 4); // L: 28
		OPOBJ1 = new ClientPacket(24, 7); // L: 29
		RESUME_P_COUNTDIALOG = new ClientPacket(25, 4); // L: 30
		CLAN_KICKUSER = new ClientPacket(26, -1); // L: 31
		OPOBJ2 = new ClientPacket(27, 7); // L: 32
		OPLOCU = new ClientPacket(28, 15); // L: 33
		IF_BUTTON3 = new ClientPacket(29, 8); // L: 34
		field2832 = new ClientPacket(30, -1); // L: 35
		OPHELD2 = new ClientPacket(31, 8); // L: 36
		IF_BUTTOND = new ClientPacket(32, 9); // L: 37
		OPOBJ5 = new ClientPacket(33, 7); // L: 38
		OPOBJT = new ClientPacket(34, 15); // L: 39
		EVENT_MOUSE_CLICK = new ClientPacket(35, 6); // L: 40
		CLAN_JOINCHAT_LEAVECHAT = new ClientPacket(36, -1); // L: 41
		OPNPC1 = new ClientPacket(37, 3); // L: 42
		OPNPCU = new ClientPacket(38, 11); // L: 43
		OPOBJU = new ClientPacket(39, 15); // L: 44
		WIDGET_TYPE_1 = new ClientPacket(40, 4); // L: 45
		IF1_BUTTON5 = new ClientPacket(41, 8); // L: 46
		IF1_BUTTON1 = new ClientPacket(42, 8); // L: 47
		OPHELD5 = new ClientPacket(43, 8); // L: 48
		CLIENT_CHEAT = new ClientPacket(44, -1); // L: 49
		BUG_REPORT = new ClientPacket(45, -2); // L: 50
		OPPLAYER3 = new ClientPacket(46, 3); // L: 51
		OPPLAYER4 = new ClientPacket(47, 3); // L: 52
		WINDOW_STATUS = new ClientPacket(48, 5); // L: 53
		PLAYER_REPORT = new ClientPacket(49, -1); // L: 54
		REFLECTION_CHECK_REPLY = new ClientPacket(50, -1); // L: 55
		RESUME_P_NAMEDIALOG = new ClientPacket(51, -1); // L: 56
		FRIENDLIST_DEL = new ClientPacket(52, -1); // L: 57
		OPLOC5 = new ClientPacket(53, 7); // L: 58
		OPLOC3 = new ClientPacket(54, 7); // L: 59
		MAP_BUILD_COMPLETE = new ClientPacket(55, 0); // L: 60
		field2858 = new ClientPacket(56, 7); // L: 61
		MOVE_GAMECLICK = new ClientPacket(57, -1); // L: 62
		MOVE_MINIMAP_CLICK = new ClientPacket(58, -1); // L: 63
		OPNPC3 = new ClientPacket(59, 3); // L: 64
		MESSAGE_PRIVATE = new ClientPacket(60, -2); // L: 65
		OPPLAYER6 = new ClientPacket(61, 3); // L: 66
		IF1_BUTTON3 = new ClientPacket(62, 8); // L: 67
		OPNPC2 = new ClientPacket(63, 3); // L: 68
		IF_BUTTON6 = new ClientPacket(64, 8); // L: 69
		FREECAM_EXIT = new ClientPacket(65, 0); // L: 70
		OPHELD3 = new ClientPacket(66, 8); // L: 71
		EVENT_CAMERA_POSITION = new ClientPacket(67, 4); // L: 72
		OPLOC4 = new ClientPacket(68, 7); // L: 73
		OPNPCT = new ClientPacket(69, 11); // L: 74
		OPNPC4 = new ClientPacket(70, 3); // L: 75
		RESUME_PAUSEBUTTON = new ClientPacket(71, 6); // L: 76
		OPHELD1 = new ClientPacket(72, 8); // L: 77
		OPPLAYERU = new ClientPacket(73, 11); // L: 78
		OPNPC5 = new ClientPacket(74, 3); // L: 79
		IF_BUTTON5 = new ClientPacket(75, 8); // L: 80
		OPOBJ3 = new ClientPacket(76, 7); // L: 81
		DETECT_MODIFIED_CLIENT = new ClientPacket(77, 4); // L: 82
		RESUME_P_STRINGDIALOG = new ClientPacket(78, -1); // L: 83
		field2881 = new ClientPacket(79, 2); // L: 84
		OPLOCT = new ClientPacket(80, 15); // L: 85
		OPLOC1 = new ClientPacket(81, 7); // L: 86
		IF_BUTTON8 = new ClientPacket(82, 8); // L: 87
		OPPLAYER8 = new ClientPacket(83, 3); // L: 88
		OPHELDT = new ClientPacket(84, 14); // L: 89
		OPPLAYER1 = new ClientPacket(85, 3); // L: 90
		field2888 = new ClientPacket(86, -1); // L: 91
		PING_STATISTICS = new ClientPacket(87, 10); // L: 92
		IF1_BUTTON2 = new ClientPacket(88, 8); // L: 93
		UPDATE_APPEARANCE = new ClientPacket(89, 13); // L: 94
		OPPLAYER5 = new ClientPacket(90, 3); // L: 95
		EVENT_MOUSE_MOVE = new ClientPacket(91, -1); // L: 96
		OPPLAYER2 = new ClientPacket(92, 3); // L: 97
		FRIENDLIST_ADD = new ClientPacket(93, -1); // L: 98
		OPLOC2 = new ClientPacket(94, 7); // L: 99
		MESSAGE_PUBLIC = new ClientPacket(95, -1); // L: 100
		CLOSE_MODAL = new ClientPacket(96, 0); // L: 101
		EVENT_KEYBOARD = new ClientPacket(97, -2); // L: 102
		field2900 = new ClientPacket(98, -1); // L: 103
		CLAN_SETRANK = new ClientPacket(99, -1); // L: 104
		OPOBJ6 = new ClientPacket(100, 2); // L: 105
		field2903 = new ClientPacket(101, -1); // L: 106
		field2875 = new ClientPacket(102, -1); // L: 107
		IGNORELIST_ADD = new ClientPacket(103, -1); // L: 108
		OPPLAYER7 = new ClientPacket(104, 3); // L: 109
		LOGIN_TIMINGS = new ClientPacket(105, -1); // L: 110
	}

	ClientPacket(int var1, int var2) {
		this.id = var1; // L: 115
		this.length = var2; // L: 116
	} // L: 117

	@ObfuscatedName("hy")
	@ObfuscatedSignature(
		descriptor = "(ZI)V",
		garbageValue = "1131418917"
	)
	static final void method5001(boolean var0) {
		class148.playPcmPlayers(); // L: 6323
		++Client.packetWriter.pendingWrites; // L: 6324
		if (Client.packetWriter.pendingWrites >= 50 || var0) { // L: 6325
			Client.packetWriter.pendingWrites = 0; // L: 6326
			if (!Client.hadNetworkError && Client.packetWriter.getSocket() != null) { // L: 6327
				PacketBufferNode var1 = HitSplatDefinition.getPacketBufferNode(NO_TIMEOUT, Client.packetWriter.isaacCipher); // L: 6329
				Client.packetWriter.addNode(var1); // L: 6330

				try {
					Client.packetWriter.flush(); // L: 6332
				} catch (IOException var3) { // L: 6334
					Client.hadNetworkError = true; // L: 6335
				}
			}

		}
	} // L: 6338
}
