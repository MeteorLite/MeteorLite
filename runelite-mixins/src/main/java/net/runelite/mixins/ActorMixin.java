package net.runelite.mixins;

import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.image.BufferedImage;

import net.runelite.api.*;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.ActorDeath;
import net.runelite.api.events.AnimationChanged;
import net.runelite.api.events.HealthBarUpdated;
import net.runelite.api.events.InteractingChanged;
import net.runelite.api.mixins.*;
import net.runelite.rs.api.RSActor;
import net.runelite.rs.api.RSClient;
import net.runelite.rs.api.RSHealthBar;
import net.runelite.rs.api.RSHealthBarDefinition;
import net.runelite.rs.api.RSHealthBarUpdate;
import net.runelite.rs.api.RSIterableNodeDeque;
import net.runelite.rs.api.RSNode;

@Mixin(RSActor.class)
public abstract class ActorMixin implements RSActor {

  @Shadow("client")
  private static RSClient client;

  @Inject
  @Override
  public WorldPoint getWorldLocation() {
    return WorldPoint.fromLocal(client,
        this.getPathX()[0] * Perspective.LOCAL_TILE_SIZE + Perspective.LOCAL_TILE_SIZE / 2,
        this.getPathY()[0] * Perspective.LOCAL_TILE_SIZE + Perspective.LOCAL_TILE_SIZE / 2,
        client.getPlane());
  }

  @Inject
  @Override
  public WorldArea getWorldArea()
  {
    int size = 1;
    if (this instanceof NPC)
    {
      NPCComposition composition = ((NPC)this).getComposition();
      if (composition != null && composition.getConfigs() != null)
      {
        composition = composition.transform$api();
      }
      if (composition != null)
      {
        size = composition.getSize();
      }
    }

    return new WorldArea(this.getWorldLocation(), size, size);
  }

  @Inject
  @Override
  public Actor getInteracting()
  {
    int index = getRSInteracting();
    if (index == -1 || index == 65535)
    {
      return null;
    }

    if (index < 32768)
    {
      NPC[] npcs = client.getCachedNPCs();
      return npcs[index];
    }

    index -= 32768;
    Player[] players = client.getCachedPlayers();
    return players[index];
  }

  @Inject
  @Override
  public int getHealthRatio() {
    RSIterableNodeDeque healthBars = getHealthBars();
    if (healthBars != null) {
      RSNode current = healthBars.getCurrent();
      RSNode next = current.getNext();
      if (next instanceof RSHealthBar) {
        RSHealthBar wrapper = (RSHealthBar) next;
        RSIterableNodeDeque updates = wrapper.getUpdates();

        RSNode currentUpdate = updates.getCurrent();
        RSNode nextUpdate = currentUpdate.getNext();
        if (nextUpdate instanceof RSHealthBarUpdate) {
          RSHealthBarUpdate update = (RSHealthBarUpdate) nextUpdate;
          return update.getHealthRatio();
        }
      }
    }
    return -1;
  }

  @Inject
  @Override
  public int getHealthScale() {
    RSIterableNodeDeque healthBars = getHealthBars();
    if (healthBars != null) {
      RSNode current = healthBars.getCurrent();
      RSNode next = current.getNext();
      if (next instanceof RSHealthBar) {
        RSHealthBar wrapper = (RSHealthBar) next;
        RSHealthBarDefinition definition = wrapper.getDefinition();
        return definition.getHealthScale();
      }
    }
    return -1;
  }

  @FieldHook("sequence")
  @Inject
  public void animationChanged(int idx)
  {
    AnimationChanged animationChange = new AnimationChanged();
    animationChange.setActor(this);
    client.getCallbacks().post(animationChange);
  }


  @Inject
  @Override
  public Point getCanvasTextLocation(Graphics2D graphics, String text, int zOffset)
  {
    return Perspective.getCanvasTextLocation(client, graphics, getLocalLocation(), text, zOffset);
  }

  @Inject
  @Override
  public Polygon getCanvasTilePoly()
  {
    return Perspective.getCanvasTilePoly(client, getLocalLocation());
  }

  @Inject
  @Override
  public LocalPoint getLocalLocation()
  {
    return new LocalPoint(getX(), getY());
  }

  @Inject
  @Override
  public Point getCanvasImageLocation(BufferedImage image, int zOffset)
  {
    return Perspective.getCanvasImageLocation(client, getLocalLocation(), image, zOffset);
  }

  @Inject
  @Override
  public Point getMinimapLocation()
  {
    return Perspective.localToMinimap(client, getLocalLocation());
  }

  @Inject
  private boolean dead;

  @Inject
  @Override
  public boolean isDead()
  {
    return dead;
  }

  @Inject
  @Override
  public void setDead(boolean dead)
  {
    this.dead = dead;
  }

  @Inject
  @MethodHook("addHealthBar")
  public void setCombatInfo(int combatInfoId, int gameCycle, int var3, int var4, int healthRatio, int health)
  {
    if (healthRatio == 0)
    {
      final ActorDeath event = new ActorDeath(this);
      client.getCallbacks().post(event);

      this.setDead(true);
    }

    final HealthBarUpdated event = new HealthBarUpdated(this, healthRatio);
    client.getCallbacks().post(event);
  }

  @Inject
  @Override
  public boolean isMoving()
  {
    return getPathLength() > 0;
  }

  @FieldHook("targetIndex")
  @Inject
  public void interactingChanged(int idx)
  {
    InteractingChanged interactingChanged = new InteractingChanged(this, getInteracting());
    client.getCallbacks().post(interactingChanged);
  }
}
