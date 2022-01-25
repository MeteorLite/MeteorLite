/*
 * Copyright (c) 2019, Lucas <https://github.com/Lucwousin>
 * All rights reserved.
 *
 * This code is licensed under GPL3, see the complete license in
 * the LICENSE file in the root directory of this submodule.
 *
 * Copyright (c) 2016-2017, Adam <Adam@sigterm.info>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.openosrs.injector.net;

import com.openosrs.injector.injection.InjectData;
import com.openosrs.injector.injectors.AbstractInjector;
import net.runelite.asm.ClassFile;
import net.runelite.asm.Method;
import net.runelite.asm.attributes.code.Instruction;
import net.runelite.asm.attributes.code.instructions.BiPush;
import net.runelite.asm.attributes.code.instructions.SiPush;
import net.runelite.rs.ScriptOpcodes;
import org.sponge.util.Logger;

/*
 * This finds packet opcodes
 */
public class DecodeNet extends AbstractInjector {
    private static final Logger log = new Logger("Packets");
    public int totalFound = 0;
    public final int expected = 52;

    public DecodeNet(InjectData inject) {
        super(inject);
    }

    public void inject() {
        for (final ClassFile deobClass : inject.getDeobfuscated()) {
            scanMethods(deobClass);
        }

        log.warn("Found " + totalFound + "/" + expected + " packets");
    }

    private void scanMethods(ClassFile deobClass) {
        for (Method deobMethod : deobClass.getMethods()) {
            if (deobMethod.getName().equals("handleMouseScriptOpcode")) {
                totalFound += extractMouseScriptOpcodePackets(deobMethod);
            }

            if (deobMethod.getName().equals("handleMenuScriptOpcode")) {
                totalFound += extractMenuScriptOpcodePackets(deobMethod);
            }
        }
    }

