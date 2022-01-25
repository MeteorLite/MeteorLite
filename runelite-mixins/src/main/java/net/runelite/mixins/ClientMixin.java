package net.runelite.mixins;

import com.google.common.primitives.Doubles;
import meteor.events.PlaneChanged;
import net.runelite.api.*;
import net.runelite.api.clan.ClanChannel;
import net.runelite.api.clan.ClanRank;
import net.runelite.api.clan.ClanSettings;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.*;
import net.runelite.api.hooks.Callbacks;
import net.runelite.api.hooks.DrawCallbacks;
import net.runelite.api.mixins.*;
import net.runelite.api.vars.AccountType;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.api.widgets.WidgetType;
import net.runelite.rs.api.*;
import org.sponge.util.Logger;

import javax.annotation.Nullable;
import java.math.BigInteger;
import java.util.*;

import static net.runelite.api.MenuAction.*;
import static net.runelite.api.Perspective.LOCAL_TILE_SIZE;
import static net.runelite.mixins.meteor.CameraMixin.*;

@Mixin(RSClient.class)
public abstract class ClientMixin implements RSClient {

  @Shadow("client")
  public static RSClient client;
  @Inject
  public static int viewportColor;
  @Inject
  public static Logger logger = new Logger("injected-clients");
  @Inject
  static int skyboxColor;
  @Inject
  private static RSClient clientInstance;
  @Inject
  private static int tickCount;
  @Inject
  private static RSTileItem lastItemDespawn;
  @Inject
  private static RSPlayer[] oldPlayers = new RSPlayer[2048];
  @Inject
  private static ArrayList<WidgetItem> widgetItems = new ArrayList<WidgetItem>();
  @Inject
  private static ArrayList<Widget> hiddenWidgets = new ArrayList<Widget>();
  @Inject
  private static boolean interpolateWidgetAnimations;
  @Inject
  private static int oldMenuEntryCount;
  @Inject
  private static Set<String> unhiddenCasts = new HashSet<String>();
  @Inject
  private ArrayList<String> outdatedScripts;
  @Inject
  boolean occluderEnabled = false;
  @Inject
  private boolean isMirrored = false;
  @Inject
  @javax.inject.Inject
  private Callbacks callbacks;
  @Inject
  private DrawCallbacks drawCallbacks;
  @Inject
  private boolean gpu;
  @Inject
  private static boolean interpolatePlayerAnimations;
  @Inject
  private static boolean interpolateNpcAnimations;
  @Inject
  private static boolean interpolateObjectAnimations;
  @Inject
  public static HashMap<Integer, RSNPCComposition> npcDefCache = new HashMap<>();
  @Inject
  public static HashMap<Integer, RSObjectComposition> objDefCache = new HashMap<>();
  @Inject
  public static HashMap<Integer, RSItemComposition> itemDefCache = new HashMap<>();
  @Inject
  private static boolean lowCpu;
  @Inject
  private static boolean allWidgetsAreOpTargetable = false;
  @Inject
  private static boolean hdMinimapEnabled;
  @Inject
  public static boolean unlockedFps;
  @Inject
  public static double tmpCamAngleY;
  @Inject
  public static double tmpCamAngleX;
  @Inject
  public long lastNanoTime;
  @Inject
  public long delayNanoTime;
  
  @Inject
  @FieldHook("gameState")
  public static void gameStateChanged(int idx)
  {
    clientInstance = client;
    GameState gameState = client.getGameState();
    client.getLogger().debug("Game state changed: {}", gameState);
    GameStateChanged gameStateChange = new GameStateChanged();
    gameStateChange.setGameState(gameState);
    client.getCallbacks().post(gameStateChange);

    if (gameState == GameState.LOGGED_IN)
    {
      if (client.getLocalPlayer() == null)
      {
        return;
      }

      int plane = client.getPlane();
      RSScene scene = client.getScene();
      RSTile[][][] tiles = scene.getTiles();
      RSNodeDeque[][][] allItemDeque = client.getGroundItemDeque();
      RSNodeDeque[][] planeItems = allItemDeque[plane];

      for (int x = 0; x < 104; x++)
      {
        for (int y = 0; y < 104; y++)
        {
          RSNodeDeque itemDeque = planeItems[x][y];
          if (itemDeque != null)
          {
            RSTile tile = tiles[plane][x][y];
            RSNode head = itemDeque.getSentinel();

            for (RSNode current = head.getNext(); current != head; current = current.getNext())
            {
              RSTileItem item = (RSTileItem) current;
              item.setX(x);
              item.setY(y);
              ItemSpawned event = new ItemSpawned(tile, item);
              client.getCallbacks().post(event);
            }
          }
        }
      }
    }
  }

  @Inject
  @Override
  public void setGameState(GameState gameState)
  {
    assert this.isClientThread() : "setGameState must be called on client thread";
    setGameState(gameState.getState());
  }

