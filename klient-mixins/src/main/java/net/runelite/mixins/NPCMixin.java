package net.runelite.mixins;

import net.runelite.api.MenuAction;
import net.runelite.api.NPCComposition;
import net.runelite.api.Perspective;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.events.NpcChanged;
import net.runelite.api.events.NpcDespawned;
import net.runelite.api.mixins.FieldHook;
import net.runelite.api.mixins.Inject;
import net.runelite.api.mixins.Mixin;
import net.runelite.api.mixins.Shadow;
import net.runelite.api.util.Text;
import net.runelite.rs.api.RSClient;
import net.runelite.rs.api.RSModel;
import net.runelite.rs.api.RSNPC;
import net.runelite.rs.api.RSNPCComposition;

import java.awt.*;
import java.util.HashMap;

@Mixin(RSNPC.class)
public abstract class NPCMixin implements RSNPC {

  @Shadow("client")
  private static RSClient client;

  @Inject
  private int npcIndex;

  @Shadow("npcDefCache")
  private static HashMap<Integer, RSNPCComposition> npcDefCache;



  @Inject
  @Override
  public int getIndex() {
    return npcIndex;
  }

  @Inject
  @Override
  public void setIndex(int id) {
    npcIndex = id;
  }

  @FieldHook(value = "definition", before = true)
  @Inject
  public void onDefinitionChanged(RSNPCComposition composition) {
    if (composition == null) {
      client.getCallbacks().post(new NpcDespawned(this));
      npcDefCache.remove(getIndex());
    } else if (this.getId() != -1) {
      RSNPCComposition oldComposition = getComposition();
      if (oldComposition == null) {
        return;
      }

      if (composition.getId() == oldComposition.getId()) {
        return;
      }

      client.getCallbacks().postDeferred(new NpcChanged(this, oldComposition));
    }
  }

  @Inject
  @Override
  public Shape getConvexHull() {
    RSModel model = getModel$api();
    if (model == null) {
      return null;
    }

    int size = getComposition().getSize();
    LocalPoint tileHeightPoint = new LocalPoint(
            size * Perspective.LOCAL_HALF_TILE_SIZE - Perspective.LOCAL_HALF_TILE_SIZE + getX(),
            size * Perspective.LOCAL_HALF_TILE_SIZE - Perspective.LOCAL_HALF_TILE_SIZE + getY());

    int tileHeight = Perspective.getTileHeight(client, tileHeightPoint, client.getPlane());

    return model.getConvexHull(getX(), getY(), getOrientation(), tileHeight);
  }

  @Inject
  @Override
  public NPCComposition getTransformedComposition()
  {
    RSNPCComposition composition = getComposition();
    if (composition != null && composition.getConfigs() != null)
    {
      composition = composition.transform$api();
    }
    return composition;
  }

  @Inject
  @Override
  public int getDistanceFromLocalPlayer() {
    //Mancrappen :tm:
    int distanceX;
    int distanceY;
    LocalPoint localPlayerPosition = client.getLocalPlayer().getLocalLocation();

    if (getX() > localPlayerPosition.getX())
      distanceX = getX() - localPlayerPosition.getX();
    else
      distanceX = localPlayerPosition.getX() - getX();

    if (getY() > localPlayerPosition.getY())
      distanceY = getY() - localPlayerPosition.getY();
    else
      distanceY = localPlayerPosition.getY() - getY();

    return (distanceX + distanceY) / 2;
  }

  @Inject
  @Override
  public int getId()
  {
    RSNPCComposition composition = transformIfRequired();
    return composition == null ? -1 : composition.getId();
  }

  @Inject
  @Override
  public String getName()
  {
    RSNPCComposition composition = transformIfRequired();
    return composition == null ? null : Text.removeTags(Text.sanitize(composition.getName()));
  }

  @Inject
  @Override
  public int getCombatLevel()
  {
    RSNPCComposition composition = transformIfRequired();
    return composition == null ? -1 : composition.getCombatLevel();
  }

  @Inject
  @Override
  public String[] getRawActions()
  {
    RSNPCComposition composition = transformIfRequired();
    return composition == null ? null : composition.getActions();
  }

  @Inject
  private RSNPCComposition transformIfRequired() {
    RSNPCComposition composition = getComposition();
    if (isTransformRequired()) {
      if (!npcDefCache.containsKey(getIndex())) {
        assert client.isClientThread() : "NPCComposition.getTransformed must be called on client thread";
        composition = composition.transform$api();
        npcDefCache.put(getIndex(), composition);
      } else {
        composition = npcDefCache.get(getIndex());
      }
    }

    return composition;
  }

  @Inject
  @Override
  public boolean isDefinitionCached() {
    return npcDefCache.containsKey(getIndex());
  }

  @Override
  @Inject
  public void interact(String action) {
    interact(getActions().indexOf(action));
  }

  @Override
  @Inject
  public int getActionId(int action) {
    switch (action) {
      case 0:
        return MenuAction.NPC_FIRST_OPTION.getId();
      case 1:
        return MenuAction.NPC_SECOND_OPTION.getId();
      case 2:
        return MenuAction.NPC_THIRD_OPTION.getId();
      case 3:
        return MenuAction.NPC_FOURTH_OPTION.getId();
      case 4:
        return MenuAction.NPC_FIFTH_OPTION.getId();
      default:
        throw new IllegalArgumentException("action = " + action);
    }
  }

  @Override
  @Inject
  public void interact(int action) {
    interact(getIndex(), getActionId(action));
  }

  @Override
  @Inject
  public void interact(int identifier, int opcode, int param0, int param1) {
    Point screenCoords = getScreenCoords();
    int x = screenCoords != null ? screenCoords.getX() : -1;
    int y = screenCoords != null ? screenCoords.getY() : -1;

    client.interact(identifier, opcode, param0, param1, x, y);
  }

  @Inject
  @Override
  public void interact(int index, int menuAction) {
    interact(getIndex(), menuAction, 0, 0);
  }

  @Inject
  @Override
  public boolean isTransformRequired() {
    return getComposition() != null && getComposition().getConfigs() != null;
  }

  @Inject
  @Override
  public String toString() {
    return getIndex() + ": " + getName() + " (" + getId() + ") at " + getWorldLocation();
  }

  @Inject
  private Point getScreenCoords() {
    return Perspective.localToCanvas(client, getLocalLocation(), client.getPlane());
  }
}
