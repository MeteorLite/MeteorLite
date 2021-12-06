package osrs;

import net.runelite.mapping.Export;
import net.runelite.mapping.Implements;
import net.runelite.mapping.ObfuscatedGetter;
import net.runelite.mapping.ObfuscatedName;
import net.runelite.mapping.ObfuscatedSignature;

@ObfuscatedName("ik")
@Implements("ClientPacket")
public class ClientPacket implements class238 {

   // Allows us to create instance and provide in module
   public ClientPacket() {
      id = -1;
      length = -1;
   }

   @ObfuscatedName("i")
   @ObfuscatedSignature(
      descriptor = "Lik;"
   )
   public static final ClientPacket IF_BUTTON5 = new ClientPacket(0, 8);
   @ObfuscatedName("w")
   @ObfuscatedSignature(
      descriptor = "Lik;"
   )
   public static final ClientPacket field2684 = new ClientPacket(1, -1);
   @ObfuscatedName("s")
   @ObfuscatedSignature(
      descriptor = "Lik;"
   )
   public static final ClientPacket field2757 = new ClientPacket(2, 2);
   @ObfuscatedName("a")
   @ObfuscatedSignature(
      descriptor = "Lik;"
   )
   public static final ClientPacket field2667 = new ClientPacket(3, 8);
   @ObfuscatedName("o")
   @ObfuscatedSignature(
      descriptor = "Lik;"
   )
   public static final ClientPacket field2668 = new ClientPacket(4, -1);
   @ObfuscatedName("g")
   @ObfuscatedSignature(
      descriptor = "Lik;"
   )
   public static final ClientPacket field2669 = new ClientPacket(5, 9);
   @ObfuscatedName("e")
   @ObfuscatedSignature(
      descriptor = "Lik;"
   )
   public static final ClientPacket OPLOC5 = new ClientPacket(6, 7);
   @ObfuscatedName("p")
   @ObfuscatedSignature(
      descriptor = "Lik;"
   )
   public static final ClientPacket field2755 = new ClientPacket(7, 2);
   @ObfuscatedName("j")
   @ObfuscatedSignature(
      descriptor = "Lik;"
   )
   public static final ClientPacket OPNPC5 = new ClientPacket(8, 3);
   @ObfuscatedName("b")
   @ObfuscatedSignature(
      descriptor = "Lik;"
   )
   public static final ClientPacket field2673 = new ClientPacket(9, 3);
   @ObfuscatedName("x")
   @ObfuscatedSignature(
      descriptor = "Lik;"
   )
   public static final ClientPacket field2745 = new ClientPacket(10, 5);
   @ObfuscatedName("y")
   @ObfuscatedSignature(
      descriptor = "Lik;"
   )
   public static final ClientPacket field2675 = new ClientPacket(11, -2);
   @ObfuscatedName("k")
   @ObfuscatedSignature(
      descriptor = "Lik;"
   )
   public static final ClientPacket field2676 = new ClientPacket(12, -1);
   @ObfuscatedName("t")
   @ObfuscatedSignature(
      descriptor = "Lik;"
   )
   public static final ClientPacket OPHELD3 = new ClientPacket(13, 8);
   @ObfuscatedName("l")
   @ObfuscatedSignature(
      descriptor = "Lik;"
   )
   public static final ClientPacket field2713 = new ClientPacket(14, 0);
   @ObfuscatedName("u")
   @ObfuscatedSignature(
      descriptor = "Lik;"
   )
   public static final ClientPacket OPNPC2 = new ClientPacket(15, 3);
   @ObfuscatedName("n")
   @ObfuscatedSignature(
      descriptor = "Lik;"
   )
   public static final ClientPacket field2680 = new ClientPacket(16, 10);
   @ObfuscatedName("z")
   @ObfuscatedSignature(
      descriptor = "Lik;"
   )
   public static final ClientPacket IF_BUTTON2 = new ClientPacket(17, 8);
   @ObfuscatedName("q")
   @ObfuscatedSignature(
      descriptor = "Lik;"
   )
   public static final ClientPacket IF_BUTTON3 = new ClientPacket(18, 8);
   @ObfuscatedName("d")
   @ObfuscatedSignature(
      descriptor = "Lik;"
   )
   public static final ClientPacket IF_BUTTON4 = new ClientPacket(19, 8);
   @ObfuscatedName("r")
   @ObfuscatedSignature(
      descriptor = "Lik;"
   )
   public static final ClientPacket field2699 = new ClientPacket(20, 4);
   @ObfuscatedName("m")
   @ObfuscatedSignature(
      descriptor = "Lik;"
   )
   public static final ClientPacket field2685 = new ClientPacket(21, 2);
   @ObfuscatedName("c")
   @ObfuscatedSignature(
      descriptor = "Lik;"
   )
   public static final ClientPacket IF_BUTTON1 = new ClientPacket(22, 8);
   @ObfuscatedName("f")
   @ObfuscatedSignature(
      descriptor = "Lik;"
   )
   public static final ClientPacket field2687 = new ClientPacket(23, 3);
   @ObfuscatedName("h")
   @ObfuscatedSignature(
      descriptor = "Lik;"
   )
   public static final ClientPacket field2688 = new ClientPacket(24, 13);
   @ObfuscatedName("v")
   @ObfuscatedSignature(
      descriptor = "Lik;"
   )
   public static final ClientPacket IF_BUTTON10 = new ClientPacket(25, 8);
   @ObfuscatedName("ag")
   @ObfuscatedSignature(
      descriptor = "Lik;"
   )
   public static final ClientPacket IF_BUTTON7 = new ClientPacket(26, 8);
   @ObfuscatedName("ae")
   @ObfuscatedSignature(
      descriptor = "Lik;"
   )
   public static final ClientPacket RESUME_P_COUNTDIALOG = new ClientPacket(27, 4);
   @ObfuscatedName("aq")
   @ObfuscatedSignature(
      descriptor = "Lik;"
   )
   public static final ClientPacket field2750 = new ClientPacket(28, -1);
   @ObfuscatedName("al")
   @ObfuscatedSignature(
      descriptor = "Lik;"
   )
   public static final ClientPacket field2758 = new ClientPacket(29, -1);
   @ObfuscatedName("am")
   @ObfuscatedSignature(
      descriptor = "Lik;"
   )
   public static final ClientPacket field2692 = new ClientPacket(30, 3);
   @ObfuscatedName("ai")
   @ObfuscatedSignature(
      descriptor = "Lik;"
   )
   public static final ClientPacket field2733 = new ClientPacket(31, 3);
   @ObfuscatedName("ah")
   @ObfuscatedSignature(
      descriptor = "Lik;"
   )
   public static final ClientPacket field2696 = new ClientPacket(32, 3);
   @ObfuscatedName("as")
   @ObfuscatedSignature(
      descriptor = "Lik;"
   )
   public static final ClientPacket field2697 = new ClientPacket(33, 2);
   @ObfuscatedName("at")
   @ObfuscatedSignature(
      descriptor = "Lik;"
   )
   static final ClientPacket field2698 = new ClientPacket(34, -1);
   @ObfuscatedName("az")
   @ObfuscatedSignature(
      descriptor = "Lik;"
   )
   public static final ClientPacket field2666 = new ClientPacket(35, -1);
   @ObfuscatedName("ac")
   @ObfuscatedSignature(
      descriptor = "Lik;"
   )
   public static final ClientPacket field2700 = new ClientPacket(36, -1);
   @ObfuscatedName("ak")
   @ObfuscatedSignature(
      descriptor = "Lik;"
   )
   public static final ClientPacket OPNPCU = new ClientPacket(37, 11);
   @ObfuscatedName("ab")
   @ObfuscatedSignature(
      descriptor = "Lik;"
   )
   public static final ClientPacket field2739 = new ClientPacket(38, 4);
   @ObfuscatedName("ar")
   @ObfuscatedSignature(
      descriptor = "Lik;"
   )
   public static final ClientPacket IF_BUTTON6 = new ClientPacket(39, 8);
   @ObfuscatedName("ad")
   @ObfuscatedSignature(
      descriptor = "Lik;"
   )
   public static final ClientPacket field2704 = new ClientPacket(40, -1);
   @ObfuscatedName("an")
   @ObfuscatedSignature(
      descriptor = "Lik;"
   )
   public static final ClientPacket OPLOC4 = new ClientPacket(41, 7);
   @ObfuscatedName("ax")
   @ObfuscatedSignature(
      descriptor = "Lik;"
   )
   public static final ClientPacket field2694 = new ClientPacket(42, 8);
   @ObfuscatedName("av")
   @ObfuscatedSignature(
      descriptor = "Lik;"
   )
   public static final ClientPacket OPHELDU = new ClientPacket(43, 16);
   @ObfuscatedName("ap")
   @ObfuscatedSignature(
      descriptor = "Lik;"
   )
   static final ClientPacket field2708 = new ClientPacket(44, -1);
   @ObfuscatedName("au")
   @ObfuscatedSignature(
      descriptor = "Lik;"
   )
   public static final ClientPacket groundItem3 = new ClientPacket(45, 7);
   @ObfuscatedName("aj")
   @ObfuscatedSignature(
      descriptor = "Lik;"
   )
   public static final ClientPacket field2710 = new ClientPacket(46, 3);
   @ObfuscatedName("af")
   @ObfuscatedSignature(
      descriptor = "Lik;"
   )
   public static final ClientPacket IF_BUTTON9 = new ClientPacket(47, 8);
   @ObfuscatedName("aw")
   @ObfuscatedSignature(
      descriptor = "Lik;"
   )
   public static final ClientPacket CLOSE_MODAL = new ClientPacket(48, 0);
   @ObfuscatedName("ao")
   @ObfuscatedSignature(
      descriptor = "Lik;"
   )
   public static final ClientPacket field2714 = new ClientPacket(49, 3);
   @ObfuscatedName("aa")
   @ObfuscatedSignature(
      descriptor = "Lik;"
   )
   public static final ClientPacket field2679 = new ClientPacket(50, 4);
   @ObfuscatedName("ay")
   @ObfuscatedSignature(
      descriptor = "Lik;"
   )
   public static final ClientPacket groundItem4 = new ClientPacket(51, 7);
   @ObfuscatedName("bg")
   @ObfuscatedSignature(
      descriptor = "Lik;"
   )
   public static final ClientPacket field2716 = new ClientPacket(52, -2);
   @ObfuscatedName("bl")
   @ObfuscatedSignature(
      descriptor = "Lik;"
   )
   public static final ClientPacket OPNPC1 = new ClientPacket(53, 3);
   @ObfuscatedName("bx")
   @ObfuscatedSignature(
      descriptor = "Lik;"
   )
   public static final ClientPacket field2718 = new ClientPacket(54, 8);
   @ObfuscatedName("bd")
   @ObfuscatedSignature(
      descriptor = "Lik;"
   )
   public static final ClientPacket field2719 = new ClientPacket(55, 0);
   @ObfuscatedName("bt")
   @ObfuscatedSignature(
      descriptor = "Lik;"
   )
   public static final ClientPacket field2720 = new ClientPacket(56, 4);
   @ObfuscatedName("bf")
   @ObfuscatedSignature(
      descriptor = "Lik;"
   )
   public static final ClientPacket field2721 = new ClientPacket(57, -1);
   @ObfuscatedName("bs")
   @ObfuscatedSignature(
      descriptor = "Lik;"
   )
   public static final ClientPacket OPHELDT = new ClientPacket(58, 14);
   @ObfuscatedName("bh")
   @ObfuscatedSignature(
      descriptor = "Lik;"
   )
   public static final ClientPacket OPNPC3 = new ClientPacket(59, 3);
   @ObfuscatedName("bw")
   @ObfuscatedSignature(
      descriptor = "Lik;"
   )
   public static final ClientPacket field2724 = new ClientPacket(60, -2);
   @ObfuscatedName("bm")
   @ObfuscatedSignature(
      descriptor = "Lik;"
   )
   public static final ClientPacket EVENT_MOUSE_CLICK = new ClientPacket(61, 6);
   @ObfuscatedName("br")
   @ObfuscatedSignature(
      descriptor = "Lik;"
   )
   public static final ClientPacket field2726 = new ClientPacket(62, 0);
   @ObfuscatedName("bj")
   @ObfuscatedSignature(
      descriptor = "Lik;"
   )
   public static final ClientPacket field2727 = new ClientPacket(63, 16);
   @ObfuscatedName("bo")
   @ObfuscatedSignature(
      descriptor = "Lik;"
   )
   public static final ClientPacket RESUME_PAUSEBUTTON = new ClientPacket(64, 6);
   @ObfuscatedName("bv")
   @ObfuscatedSignature(
      descriptor = "Lik;"
   )
   public static final ClientPacket OPLOCU = new ClientPacket(65, 15);
   @ObfuscatedName("bu")
   @ObfuscatedSignature(
      descriptor = "Lik;"
   )
   public static final ClientPacket OPNPC4 = new ClientPacket(66, 3);
   @ObfuscatedName("bz")
   @ObfuscatedSignature(
      descriptor = "Lik;"
   )
   public static final ClientPacket OPHELD5 = new ClientPacket(67, 8);
   @ObfuscatedName("bc")
   @ObfuscatedSignature(
      descriptor = "Lik;"
   )
   static final ClientPacket field2732 = new ClientPacket(68, 7);
   @ObfuscatedName("bi")
   @ObfuscatedSignature(
      descriptor = "Lik;"
   )
   public static final ClientPacket field2691 = new ClientPacket(69, 8);
   @ObfuscatedName("bk")
   @ObfuscatedSignature(
      descriptor = "Lik;"
   )
   public static final ClientPacket RESUME_P_STRINGDIALOG = new ClientPacket(70, -1);
   @ObfuscatedName("bp")
   @ObfuscatedSignature(
      descriptor = "Lik;"
   )
   public static final ClientPacket field2735 = new ClientPacket(71, 15);
   @ObfuscatedName("bn")
   @ObfuscatedSignature(
      descriptor = "Lik;"
   )
   public static final ClientPacket field2736 = new ClientPacket(72, 9);
   @ObfuscatedName("be")
   @ObfuscatedSignature(
      descriptor = "Lik;"
   )
   public static final ClientPacket field2737 = new ClientPacket(73, -1);
   @ObfuscatedName("bb")
   @ObfuscatedSignature(
      descriptor = "Lik;"
   )
   public static final ClientPacket field2738 = new ClientPacket(74, 15);
   @ObfuscatedName("by")
   @ObfuscatedSignature(
      descriptor = "Lik;"
   )
   static final ClientPacket field2674 = new ClientPacket(75, -1);
   @ObfuscatedName("ba")
   @ObfuscatedSignature(
      descriptor = "Lik;"
   )
   public static final ClientPacket OPHELD4 = new ClientPacket(76, 8);
   @ObfuscatedName("bq")
   @ObfuscatedSignature(
      descriptor = "Lik;"
   )
   public static final ClientPacket spellOnNpc = new ClientPacket(77, 11);
   @ObfuscatedName("ca")
   @ObfuscatedSignature(
      descriptor = "Lik;"
   )
   public static final ClientPacket field2742 = new ClientPacket(78, 0);
   @ObfuscatedName("cq")
   @ObfuscatedSignature(
      descriptor = "Lik;"
   )
   public static final ClientPacket field2743 = new ClientPacket(79, 1);
   @ObfuscatedName("ck")
   @ObfuscatedSignature(
      descriptor = "Lik;"
   )
   public static final ClientPacket OPLOC1 = new ClientPacket(80, 7);
   @ObfuscatedName("ci")
   @ObfuscatedSignature(
      descriptor = "Lik;"
   )
   public static final ClientPacket field2728 = new ClientPacket(81, 3);
   @ObfuscatedName("cx")
   @ObfuscatedSignature(
      descriptor = "Lik;"
   )
   public static final ClientPacket field2670 = new ClientPacket(82, 8);
   @ObfuscatedName("cn")
   @ObfuscatedSignature(
      descriptor = "Lik;"
   )
   public static final ClientPacket field2747 = new ClientPacket(83, -1);
   @ObfuscatedName("ct")
   @ObfuscatedSignature(
      descriptor = "Lik;"
   )
   public static final ClientPacket OPLOC3 = new ClientPacket(84, 7);
   @ObfuscatedName("cd")
   @ObfuscatedSignature(
      descriptor = "Lik;"
   )
   public static final ClientPacket field2749 = new ClientPacket(85, -1);
   @ObfuscatedName("cj")
   @ObfuscatedSignature(
      descriptor = "Lik;"
   )
   public static final ClientPacket groundItem5 = new ClientPacket(86, 7);
   @ObfuscatedName("cl")
   @ObfuscatedSignature(
      descriptor = "Lik;"
   )
   public static final ClientPacket field2746 = new ClientPacket(87, -1);
   @ObfuscatedName("cs")
   @ObfuscatedSignature(
      descriptor = "Lik;"
   )
   public static final ClientPacket field2752 = new ClientPacket(88, -1);
   @ObfuscatedName("ce")
   @ObfuscatedSignature(
      descriptor = "Lik;"
   )
   public static final ClientPacket field2753 = new ClientPacket(89, 16);
   @ObfuscatedName("cp")
   @ObfuscatedSignature(
      descriptor = "Lik;"
   )
   public static final ClientPacket OPLOC2 = new ClientPacket(90, 7);
   @ObfuscatedName("cg")
   @ObfuscatedSignature(
      descriptor = "Lik;"
   )
   public static final ClientPacket IF_BUTTON8 = new ClientPacket(91, 8);
   @ObfuscatedName("cw")
   @ObfuscatedSignature(
      descriptor = "Lik;"
   )
   public static final ClientPacket field2756 = new ClientPacket(92, 15);
   @ObfuscatedName("cr")
   @ObfuscatedSignature(
      descriptor = "Lik;"
   )
   public static final ClientPacket OPHELD1 = new ClientPacket(93, 8);
   @ObfuscatedName("cu")
   @ObfuscatedSignature(
      descriptor = "Lik;"
   )
   public static final ClientPacket OPHELD2 = new ClientPacket(94, 8);
   @ObfuscatedName("cc")
   @ObfuscatedSignature(
      descriptor = "Lik;"
   )
   public static final ClientPacket field2759 = new ClientPacket(95, -1);
   @ObfuscatedName("ch")
   @ObfuscatedSignature(
      descriptor = "Lik;"
   )
   public static final ClientPacket MOVE_GAMECLICK = new ClientPacket(96, -1);
   @ObfuscatedName("cz")
   @ObfuscatedSignature(
      descriptor = "Lik;"
   )
   public static final ClientPacket groundItem1 = new ClientPacket(97, 7);
   @ObfuscatedName("cv")
   @ObfuscatedSignature(
      descriptor = "Lik;"
   )
   static final ClientPacket field2762 = new ClientPacket(98, 2);
   @ObfuscatedName("cf")
   @ObfuscatedSignature(
      descriptor = "Lik;"
   )
   public static final ClientPacket field2769 = new ClientPacket(99, 3);
   @ObfuscatedName("cm")
   @ObfuscatedSignature(
      descriptor = "Lik;"
   )
   public static final ClientPacket RESUME_P_NAMEDIALOG = new ClientPacket(100, -1);
   @ObfuscatedName("co")
   @ObfuscatedSignature(
      descriptor = "Lik;"
   )
   public static final ClientPacket field2765 = new ClientPacket(101, -1);
   @ObfuscatedName("cy")
   @ObfuscatedSignature(
      descriptor = "Lik;"
   )
   public static final ClientPacket field2766 = new ClientPacket(102, -1);
   @ObfuscatedName("cb")
   @ObfuscatedSignature(
      descriptor = "Lik;"
   )
   public static final ClientPacket field2678 = new ClientPacket(103, 11);
   @ObfuscatedName("dq")
   @ObfuscatedSignature(
      descriptor = "Lik;"
   )
   public static final ClientPacket groundItem2 = new ClientPacket(104, 7);
   @ObfuscatedName("df")
   @ObfuscatedSignature(
      descriptor = "Lik;"
   )
   public static final ClientPacket field2760 = new ClientPacket(105, 11);
   @ObfuscatedName("da")
   @ObfuscatedGetter(
      intValue = 1306190951
   )
   @Export("id")
   final int id;
   @ObfuscatedName("dw")
   @ObfuscatedGetter(
      intValue = -2069383305
   )
   @Export("length")
   final int length;

