package net.runelite.mixins;

import com.google.common.primitives.Doubles;
import meteor.Logger;
import meteor.eventbus.events.GameStateChanged;
import meteor.eventbus.events.NpcSpawned;
import net.runelite.api.*;
import net.runelite.api.hooks.Callbacks;
import net.runelite.api.hooks.DrawCallbacks;
import net.runelite.api.mixins.*;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.api.widgets.WidgetType;
import net.runelite.rs.api.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

@Mixin(RSClient.class)
public abstract class ClientMixin implements RSClient {
  @Shadow("client")
  public static RSClient client;

  @Inject
  public static Logger logger = Logger.Companion.getLogger(Class.class);
  @Inject
  @Override
  public Logger getLogger() {
    return logger;
  }

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

  @Inject
  private static ArrayList<WidgetItem> widgetItems = new ArrayList<>();

  @Inject
  private static ArrayList<Widget> hiddenWidgets = new ArrayList<>();

  @Inject
  @MethodHook(value = "drawInterface", end = true)
  public static void postRenderWidgetLayer(Widget[] widgets, int parentId, int minX, int minY,
                                           int maxX, int maxY, int x, int y, int var8) {
    Callbacks callbacks = client.getCallbacks();
    int oldSize = widgetItems.size();

    for (Widget rlWidget : widgets) {
      RSWidget widget = (RSWidget) rlWidget;
      if (widget == null || widget.getRSParentId() != parentId || widget.isSelfHidden()) {
        continue;
      }

      int type = widget.getType();
      if (type == WidgetType.GRAPHIC && widget.getItemId() != -1) {
        final int renderX = x + widget.getRelativeX();
        final int renderY = y + widget.getRelativeY();
        if (renderX >= minX && renderX <= maxX && renderY >= minY && renderY <= maxY) {
          WidgetItem widgetItem = new WidgetItem(client, widget.getItemId(), widget.getItemQuantity(), -1,
                  widget.getBounds(), widget, null);
          widgetItems.add(widgetItem);
        }
      } else if (type == WidgetType.INVENTORY) {
        widgetItems.addAll(widget.getWidgetItems());
      }
    }

    List<WidgetItem> subList = Collections.emptyList();
    if (oldSize < widgetItems.size()) {
      if (oldSize > 0) {
        subList = widgetItems.subList(oldSize, widgetItems.size());
      } else {
        subList = widgetItems;
      }
    }

    if (parentId == 0xabcdabcd) {
      widgetItems.clear();
    } else if (parentId != -1) {
      Widget widget = client.getWidget(parentId);
      Widget[] children = widget.getChildren();
      if (children == null || children == widgets) {
        callbacks.drawLayer(widget, subList);
      }
    } else {
      int group = -1;
      for (Widget widget : widgets) {
        if (widget != null) {
          group = WidgetInfo.TO_GROUP(widget.getId());
          break;
        }
      }

      if (group == -1) {
        return;
      }

      callbacks.drawInterface(group, widgetItems);
      widgetItems.clear();
      for (int i = hiddenWidgets.size() - 1; i >= 0; i--) {
        Widget widget = hiddenWidgets.get(i);
        if (WidgetInfo.TO_GROUP(widget.getId()) == group) {
          widget.setHidden(false);
          hiddenWidgets.remove(i);
        }
      }
    }
  }

  @Inject
  @Override
  public Widget getWidget(int id) {
    return getWidget(WidgetInfo.TO_GROUP(id), WidgetInfo.TO_CHILD(id));
  }

  @Inject
  @Override
  public Widget getWidget(int groupId, int childId) {
    RSWidget[][] widgets = getWidgets();

    if (widgets == null || widgets.length <= groupId) {
      return null;
    }

    RSWidget[] childWidgets = widgets[groupId];
    if (childWidgets == null || childWidgets.length <= childId) {
      return null;
    }

    return childWidgets[childId];
  }

  @Inject
  private static boolean interpolateWidgetAnimations;

  @Inject
  @Override
  public Point getMouseCanvasPosition() {
    return new Point(getMouseX(), getMouseY());
  }

  @Inject
  @Override
  public boolean isInterpolateWidgetAnimations() {
    return interpolateWidgetAnimations;
  }

  @Inject
  @Override
  public void setInterpolateWidgetAnimations(boolean interpolate)
  {
    interpolateWidgetAnimations = interpolate;
  }

  @Override
  public boolean isGpu() {return false;}

  @Inject
  @Override
  public List<NPC> getNpcs()
  {
    int validNpcIndexes = getNpcIndexesCount();
    int[] npcIndexes = getNpcIndices();
    NPC[] cachedNpcs = getCachedNPCs();
    List<NPC> npcs = new ArrayList<>(validNpcIndexes);

    for (int i = 0; i < validNpcIndexes; ++i)
    {
      npcs.add(cachedNpcs[npcIndexes[i]]);
    }

    return npcs;
  }

  @Inject
  @Override
  public void uncacheNPC(int id) {
    npcDefCache.remove(id);
  }

  @Inject
  @Override
  public void clearNPCCache() {
    npcDefCache.clear();
  }

  @Inject
  @MethodHook("draw")
  public void draw$api(boolean var1) {
    callbacks.frame();
    updateCamera();
  }

  @Inject
  public long delayNanoTime;

  @Inject
  public void setUnlockedFpsTarget(int var1)
  {
    if (var1 <= 0)
    {
      delayNanoTime = 0L;
    }
    else
    {
      delayNanoTime = 1000000000L / (long) var1;
    }
  }

