/*
 * Copyright (c) 2019, Lucas <https://github.com/Lucwousin>
 * All rights reserved.
 *
 * This code is licensed under GPL3, see the complete license in
 * the LICENSE file in the root directory of this submodule.
 */
package com.openosrs.injector.injectors;

import static com.openosrs.injector.rsapi.RSApi.API_BASE;
import static meteor.Logger.*;

import com.openosrs.injector.injection.InjectData;
import net.runelite.asm.ClassFile;
import net.runelite.asm.Interfaces;
import net.runelite.asm.pool.Class;
import net.runelite.deob.DeobAnnotations;
import meteor.Logger;
import meteor.Message;

public class InterfaceInjector extends AbstractInjector {

  private int implemented = 0;

  public InterfaceInjector(InjectData inject) {
    super(inject);
  }
  private Logger log = Logger.Companion.getLogger(InterfaceInjector.class);

  public void inject() {
    // forEachPair performs actions on a deob-vanilla pair, which is what's needed here
    inject.forEachPair(this::injectInterface);

    //log.info("[INFO] Injected {} interfaces", implemented);
  }

  private void injectInterface(final ClassFile deobCf, final ClassFile vanillaCf) {
    String impls = DeobAnnotations.getImplements(deobCf);

    if (impls == null) {
      return;
    }

    impls = impls.replace("osrs/", "");

    final String fullName = API_BASE + impls;
    if (fullName.contains("class"))
      return;
    String message = Message.Companion.newMessage()
            .add(ANSI_PURPLE, "[WARNING]")
            .addDefault(" Class ")
            .add(ANSI_CYAN, deobCf.getName())
            .addDefault(" implements nonexistent interface: ")
            .add(ANSI_BLUE, fullName + ", ")
            .addDefault("skipping interface injection...")
            .build();
    if (!inject.getRsApi().hasClass(fullName)) {
      	log.debug(message);
      return;
    }

    final Interfaces interfaces = vanillaCf.getInterfaces();
    interfaces.addInterface(new Class(fullName));
    implemented++;

    inject.addToDeob(fullName, deobCf);
  }
}