   ClientPacket(int var1, int var2) {
      this.id = var1;
      this.length = var2;
   }

   @ObfuscatedName("i")
   @ObfuscatedSignature(
      descriptor = "(IB)Z",
      garbageValue = "34"
   )
   public static boolean method4710(int var0) {
      return (var0 & 1) != 0;
   }

   @ObfuscatedName("gh")
   @ObfuscatedSignature(
      descriptor = "(Lct;B)V",
      garbageValue = "-54"
   )
   static final void method4709(Actor var0) {
      var0.isWalking = false;
      SequenceDefinition var1;
      if (var0.movementSequence != -1) {
         var1 = MouseHandler.SequenceDefinition_get(var0.movementSequence);
         if (var1 != null && var1.frameIds != null) {
            ++var0.movementFrameCycle;
            if (var0.movementFrame < var1.frameIds.length && var0.movementFrameCycle > var1.frameLengths[var0.movementFrame]) {
               var0.movementFrameCycle = 1;
               ++var0.movementFrame;
               Interpreter.method1872(var1, var0.movementFrame, var0.x, var0.y);
            }

            if (var0.movementFrame >= var1.frameIds.length) {
               var0.movementFrameCycle = 0;
               var0.movementFrame = 0;
               Interpreter.method1872(var1, var0.movementFrame, var0.x, var0.y);
            }
         } else {
            var0.movementSequence = -1;
         }
      }

      if (var0.spotAnimation != -1 && Client.cycle >= var0.field1125) {
         if (var0.spotAnimationFrame < 0) {
            var0.spotAnimationFrame = 0;
         }

         int var3 = BZip2State.SpotAnimationDefinition_get(var0.spotAnimation).sequence;
         if (var3 != -1) {
            SequenceDefinition var2 = MouseHandler.SequenceDefinition_get(var3);
            if (var2 != null && var2.frameIds != null) {
               ++var0.spotAnimationFrameCycle;
               if (var0.spotAnimationFrame < var2.frameIds.length && var0.spotAnimationFrameCycle > var2.frameLengths[var0.spotAnimationFrame]) {
                  var0.spotAnimationFrameCycle = 1;
                  ++var0.spotAnimationFrame;
                  Interpreter.method1872(var2, var0.spotAnimationFrame, var0.x, var0.y);
               }

               if (var0.spotAnimationFrame >= var2.frameIds.length && (var0.spotAnimationFrame < 0 || var0.spotAnimationFrame >= var2.frameIds.length)) {
                  var0.spotAnimation = -1;
               }
            } else {
               var0.spotAnimation = -1;
            }
         } else {
            var0.spotAnimation = -1;
         }
      }

      if (var0.sequence != -1 && var0.sequenceDelay <= 1) {
         var1 = MouseHandler.SequenceDefinition_get(var0.sequence);
         if (var1.field1963 == 1 && var0.field1144 > 0 && var0.field1131 <= Client.cycle && var0.field1132 < Client.cycle) {
            var0.sequenceDelay = 1;
            return;
         }
      }

      if (var0.sequence != -1 && var0.sequenceDelay == 0) {
         var1 = MouseHandler.SequenceDefinition_get(var0.sequence);
         if (var1 != null && var1.frameIds != null) {
            ++var0.sequenceFrameCycle;
            if (var0.sequenceFrame < var1.frameIds.length && var0.sequenceFrameCycle > var1.frameLengths[var0.sequenceFrame]) {
               var0.sequenceFrameCycle = 1;
               ++var0.sequenceFrame;
               Interpreter.method1872(var1, var0.sequenceFrame, var0.x, var0.y);
            }

            if (var0.sequenceFrame >= var1.frameIds.length) {
               var0.sequenceFrame -= var1.frameCount;
               ++var0.field1116;
               if (var0.field1116 >= var1.field1962) {
                  var0.sequence = -1;
               } else if (var0.sequenceFrame >= 0 && var0.sequenceFrame < var1.frameIds.length) {
                  Interpreter.method1872(var1, var0.sequenceFrame, var0.x, var0.y);
               } else {
                  var0.sequence = -1;
               }
            }

            var0.isWalking = var1.field1958;
         } else {
            var0.sequence = -1;
         }
      }

      if (var0.sequenceDelay > 0) {
         --var0.sequenceDelay;
      }

   }
}
