package net.runelite.mixins;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.runelite.api.*;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.events.OverheadPrayerChanged;
import net.runelite.api.events.PlayerSkullChanged;
import net.runelite.api.mixins.*;
import net.runelite.rs.api.RSClient;
import net.runelite.rs.api.RSModel;
import net.runelite.rs.api.RSPlayer;
import net.runelite.rs.api.RSUsername;

import static net.runelite.api.SkullIcon.*;

@Mixin(RSPlayer.class)
public abstract class PlayerMixin implements RSPlayer {

  @Shadow("client")
  private static RSClient client;

  @Inject
  private boolean friended;

  @Inject
  private int oldHeadIcon = -2;

  @Inject
  private int oldSkullIcon = -2;


  @Inject
  @Override
  public String getName()
  {
    final RSUsername rsName = getRsName();

    if (rsName == null)
    {
      return null;
    }

    String name = rsName.getName$api();

    if (name == null)
    {
      return null;
    }

    return name.replace('\u00A0', ' ');
  }

  @Inject
  @Override
  public Shape getConvexHull() {
    RSModel model = getModel$api();
    if (model == null) {
      return null;
    }

    int tileHeight = Perspective
        .getTileHeight(client, new LocalPoint(getX(), getY()), client.getPlane());

    return model.getConvexHull(getX(), getY(), getOrientation(), tileHeight);
  }

  @Inject
  public boolean isFriended()
  {
    return isFriend$api() || friended;
  }

  @Inject
  @Override
  public boolean isIdle() {
    return (getIdlePoseAnimation() == getPoseAnimation() && getAnimation() == -1)
        && (getInteracting() == null || getInteracting().isDead());
  }

  @Inject
  @Override
  public int getIndex() {
    for (int i = 0; i < client.getCachedPlayers().length; i++) {
      RSPlayer player = client.getCachedPlayers()[i];
      if (player != null && player.equals(this)) {
        return i;
      }
    }

    return -1;
  }

  @Inject
  @MethodHook(value = "checkIsFriend", end = true)
  void updateFriended()
  {
    friended = client.getFriendManager().isFriended(getRsName(), false);
  }

  @Inject
  @FieldHook("headIconPrayer")
  public void prayerChanged(int idx)
  {
    if (getRsOverheadIcon() != oldHeadIcon)
    {
      final HeadIcon headIcon = getHeadIcon(getRsOverheadIcon());
      client.getCallbacks().post(
              new OverheadPrayerChanged(this, getHeadIcon(oldHeadIcon), headIcon));
    }
    oldHeadIcon = getRsOverheadIcon();
  }

  @Inject
  @Override
  public HeadIcon getOverheadIcon()
  {
    return getHeadIcon(getRsOverheadIcon());
  }

  @Inject
  @FieldHook("headIconPk")
  public void skullChanged(int idx)
  {
    final SkullIcon skullIcon = skullFromInt(getRsSkullIcon());
    if (getRsSkullIcon() != oldSkullIcon)
    {
      client.getCallbacks().post(
              new PlayerSkullChanged(this, skullFromInt(getRsSkullIcon()), skullIcon));
    }
    oldSkullIcon = getRsSkullIcon();
  }

  @Inject
  @Override
  public SkullIcon getSkullIcon()
  {
    return skullFromInt(getRsSkullIcon());
  }

  @Inject
  private HeadIcon getHeadIcon(int overheadIcon)
  {
    if (overheadIcon == -1)
    {
      return null;
    }

    return HeadIcon.values()[overheadIcon];
  }

  @Inject
  private SkullIcon skullFromInt(int skull)
  {
    switch (skull)
    {
      case 0:
        return SKULL;
      case 1:
        return SKULL_FIGHT_PIT;
      case 8:
        return DEAD_MAN_FIVE;
      case 9:
        return DEAD_MAN_FOUR;
      case 10:
        return DEAD_MAN_THREE;
      case 11:
        return DEAD_MAN_TWO;
      case 12:
        return DEAD_MAN_ONE;
      default:
        return null;
    }
  }

  @Inject
  @Override
  public Polygon[] getPolygons()
  {
    Model model = getModel$api();

    if (model == null)
    {
      return null;
    }

    int[] x2d = new int[model.getVerticesCount()];
    int[] y2d = new int[model.getVerticesCount()];

    int localX = getX();
    int localY = getY();

    final int tileHeight = Perspective.getTileHeight(client, new LocalPoint(localX, localY), client.getPlane());

    Perspective.modelToCanvas(client, model.getVerticesCount(), localX, localY, tileHeight, getOrientation(), model.getVerticesX(), model.getVerticesZ(), model.getVerticesY(), x2d, y2d);
    ArrayList polys = new ArrayList(model.getTrianglesCount());

    int[] trianglesX = model.getTrianglesX();
    int[] trianglesY = model.getTrianglesY();
    int[] trianglesZ = model.getTrianglesZ();

    for (int triangle = 0; triangle < model.getTrianglesCount(); ++triangle)
    {
      int[] xx =
              {
                      x2d[trianglesX[triangle]], x2d[trianglesY[triangle]], x2d[trianglesZ[triangle]]
              };

      int[] yy =
              {
                      y2d[trianglesX[triangle]], y2d[trianglesY[triangle]], y2d[trianglesZ[triangle]]
              };

      polys.add(new Polygon(xx, yy, 3));
    }

    return (Polygon[]) polys.toArray(new Polygon[0]);
  }

  @Override
  public int getActionId(int action) {
    switch (action) {
      case 0:
        return MenuAction.PLAYER_FIRST_OPTION.getId();
      case 1:
        return MenuAction.PLAYER_SECOND_OPTION.getId();
      case 2:
        return MenuAction.PLAYER_THIRD_OPTION.getId();
      case 3:
        return MenuAction.PLAYER_FOURTH_OPTION.getId();
      case 4:
        return MenuAction.PLAYER_FIFTH_OPTION.getId();
      case 5:
        return MenuAction.PLAYER_SIXTH_OPTION.getId();
      case 6:
        return MenuAction.PLAYER_SEVENTH_OPTION.getId();
      case 7:
        return MenuAction.PLAYER_EIGTH_OPTION.getId();
      default:
        throw new IllegalArgumentException("action = " + action);
    }
  }

  @Override
  public List<String> actions() {
    return Arrays.asList(getActions());
  }

  @Override
  @Inject
  public void interact(int action) {
    interact(getIndex(), getActionId(action));
  }

  @Inject
  @Override
  public void interact(String action) {
    interact(actions().indexOf(action));
  }

  @Inject
  @Override
  public void interact(int index, int menuAction) {
    interact(getIndex(), menuAction, 0, 0);
  }

  @Inject
  @Override
  public void interact(int identifier, int opcode, int param0, int param1) {
    client.interact(identifier, opcode, param0, param1);
  }
}
