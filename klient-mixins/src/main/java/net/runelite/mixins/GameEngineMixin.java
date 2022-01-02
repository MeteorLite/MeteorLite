package net.runelite.mixins;

import net.runelite.api.hooks.DrawCallbacks;
import net.runelite.api.mixins.*;
import net.runelite.rs.api.RSClient;
import net.runelite.rs.api.RSGameEngine;

@Mixin(RSGameEngine.class)
public abstract class GameEngineMixin implements RSGameEngine {

  @Shadow("client")
  private static RSClient client;

  @Shadow("viewportColor")
  private static int viewportColor;
  @Inject
  private Thread thread;

  @Inject
  @MethodHook("post")
  public void onPost(Object canvas) {
    DrawCallbacks drawCallbacks = client.getDrawCallbacks();
    if (drawCallbacks != null) {
      drawCallbacks.draw(viewportColor);
      viewportColor = 0;
    }
  }

  @Inject
  @Override
  public Thread getClientThread() {
    return thread;
  }

  @Inject
  @Override
  public boolean isClientThread() {
    return thread == Thread.currentThread();
  }

  @Inject
  @MethodHook("run")
  public void onRun() {
    thread = Thread.currentThread();
    thread.setName("Client");
  }

  @FieldHook("isCanvasInvalid")
  @Inject
  public void onReplaceCanvasNextFrameChanged(int idx) {
    // when this is initially called the client instance doesn't exist yet
    if (client != null && client.isGpu() && isReplaceCanvasNextFrame()) {
      setReplaceCanvasNextFrame(false);
      setResizeCanvasNextFrame(true);
    }
  }

  @Replace("checkHost")
  protected final boolean checkHost() {
    //Always allow host.
    return true;
  }

  @Copy("replaceCanvas")
  @Replace("replaceCanvas")
  @SuppressWarnings("InfiniteRecursion")
  public void copy$replaceCanvas() {
    if (client != null && client.isGpu()) {
      setFullRedraw(false);
      return;
    }

    copy$replaceCanvas();
  }
}
