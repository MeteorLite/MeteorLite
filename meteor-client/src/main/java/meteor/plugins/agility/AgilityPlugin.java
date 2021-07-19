package meteor.plugins.agility;

import meteor.Plugin;
import meteor.eventbus.Subscribe;
import net.runelite.api.*;
import net.runelite.api.Point;
import net.runelite.api.events.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AgilityPlugin extends Plugin {

    private final Map<TileObject, Obstacle> obstacles = new HashMap<>();
    public List<TileItem> marks = new ArrayList<>();

    Color overlayFillColor;
    private Color activeColor;
    private final int OFFSET_Z = 20;
    private final String MARK_OF_GRACE = "Mark of Grace";

    @Subscribe
    public void onGameStateChanged(GameStateChanged event)
    {
        if (event.getGameState() == GameState.LOADING) {
            marks.clear();
            obstacles.clear();
        }
    }

    @Subscribe
    public void onGameObjectSpawned(GameObjectSpawned event)
    {
        onTileObject(event.getTile(), null, event.getGameObject());
    }

    @Subscribe
    public void onGameObjectChanged(GameObjectChanged event)
    {
        onTileObject(event.getTile(), event.getPrevious(), event.getGameObject());
    }

    @Subscribe
    public void onGameObjectDespawned(GameObjectDespawned event)
    {
        onTileObject(event.getTile(), event.getGameObject(), null);
    }

    @Subscribe
    public void onGroundObjectSpawned(GroundObjectSpawned event)
    {
        onTileObject(event.getTile(), null, event.getGroundObject());
    }

    @Subscribe
    public void onGroundObjectChanged(GroundObjectChanged event)
    {
        onTileObject(event.getTile(), event.getPrevious(), event.getGroundObject());
    }

    @Subscribe
    public void onGroundObjectDespawned(GroundObjectDespawned event)
    {
        onTileObject(event.getTile(), event.getGroundObject(), null);
    }

    @Subscribe
    public void onWallObjectSpawned(WallObjectSpawned event)
    {
        onTileObject(event.getTile(), null, event.getWallObject());
    }

    @Subscribe
    public void onWallObjectChanged(WallObjectChanged event)
    {
        onTileObject(event.getTile(), event.getPrevious(), event.getWallObject());
    }

    @Subscribe
    public void onWallObjectDespawned(WallObjectDespawned event)
    {
        onTileObject(event.getTile(), event.getWallObject(), null);
    }

    @Subscribe
    public void onDecorativeObjectSpawned(DecorativeObjectSpawned event)
    {
        onTileObject(event.getTile(), null, event.getDecorativeObject());
    }

    @Subscribe
    public void onDecorativeObjectChanged(DecorativeObjectChanged event)
    {
        onTileObject(event.getTile(), event.getPrevious(), event.getDecorativeObject());
    }

    @Subscribe
    public void onDecorativeObjectDespawned(DecorativeObjectDespawned event)
    {
        onTileObject(event.getTile(), event.getDecorativeObject(), null);
    }

    @Subscribe
    public void onItemSpawned(ItemSpawned event)
    {
        if (event.getItem().getId() == ItemID.MARK_OF_GRACE)
            marks.add(event.getItem());
    }

    @Subscribe
    public void onItemDespawned(ItemDespawned event)
    {
        if (event.getItem().getId() == ItemID.MARK_OF_GRACE)
            marks.remove(event.getItem());
    }

    private void onTileObject(Tile tile, TileObject oldObject, TileObject newObject)
    {
        obstacles.remove(oldObject);

        if (newObject == null)
        {
            return;
        }

        if (Obstacles.OBSTACLE_IDS.contains(newObject.getId()) ||
                Obstacles.PORTAL_OBSTACLE_IDS.contains(newObject.getId()) ||
                (Obstacles.TRAP_OBSTACLE_IDS.contains(newObject.getId())
                        && Obstacles.TRAP_OBSTACLE_REGIONS.contains(newObject.getWorldLocation().getRegionID())) ||
                Obstacles.SEPULCHRE_OBSTACLE_IDS.contains(newObject.getId()) ||
                Obstacles.SEPULCHRE_SKILL_OBSTACLE_IDS.contains(newObject.getId()))
        {
            obstacles.put(newObject, new Obstacle(tile, null));
        }

        if (Obstacles.SHORTCUT_OBSTACLE_IDS.containsKey(newObject.getId()))
        {
            AgilityShortcut closestShortcut = null;
            int distance = -1;

            // Find the closest shortcut to this object
            for (AgilityShortcut shortcut : Obstacles.SHORTCUT_OBSTACLE_IDS.get(newObject.getId()))
            {
                if (!shortcut.matches(newObject))
                {
                    continue;
                }

                if (shortcut.getWorldLocation() == null)
                {
                    closestShortcut = shortcut;
                    break;
                }
                else
                {
                    int newDistance = shortcut.getWorldLocation().distanceTo2D(newObject.getWorldLocation());
                    if (closestShortcut == null || newDistance < distance)
                    {
                        closestShortcut = shortcut;
                        distance = newDistance;
                    }
                }
            }

            if (closestShortcut != null)
            {
                obstacles.put(newObject, new Obstacle(tile, closestShortcut));
            }
        }
    }

    @Override
    public void paintAboveScene(Graphics2D graphics2D)
    {
        if (marks.size() > 0)
            activeColor = Color.red;
        else
            activeColor = Color.cyan;

        overlayFillColor = new Color(activeColor.darker().getRed(), activeColor.darker().getGreen(), activeColor.darker().getBlue(), 125);

        graphics2D.setColor(activeColor);

        for (TileObject o : obstacles.keySet())
        {
            Color prevColor = activeColor;
            Color prevOverlayFillColor = overlayFillColor;
            AgilityShortcut as = obstacles.get(o).getShortcut();
            if (as != null)
            {
                if (as.getLevel() > client.getBoostedSkillLevel(Skill.AGILITY))
                {
                    activeColor = Color.red;
                }
                else
                {
                    activeColor = Color.green;
                }
                prevOverlayFillColor = overlayFillColor;
                overlayFillColor = new Color(activeColor.darker().getRed(), activeColor.darker().getGreen(), activeColor.darker().getBlue(), 125);
            }
            paintClickBox(graphics2D, o.getPlane(), o.getClickbox());
            activeColor = prevColor;
            overlayFillColor = prevOverlayFillColor;
        }

        for (TileItem tileItem : marks)
        {
            graphics2D.setColor(activeColor);
            graphics2D.draw(Perspective.getCanvasTileAreaPoly(client, tileItem.getTile().getLocalLocation(), 1));
            graphics2D.setColor(Color.white);
            Point p = Perspective.getCanvasTextLocation(client, graphics2D,  tileItem.getTile().getLocalLocation(), MARK_OF_GRACE, OFFSET_Z);
            if (p != null)
                graphics2D.drawString(MARK_OF_GRACE, p.getX(), p.getY());
        }
    }

    private void paintClickBox(Graphics2D graphics2D, int plane, Shape clickbox) {

        if (plane == client.getPlane())
        {
            if (clickbox != null)
            {
                graphics2D.setColor(activeColor);
                graphics2D.draw(clickbox);
                graphics2D.setColor(overlayFillColor);
                graphics2D.fill(clickbox);
            }
        }
    }
}