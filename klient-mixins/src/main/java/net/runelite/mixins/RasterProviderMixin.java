package net.runelite.mixins;

import net.runelite.api.mixins.*;
import net.runelite.rs.api.RSClient;
import net.runelite.rs.api.RSRasterProvider;

import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.image.*;
import java.nio.IntBuffer;
import java.util.Hashtable;

@Mixin(RSRasterProvider.class)
public abstract class RasterProviderMixin implements RSRasterProvider {

  @Shadow("client")
  private static RSClient client;

  @Inject
  private IntBuffer buffer;

  @MethodHook(value = "<init>", end = true)
  @Inject
  public void init(int width, int height, Component canvas) {
    if (!client.isGpu()) {
      return;
    }

    final int[] pixels = getPixels();

    // we need to make our own buffered image for the client with the alpha channel enabled in order to
    // have alphas for the overlays applied correctly
    DataBufferInt dataBufferInt = new DataBufferInt(pixels, pixels.length);
    DirectColorModel directColorModel = new DirectColorModel(
        ColorSpace.getInstance(ColorSpace.CS_sRGB),
        32, 0xff0000, 0xff00, 0xff, 0xff000000,
        true, DataBuffer.TYPE_INT);
    WritableRaster writableRaster = Raster
        .createWritableRaster(directColorModel.createCompatibleSampleModel(width, height),
            dataBufferInt, null);
    BufferedImage bufferedImage = new BufferedImage(directColorModel, writableRaster, true,
        new Hashtable());

    setImage(bufferedImage);
  }

  @Inject
  @Copy("drawFull0")
  final void draw$copy(Graphics graphics, int x, int y) {
  }

  /**
   * Replacing this method makes it so we can completely control when/what is drawn on the game's
   * canvas, as the method that is replaced draws the game's image on the canvas.
   */
  @Replace("drawFull0")
  final void draw(Graphics graphics, int x, int y) {
    //draw$copy(graphics, x, y);
    client.getCallbacks().draw(this, graphics, x, y);
  }
}
