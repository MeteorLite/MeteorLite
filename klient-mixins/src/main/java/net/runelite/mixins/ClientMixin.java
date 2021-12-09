package net.runelite.mixins;

import net.runelite.api.hooks.Callbacks;
import net.runelite.api.hooks.DrawCallbacks;
import net.runelite.api.mixins.*;
import net.runelite.rs.api.*;

@Mixin(RSClient.class)
public abstract class ClientMixin implements RSClient {
  @Shadow("client")
  public static RSClient client;

  @Inject
  private Callbacks callbacks;

  @Inject
  @Override
  public Callbacks getCallbacks() {
    return callbacks;
  }

  @Inject
  @Override
  public void setCallbacks(Callbacks callbacks) {
    this.callbacks = callbacks;
  }

  @Inject
  private DrawCallbacks drawCallbacks;

  @Inject
  @Override
  public void setDrawCallbacks(DrawCallbacks drawCallbacks) {
    this.drawCallbacks = drawCallbacks;
  }

  @Inject
  @Override
  public DrawCallbacks getDrawCallbacks() {
    return drawCallbacks;
  }
}
