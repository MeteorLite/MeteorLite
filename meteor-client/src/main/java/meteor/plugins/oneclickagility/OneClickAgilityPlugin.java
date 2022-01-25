package meteor.plugins.oneclickagility;

import com.google.inject.Provides;
import meteor.eventbus.events.ConfigChanged;
import net.runelite.api.Client;
import net.runelite.api.DecorativeObject;
import net.runelite.api.GameObject;
import net.runelite.api.GameState;
import net.runelite.api.MenuAction;
import net.runelite.api.MenuEntry;
import net.runelite.api.Skill;
import net.runelite.api.Tile;
import net.runelite.api.TileItem;
import net.runelite.api.TileObject;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.ClientTick;
import net.runelite.api.events.DecorativeObjectDespawned;
import net.runelite.api.events.DecorativeObjectSpawned;
import net.runelite.api.events.GameObjectDespawned;
import net.runelite.api.events.GameObjectSpawned;
import net.runelite.api.events.GroundObjectDespawned;
import net.runelite.api.events.GroundObjectSpawned;
import net.runelite.api.events.ItemDespawned;
import net.runelite.api.events.ItemSpawned;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.events.WallObjectDespawned;
import net.runelite.api.events.WallObjectSpawned;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.api.widgets.WidgetItem;
import meteor.eventbus.Subscribe;
import meteor.game.ItemManager;
import meteor.plugins.Plugin;
import meteor.plugins.PluginDescriptor;
import meteor.config.ConfigManager;
import meteor.util.GameEventManager;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@PluginDescriptor(
        name = "One Click Agility",
        description = "Reclined gaming",
        enabledByDefault = false
)
public class OneClickAgilityPlugin extends Plugin
{

    @Inject
    private Client client;

    @Inject
    GameEventManager gameEventManager;

    @Inject
    ItemManager itemManager;

    @Inject
    private OneClickAgilityConfig config;

    @Inject
    private ConfigManager configManager;

    @Provides
    public OneClickAgilityConfig getConfig(ConfigManager configManager)
    {
        return configManager.getConfig(OneClickAgilityConfig.class);
    }

    private static final int MARK_ID = 11849;
    private static final int COIN_ID = 995;
    private static final Set<Integer> PORTAL_IDS = Set.of(36241,36242,36243,36244,36245,36246);
    private static final Set<Integer> SUMMER_PIE_ID = Set.of(7220,7218);
    private static final WorldPoint SEERS_END = new WorldPoint(2704,3464,0);
    private static final WorldPoint PYRAMID_TOP_RIGHT = new WorldPoint(3043,4697,3);
    private static final WorldPoint PYRAMID_TOP_LEFT = new WorldPoint(3042,4697,3);

    ArrayList<Tile> marks = new ArrayList<>();
    ArrayList<Tile> coins = new ArrayList<>();
    ArrayList<GameObject> portals = new ArrayList<>();
    DecorativeObject pyramidTopObstacle;
    GameObject pyramidTop;
    Course course;

    @Override
    public void startup()
    {
        course = CourseFactory.build(config.courseSelection());
    }

    @Override
    public void shutdown()
    {

    }

    @Subscribe
    public void onConfigChanged(ConfigChanged event)
    {
        if(event.getGroup().equals("oneclickagility"))
        {
            course = CourseFactory.build(config.courseSelection());
            gameEventManager.simulateGameEvents(this);
        }
    }


    @Subscribe
    public void onMenuOptionClicked(MenuOptionClicked event)
    {
        if(event.getMenuOption().equals("<col=00ff00>One Click Agility"))
        {
            handleClick(event);
        }
        else if(event.getMenuOption().equals("One Click Agility"))
        {
            event.consume();
        }
    }

    @Subscribe
    private void onClientTick(ClientTick event)
    {
        if(client.getLocalPlayer() == null || client.getGameState() != GameState.LOGGED_IN) return;
        String text;

        if(course.getCurrentObstacleArea(client.getLocalPlayer()) == null)
        {
            if (config.consumeMisclicks())
            {
                text = "One Click Agility";
            }
            else
            {
                return;
            }
        }
        else
        {
            text =  "<col=00ff00>One Click Agility";
        }

        client.insertMenuItem(
                text,
                "",
                MenuAction.UNKNOWN.getId(),
                0,
                0,
                0,
                true);
    }

    @Subscribe
    public void onGameObjectSpawned(GameObjectSpawned event)
    {
        if(event.getGameObject() == null)
        {
            return;
        }

        if(event.getGameObject().getId() == 10869)
        {
            pyramidTop = event.getGameObject();
        }
        if (PORTAL_IDS.contains(event.getGameObject().getId()))
        {
            portals.add(event.getGameObject());
            return;
        }

        addToCourse(event.getGameObject());
    }