    private int extractMenuScriptOpcodePackets(Method deobMethod) {
        int lastCompare = 0;
        int found = 0;
        String lastPacketField = "";
        boolean foundObj6 = false;
        for (Instruction i : deobMethod.getCode().getInstructions().getInstructions()) {
            if (i.toString().contains("label getstatic static Losrs/ClientPacket;")) {
                lastPacketField = i.toString().split("osrs/ClientPacket; ")[1].split(" ")[0];
                switch (lastCompare) {
                    case 6 -> {
                        found++;
                        log.warn("Found OPLOC4 : " + lastPacketField);
                    }
                    case 7 -> {
                        found++;
                        log.warn("Found OPNPCU : " + lastPacketField);
                    }
                    case 8 -> {
                        found++;
                        log.warn("Found OPNPCT : " + lastPacketField);
                    }
                    case 9 -> {
                        found++;
                        log.warn("Found OPNPC1 : " + lastPacketField);
                    }
                    case 10 -> {
                        found++;
                        log.warn("Found OPNPC2 : " + lastPacketField);
                    }
                    case 11 -> {
                        found++;
                        log.warn("Found OPNPC3 : " + lastPacketField);
                    }
                    case 12 -> {
                        found++;
                        log.warn("Found OPNPC4 : " + lastPacketField);
                    }
                    case 13 -> {
                        found++;
                        log.warn("Found OPNPC5 : " + lastPacketField);
                    }
                    case 14 -> {
                        found++;
                        log.warn("Found OPPLAYERU : " + lastPacketField);
                    }
                    case 15 -> {
                        found++;
                        log.warn("Found OPPLAYERT : " + lastPacketField);
                    }
                    case 16 -> {
                        found++;
                        log.warn("Found OPOBJU : " + lastPacketField);
                    }
                    case 17 -> {
                        found++;
                        log.warn("Found OPOBJT : " + lastPacketField);
                    }
                    case 18 -> {
                        found++;
                        log.warn("Found OPOBJ1 : " + lastPacketField);
                    }
                    case 19 -> {
                        found++;
                        log.warn("Found OPOBJ2 : " + lastPacketField);
                    }
                    case 20 -> {
                        found++;
                        log.warn("Found OPOBJ3 : " + lastPacketField);
                    }
                    case 21 -> {
                        found++;
                        log.warn("Found OPOBJ4 : " + lastPacketField);
                    }
                    case 22 -> {
                        found++;
                        log.warn("Found OPOBJ5 : " + lastPacketField);
                    }
                    case 24 -> {
                        found++;
                        log.warn("Found BUTTON_CLICK : " + lastPacketField);
                    }
                    case 31 -> {
                        found++;
                        log.warn("Found OPHELDU : " + lastPacketField);
                    }
                    case 32 -> {
                        found++;
                        log.warn("Found OPHELDT : " + lastPacketField);
                    }
                    case 33 -> {
                        found++;
                        log.warn("Found OPHELD1 : " + lastPacketField);
                    }
                    case 34 -> {
                        found++;
                        log.warn("Found OPHELD2 : " + lastPacketField);
                    }
                    case 35 -> {
                        found++;
                        log.warn("Found OPHELD3 : " + lastPacketField);
                    }
                    case 36 -> {
                        found++;
                        log.warn("Found OPHELD4 : " + lastPacketField);
                    }
                    case 37 -> {
                        found++;
                        log.warn("Found OPHELD5 : " + lastPacketField);
                    }
                    case 39 -> {
                        found++;
                        log.warn("Found IF1_BUTTON1 : " + lastPacketField);
                        lastCompare = 1000;
                    }
                    case 40 -> {
                        found++;
                        log.warn("Found IF1_BUTTON2 : " + lastPacketField);
                    }
                    case 41 -> {
                        found++;
                        log.warn("Found IF1_BUTTON3 : " + lastPacketField);
                    }
                    case 42 -> {
                        found++;
                        log.warn("Found IF1_BUTTON4 : " + lastPacketField);
                    }
                    case 43 -> {
                        found++;
                        log.warn("Found IF1_BUTTON5 : " + lastPacketField);
                    }
                    case 44 -> {
                        found++;
                        log.warn("Found OPPLAYER1 : " + lastPacketField);
                    }
                    case 45 -> {
                        found++;
                        log.warn("Found OPPLAYER2 : " + lastPacketField);
                    }
                    case 46 -> {
                        found++;
                        log.warn("Found OPPLAYER3 : " + lastPacketField);
                    }
                    case 47 -> {
                        found++;
                        log.warn("Found OPPLAYER4 : " + lastPacketField);
                    }
                    case 48 -> {
                        found++;
                        log.warn("Found OPPLAYER5 : " + lastPacketField);
                    }
                    case 49 -> {
                        found++;
                        log.warn("Found OPPLAYER6 : " + lastPacketField);
                    }
                    case 50 -> {
                        found++;
                        log.warn("Found OPPLAYER7 : " + lastPacketField);
                    }
                    case 51 -> {
                        found++;
                        log.warn("Found OPPLAYER8 : " + lastPacketField);
                    }
                    case 52 -> {
                        found++;
                        log.warn("Found OPPLAYER8 : " + lastPacketField);
                    }
                    case 58 -> {
                        found++;
                        log.warn("Found IF_BUTTONT : " + lastPacketField);
                    }
                    case 1001 -> {
                        found++;
                        log.warn("Found OPLOC5 : " + lastPacketField);
                    }
                    case 1002 -> {
                        found++;
                        log.warn("Found OPLOC6 : " + lastPacketField);
                    }
                    case 1003 -> {
                        found++;
                        log.warn("Found OPNPC6 : " + lastPacketField);
                    }
                    case 1004 -> {
                        if (!foundObj6) {
                            found++;
                            log.warn("Found OPOBJ6 : " + lastPacketField);
                            foundObj6 = true;
                        }
                    }
                    default -> lastCompare = 222222;
                }
            } else if (i instanceof BiPush) {
                if (((BiPush) i).getOperand() == 82)
                    switch (found) {
                        case 0 -> {
                            found++;
                            log.warn("Found OPLOCU : " + lastPacketField);
                        }
                        case 1 -> {
                            found++;
                            log.warn("Found OPLOCT : " + lastPacketField);
                        }
                        case 2 -> {
                            found++;
                            log.warn("Found OPLOC1 : " + lastPacketField);
                        }
                        case 3 -> {
                            found++;
                            log.warn("Found OPLOC2 : " + lastPacketField);
                        }
                        case 4 -> {
                            found++;
                            log.warn("Found OPLOC3 : " + lastPacketField);
                        }
                    }
                else if (((BiPush) i).getOperand() == 6) {
                    lastCompare = 6;
                } else if (((BiPush) i).getOperand() == 7) {
                    lastCompare = 7;
                } else if (((BiPush) i).getOperand() == 8) {
                    lastCompare = 8;
                } else if (((BiPush) i).getOperand() == 9) {
                    lastCompare = 9;
                } else if (((BiPush) i).getOperand() == 10) {
                    lastCompare = 10;
                } else if (((BiPush) i).getOperand() == 11) {
                    lastCompare = 11;
                } else if (((BiPush) i).getOperand() == 12) {
                    lastCompare = 12;
                } else if (((BiPush) i).getOperand() == 13) {
                    lastCompare = 13;
                } else if (((BiPush) i).getOperand() == 14) {
                    lastCompare = 14;
                } else if (((BiPush) i).getOperand() == 15) {
                    lastCompare = 15;
                } else if (((BiPush) i).getOperand() == 16) {
                    lastCompare = 16;
                } else if (((BiPush) i).getOperand() == 17) {
                    lastCompare = 17;
                } else if (((BiPush) i).getOperand() == 18) {
                    lastCompare = 18;
                } else if (((BiPush) i).getOperand() == 19) {
                    lastCompare = 19;
                } else if (((BiPush) i).getOperand() == 20) {
                    lastCompare = 20;
                } else if (((BiPush) i).getOperand() == 21) {
                    lastCompare = 21;
                } else if (((BiPush) i).getOperand() == 22) {
                    lastCompare = 22;
                } else if (((BiPush) i).getOperand() == 23) {
                    lastCompare = 23;
                } else if (((BiPush) i).getOperand() == 24) {
                    lastCompare = 24;
                } else if (((BiPush) i).getOperand() == 25) {
                    lastCompare = 25;
                } else if (((BiPush) i).getOperand() == 31) {
                    lastCompare = 31;
                } else if (((BiPush) i).getOperand() == 32) {
                    lastCompare = 32;
                } else if (((BiPush) i).getOperand() == 33) {
                    lastCompare = 33;
                } else if (((BiPush) i).getOperand() == 34) {
                    lastCompare = 34;
                } else if (((BiPush) i).getOperand() == 35) {
                    lastCompare = 35;
                } else if (((BiPush) i).getOperand() == 36) {
                    lastCompare = 36;
                } else if (((BiPush) i).getOperand() == 37) {
                    lastCompare = 37;
                } else if (((BiPush) i).getOperand() == 39) {
                    lastCompare = 39;
                } else if (((BiPush) i).getOperand() == 40) {
                    lastCompare = 40;
                } else if (((BiPush) i).getOperand() == 41) {
                    lastCompare = 41;
                } else if (((BiPush) i).getOperand() == 42) {
                    lastCompare = 42;
                } else if (((BiPush) i).getOperand() == 43) {
                    lastCompare = 43;
                } else if (((BiPush) i).getOperand() == 44) {
                    lastCompare = 44;
                } else if (((BiPush) i).getOperand() == 45) {
                    lastCompare = 45;
                } else if (((BiPush) i).getOperand() == 46) {
                    lastCompare = 46;
                } else if (((BiPush) i).getOperand() == 47) {
                    lastCompare = 47;
                } else if (((BiPush) i).getOperand() == 48) {
                    lastCompare = 48;
                } else if (((BiPush) i).getOperand() == 49) {
                    lastCompare = 49;
                } else if (((BiPush) i).getOperand() == 50) {
                    lastCompare = 50;
                } else if (((BiPush) i).getOperand() == 51) {
                    lastCompare = 51;
                } else if (((BiPush) i).getOperand() == 52) {
                    lastCompare = 52;
                } else if (((BiPush) i).getOperand() == 58) {
                    lastCompare = 58;
                }
            } else if (i instanceof SiPush) {
                if (((SiPush) i).getOperand() == 1001) {
                    lastCompare = 1001;
                } else if (((SiPush) i).getOperand() == 1002) {
                    lastCompare = 1002;
                } else if (((SiPush) i).getOperand() == 1003) {
                    lastCompare = 1003;
                } else if (((SiPush) i).getOperand() == 1004) {
                    lastCompare = 1004;
                }
            }
        }
        return found;
    }

