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
import net.runelite.api.hooks.DrawCallbacks;
import net.runelite.api.mixins.*;
import net.runelite.rs.api.*;

import java.awt.*;
import java.awt.image.BufferedImage;

@Mixin(RSGameEngine.class)
public abstract class GameEngineMixin implements RSGameEngine {

  @Shadow("client")
  private static RSClient client;

  @Inject
  private Thread thread;

  @Inject
  @Override
  public Thread getClientThread() {
    return thread;
  }

  @Inject
  @Override
  public boolean isClientThread() {
    return thread == Thread.currentThread();
  }

  @Inject
  @MethodHook("run")
  public void onRun() {
    thread = Thread.currentThread();
    thread.setName("Client");
  }
}
