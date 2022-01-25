package dev.hoot.api.movement.pathfinder;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.hoot.api.entities.Players;
import dev.hoot.api.game.Game;
import dev.hoot.api.movement.Reachable;
import dev.hoot.api.scene.Tiles;
import meteor.plugins.regions.RegionConfig;
import meteor.plugins.regions.model.TileFlag;
import net.runelite.api.CollisionData;
import net.runelite.api.CollisionDataFlag;
import net.runelite.api.GameState;
import net.runelite.api.Tile;
import net.runelite.api.coords.Direction;
import net.runelite.api.coords.WorldPoint;
import okhttp3.*;
import org.sponge.util.Logger;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Singleton
public class RegionManager {
    private static final Logger logger = new Logger("RegionManager");
    private static final int VERSION = 3;
    public static final MediaType JSON_MEDIATYPE = MediaType.parse("application/json");
    public static final String API_URL = "http://174.138.15.181:8080";
    public static final Gson GSON = new GsonBuilder().create();

    @Inject
    private OkHttpClient okHttpClient;

    @Inject
    private RegionConfig regionConfig;

    @Inject
    private ScheduledExecutorService executorService;

    public void sendRegion() {
        if (Game.getState() != GameState.LOGGED_IN || regionConfig.apiKey().isBlank()) {
            return;
        }

        if (Game.getClient().isInInstancedRegion()) {
            executorService.schedule(() -> {
                try {
                    Request request = new Request.Builder()
                            .get()
                            .header("api-key", regionConfig.apiKey())
                            .url(API_URL + "/regions/instance/" + Players.getLocal().getWorldLocation().getRegionID())
                            .build();
                    Response response = okHttpClient.newCall(request)
                            .execute();
                    int code = response.code();
                    if (code != 200) {
                        logger.error("Instance store request was unsuccessful: {}", code);
                        return;
                    }

                    logger.debug("Instanced region stored successfully");
                } catch (Exception e) {
                    logger.error("Failed to POST: {}", e.getMessage());
                    e.printStackTrace();
                }
            }, 5_000, TimeUnit.MILLISECONDS);

            return;
        }

        CollisionData[] col = Game.getClient().getCollisionMaps();
        if (col == null) {
            return;
        }

        List<TileFlag> tileFlags = new ArrayList<>();
        Map<WorldPoint, List<Transport>> transportLinks = Walker.buildTransportLinks();
        int plane = Game.getClient().getPlane();
        CollisionData data = col[plane];
        if (data == null) {
            return;
        }

        int[][] flags = data.getFlags();
        for (int x = 0; x < flags.length; x++) {
            for (int y = 0; y < flags.length; y++) {
                int tileX = x + Game.getClient().getBaseX();
                int tileY = y + Game.getClient().getBaseY();
                int flag = flags[x][y];

                // Stop if we reach any tiles which dont have collision data loaded
                // Usually occurs for tiles which are loaded in the 104x104 scene, but are outside the region
                if (flag == 0xFFFFFF) {
                    continue;
                }

                int regionId = ((tileX >> 6) << 8) | (tileY >> 6);

                // Set the full block flag in case tiles are null (ex. on upper levels)
                TileFlag tileFlag = new TileFlag(tileX, tileY, plane, CollisionDataFlag.BLOCK_MOVEMENT_FULL, regionId);
                Tile tile = Tiles.getAt(tileX, tileY, plane);
                if (tile == null) {
                    tileFlags.add(tileFlag);
                    continue;
                }

                tileFlag.setFlag(flag);
                WorldPoint tileCoords = tile.getWorldLocation();

                // Check if we are blocked by objects
                // We don't need to parse west/south because they're checked by parsing adjacent tiles for north/east
                // We also skip the current tile if an adjacent tile does not have their flags loaded
                WorldPoint northernTile = tileCoords.dy(1);
                if (Reachable.getCollisionFlag(northernTile) == 0xFFFFFF) {
                    continue;
                }

                if (Reachable.isObstacle(northernTile)
                        && !Reachable.isWalled(Direction.NORTH, tileFlag.getFlag())
                ) {
                    tileFlag.setFlag(tileFlag.getFlag() + CollisionDataFlag.BLOCK_MOVEMENT_NORTH);
                }

                WorldPoint easternTile = tileCoords.dx(1);
                if (Reachable.getCollisionFlag(easternTile) == 0xFFFFFF) {
                    continue;
                }

                if (Reachable.isObstacle(easternTile)
                        && !Reachable.isWalled(Direction.EAST, tileFlag.getFlag())
                ) {
                    tileFlag.setFlag(tileFlag.getFlag() + CollisionDataFlag.BLOCK_MOVEMENT_EAST);
                }

                List<Transport> transports = transportLinks.get(tileCoords);
                if (plane == Game.getClient().getPlane()) {
                    for (Direction direction : Direction.values()) {
                        switch (direction) {
                            case NORTH:
                                if ((Reachable.hasDoor(tile, direction) || Reachable.hasDoor(northernTile, Direction.SOUTH))
                                        && !isTransport(transports, tileCoords, northernTile)) {
                                    tileFlag.setFlag(tileFlag.getFlag() - CollisionDataFlag.BLOCK_MOVEMENT_NORTH);
                                }

                                break;
                            case EAST:
                                if ((Reachable.hasDoor(tile, direction) || Reachable.hasDoor(easternTile, Direction.WEST))
                                        && !isTransport(transports, tileCoords, easternTile)) {
                                    tileFlag.setFlag(tileFlag.getFlag() - CollisionDataFlag.BLOCK_MOVEMENT_EAST);
                                }

                                break;
                        }
                    }
                }

                tileFlags.add(tileFlag);
            }
        }

        executorService.schedule(() -> {
            try {
                String json = GSON.toJson(tileFlags);
                RequestBody body = RequestBody.create(JSON_MEDIATYPE, json);
                Request request = new Request.Builder()
                        .post(body)
                        .header("api-key", regionConfig.apiKey())
                        .url(API_URL + "/regions/" + VERSION)
                        .build();
                Response response = okHttpClient.newCall(request)
                        .execute();
                int code = response.code();
                if (code != 200) {
                    logger.error("Request was unsuccessful: {}", code);
                    return;
                }

                logger.debug("Region saved successfully");
            } catch (Exception e) {
                logger.error("Failed to POST: {}", e.getMessage());
                e.printStackTrace();
            }
        }, 5, TimeUnit.SECONDS);
    }

    public boolean isTransport(List<Transport> transports, WorldPoint from, WorldPoint to) {
        if (transports == null) {
            return false;
        }

        return transports.stream().anyMatch(t -> t.getSource().equals(from) && t.getDestination().equals(to));
    }
}
