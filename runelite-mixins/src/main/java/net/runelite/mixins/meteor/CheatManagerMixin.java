package net.runelite.mixins.meteor;

import meteor.eventbus.events.ToolbarToggled;
import net.runelite.api.mixins.Copy;
import net.runelite.api.mixins.Inject;
import net.runelite.api.mixins.Mixin;
import net.runelite.api.mixins.Replace;
import net.runelite.api.mixins.Shadow;
import net.runelite.rs.Reflection;
import net.runelite.rs.api.RSClient;

@Mixin(RSClient.class)
public abstract class CheatManagerMixin implements RSClient {

  @Shadow("client")
  public static RSClient client;

  @Inject
  @Copy("doCheat")
  public static void rs$doCheat(String s) {

  }

  @Replace("doCheat")
  public static void doCheat$api(String s) {
    boolean foundCustomCheat = false;
    if (s.equals("reflection")) {
      Reflection.printDebugMessages = !Reflection.printDebugMessages;
      client.getLogger().debug("Toggled Reflection debug messages");
      foundCustomCheat = true;
    }
    if (s.equals("occluder")) {
      client.setOccluderEnabled(!client.getOccluderEnabled());
      client.getLogger().debug("Toggled Occluder");
      foundCustomCheat = true;
    }
    if (s.equals("toolbar")) {
      client.getCallbacks().post(ToolbarToggled.INSTANCE);
      client.getLogger().debug("Toggled Toolbar");
      foundCustomCheat = true;
    }

    if (!foundCustomCheat) {
      rs$doCheat(s);
    }
  }
}
