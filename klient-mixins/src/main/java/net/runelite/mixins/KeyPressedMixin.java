/*
 * Copyright (c) 2020, Owain van Brakel <https://github.com/Owain94>
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
package net.runelite.mixins;

import net.runelite.api.events.FocusChanged;
import net.runelite.api.mixins.*;
import net.runelite.rs.api.RSClient;
import net.runelite.rs.api.RSKeyHandler;

import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;

@Mixin(RSKeyHandler.class)
public abstract class KeyPressedMixin implements RSKeyHandler {
  @Shadow("client")
  private static RSClient client;

  @Override
  @Copy("keyPressed")
  @Replace("keyPressed")
  public final synchronized void keyPressed(KeyEvent keyEvent)
  {
    client.getCallbacks().keyPressed(keyEvent);
    if (!keyEvent.isConsumed())
    {
      keyPressed(keyEvent);
    }
  }

  @Override
  @Copy("keyReleased")
  @Replace("keyReleased")
  public final synchronized void keyReleased(KeyEvent keyEvent)
  {
    client.getCallbacks().keyReleased(keyEvent);
    if (!keyEvent.isConsumed())
    {
      keyReleased(keyEvent);
    }
  }

  @Override
  @Copy("keyTyped")
  @Replace("keyTyped")
  public final void keyTyped(KeyEvent keyEvent)
  {
    client.getCallbacks().keyTyped(keyEvent);
    if (!keyEvent.isConsumed())
    {
      keyTyped(keyEvent);
    }
  }

  @Inject
  @MethodHook("focusLost")
  public void onFocusLost(FocusEvent focusEvent)
  {
    final meteor.eventbus.events.FocusChanged focusChanged = new meteor.eventbus.events.FocusChanged(false);
    client.getCallbacks().post(FocusChanged.class, focusChanged);
  }
}