    private int extractMouseScriptOpcodePackets(Method deobMethod) {
        int lastCompare = 0;
        int expected = 4;
        int found = 0;
        for (Instruction i : deobMethod.getCode().getInstructions().getInstructions())
            if (i instanceof SiPush) {
                int i1 = ((SiPush) i).getOperand();
                if (i1 != 500)
                    lastCompare = i1;
            } else if (i.toString().contains("label getstatic static Losrs/ClientPacket;")) {
                if (lastCompare == ScriptOpcodes.RESUME_NAMEDIALOG) {
                    log.warn("Found RESUME_P_NAMEDIALOG : " + i.toString().split("osrs/ClientPacket; ")[1].split(" ")[0]);
                    found++;
                } else if (lastCompare == ScriptOpcodes.RESUME_STRINGDIALOG) {
                    log.warn("Found RESUME_P_STRINGDIALOG : " + i.toString().split("osrs/ClientPacket; ")[1].split(" ")[0]);
                    found++;
                } else if (lastCompare == ScriptOpcodes.RESUME_OBJDIALOG) {
                    log.warn("Found RESUME_P_OBJDIALOG : " + i.toString().split("osrs/ClientPacket; ")[1].split(" ")[0]);
                    found++;
                } else if (lastCompare == ScriptOpcodes.BUG_REPORT) {
                    log.warn("Found BUG_REPORT : " + i.toString().split("osrs/ClientPacket; ")[1].split(" ")[0]);
                    found++;
                }
            }
        return found;
    }

}
