package dev.hoot.api.movement.pathfinder;

import net.runelite.api.coords.WorldPoint;

import java.util.HashMap;
import java.util.Map;

public class CoordMap {
    public static final byte NONE = 0;
    public static final byte CUSTOM = 1;
    public static final byte N = 2;
    public static final byte NE = 3;
    public static final byte E = 4;
    public static final byte SE = 5;
    public static final byte S = 6;
    public static final byte SW = 7;
    public static final byte W = 8;
    public static final byte NW = 9;
    private final byte[][] regions = new byte[256 * 256][];
    private final Map<WorldPoint, WorldPoint> custom = new HashMap<>();

    public boolean containsKey(WorldPoint key) {
        return region(key)[index(key)] != 0;
    }

    public WorldPoint get(WorldPoint key) {
        var code = region(key)[index(key)];

        return switch (code) {
            case NONE -> null;
            case CUSTOM -> custom.get(key);
            case N -> key.dy(1);
            case NE -> key.dx(1).dy(1);
            case E -> key.dx(1);
            case SE -> key.dx(1).dy(-1);
            case S -> key.dy(-1);
            case SW -> key.dx(-1).dy(-1);
            case W -> key.dx(-1);
            case NW -> key.dx(-1).dy(1);
            default -> throw new AssertionError();
        };
    }

    public void put(WorldPoint key, WorldPoint value) {
        region(key)[index(key)] = CUSTOM;
        custom.put(key, value);
    }

    public void putN(WorldPoint key) {
        region(key)[index(key)] = N;
    }

    public void putNE(WorldPoint key) {
        region(key)[index(key)] = NE;
    }

    public void putE(WorldPoint key) {
        region(key)[index(key)] = E;
    }

    public void putSE(WorldPoint key) {
        region(key)[index(key)] = SE;
    }

    public void putS(WorldPoint key) {
        region(key)[index(key)] = S;
    }

    public void putSW(WorldPoint key) {
        region(key)[index(key)] = SW;
    }

    public void putW(WorldPoint key) {
        region(key)[index(key)] = W;
    }

    public void putNW(WorldPoint key) {
        region(key)[index(key)] = NW;
    }

    private int index(WorldPoint WorldPoint) {
        return WorldPoint.getX() % 64 + WorldPoint.getY() % 64 * 64 + (WorldPoint.getPlane() % 64) * 64 * 64;
    }

    private byte[] region(WorldPoint WorldPoint) {
        var regionIndex = WorldPoint.getX() / 64 * 256 + WorldPoint.getY() / 64;

        var region = regions[regionIndex];

        if (region == null) {
            region = regions[regionIndex] = new byte[4 * 64 * 64];
        }

        return region;
    }
}
