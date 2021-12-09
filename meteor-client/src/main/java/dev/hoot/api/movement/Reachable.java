package dev.hoot.api.movement;

import dev.hoot.api.game.Game;
import dev.hoot.api.game.GameThread;
import dev.hoot.api.scene.Tiles;
import net.runelite.api.*;
import net.runelite.api.coords.Direction;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;

import java.util.*;
import java.util.stream.Collectors;

public class Reachable {
    private static final int MAX_ATTEMPTED_TILES = 1000;

    public static boolean check(int flag, int checkFlag) {
        return (flag & checkFlag) != 0;
    }

    public static boolean isObstacle(int endFlag) {
        return check(endFlag, 0x100 | 0x20000 | 0x200000 | 0x1000000);
    }

    public static boolean isObstacle(WorldPoint worldPoint) {
        return isObstacle(getCollisionFlag(worldPoint));
    }

    public static int getCollisionFlag(WorldPoint point) {
        CollisionData[] collisionMaps = Game.getClient().getCollisionMaps();
        if (collisionMaps == null) {
            return 0xFFFFFF;
        }

        CollisionData collisionData = collisionMaps[Game.getClient().getPlane()];
        if (collisionData == null) {
            return 0xFFFFFF;
        }

        LocalPoint localPoint = LocalPoint.fromWorld(Game.getClient(), point);
        if (localPoint == null) {
            return 0xFFFFFF;
        }

        return collisionData.getFlags()[localPoint.getSceneX()][localPoint.getSceneY()];
    }

    public static boolean isWalled(Direction direction, int startFlag) {
        return switch (direction) {
            case NORTH -> check(startFlag, 0x2);
            case SOUTH -> check(startFlag, 0x20);
            case WEST -> check(startFlag, 0x80);
            case EAST -> check(startFlag, 0x8);
        };
    }

    public static boolean isWalled(WorldPoint source, WorldPoint destination) {
        return isWalled(Tiles.getAt(source), Tiles.getAt(destination));
    }

    public static boolean isWalled(Tile source, Tile destination) {
        WallObject wall = source.getWallObject();
        if (wall == null) {
            return false;
        }

        WorldPoint a = source.getWorldLocation();
        WorldPoint b = destination.getWorldLocation();

        return switch (wall.getOrientationA()) {
            case 1 -> a.dx(-1).equals(b) || a.dx(-1).dy(1).equals(b) || a.dx(-1).dy(-1).equals(b);
            case 2 -> a.dy(1).equals(b) || a.dx(-1).dy(1).equals(b) || a.dx(1).dy(1).equals(b);
            case 4 -> a.dx(1).equals(b) || a.dx(1).dy(1).equals(b) || a.dx(1).dy(-1).equals(b);
            case 8 -> a.dy(-1).equals(b) || a.dx(-1).dy(-1).equals(b) || a.dx(-1).dy(1).equals(b);
            default -> false;
        };
    }

    public static boolean hasDoor(WorldPoint source, Direction direction) {
        Tile tile = Tiles.getAt(source);
        if (tile == null) {
            return false;
        }

        return hasDoor(tile, direction);
    }

    public static boolean hasDoor(Tile source, Direction direction) {
        WallObject wall = source.getWallObject();
        if (wall == null) {
            return false;
        }

        if (!wall.isDefinitionCached()) {
            GameThread.invokeLater(() -> Game.getClient().getObjectComposition(wall.getId()));
        }

        return isWalled(direction, getCollisionFlag(source.getWorldLocation())) && wall.hasAction("Open", "Close");
    }

    public static boolean isDoored(Tile source, Tile destination) {
        WallObject wall = source.getWallObject();
        if (wall == null) {
            return false;
        }

        if (!wall.isDefinitionCached()) {
            GameThread.invokeLater(() -> Game.getClient().getObjectComposition(wall.getId()));
        }

        return isWalled(source, destination) && wall.hasAction("Open");
    }

    public static boolean canWalk(Direction direction, int startFlag, int endFlag) {
        if (isObstacle(endFlag)) {
            return false;
        }

        return !isWalled(direction, startFlag);
    }

    public static WorldPoint getNeighbour(Direction direction, WorldPoint source) {
        return switch (direction) {
            case NORTH -> source.dy(1);
            case SOUTH -> source.dy(-1);
            case WEST -> source.dx(-1);
            case EAST -> source.dx(1);
        };
    }

    public static List<WorldPoint> getNeighbours(WorldPoint current, Locatable targetObject) {
        List<WorldPoint> out = new ArrayList<>();
        for (Direction dir : Direction.values()) {
            WorldPoint neighbour = getNeighbour(dir, current);
            if (!neighbour.isInScene(Game.getClient())) {
                continue;
            }

            if (targetObject != null) {
                boolean containsPoint;
                if (targetObject instanceof GameObject go) {
                    containsPoint = go.getWorldArea().contains(neighbour);
                } else {
                    containsPoint = targetObject.getWorldLocation().equals(neighbour);
                }

                if (containsPoint
                        && (!isWalled(dir, getCollisionFlag(current)) || targetObject instanceof WallObject)) {
                    out.add(neighbour);
                    continue;
                }
            }

            if (!canWalk(dir, getCollisionFlag(current), getCollisionFlag(neighbour))) {
                continue;
            }

            out.add(neighbour);
        }

        return out;
    }

    public static List<WorldPoint> getVisitedTiles(WorldPoint destination, Locatable targetObject) {
        Player local = Game.getClient().getLocalPlayer();
        // Don't check if too far away
        if (local == null || destination.distanceTo(local.getWorldLocation()) > 35) {
            return Collections.emptyList();
        }

        List<WorldPoint> visitedTiles = new ArrayList<>();
        LinkedList<WorldPoint> queue = new LinkedList<>();

        if (local.getWorldLocation().getPlane() != destination.getPlane()) {
            return visitedTiles;
        }

        queue.add(local.getWorldLocation());

        while (!queue.isEmpty()) {
            // Stop if too many attempts, for performance
            if (visitedTiles.size() > MAX_ATTEMPTED_TILES) {
                return visitedTiles;
            }

            WorldPoint current = queue.pop();
            visitedTiles.add(current);

            if (current.equals(destination)) {
                return visitedTiles;
            }

            List<WorldPoint> neighbours = getNeighbours(current, targetObject)
                    .stream().filter(x -> !visitedTiles.contains(x) && !queue.contains(x))
                    .collect(Collectors.toList());
            queue.addAll(neighbours);
        }

        return visitedTiles;
    }

    public static boolean isInteractable(Locatable locatable) {
        return getVisitedTiles(locatable.getWorldLocation(), locatable).contains(locatable.getWorldLocation());
    }

    public static boolean isWalkable(WorldPoint worldPoint) {
        return getVisitedTiles(worldPoint, null).contains(worldPoint);
    }
}
