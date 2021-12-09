package net.runelite.mixins;

import net.runelite.api.mixins.*;
import net.runelite.rs.api.RSClient;

@Mixin(RSClient.class)
public abstract class Rasterizer2DMixin implements RSClient {

  @Shadow("client")
  private static RSClient client;

  @Inject
  private static void drawAlpha(int[] pixels, int index, int value, int alpha) {
    if (!client.isGpu() || pixels != client.getBufferProvider().getPixels()) {
      pixels[index] = value;
      return;
    }

    // (int) x * 0x8081 >>> 23 is equivalent to (short) x / 255
    int outAlpha = alpha + ((pixels[index] >>> 24) * (255 - alpha) * 0x8081 >>> 23);
    pixels[index] = value & 0x00FFFFFF | outAlpha << 24;
  }

  @Inject
  @Override
  public boolean isGpu() {
    return false;
  }
}