    @Subscribe
    public void onGameObjectDepawned(GameObjectDespawned event)
    {
        if(event.getGameObject() == null)
        {
            return;
        }
        if (PORTAL_IDS.contains(event.getGameObject().getId()))
        {
            portals.remove(event.getGameObject());
            return;
        }
        if(event.getGameObject().getId() == 10869)
        {
            pyramidTop = null;
        }
        removeFromCourse(event.getGameObject());
    }

    @Subscribe
    public void onWallObjectSpawned(WallObjectSpawned event)
    {
        addToCourse(event.getWallObject());
    }


    @Subscribe
    public void onWallObjectDespawned(WallObjectDespawned event)
    {
        removeFromCourse(event.getWallObject());
    }


    @Subscribe
    public void DecorativeObjectSpawned(DecorativeObjectSpawned event)
    {
        if(event.getDecorativeObject().getId() == 10851)
        {
            if(pyramidTopObstacle == null || pyramidTopObstacle.getY() > event.getDecorativeObject().getY())
            {
                pyramidTopObstacle = event.getDecorativeObject();
                return;
            }
        }

        addToCourse(event.getDecorativeObject());
    }

    @Subscribe
    public void DecorativeObjectDespawned(DecorativeObjectDespawned event)
    {
        if(event.getDecorativeObject().getId() == 10851 && event.getDecorativeObject() == pyramidTopObstacle)
        {
            pyramidTopObstacle = null;
            return;
        }

        removeFromCourse(event.getDecorativeObject());
    }

    @Subscribe
    public void onGroundObjectSpawned(GroundObjectSpawned event)
    {
        addToCourse(event.getGroundObject());
    }

    @Subscribe
    public void onGroundObjectDespawned(GroundObjectDespawned event)
    {
        removeFromCourse(event.getGroundObject());
    }

    @Subscribe
    public void onItemSpawned(ItemSpawned event)
    {
        if (event.getItem().getId() == MARK_ID)
        {
            marks.add(event.getTile());
        }
        if (event.getItem().getId() == COIN_ID)
        {
            coins.add(event.getTile());
        }
    }

    @Subscribe
    public void ItemDespawned(ItemDespawned event)
    {
        if (event.getItem().getId() == MARK_ID)
        {
            marks.remove(event.getTile());
        }
        if (event.getItem().getId() == COIN_ID)
        {
            coins.remove(event.getTile());
        }
    }

    private void addToCourse(TileObject tileObject)
    {
        if (course.obstacleIDs.contains(tileObject.getId()))
        {
            course.addObstacle(tileObject);
        }
    }

    private void removeFromCourse(TileObject tileObject)
    {
        if (course.obstacleIDs.contains(tileObject.getId()))
        {
            course.removeObstacle(tileObject);
        }
    }

    private void handleClick(MenuOptionClicked event)
    {
        if(config.skillBoost())
        {
            int boost = client.getBoostedSkillLevel(Skill.AGILITY)-client.getRealSkillLevel(Skill.AGILITY);
            if(config.boostAmount()>boost)
            {
                WidgetItem food = getWidgetItem(SUMMER_PIE_ID);
                if (food != null)
                {
                    event.setMenuEntry(createSummerPieMenuEntry(food));
                    return;
                }
            }
        }

        if(config.seersTele() && config.courseSelection() == AgilityCourse.SEERS_VILLAGE)
        {   //spellbook varbit, worldpoint of dropdown tile, teleportation animation ID
            if(client.getVarbitValue(4070) == 0 && client.getLocalPlayer().getWorldLocation().equals(SEERS_END) && client.getLocalPlayer().getAnimation() != 714)
            {
                event.setMenuEntry(createSeersTeleportMenuEntry());
                return;
            }
        }

        if(config.courseSelection() == AgilityCourse.AGILITY_PYRAMID)
        {
            if((client.getLocalPlayer().getWorldLocation().equals(PYRAMID_TOP_RIGHT) || client.getLocalPlayer().getWorldLocation().equals(PYRAMID_TOP_LEFT))
                    && pyramidTop.getRenderable().getModelHeight() == 309)
            {
                event.setMenuEntry(createPyramidTopMenuEntry());
                return;
            }
        }

        ObstacleArea obstacleArea = course.getCurrentObstacleArea(client.getLocalPlayer());
        if (obstacleArea == null)
        {
            return;
        }

        if (config.pickUpMarks() && !marks.isEmpty())
        {
            Tile wrongMarkTile = null;
            for (Tile mark : marks)
            {
                if (obstacleArea.containsObject(mark))
                {
                    Tile markTile = client.getScene().getTiles()[mark.getPlane()][mark.getSceneLocation().getX()][mark.getSceneLocation().getY()];

                    if (markTile != null && checkTileForMark(markTile))
                    {
                        event.setMenuEntry(createMarkMenuEntry(mark));
                        return;
                    }
                    else
                    {
                        wrongMarkTile = mark;
                    }
                }
            }

            if(wrongMarkTile != null)
            {
                marks.remove(wrongMarkTile);
            }
        }
        if (config.pickUpCoins() && !coins.isEmpty())
        {
            Tile wrongCoinsTile = null;
            for (Tile coin : coins)
            {
                if (obstacleArea.containsObject(coin))
                {
                    Tile coinTile = client.getScene().getTiles()[coin.getPlane()][coin.getSceneLocation().getX()][coin.getSceneLocation().getY()];

                    if (coinTile != null && checkTileForCoins(coinTile))
                    {
                        event.setMenuEntry(createCoinsMenuEntry(coin));
                        return;
                    }
                    else
                    {
                        wrongCoinsTile = coin;
                    }
                }
            }

            if(wrongCoinsTile != null)
            {
                coins.remove(wrongCoinsTile);
            }
        }
        if (!portals.isEmpty())
        {
            for(GameObject portal:portals)
            {
                if (obstacleArea.containsObject(portal) && portal.getClickbox() != null)
                {
                    event.setMenuEntry(createPortalMenuEntry(portal));
                    return;
                }
            }
        }
        if(config.consumeMisclicks() &&
                (client.getLocalPlayer().isMoving()
                        || client.getLocalPlayer().getPoseAnimation() != client.getLocalPlayer().getIdlePoseAnimation()))
        {
            event.consume();
            return;
        }

        event.setMenuEntry(obstacleArea.createMenuEntry());
    }

