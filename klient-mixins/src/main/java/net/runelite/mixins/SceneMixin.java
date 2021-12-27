/*
 * Copyright (c) 2016-2017, Adam <Adam@sigterm.info>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.runelite.mixins;

import net.runelite.api.Point;
import net.runelite.api.*;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.events.WidgetHiddenChanged;
import net.runelite.api.events.WidgetPositioned;
import net.runelite.api.hooks.DrawCallbacks;
import net.runelite.api.mixins.*;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.api.widgets.WidgetType;
import net.runelite.rs.api.*;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static net.runelite.api.Constants.*;
import static net.runelite.api.Constants.ROOF_FLAG_BETWEEN;
import static net.runelite.api.widgets.WidgetInfo.TO_CHILD;
import static net.runelite.api.widgets.WidgetInfo.TO_GROUP;

@Mixin(RSScene.class)
public abstract class SceneMixin implements RSScene
{
  private static final int INVALID_HSL_COLOR = 12345678;
  private static final int DEFAULT_DISTANCE = 25;
  private static final int PITCH_LOWER_LIMIT = 128;
  private static final int PITCH_UPPER_LIMIT = 383;
  @Inject
  private static boolean pitchRelaxEnabled = false;
  @Inject
  private static int rl$roofRemovalMode = 0;
  @Inject
  private static int[][][] rl$tiles = new int[4][104][104];
  @Inject
  private static final Set<Integer> rl$tilesToRemove = new HashSet<>();
  @Inject
  private static int rl$hoverX = -1;
  @Inject
  private static int rl$hoverY = -1;
  @Inject
  private static int rl$drawDistance;
  @Shadow("skyboxColor")
  static int skyboxColor;
  @Shadow("client")
  private static RSClient client;

  @Inject
  public static boolean blocking(int plane, int x, int y) {
    return (client.getTileSettings()[plane][x][y] & 4) != 0;
  }

  @Replace("draw")
  void drawScene(int cameraX, int cameraY, int cameraZ, int cameraPitch, int cameraYaw, int plane) {
    final DrawCallbacks drawCallbacks = client.getDrawCallbacks();
    if (drawCallbacks != null) {
      drawCallbacks.drawScene(cameraX, cameraY, cameraZ, cameraPitch, cameraYaw, plane);
    }

    final boolean isGpu = client.isGpu();
    final boolean checkClick = client.isCheckClick();
    final boolean menuOpen = client.isMenuOpen();

    if (!menuOpen && !checkClick) {
      client.getScene().menuOpen$api(client.getPlane(), client.getMouseX() - client.getViewportXOffset(), client.getMouseY() - client.getViewportYOffset(), false);
    }

    if (!isGpu && skyboxColor != 0) {
      client.rasterizerFillRectangle(
              client.getViewportXOffset(),
              client.getViewportYOffset(),
              client.getViewportWidth(),
              client.getViewportHeight(),
              skyboxColor
      );
    }

    final int maxX = getMaxX();
    final int maxY = getMaxY();
    final int maxZ = getMaxZ();

    final int minLevel = getMinLevel();

    final RSTile[][][] tiles = getTiles();
    final int distance = isGpu ? rl$drawDistance : DEFAULT_DISTANCE;

    if (cameraX < 0) {
      cameraX = 0;
    } else if (cameraX >= maxX * Perspective.LOCAL_TILE_SIZE) {
      cameraX = maxX * Perspective.LOCAL_TILE_SIZE - 1;
    }

    if (cameraZ < 0) {
      cameraZ = 0;
    } else if (cameraZ >= maxZ * Perspective.LOCAL_TILE_SIZE) {
      cameraZ = maxZ * Perspective.LOCAL_TILE_SIZE - 1;
    }

    // we store the uncapped pitch for setting camera angle for the pitch relaxer
    // we still have to cap the pitch in order to access the visibility map, though
    int realPitch = cameraPitch;
    if (cameraPitch < PITCH_LOWER_LIMIT) {
      cameraPitch = PITCH_LOWER_LIMIT;
    } else if (cameraPitch > PITCH_UPPER_LIMIT) {
      cameraPitch = PITCH_UPPER_LIMIT;
    }
    if (!pitchRelaxEnabled) {
      realPitch = cameraPitch;
    }

    client.setCycle(client.getCycle() + 1);

    client.setPitchSin(Perspective.SINE[realPitch]);
    client.setPitchCos(Perspective.COSINE[realPitch]);
    client.setYawSin(Perspective.SINE[cameraYaw]);
    client.setYawCos(Perspective.COSINE[cameraYaw]);

    final int[][][] tileHeights = client.getTileHeights();
    boolean[][] renderArea = client.getVisibilityMaps()[(cameraPitch - 128) / 32][cameraYaw / 64];
    client.setRenderArea(renderArea);

    client.setCameraX2(cameraX);
    client.setCameraY2(cameraY);
    client.setCameraZ2(cameraZ);

    int screenCenterX = cameraX / Perspective.LOCAL_TILE_SIZE;
    int screenCenterZ = cameraZ / Perspective.LOCAL_TILE_SIZE;

    client.setScreenCenterX(screenCenterX);
    client.setScreenCenterZ(screenCenterZ);
    client.setScenePlane(plane);

    int minTileX = screenCenterX - distance;
    if (minTileX < 0) {
      minTileX = 0;
    }

    int minTileZ = screenCenterZ - distance;
    if (minTileZ < 0) {
      minTileZ = 0;
    }

    int maxTileX = screenCenterX + distance;
    if (maxTileX > maxX) {
      maxTileX = maxX;
    }

    int maxTileZ = screenCenterZ + distance;
    if (maxTileZ > maxZ) {
      maxTileZ = maxZ;
    }

    client.setMinTileX(minTileX);
    client.setMinTileZ(minTileZ);
    client.setMaxTileX(maxTileX);
    client.setMaxTileZ(maxTileZ);

    updateOccluders();

    client.setTileUpdateCount(0);

    if (rl$roofRemovalMode != 0) {
      rl$tilesToRemove.clear();
      RSPlayer localPlayer = client.getLocalPlayer();
      if (localPlayer != null && (rl$roofRemovalMode & ROOF_FLAG_POSITION) != 0) {
        LocalPoint localLocation = localPlayer.getLocalLocation();
        if (localLocation.isInScene()) {
          rl$tilesToRemove.add(
                  rl$tiles[client.getPlane()][localLocation.getSceneX()][localLocation.getSceneY()]);
        }
      }

      if (rl$hoverX >= 0 && rl$hoverX < 104 && rl$hoverY >= 0 && rl$hoverY < 104
              && (rl$roofRemovalMode & ROOF_FLAG_HOVERED) != 0) {
        rl$tilesToRemove.add(rl$tiles[client.getPlane()][rl$hoverX][rl$hoverY]);
      }

      LocalPoint localDestinationLocation = client.getLocalDestinationLocation();
      if (localDestinationLocation != null && localDestinationLocation.isInScene()
              && (rl$roofRemovalMode & ROOF_FLAG_DESTINATION) != 0) {
        rl$tilesToRemove.add(
                rl$tiles[client.getPlane()][localDestinationLocation.getSceneX()][localDestinationLocation.getSceneY()]);
      }

      if (client.getCameraPitch() < 310 && (rl$roofRemovalMode & ROOF_FLAG_BETWEEN) != 0
              && localPlayer != null) {
        int playerX = localPlayer.getX() >> 7;
        int playerY = localPlayer.getY() >> 7;
        int var29 = client.getCameraX$api() >> 7;
        int var30 = client.getCameraY$api() >> 7;
        if (playerX >= 0 && playerY >= 0 && var29 >= 0 && var30 >= 0 && playerX < 104
                && playerY < 104 && var29 < 104 && var30 < 104) {
          int var31 = Math.abs(playerX - var29);
          int var32 = Integer.compare(playerX, var29);
          int var33 = -Math.abs(playerY - var30);
          int var34 = Integer.compare(playerY, var30);
          int var35 = var31 + var33;

          while (var29 != playerX || var30 != playerY) {
            if (blocking(client.getPlane(), var29, var30)) {
              rl$tilesToRemove.add(rl$tiles[client.getPlane()][var29][var30]);
            }

            int var36 = 2 * var35;
            if (var36 >= var33) {
              var35 += var33;
              var29 += var32;
            } else {
              var35 += var31;
              var30 += var34;
            }
          }
        }
      }
    }

    if (!menuOpen) {
      rl$hoverY = -1;
      rl$hoverX = -1;
    }

    for (int z = minLevel; z < maxY; ++z) {
      RSTile[][] planeTiles = tiles[z];

      for (int x = minTileX; x < maxTileX; ++x) {
        for (int y = minTileZ; y < maxTileZ; ++y) {
          RSTile tile = planeTiles[x][y];
          if (tile != null) {
            int var30 = rl$tiles[client.getPlane()][x][y];
            if (tile.getPhysicalLevel() > plane && rl$roofRemovalMode == 0
                    || !isGpu && !renderArea[x - screenCenterX + DEFAULT_DISTANCE][y - screenCenterZ
                    + DEFAULT_DISTANCE]
                    && tileHeights[z][x][y] - cameraY < 2000
                    || rl$roofRemovalMode != 0 && client.getPlane() < tile.getPhysicalLevel()
                    && var30 != 0 && rl$tilesToRemove.contains(var30)) {
              tile.setDraw(false);
              tile.setVisible(false);
              tile.setWallCullDirection(0);
            } else {
              tile.setDraw(true);
              tile.setVisible(true);
              tile.setDrawEntities(true);
              client.setTileUpdateCount(client.getTileUpdateCount() + 1);
            }
          }
        }
      }
    }

    for (int z = minLevel; z < maxY; ++z) {
      RSTile[][] planeTiles = tiles[z];

      for (int x = -distance; x <= 0; ++x) {
        int var10 = x + screenCenterX;
        int var16 = screenCenterX - x;
        if (var10 >= minTileX || var16 < maxTileX) {
          for (int y = -distance; y <= 0; ++y) {
            int var13 = y + screenCenterZ;
            int var14 = screenCenterZ - y;
            if (var10 >= minTileX) {
              if (var13 >= minTileZ) {
                RSTile tile = planeTiles[var10][var13];
                if (tile != null && tile.isDraw()) {
                  draw(tile, true);
                }
              }

              if (var14 < maxTileZ) {
                RSTile tile = planeTiles[var10][var14];
                if (tile != null && tile.isDraw()) {
                  draw(tile, true);
                }
              }
            }

            if (var16 < maxTileX) {
              if (var13 >= minTileZ) {
                RSTile tile = planeTiles[var16][var13];
                if (tile != null && tile.isDraw()) {
                  draw(tile, true);
                }
              }

              if (var14 < maxTileZ) {
                RSTile tile = planeTiles[var16][var14];
                if (tile != null && tile.isDraw()) {
                  draw(tile, true);
                }
              }
            }

            if (client.getTileUpdateCount() == 0) {
              if (!isGpu && (client.getOculusOrbState() != 0 && !client.getComplianceValue(
                      "orbInteraction"))) {
                client.setEntitiesAtMouseCount(0);
              }
              client.setCheckClick(false);

              client.getCallbacks().drawScene();

              if (client.getDrawCallbacks() != null) {
                client.getDrawCallbacks().postDrawScene();
              }

              return;
            }
          }
        }
      }
    }
    outer:
    for (int z = minLevel; z < maxY; ++z) {
      RSTile[][] planeTiles = tiles[z];

      for (int x = -distance; x <= 0; ++x) {
        int var10 = x + screenCenterX;
        int var16 = screenCenterX - x;
        if (var10 >= minTileX || var16 < maxTileX) {
          for (int y = -distance; y <= 0; ++y) {
            int var13 = y + screenCenterZ;
            int var14 = screenCenterZ - y;
            if (var10 >= minTileX) {
              if (var13 >= minTileZ) {
                RSTile tile = planeTiles[var10][var13];
                if (tile != null && tile.isDraw()) {
                  draw(tile, false);
                }
              }

              if (var14 < maxTileZ) {
                RSTile tile = planeTiles[var10][var14];
                if (tile != null && tile.isDraw()) {
                  draw(tile, false);
                }
              }
            }

            if (var16 < maxTileX) {
              if (var13 >= minTileZ) {
                RSTile tile = planeTiles[var16][var13];
                if (tile != null && tile.isDraw()) {
                  draw(tile, false);
                }
              }

              if (var14 < maxTileZ) {
                RSTile tile = planeTiles[var16][var14];
                if (tile != null && tile.isDraw()) {
                  draw(tile, false);
                }
              }
            }

            if (client.getTileUpdateCount() == 0) {
              // exit the loop early and go straight to "if (!isGpu && (client..."
              break outer;
            }
          }
        }
      }
    }

    if (!isGpu && (client.getOculusOrbState() != 0 && !client.getComplianceValue(
            "orbInteraction"))) {
      client.setEntitiesAtMouseCount(0);
    }
    client.setCheckClick(false);

    client.getCallbacks().drawScene();
    if (client.getDrawCallbacks() != null) {
      client.getDrawCallbacks().postDrawScene();
    }
  }

}
