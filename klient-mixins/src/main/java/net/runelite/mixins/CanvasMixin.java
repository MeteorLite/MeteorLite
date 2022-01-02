package net.runelite.mixins;

import net.runelite.api.mixins.Inject;
import net.runelite.api.mixins.Mixin;
import net.runelite.api.mixins.Shadow;
import net.runelite.rs.api.RSCanvas;
import net.runelite.rs.api.RSClient;

@Mixin(RSCanvas.class)
public abstract class CanvasMixin extends java.awt.Canvas implements RSCanvas {

  @Shadow("client")
  private static RSClient client;

  @Inject
  @Override
  public void setSize(int width, int height) {
    if (client.isStretchedEnabled()) {
      super.setSize(client.getStretchedDimensions().width, client.getStretchedDimensions().height);
    } else {
      super.setSize(width, height);
    }
  }

  @Inject
  @Override
  public void setLocation(int x, int y) {
    if (client.isStretchedEnabled()) {
      super.setLocation((getParent().getWidth() - client.getStretchedDimensions().width) / 2, 0);
    } else {
      super.setLocation(x, y);
    }
  }
}