  @Inject
  @Override
  public void setGameState(int state)
  {
    assert this.isClientThread() : "setGameState must be called on client thread";
    client.setRSGameState(state);
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
      client.getCallbacks().postDeferred(new NpcSpawned(npc));
    }
  }

  @Inject
  @MethodHook("updateNpcs")
  public static void updateNpcs(boolean var0, RSPacketBuffer var1) {
    client.getCallbacks().updateNpcs();
  }

  @FieldHook("players")
  @Inject
  public static void cachedPlayersChanged(int idx) {
    RSPlayer[] cachedPlayers = client.getCachedPlayers();
    if (idx < 0 || idx >= cachedPlayers.length) {
      return;
    }

    RSPlayer player = cachedPlayers[idx];
    RSPlayer oldPlayer = oldPlayers[idx];
    oldPlayers[idx] = player;

    if (oldPlayer != null) {
      client.getCallbacks().post(new PlayerDespawned(oldPlayer));
    }
    if (player != null) {
      client.getCallbacks().postDeferred(new PlayerSpawned(player));
    }
  }

  @Inject
  @Override
  @Nullable
  public LocalPoint getLocalDestinationLocation()
  {
    int sceneX = getDestinationX();
    int sceneY = getDestinationY();
    if (sceneX != 0 && sceneY != 0)
    {
      return LocalPoint.fromScene(sceneX, sceneY);
    }
    return null;
  }

  @Inject
  @Override
  public void changeMemoryMode(boolean lowMemory)
  {
    setLowMemory(lowMemory);
    setSceneLowMemory(lowMemory);
    setAudioHighMemory(true);
    setObjectDefinitionLowDetail(lowMemory);
    if (getGameState() == GameState.LOGGED_IN)
    {
      setGameState(GameState.LOADING);
    }
  }

  @MethodHook("drawInterface")
  @Inject
  public static void preRenderWidgetLayer(Widget[] widgets, int parentId, int minX, int minY,
                                          int maxX, int maxY, int x, int y, int var8) {
    @SuppressWarnings("unchecked") HashTable<WidgetNode> componentTable = client
            .getComponentTable();

    for (int i = 0; i < widgets.length; i++) {
      RSWidget widget = (RSWidget) widgets[i];
      if (widget == null || widget.getRSParentId() != parentId || widget.isSelfHidden()) {
        continue;
      }

      if (parentId != -1) {
        widget.setRenderParentId(parentId);
      }

      final int renderX = x + widget.getRelativeX();
      final int renderY = y + widget.getRelativeY();
      widget.setRenderX(renderX);
      widget.setRenderY(renderY);

      if (widget.getType() == WidgetType.RECTANGLE && renderX == client.getViewportXOffset()
              && renderY == client.getViewportYOffset()
              && widget.getWidth() == client.getViewportWidth() && widget.getHeight() == client
              .getViewportHeight()
              && widget.getOpacity() > 0 && widget.isFilled() && widget.getFillMode().getOrdinal() == 0
              && client.isGpu()) {
        int tc = widget.getTextColor();
        int alpha = widget.getOpacity() & 0xFF;
        int inverseAlpha = 256 - alpha;
        int vpc = viewportColor;
        int c1 =
                (inverseAlpha * (tc & 0xFF00FF) >> 8 & 0xFF00FF) + (inverseAlpha * (tc & 0x00FF00) >> 8
                        & 0x00FF00);
        int c2 =
                (alpha * (vpc & 0xFF00FF) >> 8 & 0xFF00FF) + (alpha * (vpc & 0x00FF00) >> 8 & 0x00FF00);
        int outAlpha = inverseAlpha + ((vpc >>> 24) * (255 - inverseAlpha) * 0x8081 >>> 23);
        viewportColor = outAlpha << 24 | c1 + c2;
        widget.setHidden(true);
        hiddenWidgets.add(widget);
      } else {
        WidgetNode childNode = componentTable.get$api(widget.getId());
        if (childNode != null) {
          int widgetId = widget.getId();
          int groupId = childNode.getId();
          RSWidget[] children = client.getWidgets()[groupId];

          for (RSWidget child : children) {
            if (child.getRSParentId() == -1) {
              child.setRenderParentId(widgetId);
            }
          }
        }
      }
    }
  }

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
  public static HashMap<Skill, Integer> oldXpMap = new HashMap<Skill, Integer>();

  @FieldHook("experience")
  @Inject
  public static void experiencedChanged(int idx) {
    Skill[] possibleSkills = Skill.values();

    // We subtract one here because 'Overall' isn't considered a skill that's updated.
    if (idx < possibleSkills.length - 1) {
      Skill updatedSkill = possibleSkills[idx];
      int newXp = client.getSkillExperience(updatedSkill);
      int oldXp;
      try
      {
        oldXp = oldXpMap.get(updatedSkill);
      }
      catch (Exception e)
      {
        oldXp = 0;
      }
      int level = client.getRealSkillLevel(updatedSkill);
      StatChanged statChanged = new StatChanged(
              updatedSkill,
              newXp,
              level,
              client.getBoostedSkillLevel(updatedSkill),
              newXp - oldXp
      );
      if (oldXp == 0 && newXp > 0) {
        oldXp = newXp;
        oldXpMap.put(updatedSkill, newXp);
      }

      client.getCallbacks().post(statChanged);
      experienceGained(oldXp, newXp, level, updatedSkill);
    }
  }

  @Inject
  public static void experienceGained(int oldExp, int currentExp, int skillLevel, Skill updatedSkill) {
    if (currentExp > oldExp) {
      ExperienceGained experienceGained = new ExperienceGained(
              updatedSkill,
              oldExp,
              currentExp,
              skillLevel
      );

      client.getCallbacks().post(experienceGained);
      oldXpMap.put(updatedSkill, currentExp);
    }
  }

  @FieldHook("changedSkills")
  @Inject
  public static void boostedSkillLevelsChanged(int idx) {
    if (idx == -1) {
      return;
    }

    int changedSkillIdx = idx - 1 & 31;
    int skillIdx = client.getChangedSkillLevels()[changedSkillIdx];
    Skill[] skills = Skill.values();
    if (skillIdx >= 0 && skillIdx < skills.length - 1) {
      StatChanged statChanged = new StatChanged(
              skills[skillIdx],
              client.getSkillExperiences()[skillIdx],
              client.getRealSkillLevels()[skillIdx],
              client.getBoostedSkillLevels()[skillIdx],
              0
      );
      client.getCallbacks().post(statChanged);
    }
  }

  @FieldHook("menuOptionsCount")
  @Inject
  public static void onMenuOptionsChanged(int idx) {
    int oldCount = oldMenuEntryCount;
    int newCount = client.getMenuOptionCount();

    oldMenuEntryCount = newCount;

    final String[] options = client.getMenuOptions();
    final String[] targets = client.getMenuTargets();
    final int[] identifiers = client.getMenuIdentifiers();
    final int[] opcodes = client.getMenuOpcodes();
    final int[] arguments1 = client.getMenuArguments1();
    final int[] arguments2 = client.getMenuArguments2();
    final boolean[] forceLeftClick = client.getMenuForceLeftClick();

    if (newCount == oldCount + 1) {
      MenuEntryAdded event = new MenuEntryAdded(
              options[oldCount],
              targets[oldCount],
              identifiers[oldCount],
              opcodes[oldCount],
              arguments1[oldCount],
              arguments2[oldCount],
              forceLeftClick[oldCount]
      );

      client.getCallbacks().post(event);

      if (event.isModified() && client.getMenuOptionCount() == newCount) {
        options[oldCount] = event.getOption();
        targets[oldCount] = event.getTarget();
        identifiers[oldCount] = event.getIdentifier();
        opcodes[oldCount] = event.getOpcode();
        arguments1[oldCount] = event.getActionParam();
        arguments2[oldCount] = event.getActionParam1();
        forceLeftClick[oldCount] = event.isForceLeftClick();
      }
    }
  }

  @Copy("menuAction")
  @Replace("menuAction")
  static void copy$menuAction(int param0, int param1, int opcode, int id, String option,
                              String target, int canvasX, int canvasY) {
    /*
     * The RuneScape client may deprioritize an action in the menu by incrementing the opcode with 2000,
     * undo it here so we can get the correct opcode
     */
    boolean decremented = false;
    if (opcode >= 2000) {
      decremented = true;
      opcode -= 2000;
    }

    final MenuOptionClicked menuOptionClicked = new MenuOptionClicked();
    menuOptionClicked.setActionParam(param0);
    menuOptionClicked.setMenuOption(option);
    menuOptionClicked.setMenuTarget(target);
    menuOptionClicked.setMenuAction(MenuAction.of(opcode));
    menuOptionClicked.setId(id);
    menuOptionClicked.setWidgetId(param1);
    menuOptionClicked.setSelectedItemIndex(client.getSelectedItemSlot());
    menuOptionClicked.setCanvasX(canvasX);
    menuOptionClicked.setCanvasY(canvasY);

    // Do not forward automated interaction events to eventbus
    if (!menuOptionClicked.isAutomated()) {
      client.getCallbacks().post(menuOptionClicked);
    }

    if (menuOptionClicked.isConsumed()) {
      return;
    }

    client.getLogger().debug(
            "|MenuAction|: MenuOption={} MenuTarget={} Id={} Opcode={}/{} Param0={} Param1={} CanvasX={} CanvasY={}",
            menuOptionClicked.getMenuOption(), menuOptionClicked.getMenuTarget(), menuOptionClicked.getId(),
            menuOptionClicked.getMenuAction(), opcode + (decremented ? 2000 : 0),
            menuOptionClicked.getActionParam(), menuOptionClicked.getWidgetId(), canvasX, canvasY
    );
    copy$menuAction(menuOptionClicked.getActionParam(), menuOptionClicked.getWidgetId(),
            menuOptionClicked.getMenuAction() == UNKNOWN ? opcode
                    : menuOptionClicked.getMenuAction().getId(),
            menuOptionClicked.getId(), menuOptionClicked.getMenuOption(),
            menuOptionClicked.getMenuTarget(),
            canvasX, canvasY);
  }

  @Override
  @Inject
  public void invokeMenuAction(String option, String target, int identifier, int opcode, int param0, int param1, int screenX, int screenY)
  {
    assert isClientThread() : "invokeMenuAction must be called on client thread";

    client.sendMenuAction(param0, param1, opcode, identifier, option, target, screenX, screenY);
  }

  @Inject
  @FieldHook("cycleCntr")
  public static void onCycleCntrChanged(int idx) {
    client.getCallbacks().post(ClientTick.INSTANCE);
  }

  @Inject
  @Override
  public Callbacks getCallbacks() {
    return callbacks;
  }

  @Inject
  @Override
  public DrawCallbacks getDrawCallbacks() {
    return drawCallbacks;
  }

  @Inject
  @Override
  public void setDrawCallbacks(DrawCallbacks drawCallbacks) {
    this.drawCallbacks = drawCallbacks;
  }

  @Inject
  @Override
  public GameState getGameState() {
    return GameState.of(getRSGameState$api());
  }

  @Inject
  @Override
  public Logger getLogger() {
    return logger;
  }

  @Inject
  @Override
  public boolean isInterpolatePlayerAnimations()
  {
    return interpolatePlayerAnimations;
  }

  @Inject
  @Override
  public void setInterpolatePlayerAnimations(boolean interpolate)
  {
    interpolatePlayerAnimations = interpolate;
  }

  @Inject
  @Override
  public boolean isInterpolateNpcAnimations()
  {
    return interpolateNpcAnimations;
  }

  @Inject
  @Override
  public void setInterpolateNpcAnimations(boolean interpolate)
  {
    interpolateNpcAnimations = interpolate;
  }

  @Inject
  @Override
  public boolean isInterpolateObjectAnimations()
  {
    return interpolateObjectAnimations;
  }

  @Inject
  @Override
  public void setInterpolateObjectAnimations(boolean interpolate)
  {
    interpolateObjectAnimations = interpolate;
  }


  @Inject
  @Override
  public int getSkyboxColor() {
    return skyboxColor;
  }

  @Inject
  @Override
  public boolean isGpu() {
    return gpu;
  }

  @Inject
  @Override
  public void setGpu(boolean gpu) {
    this.gpu = gpu;
  }

  @Inject
  @MethodHook("draw")
  public void draw$api(boolean var1) {
    callbacks.frame();
    updateCamera();
  }

  @Inject
  @MethodHook("doCycle")
  protected final void doCycle$api() {
    callbacks.tick();
  }

  @Override
  @Inject
  public int getTickCount() {
    return tickCount;
  }

  @Override
  @Inject
  public void setTickCount(int tick) {
    tickCount = tick;
  }

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
  public RSTileItem getLastItemDespawn() {
    return lastItemDespawn;
  }

  @Inject
  @Override
  public void setLastItemDespawn(RSTileItem lastItemDespawn) {
    ClientMixin.lastItemDespawn = lastItemDespawn;
  }

  @Inject
  @Override
  public List<Projectile> getProjectiles() {
    List<Projectile> projectiles = new ArrayList<Projectile>();
    RSNodeDeque projectileDeque = this.getProjectilesDeque();
    net.runelite.api.Node head = projectileDeque.getSentinel();

    for (net.runelite.api.Node node = head.getNext(); node != head; node = node.getNext()) {
      projectiles.add((Projectile) node);
    }

    return projectiles;
  }

  @Inject
  @Override
  public List<GraphicsObject> getGraphicsObjects() {
    List<GraphicsObject> graphicsObjects = new ArrayList<GraphicsObject>();
    RSNodeDeque graphicsObjectDeque = this.getGraphicsObjectDeque();
    net.runelite.api.Node head = graphicsObjectDeque.getSentinel();

    for (Node node = head.getNext(); node != head; node = node.getNext()) {
      graphicsObjects.add((GraphicsObject) node);
    }

    return graphicsObjects;
  }

  @Inject
  @Override
  public boolean isHdMinimapEnabled() {
    return hdMinimapEnabled;
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

  @Inject
  @Override
  public void queueChangedSkill(Skill skill)
  {
    int[] skills = client.getChangedSkills();
    int count = client.getChangedSkillsCount();
    skills[++count - 1 & 31] = skill.ordinal();
    client.setChangedSkillsCount(count);
  }

  @Inject
  @Override
  public void setAllWidgetsAreOpTargetable(boolean yes)
  {
    allWidgetsAreOpTargetable = yes;
  }

  @Inject
  @Override
  public void setUnhiddenCasts(Set<String> casts)
  {
    unhiddenCasts = casts;
  }

  @Inject
  @Override
  public boolean isMirrored()
  {
    return isMirrored;
  }

  @Inject
  @Override
  public void setMirrored(boolean isMirrored)
  {
    this.isMirrored = isMirrored;
  }

  @Inject
  @Override
  public void setOutdatedScript(String outdatedScript) {
    if (outdatedScripts == null)
      outdatedScripts = new ArrayList<>();
    if (!outdatedScripts.contains(outdatedScript)) {
      outdatedScripts.add(outdatedScript);
    }
  }

  @Inject
  @Override
  public List<String> getOutdatedScripts() {
    return this.outdatedScripts;
  }

  @Inject
  @Override
  public int getVar(VarPlayer varPlayer) {
    int[] varps = getVarps();
    return varps[varPlayer.getId()];
  }

  @Inject
  @Override
  public ScriptEvent createScriptEvent(Object... args) {
    return createRSScriptEvent(args);
  }

  @Inject
  @Override
  public RSScriptEvent createRSScriptEvent(Object... args) {
    RSScriptEvent event = createScriptEvent();
    event.setArguments(args);
    return event;
  }

  @Inject
  @Override
  public boolean getOccluderEnabled() {
    return occluderEnabled;
  }

  @Inject
  @Override
  public void setOccluderEnabled(boolean enabled) {
    occluderEnabled = enabled;
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

  /**
   * Returns the local player's current experience in the specified {@link Skill}.
   *
   * @param skill the {@link Skill} to retrieve the experience for
   * @return the local player's current experience in the specified {@link Skill}, or -1 if the
   * {@link Skill} isn't valid
   */
  @Inject
  @Override
  public int getSkillExperience(Skill skill) {
    int[] experiences = getSkillExperiences();

    if (skill == Skill.OVERALL) {
      return (int) getOverallExperience();
    }

    int idx = skill.ordinal();

    // I'm not certain exactly how needed this is, but if the Skill enum is updated in the future
    // to hold something else that's not reported it'll save us from an ArrayIndexOutOfBoundsException.
    if (idx >= experiences.length) {
      return -1;
    }

    return experiences[idx];
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
  public Point getMouseCanvasPosition() {
    return new Point(getMouseX(), getMouseY());
  }

  @Inject
  @Override
  public MenuEntry[] getMenuEntries() {
    int count = getMenuOptionCount();
    String[] menuOptions = getMenuOptions();
    String[] menuTargets = getMenuTargets();
    int[] menuIdentifiers = getMenuIdentifiers();
    int[] menuTypes = getMenuOpcodes();
    int[] params0 = getMenuArguments1();
    int[] params1 = getMenuArguments2();
    boolean[] leftClick = getMenuForceLeftClick();

    MenuEntry[] entries = new MenuEntry[count];
    for (int i = 0; i < count; ++i) {
      MenuEntry entry = entries[i] = new MenuEntry();
      entry.setOption(menuOptions[i]);
      entry.setTarget(menuTargets[i]);
      entry.setIdentifier(menuIdentifiers[i]);
      entry.setOpcode(menuTypes[i]);
      entry.setActionParam(params0[i]);
      entry.setActionParam1(params1[i]);
      entry.setForceLeftClick(leftClick[i]);
    }
    return entries;
  }

  @Inject
  @Override
  public void setMenuEntries(MenuEntry[] entries) {
    int count = 0;
    String[] menuOptions = getMenuOptions();
    String[] menuTargets = getMenuTargets();
    int[] menuIdentifiers = getMenuIdentifiers();
    int[] menuTypes = getMenuOpcodes();
    int[] params0 = getMenuArguments1();
    int[] params1 = getMenuArguments2();
    boolean[] leftClick = getMenuForceLeftClick();

    for (MenuEntry entry : entries) {
      if (entry == null) {
        continue;
      }

      menuOptions[count] = entry.getOption();
      menuTargets[count] = entry.getTarget();
      menuIdentifiers[count] = entry.getIdentifier();
      menuTypes[count] = entry.getOpcode();
      params0[count] = entry.getActionParam();
      params1[count] = entry.getActionParam1();
      leftClick[count] = entry.isForceLeftClick();
      ++count;
    }

    setMenuOptionCount(count);
    oldMenuEntryCount = count;
  }

  @Inject
  @Override
  public EnumSet<WorldType> getWorldType() {
    int flags = getFlags();
    return WorldType.fromMask(flags);
  }

  @Inject
  @Override
  public Tile getSelectedSceneTile() {
    int tileX = getSelectedSceneTileX();
    int tileY = getSelectedSceneTileY();

    if (tileX == -1 || tileY == -1) {
      return null;
    }

    return getScene().getTiles()[getPlane()][tileX][tileY];
  }

  @Inject
  @Override
  public RSItemContainer getItemContainer(InventoryID inventory) {
    RSNodeHashTable itemContainers = getItemContainers();
    return (RSItemContainer) itemContainers.get$api(inventory.getId());
  }

  @Inject
  @Override
  public RSItemContainer getItemContainer(int id)
  {
    RSNodeHashTable itemContainers = getItemContainers();

    for (Object itemContainer : itemContainers)
    {
      RSItemContainer container = ((RSItemContainer) itemContainer);

      if (((RSItemContainer) itemContainer).getId() == id)
      {
        return container;
      }
    }

    return null;
  }

  @Inject
  @Override
  public void clearHintArrow()
  {
    client.setHintArrowTargetType(HintArrowType.NONE.getValue());
  }

  @Inject
  @Override
  public NPC getHintArrowNpc()
  {
    if (getHintArrowType() == HintArrowType.NPC)
    {
      int idx = client.getHintArrowNpcTargetIdx();
      RSNPC[] npcs = client.getCachedNPCs();

      if (idx < 0 || idx >= npcs.length)
      {
        return null;
      }

      return npcs[idx];
    }

    return null;
  }

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

  @Copy("runWidgetOnLoadListener")
  @Replace("runWidgetOnLoadListener")
  @SuppressWarnings("InfiniteRecursion")
  public static void copy$runWidgetOnLoadListener(int groupId)
  {
    copy$runWidgetOnLoadListener(groupId);

    RSWidget[][] widgets = client.getWidgets();
    boolean loaded = widgets != null && widgets[groupId] != null;

    if (loaded)
    {
      WidgetLoaded event = new WidgetLoaded();
      event.setGroupId(groupId);
      client.getCallbacks().post(event);
    }
  }

  @Inject
  @Override
  public HintArrowType getHintArrowType()
  {
    int type = client.getHintArrowTargetType();
    if (type == HintArrowType.NPC.getValue())
    {
      return HintArrowType.NPC;
    }
    else if (type == HintArrowType.PLAYER.getValue())
    {
      return HintArrowType.PLAYER;
    }
    else if (type == HintArrowType.WORLD_POSITION.getValue())
    {
      return HintArrowType.WORLD_POSITION;
    }
    else
    {
      return HintArrowType.NONE;
    }
  }

  @Inject
  @Override
  public void setHintArrow(NPC npc)
  {
    client.setHintArrowTargetType(HintArrowType.NPC.getValue());
    client.setHintArrowNpcTargetIdx(npc.getIndex());
  }

  @Inject
  @Override
  public void setHintArrow(Player player)
  {
    client.setHintArrowTargetType(HintArrowType.PLAYER.getValue());
    client.setHintArrowPlayerTargetIdx(((RSPlayer) player).getPlayerId());
    hintPlayerChanged(-1);
  }

  @Inject
  @Override
  public void setHintArrow(WorldPoint point)
  {
    client.setHintArrowTargetType(HintArrowType.WORLD_POSITION.getValue());
    client.setHintArrowX(point.getX());
    client.setHintArrowY(point.getY());
    // position the arrow in center of the tile
    client.setHintArrowOffsetX(LOCAL_TILE_SIZE / 2);
    client.setHintArrowOffsetY(LOCAL_TILE_SIZE / 2);
  }

  @Inject
  @Override
  public WorldPoint getHintArrowPoint()
  {
    if (getHintArrowType() == HintArrowType.WORLD_POSITION)
    {
      int x = client.getHintArrowX();
      int y = client.getHintArrowY();
      return new WorldPoint(x, y, client.getPlane());
    }

    return null;
  }

  @Inject
  @Override
  public Player getHintArrowPlayer()
  {
    if (getHintArrowType() == HintArrowType.PLAYER)
    {
      int idx = client.getHintArrowPlayerTargetIdx();
      RSPlayer[] players = client.getCachedPlayers();

      if (idx < 0 || idx >= players.length)
      {
        return null;
      }

      return players[idx];
    }

    return null;
  }

  @FieldHook("hintArrowPlayerIndex")
  @Inject
  public static void hintPlayerChanged(int ignored)
  {
    // Setting the localInteractingIndex (aka player target index, it only applies to players)
    // causes that player to get priority over others when rendering/menus are added
    if (client.getVar(VarPlayer.ATTACKING_PLAYER) == -1)
    {
      client.setLocalInteractingIndex(client.getHintArrowPlayerTargetIdx() & 2047);
    }
  }

  @Inject
  public boolean isKeyPressed(int keycode) {
    boolean[] pressedKeys = client.getPressedKeys();
    return pressedKeys[keycode];
  }

  @Inject
  private static boolean invertYaw;

  @Inject
  @Override
  public void setInvertYaw(boolean state)
  {
    invertYaw = state;
  }

  @Inject
  private static boolean invertPitch;

  @Inject
  @Override
  public void setInvertPitch(boolean state)
  {
    invertPitch = state;
  }

  @Inject
  @Override
  public void stopNow()
  {
    setStopTimeMs(1);
  }

  @Inject
  @Override
  public boolean isPrayerActive(Prayer prayer)
  {
    return getVar(prayer.getVarbit()) == 1;
  }

  @Inject
  public static RSArchive[] archives = new RSArchive[21];

  @Inject
  @Override
  public List<NPC> getNpcs()
  {
    int validNpcIndexes = getNpcIndexesCount();
    int[] npcIndexes = getNpcIndices();
    NPC[] cachedNpcs = getCachedNPCs();
    List<NPC> npcs = new ArrayList<NPC>(validNpcIndexes);

    for (int i = 0; i < validNpcIndexes; ++i)
    {
      npcs.add(cachedNpcs[npcIndexes[i]]);
    }

    return npcs;
  }

  @FieldHook("canvasWidth")
  @Inject
  public static void canvasWidthChanged(int idx)
  {
    client.getCallbacks().post(CanvasSizeChanged.INSTANCE);
  }

  @FieldHook("canvasHeight")
  @Inject
  public static void canvasHeightChanged(int idx)
  {
    client.getCallbacks().post(CanvasSizeChanged.INSTANCE);
  }

  @Inject
  public MessageNode addChatMessage(ChatMessageType type, String name, String message, String sender, boolean postEvent)
  {
    copy$addChatMessage(type.getType(), name, message, sender);

    // Get the message node which was added
    @SuppressWarnings("unchecked") Map<Integer, RSChatChannel> chatLineMap = client.getChatLineMap();
    RSChatChannel chatLineBuffer = chatLineMap.get(type.getType());
    MessageNode messageNode = chatLineBuffer.getLines()[0];

    if (postEvent)
    {
      final ChatMessage chatMessage = new ChatMessage(messageNode, type, name, message, sender, messageNode.getTimestamp());
      client.getCallbacks().post(chatMessage);
    }

    return messageNode;
  }

  @Inject
  @Override
  public MessageNode addChatMessage(ChatMessageType type, String name, String message, String sender)
  {
    return addChatMessage(type, name, message, sender, true);
  }

  @SuppressWarnings("InfiniteRecursion")
  @Copy("addChatMessage")
  @Replace("addChatMessage")
  public static void copy$addChatMessage(int type, String name, String message, String sender)
  {
    copy$addChatMessage(type, name, message, sender);

    // Get the message node which was added
    @SuppressWarnings("unchecked") Map<Integer, RSChatChannel> chatLineMap = client.getChatLineMap();
    RSChatChannel chatLineBuffer = chatLineMap.get(type);
    MessageNode messageNode = chatLineBuffer.getLines()[0];

    final ChatMessageType chatMessageType = ChatMessageType.of(type);
    final ChatMessage chatMessage = new ChatMessage(messageNode, chatMessageType, name, message, sender, messageNode.getTimestamp());
    client.getCallbacks().post(chatMessage);
  }

  @Inject
  @Override
  public boolean isFriended(String name, boolean mustBeLoggedIn)
  {
    RSUsername rsName = createName(name, getLoginType());
    return getFriendManager().isFriended(rsName, mustBeLoggedIn);
  }

  @Inject
  @Override
  public NameableContainer<Friend> getFriendContainer()
  {
    return getFriendManager().getFriendContainer();
  }

  @Inject
  @Override
  public NameableContainer<Ignore> getIgnoreContainer()
  {
    return getFriendManager().getIgnoreContainer();
  }

  @Inject
  private HashMap<Integer, RSEnumComposition> enumCache;

  @Inject
  @Override
  public EnumComposition getEnum(int id)
  {
    assert isClientThread() : "getEnum must be called on client thread";

    if (enumCache == null)
      enumCache = new HashMap<Integer, RSEnumComposition>();

    RSEnumComposition rsEnumDefinition;
    if (enumCache.containsKey(id))
    {
      return enumCache.get(id);
    }

    rsEnumDefinition = getRsEnum(id);
    enumCache.put(id, rsEnumDefinition);
    return rsEnumDefinition;
  }

  @Inject
  @Override
  public List<Player> getPlayers()
  {
    int validPlayerIndexes = getPlayerIndexesCount();
    int[] playerIndexes = getPlayerIndices();
    Player[] cachedPlayers = getCachedPlayers();
    List<Player> players = new ArrayList<Player>(validPlayerIndexes);

    for (int i = 0; i < validPlayerIndexes; ++i)
    {
      players.add(cachedPlayers[playerIndexes[i]]);
    }

    return players;
  }

  @Inject
  @Override
  public void setModIcons(IndexedSprite[] modIcons)
  {
    setRSModIcons((RSIndexedSprite[]) modIcons);
  }

  @Inject
  @Override
  public RSSpritePixels[] getSprites(IndexDataBase source, int archiveId, int fileId)
  {
    RSAbstractArchive rsSource = (RSAbstractArchive) source;
    byte[] configData = rsSource.getConfigData(archiveId, fileId);
    if (configData == null)
    {
      return null;
    }

    decodeSprite(configData);

    int indexedSpriteCount = getIndexedSpriteCount();
    int maxWidth = getIndexedSpriteWidth();
    int maxHeight = getIndexedSpriteHeight();
    int[] offsetX = getIndexedSpriteOffsetXs();
    int[] offsetY = getIndexedSpriteOffsetYs();
    int[] widths = getIndexedSpriteWidths();
    int[] heights = getIndexedSpriteHeights();
    byte[][] spritePixelsArray = getSpritePixels();
    int[] indexedSpritePalette = getIndexedSpritePalette();

    RSSpritePixels[] array = new RSSpritePixels[indexedSpriteCount];

    for (int i = 0; i < indexedSpriteCount; ++i)
    {
      int width = widths[i];
      int height = heights[i];

      byte[] pixelArray = spritePixelsArray[i];
      int[] pixels = new int[width * height];

      RSSpritePixels spritePixels = createSpritePixels(pixels, width, height);
      spritePixels.setMaxHeight(maxHeight);
      spritePixels.setMaxWidth(maxWidth);
      spritePixels.setOffsetX(offsetX[i]);
      spritePixels.setOffsetY(offsetY[i]);

      for (int j = 0; j < width * height; ++j)
      {
        pixels[j] = indexedSpritePalette[pixelArray[j] & 0xff];
      }

      array[i] = spritePixels;
    }

    setIndexedSpriteOffsetXs(null);
    setIndexedSpriteOffsetYs(null);
    setIndexedSpriteWidths(null);
    setIndexedSpriteHeights(null);
    setIndexedSpritePalette(null);
    setSpritePixels(null);

    return array;
  }

  @Inject
  private static int inventoryDragDelay;

  @Inject
  private static int itemPressedDurationBuffer;

  @FieldHook("itemDragDuration")
  @Inject
  public static void itemPressedDurationChanged(int idx)
  {
    if (client.getItemPressedDuration() > 0)
    {
      itemPressedDurationBuffer++;
      if (itemPressedDurationBuffer >= inventoryDragDelay)
      {
        client.setItemPressedDuration(itemPressedDurationBuffer);
      }
      else
      {
        client.setItemPressedDuration(0);
      }
    }
    else
    {
      itemPressedDurationBuffer = 0;
    }
  }

  @Inject
  @Override
  public void setInventoryDragDelay(int delay)
  {
    inventoryDragDelay = delay;
  }

  @Inject
  @Override
  public void refreshChat()
  {
    setChatCycle(getCycleCntr());
  }

  @FieldHook("Varps_main")
  @Inject
  public static void settingsChanged(int idx)
  {
    VarbitChanged varbitChanged = new VarbitChanged();
    varbitChanged.setIndex(idx);
    client.getCallbacks().post(varbitChanged);
  }

  @Inject
  @Override
  public AccountType getAccountType()
  {
    int varbit = getVar(Varbits.ACCOUNT_TYPE);

    switch (varbit)
    {
      case 1:
        return AccountType.IRONMAN;
      case 2:
        return AccountType.ULTIMATE_IRONMAN;
      case 3:
        return AccountType.HARDCORE_IRONMAN;
    }

    return AccountType.NORMAL;
  }

  @Inject
  @Override
  public RSWidget[] getGroup(int groupId)
  {
    RSWidget[][] widgets = getWidgets();

    if (widgets == null || groupId < 0 || groupId >= widgets.length || widgets[groupId] == null)
    {
      return null;
    }

    return widgets[groupId];
  }

  @Inject
  @Override
  public int getVarpValue(int[] varps, int varpId)
  {
    return varps[varpId];
  }

  @Inject
  @Override
  public int getVarpValue(int varpId)
  {
    return getVarpValue(getVarps(), varpId);
  }

  @Inject
  @Override
  public ClanChannel getClanChannel()
  {
    return getCurrentClanChannels()[0];
  }

  @Inject
  @Override
  public ClanSettings getClanSettings()
  {
    return getCurrentClanSettingsAry()[0];
  }

  @Inject
  @Override
  public ClanRank getClanRankFromRs(int rank)
  {
    switch (rank)
    {
      case -1:
        return ClanRank.GUEST;
      case 10:
        return ClanRank.CLAN_RANK_2;
      case 20:
        return ClanRank.CLAN_RANK_3;
      case 30:
        return ClanRank.CLAN_RANK_4;
      case 40:
        return ClanRank.CLAN_RANK_5;
      case 50:
        return ClanRank.CLAN_RANK_6;
      case 60:
        return ClanRank.CLAN_RANK_7;
      case 70:
        return ClanRank.CLAN_RANK_8;
      case 80:
        return ClanRank.CLAN_RANK_9;
      case 90:
        return ClanRank.CLAN_RANK_10;
      case 100:
        return ClanRank.ADMINISTRATOR;
      case 105:
        return ClanRank.CLAN_RANK_11;
      case 110:
        return ClanRank.CLAN_RANK_12;
      case 115:
        return ClanRank.CLAN_RANK_13;
      case 120:
        return ClanRank.CLAN_RANK_14;
      case 125:
        return ClanRank.DEPUTY_OWNER;
      case 126:
        return ClanRank.OWNER;
      case 127:
        return ClanRank.JMOD;
      default:
        return ClanRank.CLAN_RANK_1;
    }
  }

  @Inject
  @FieldHook("guestClanChannel")
  public static void onGuestClanChannelChanged(int idx)
  {
    client.getCallbacks().post(new ClanChannelChanged(client.getGuestClanChannel(), -1, true));
  }

  @Inject
  @FieldHook("currentClanChannels")
  public static void onCurrentClanChannelsChanged(int idx)
  {
    RSClanChannel[] clanChannels = client.getCurrentClanChannels();

    if (idx >= 0 && idx < clanChannels.length)
    {
      RSClanChannel clanChannel = clanChannels[idx];
      client.getCallbacks().post(new ClanChannelChanged(clanChannel, idx, false));
    }
  }

  @Inject
  @Override
  public void interact(final int identifier, final int opcode, final int param0, final int param1,
                       final int screenX, final int screenY) {
    InvokeMenuActionEvent event = new InvokeMenuActionEvent(identifier, opcode, param0, param1);
    event.clickX = screenX;
    event.clickY = screenY;

    client.getCallbacks().post(event);
  }

  @Inject
  @Override
  public long getOverallExperience()
  {
    int[] experiences = getSkillExperiences();

    long totalExperience = 0L;

    for (int experience : experiences)
    {
      totalExperience += experience;
    }

    return totalExperience;
  }

  @Inject
  @Override
  public Widget[] getWidgetRoots()
  {
    int topGroup = getTopLevelInterfaceId();
    if (topGroup == -1)
    {
      return new Widget[]{};
    }
    List<Widget> widgets = new ArrayList<Widget>();
    for (RSWidget widget : getWidgets()[topGroup])
    {
      if (widget != null && widget.getRSParentId() == -1)
      {
        widgets.add(widget);
      }
    }
    return widgets.toArray(new Widget[widgets.size()]);
  }

  @Inject
  private static boolean hideDisconnect = false;

  @Inject
  @Override
  public void setHideDisconnect(boolean dontShow)
  {
    hideDisconnect = dontShow;
  }

  @Inject
  @Override
  public String getLoginMessage() {
    if (getLoginIndex() == 14) {
      if (getBanType() == 0) {
        return "Your account has been disabled. Please visit the support page for assistance.";
      }

      if (getBanType() == 1) {
        return "Account locked as we suspect it has been stolen. Please visit the support page for assistance.";
      }
    }

    if (getLoginIndex() == 3) {
      return "Invalid credentials.";
    }

    return getLoginResponse1() + " " + getLoginResponse2() + " " + getLoginResponse3();
  }

  @Override
  @Inject
  public boolean isTileObjectValid(Tile tile, TileObject t) {
    if (!(t instanceof RSGameObject)) {
      return true;
    }

    // actors, projectiles, and graphics objects are added and removed from the scene each frame as GameObjects,
    // so ignore them.
    RSGameObject gameObject = (RSGameObject) t;
    RSRenderable renderable = gameObject.getRenderable();
    boolean invalid = renderable instanceof RSActor || renderable instanceof RSProjectile || renderable instanceof RSGraphicsObject;
    invalid |= gameObject.getStartX() != ((RSTile)tile).getX() || gameObject.getStartY() != ((RSTile) tile).getY();
    return !invalid;
  }

  @Inject
  @Override
  public boolean isItemDefinitionCached(int id) {
    return itemDefCache.containsKey(id);
  }

  @Copy("openMenu")
  @Replace("openMenu")
  public void copy$openMenu(int x, int y)
  {
    final MenuOpened event = new MenuOpened();
    event.setMenuEntries(getMenuEntries());
    callbacks.post(event);

    if (event.isModified())
    {
      setMenuEntries(event.getMenuEntries());
    }

    copy$openMenu(x, y);

    client.getScene().menuOpen$api(client.getPlane(), x - client.getViewportXOffset(), y - client.getViewportYOffset(), false);
  }

  @Inject
  @Override
  public boolean isLowCpu() {
    return lowCpu;
  }

  @Inject
  @Override
  public void setLowCpu(boolean enabled) {
    lowCpu = enabled;
  }

  @Copy("drawWidgets")
  @Replace("drawWidgets")
  static final void copy$drawWidgets(int var0, int var1, int var2, int var3, int var4, int var5, int var6, int var7) {
    if (!lowCpu) {
      copy$drawWidgets(var0, var1, var2, var3, var4, var5, var6, var7);
    }
  }

  @Copy("drawModelComponents")
  @Replace("drawModelComponents")
  static void copy$drawModelComponents(Widget[] var0, int var1) {
    if (!lowCpu) {
      copy$drawModelComponents(var0, var1);
    }
  }

  @Inject
  @Override
  public void uncacheNPC(int id) {
    npcDefCache.remove(id);
  }

  @Inject
  @Override
  public void uncacheItem(int id) {
    itemDefCache.remove(id);
  }

  @Inject
  @Override
  public void uncacheObject(int id) {
    objDefCache.remove(id);
  }

  @Inject
  @Override
  public void clearNPCCache() {
    npcDefCache.clear();
  }

  @Inject
  @Override
  public void clearItemCache() {
    itemDefCache.clear();
  }

  @Inject
  @Override
  public void clearObjectCache() {
    objDefCache.clear();
  }

  @Inject
  @MethodHook("resumePauseWidget")
  public static void onDialogProcessed(int widgetUid, int menuIndex) {
    client.getCallbacks().post(new DialogProcessed(widgetUid, menuIndex));
  }

  @Inject
  @FieldHook("rndHue")
  public static void rndHue(int idx)
  {
    int rndHue = client.getRndHue();

    if (rndHue >= -8 && rndHue <= 8)
    {
      RSScene scene = client.getScene();

      byte[][][] underlays = client.getTileUnderlays();
      byte[][][] overlays = client.getTileOverlays();
      byte[][][] tileShapes = client.getTileShapes();

      scene.setUnderlayIds(Arrays.copyOf(underlays, underlays.length));
      scene.setOverlayIds(Arrays.copyOf(overlays, overlays.length));
      scene.setTileShapes(Arrays.copyOf(tileShapes, tileShapes.length));
    }
  }

  @Inject
  @FieldHook("loginIndex")
  public static void loginIndex(int idx) {
    client.getCallbacks().post(new LoginStateChanged(client.getLoginIndex()));
  }

  @Inject
  @FieldHook("grandExchangeOffers")
  public static void onGrandExchangeOffersChanged(int idx)
  {
    if (idx == -1)
    {
      return;
    }

    GrandExchangeOffer internalOffer = client.getGrandExchangeOffers()[idx];

    if (internalOffer == null)
    {
      return;
    }

    GrandExchangeOfferChanged offerChangedEvent = new GrandExchangeOfferChanged();
    offerChangedEvent.setOffer(internalOffer);
    offerChangedEvent.setSlot(idx);
    client.getCallbacks().post(offerChangedEvent);
  }

  @Inject
  @Override
  public void addFriend(String friend)
  {
    RSFriendSystem friendSystem = getFriendManager();
    friendSystem.addFriend$api(friend);
  }

  @Inject
  @Override
  public void removeFriend(String friend)
  {
    RSFriendSystem friendSystem = getFriendManager();
    friendSystem.removeFriend$api(friend);
  }

  @Inject
  @Override
  public void setModulus(BigInteger modulus) {

  }

  @Inject
  @Override
  public void playMusicTrack(int var0, RSAbstractArchive var1, int var2, int var3, int var4, boolean var5) {

  }

  @Inject
  @Override
  public int getTotalLevel()
  {
    int totalLevel = 0;

    int[] realLevels = client.getRealSkillLevels();
    int lastSkillIdx = Skill.CONSTRUCTION.ordinal();

    for (int i = 0; i < realLevels.length; i++)
    {
      if (i <= lastSkillIdx)
      {
        totalLevel += realLevels[i];
      }
    }

    return totalLevel;
  }

  @FieldHook("playerMenuActions")
  @Inject
  public static void playerOptionsChanged(int idx)
  {
    // Reset the menu opcode
    MenuAction[] playerActions = {PLAYER_FIRST_OPTION, PLAYER_SECOND_OPTION, PLAYER_THIRD_OPTION, PLAYER_FOURTH_OPTION,
            PLAYER_FIFTH_OPTION, PLAYER_SIXTH_OPTION, PLAYER_SEVENTH_OPTION, PLAYER_EIGTH_OPTION};
    if (idx >= 0 && idx < playerActions.length)
    {
      MenuAction playerAction = playerActions[idx];
      client.getPlayerMenuTypes()[idx] = playerAction.getId();
    }

    PlayerMenuOptionsChanged optionsChanged = new PlayerMenuOptionsChanged();
    optionsChanged.setIndex(idx);
    client.getCallbacks().post(optionsChanged);
  }

  @Inject
  public Model loadModel(int id)
  {
    return loadModel(id, null, null);
  }

  @Inject
  public Model loadModel(int id, short[] colorToFind, short[] colorToReplace)
  {
    RSModelData modeldata = client.getModelData(client.getObjectDefinition_modelsArchive(), id, 0);

    if (colorToFind != null)
    {
      for (int i = 0; i < colorToFind.length; ++i)
      {
        modeldata.recolor$api(colorToFind[i], colorToReplace[i]);
      }
    }

    return modeldata.toModel(modeldata.getAmbient() + 64, modeldata.getContrast() + 850, -30, -50, -30);
  }

  @Inject
  public Sequence loadAnimation(int id)
  {
    return client.getSequenceDefinition(id);
  }

  @Inject
  @Override
  public boolean isUnlockedFps()
  {
    return unlockedFps;
  }

  @Inject
  @Override
  public void setUnlockedFps(boolean unlocked)
  {
    unlockedFps = unlocked;

    if (unlocked)
    {
      posToCameraAngle(client.getMapAngle(), client.getCameraPitch());
    }
    else
    {
      delayNanoTime = 0L;
    }
  }

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
  @Override
  public long getUnlockedFpsTarget()
  {
    return delayNanoTime;
  }

  @Inject
  public void updateCamera()
  {
    if (unlockedFps)
    {
      long nanoTime = System.nanoTime();
      long diff = nanoTime - this.lastNanoTime;
      this.lastNanoTime = nanoTime;

      if (this.getGameState() == GameState.LOGGED_IN)
      {
        this.interpolateCamera(diff);
      }
    }
  }

  @Inject
  public void interpolateCamera(long var1)
  {
    double angleDX = diffToDangle(client.getCamAngleDY(), var1);
    double angleDY = diffToDangle(client.getCamAngleDX(), var1);

    tmpCamAngleY += angleDX / 2;
    tmpCamAngleX += angleDY / 2;
    tmpCamAngleX = Doubles.constrainToRange(tmpCamAngleX, Perspective.UNIT * STANDARD_PITCH_MIN, client.getCameraPitchRelaxerEnabled() ? Perspective.UNIT * NEW_PITCH_MAX : Perspective.UNIT * STANDARD_PITCH_MAX);

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

  @Inject
  @Override
  public void posToCameraAngle(int var0, int var1)
  {
    tmpCamAngleY = var0 * Perspective.UNIT;
    tmpCamAngleX = var1 * Perspective.UNIT;
  }

  @Inject
  public static int toCameraPos(double var0)
  {
    return (int) (var0 / Perspective.UNIT) & 2047;
  }

  @Inject
  @FieldHook("Client_plane")
  public static void clientPlaneChanged(int idx) {
    client.getCallbacks().post(new PlaneChanged(client.getPlane()));
  }
}
