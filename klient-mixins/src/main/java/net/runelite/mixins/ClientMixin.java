package net.runelite.mixins;

import meteor.events.GameStateChanged;
import net.runelite.api.GameState;
import net.runelite.api.hooks.Callbacks;
import net.runelite.api.hooks.DrawCallbacks;
import net.runelite.api.mixins.*;
import net.runelite.rs.api.*;

@Mixin(RSClient.class)
public abstract class ClientMixin implements RSClient {
  @Shadow("client")
  public static RSClient client;

  @Inject
  private static GameState oldGameState;

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

  @Inject
  @MethodHook("doCycle")
  protected final void doCycle$api() {
    callbacks.tick();
  }

  @Inject
  @Override
  public GameState getGameState() {
    return GameState.of(getRSGameState$api());
  }

  @Inject
  @FieldHook("gameState")
  public static void gameStateChanged(int idx)
  {
    GameState newGameState = client.getGameState();
    GameStateChanged gameStateChange = new GameStateChanged(oldGameState, newGameState);
    oldGameState = newGameState;
    client.getCallbacks().post(gameStateChange);
  }

  @Inject
  @MethodHook("updateNpcs")
  public static void updateNpcs(boolean var0, RSPacketBuffer var1) {
    client.getCallbacks().updateNpcs();
  }
}