  @Inject
  public long lastNanoTime = 0;

  @Inject
  public void updateCamera()
  {
    long nanoTime = System.nanoTime();
    long diff = nanoTime - this.lastNanoTime;
    this.lastNanoTime = nanoTime;

    if (this.getGameState() == GameState.LOGGED_IN)
    {
      setUnlockedFpsTarget(175);
      this.interpolateCamera(diff);
    }
  }

  @Inject
  @Override
  public long getUnlockedFpsTarget()
  {
    return delayNanoTime;
  }

  @Inject
  public static int toCameraPos(double var0)
  {
    return (int) (var0 / Perspective.UNIT) & 2047;
  }

  @Inject
  public static double tmpCamAngleY;
  @Inject
  public static double tmpCamAngleX;

  public static final int STANDARD_PITCH_MIN = 128;
  public static final int STANDARD_PITCH_MAX = 383;
  public static final int NEW_PITCH_MAX = 512;

  @Inject
  public void interpolateCamera(long var1)
  {
    double angleDX = diffToDangle(client.getCamAngleDY(), var1);
    double angleDY = diffToDangle(client.getCamAngleDX(), var1);

    tmpCamAngleY += angleDX / 2;
    tmpCamAngleX += angleDY / 2;
    tmpCamAngleX = Doubles.constrainToRange(tmpCamAngleX, Perspective.UNIT * STANDARD_PITCH_MIN, Perspective.UNIT * STANDARD_PITCH_MAX);

    int yaw = toCameraPos(tmpCamAngleY);
    int pitch = toCameraPos(tmpCamAngleX);

    client.setCameraYawTarget(yaw);
    client.setCameraPitchTarget(pitch);
  }

  @Inject
  public static double diffToDangle(int var0, long var1)
  {
    double var2 = var0 * Perspective.UNIT;
    double var3 = (double) var1 / 2.0E7D;

    return var2 * var3;
  }

  @FieldHook("npcs")
  @Inject
  public static void cachedNPCsChanged(int idx) {
    RSNPC[] cachedNPCs = client.getCachedNPCs();
    if (idx < 0 || idx >= cachedNPCs.length) {
      return;
    }

    RSNPC npc = cachedNPCs[idx];
    if (npc != null) {
      npc.setIndex(idx);
      client.getCallbacks().post(new NpcSpawned(npc));
    }
  }

  @Inject
  @Override
  public Widget getWidget(WidgetInfo widget) {
    int groupId = widget.getGroupId();
    int childId = widget.getChildId();

    return getWidget(groupId, childId);
  }

  @Inject
  @Override
  public int getVar(VarPlayer varPlayer) {
    int[] varps = getVarps();
    return varps[varPlayer.getId()];
  }

  @Inject
  static int skyboxColor = 0;

  @Inject
  public RSSpritePixels createItemSprite(int itemId, int quantity, int border, int shadowColor, int stackable, boolean noted)
  {
    assert isClientThread() : "createItemSprite must be called on client thread";
    return createRSItemSprite(itemId, quantity, border, shadowColor, stackable, noted);
  }

  @Inject
  @Override
  public SpritePixels createItemSprite(int itemId, int quantity, int border, int shadowColor, int stackable, boolean noted, int scale)
  {
    assert isClientThread() : "createItemSprite must be called on client thread";

    int zoom = get3dZoom();
    set3dZoom(scale);
    try
    {
      return createItemSprite(itemId, quantity, border, shadowColor, stackable, noted);
    }
    finally
    {
      set3dZoom(zoom);
    }
  }

  @Inject
  private static RSTileItem lastItemDespawn;

  @Inject
  @Override
  public RSTileItem getLastItemDespawn() {
    return lastItemDespawn;
  }

  @Inject
  @Override
  public void setLastItemDespawn(RSTileItem lastItemDespawn) {
    ClientMixin.lastItemDespawn = lastItemDespawn;
  }

  @Inject
  public static HashMap<Integer, RSNPCComposition> npcDefCache = new HashMap<>();
  @Inject
  public static HashMap<Integer, RSObjectComposition> objDefCache = new HashMap<>();
  @Inject
  public static HashMap<Integer, RSItemComposition> itemDefCache = new HashMap<>();


  @Inject
  @Override
  public ObjectComposition getObjectComposition(int objectId) {
    if (objDefCache.containsKey(objectId)) {
      return objDefCache.get(objectId);
    }

    assert this.isClientThread() : "getObjectDefinition must be called on client thread";
    RSObjectComposition objectComposition = getRSObjectComposition(objectId);
    objDefCache.put(objectId, objectComposition);
    return objectComposition;
  }

  @Inject
  @Override
  public ItemComposition getItemComposition(int id) {
    if (itemDefCache.containsKey(id)) {
      return itemDefCache.get(id);
    }

    assert this.isClientThread() : "getItemComposition must be called on client thread";
    RSItemComposition def = getRSItemDefinition(id);
    itemDefCache.put(id, def);
    return def;
  }

  @Inject
  @Override
  public NPCComposition getNpcComposition(int id) {
    assert this.isClientThread() : "getNpcDefinition must be called on client thread";
    return getRSNpcComposition(id);
  }

  @Inject
  @Override
  public int getBoostedSkillLevel(Skill skill) {
    int[] boostedLevels = getBoostedSkillLevels();
    return boostedLevels[skill.ordinal()];
  }

  @Inject
  @Override
  public int getRealSkillLevel(Skill skill) {
    int[] realLevels = getRealSkillLevels();
    return realLevels[skill.ordinal()];
  }
}
