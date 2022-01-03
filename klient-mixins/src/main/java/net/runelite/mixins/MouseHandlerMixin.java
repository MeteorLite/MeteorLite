package net.runelite.mixins;

import net.runelite.api.mixins.*;
import net.runelite.rs.api.RSClient;
import net.runelite.rs.api.RSMouseHandler;

import java.awt.event.MouseEvent;

@Mixin(RSMouseHandler.class)
public abstract class MouseHandlerMixin implements RSMouseHandler {

  @Shadow("client")
  private static RSClient client;

  @Inject
  private int isInEvent;

  @Override
  @Copy("mousePressed")
  @Replace("mousePressed")
  public synchronized void mousePressed(MouseEvent mouseEvent) {
    if (isInEvent == 0) {
      mouseEvent = client.getCallbacks().mousePressed(mouseEvent);
    }
    if (!mouseEvent.isConsumed()) {
      isInEvent++;
      try {
        mousePressed(mouseEvent);
      } finally {
        isInEvent--;
      }
    }
  }

  @Override
  @Copy("mouseReleased")
  @Replace("mouseReleased")
  public synchronized void mouseReleased(MouseEvent mouseEvent) {
    if (isInEvent == 0) {
      mouseEvent = client.getCallbacks().mouseReleased(mouseEvent);
    }
    if (!mouseEvent.isConsumed()) {
      isInEvent++;
      try {
        mouseReleased(mouseEvent);
      } finally {
        isInEvent--;
      }
    }
  }

  @Override
  @Copy("mouseClicked")
  @Replace("mouseClicked")
  public void mouseClicked(MouseEvent event) {
    event = client.getCallbacks().mouseClicked(event);
    if (!event.isConsumed()) {
      mouseClicked(event);
    }
  }

  @Override
  @Copy("mouseEntered")
  @Replace("mouseEntered")
  public synchronized void mouseEntered(MouseEvent mouseEvent) {
    if (isInEvent == 0) {
      mouseEvent = client.getCallbacks().mouseEntered(mouseEvent);
    }
    if (!mouseEvent.isConsumed()) {
      isInEvent++;
      try {
        mouseEntered(mouseEvent);
      } finally {
        isInEvent--;
      }
    }
  }


  @Override
  @Copy("mouseExited")
  @Replace("mouseExited")
  public synchronized void mouseExited(MouseEvent mouseEvent) {
    if (isInEvent == 0) {
      mouseEvent = client.getCallbacks().mouseExited(mouseEvent);
    }
    if (!mouseEvent.isConsumed()) {
      isInEvent++;
      try {
        mouseExited(mouseEvent);
      } finally {
        isInEvent--;
      }
    }
  }

  @Override
  @Copy("mouseDragged")
  @Replace("mouseDragged")
  public synchronized void mouseDragged(MouseEvent mouseEvent) {
    if (isInEvent == 0) {
      mouseEvent = client.getCallbacks().mouseDragged(mouseEvent);
    }
    if (!mouseEvent.isConsumed()) {
      isInEvent++;
      try {
        mouseDragged(mouseEvent);
      } finally {
        isInEvent--;
      }
    }
  }

  @Override
  @Copy("mouseMoved")
  @Replace("mouseMoved")
  public synchronized void mouseMoved(MouseEvent mouseEvent) {
    if (isInEvent == 0) {
      mouseEvent = client.getCallbacks().mouseMoved(mouseEvent);
    }
    if (!mouseEvent.isConsumed()) {
      isInEvent++;
      try {
        mouseMoved(mouseEvent);
      } finally {
        isInEvent--;
      }
    }
  }

  @Override
  @Inject
  public void sendClick(int x, int y, int button) {
    int btn = button;
    /*
     * Set the button to 1337 so the client doesn't process it as click in the next cycle.
     * Adding an extra condition in osrs/Client.java L: 3828 where checking for lastButton == 1337 makes it so that
     * both the mouserecorder and the click packet are properly sent. If rev updates remove this added condition,
     * make sure to re-add it.
     */
    //TODO This breaks input atm, investigate...
/*    if (button == 1) {
      btn = 1337;
    }*/
    client.setFocused(true);
    setIdleCycles(0);
    setLastPressedX(x);
    setLastPressedY(y);
    setLastPressedMillis(client.getCurrentTime());
    setLastButton(btn);
    if (getLastButton() != 0) {
      setCurrentButton(button);
    }
  }

  @Override
  @Inject
  public void sendMovement(int x, int y) {
    client.setFocused(true);
    setIdleCycles(0);
    setCurrentX(x);
    setCurrentY(y);
    setCurrentMillis(System.currentTimeMillis());
  }
}
