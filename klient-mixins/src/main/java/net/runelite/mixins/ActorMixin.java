package net.runelite.mixins;

import meteor.eventbus.events.ActorDeath;
import meteor.eventbus.events.AnimationChanged;
import meteor.eventbus.events.HealthBarUpdated;
import meteor.eventbus.events.InteractingChanged;
import net.runelite.api.Point;
import net.runelite.api.*;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.mixins.*;
import net.runelite.rs.api.*;

import java.awt.*;
import java.awt.image.BufferedImage;

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

    client.getCallbacks().post(AnimationChanged.class, new AnimationChanged(this));
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

      client.getCallbacks().post(ActorDeath.class, new ActorDeath(this));

      this.setDead(true);
    }

    client.getCallbacks().post(HealthBarUpdated.class, new HealthBarUpdated(this, healthRatio));
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

    client.getCallbacks().post(InteractingChanged.class, new InteractingChanged(this, getInteracting()));
  }
}