    private boolean checkTileForMark(Tile tile)
    {
        List<TileItem> items = tile.getGroundItems();
        if (items == null)
        {
            return false;
        }

        for (TileItem item:items)
        {
            if (item == null)
                continue;

            if(item.getId() == MARK_ID)
                return true;
        }
        return false;
    }

    private boolean checkTileForCoins(Tile tile)
    {
        List<TileItem> items = tile.getGroundItems();
        if (items == null)
        {
            return false;
        }

        for (TileItem item:items)
        {
            if (item == null)
                continue;

            if(item.getId() == COIN_ID)
                return true;
        }
        return false;
    }

    public WidgetItem getWidgetItem(Collection<Integer> ids) {
        Widget inventoryWidget = client.getWidget(WidgetInfo.INVENTORY);
        if (inventoryWidget != null) {
            Collection<WidgetItem> items = inventoryWidget.getWidgetItems();
            for (WidgetItem item : items) {
                if (ids.contains(item.getId())) {
                    return item;
                }
            }
        }
        return null;
    }

    private MenuEntry createSeersTeleportMenuEntry()
    {
        return new MenuEntry(
                "Seers'",
                "Camelot Teleport",
                2,
                MenuAction.CC_OP.getId(),
                -1,
                14286879,
                true);
    }

    private MenuEntry createSummerPieMenuEntry(WidgetItem food)
    {
        String[] foodMenuOptions = itemManager.getItemComposition(food.getId()).getInventoryActions();
        return new MenuEntry(
                foodMenuOptions[0],
                foodMenuOptions[0],
                food.getId(),
                MenuAction.ITEM_FIRST_OPTION.getId(),
                food.getSlot(),
                9764864,
                true);
    }

    private MenuEntry createMarkMenuEntry(Tile tile)
    {
        return new MenuEntry("Take",
                "Mark of Grace",
                MARK_ID,MenuAction.GROUND_ITEM_THIRD_OPTION.getId(),
                tile.getSceneLocation().getX(),
                tile.getSceneLocation().getY(),
                true);
    }

    private MenuEntry createCoinsMenuEntry(Tile tile)
    {
        return new MenuEntry("Take",
                "Coins",
                COIN_ID,MenuAction.GROUND_ITEM_THIRD_OPTION.getId(),
                tile.getSceneLocation().getX(),
                tile.getSceneLocation().getY(),
                true);
    }

    private MenuEntry createPortalMenuEntry(GameObject portal)
    {
        return new MenuEntry(
                "Travel",
                "Portal",
                portal.getId(),
                MenuAction.GAME_OBJECT_FIRST_OPTION.getId(),
                portal.getLocalLocation().getSceneX(),
                portal.getLocalLocation().getSceneY(),
                true
        );
    }

    private MenuEntry createPyramidTopMenuEntry()
    {
        return new MenuEntry(
                "Climb",
                "Climbing rocks",
                pyramidTopObstacle.getId(),
                MenuAction.GAME_OBJECT_FIRST_OPTION.getId(),
                pyramidTopObstacle.getLocalLocation().getSceneX(),
                pyramidTopObstacle.getLocalLocation().getSceneY(),
                true
        );
    }

}
