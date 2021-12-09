package net.runelite.rs.api;

import net.runelite.api.packets.ClientPacket;
import net.runelite.mapping.Import;

public interface RSClientPacket extends ClientPacket {
    @Import("id")
    @Override
    int getId();

    @Import("length")
    @Override
    int getLength();

    @Import("OPHELDT")
    @Override
    RSClientPacket OPHELDT();

    @Import("spellOnNpc")
    @Override
    RSClientPacket spellOnNpc();

    @Import("EVENT_MOUSE_CLICK")
    @Override
    RSClientPacket EVENT_MOUSE_CLICK();

    @Import("OPNPCU")
    @Override
    RSClientPacket OPNPCU();

    @Import("OPLOCU")
    @Override
    RSClientPacket OPLOCU();

    @Import("OPHELD1")
    @Override
    RSClientPacket OPHELD1();

    @Import("OPHELD4")
    @Override
    RSClientPacket OPHELD4();

    @Import("OPHELD5")
    @Override
    RSClientPacket OPHELD5();

    @Import("OPHELDU")
    @Override
    RSClientPacket OPHELDU();

    @Import("RESUME_PAUSEBUTTON")
    @Override
    RSClientPacket RESUME_PAUSEBUTTON();

    @Import("IF_BUTTON1")
    @Override
    RSClientPacket IF_BUTTON1();

    @Import("IF_BUTTON2")
    @Override
    RSClientPacket IF_BUTTON2();

    @Import("IF_BUTTON3")
    @Override
    RSClientPacket IF_BUTTON3();

    @Import("IF_BUTTON4")
    @Override
    RSClientPacket IF_BUTTON4();

    @Import("IF_BUTTON5")
    @Override
    RSClientPacket IF_BUTTON5();

    @Import("IF_BUTTON6")
    @Override
    RSClientPacket IF_BUTTON6();

    @Import("IF_BUTTON7")
    @Override
    RSClientPacket IF_BUTTON7();

    @Import("IF_BUTTON8")
    @Override
    RSClientPacket IF_BUTTON8();

    @Import("IF_BUTTON9")
    @Override
    RSClientPacket IF_BUTTON9();

    @Import("IF_BUTTON10")
    @Override
    RSClientPacket IF_BUTTON10();

    @Import("OPLOC1")
    @Override
    RSClientPacket OPLOC1();

    @Import("OPLOC2")
    @Override
    RSClientPacket OPLOC2();

    @Import("OPLOC3")
    @Override
    RSClientPacket OPLOC3();

    @Import("OPLOC4")
    @Override
    RSClientPacket OPLOC4();

    @Import("OPLOC5")
    @Override
    RSClientPacket OPLOC5();

    @Import("grounditem1")
    @Override
    RSClientPacket grounditem1();

    @Import("grounditem2")
    @Override
    RSClientPacket grounditem2();

    @Import("grounditem3")
    @Override
    RSClientPacket grounditem3();

    @Import("grounditem4")
    @Override
    RSClientPacket grounditem4();

    @Import("grounditem5")
    @Override
    RSClientPacket grounditem5();

    @Import("MOVE_GAMECLICK")
    @Override
    RSClientPacket MOVE_GAMECLICK();

    @Import("RESUME_P_COUNTDIALOG")
    @Override
    RSClientPacket RESUME_P_COUNTDIALOG();

    @Import("RESUME_P_STRINGDIALOG")
    @Override
    RSClientPacket RESUME_P_STRINGDIALOG();

    @Import("RESUME_P_NAMEDIALOG")
    @Override
    RSClientPacket RESUME_P_NAMEDIALOG();

    @Import("OPNPC1")
    @Override
    RSClientPacket OPNPC1();

    @Import("OPNPC2")
    @Override
    RSClientPacket OPNPC2();

    @Import("OPNPC3")
    @Override
    RSClientPacket OPNPC3();

    @Import("OPNPC4")
    @Override
    RSClientPacket OPNPC4();

    @Import("OPNPC5")
    @Override
    RSClientPacket OPNPC5();
}
