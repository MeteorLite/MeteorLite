package meteor.plugins.prayerFlicker;

import com.google.inject.Provides;
import dev.hoot.api.packets.MousePackets;
import dev.hoot.api.widgets.Prayers;
import meteor.callback.ClientThread;
import meteor.config.ConfigManager;
import meteor.eventbus.Subscribe;
import meteor.input.KeyManager;
import meteor.plugins.Plugin;
import meteor.plugins.PluginDescriptor;
import meteor.util.ColorUtil;
import meteor.util.HotkeyListener;
import net.runelite.api.GameState;
import net.runelite.api.MenuAction;
import net.runelite.api.Varbits;
import net.runelite.api.events.ClientTick;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.widgets.WidgetInfo;

import javax.inject.Inject;
import java.awt.*;

@PluginDescriptor(
        name = "Prayer Flicker",
        description = "prayer flicker for quick prayers",
        enabledByDefault = false
)
public class PrayerFlickerPlugin extends Plugin
{
    public int timeout = 0;
    @Inject
    private ClientThread clientThread;

    @Inject
    private KeyManager keyManager;
    @Inject
    private PrayerFlickerConfig config;

    private int quickPrayerWidgetID = WidgetInfo.MINIMAP_QUICK_PRAYER_ORB.getPackedId();

    @Provides
    public PrayerFlickerConfig getConfig(ConfigManager configManager)
    {
        return configManager.getConfig(PrayerFlickerConfig.class);
    }

    private void togglePrayer()
    {
        MousePackets.queueClickPacket(0, 0);
        client.invokeMenuAction("", "", 1, MenuAction.CC_OP.getId(), -1, quickPrayerWidgetID);
    }

    @Override
    public void startup()
    {
        keyManager.registerKeyListener(prayerToggle, this.getClass());
    }

    @Override
    public void shutdown()
    {
        keyManager.unregisterKeyListener(prayerToggle);
        toggle = false;
        if (client.getGameState() != GameState.LOGGED_IN)
        {
            return;
        }
        clientThread.invoke(() ->
        {
            if (Prayers.isQuickPrayerEnabled())
            {
                togglePrayer();
            }
        });
    }

    boolean toggle;

    @Subscribe
    public void onGameTick(GameTick event)
    {
        if (client.getGameState() != GameState.LOGGED_IN)
        {
            return;
        }
        if (toggle)
        {
            boolean quickPrayer = client.getVar(Varbits.QUICK_PRAYER) == 1;
            if (quickPrayer)
            {
                togglePrayer();
            }
            togglePrayer();
        }
    }

    private final HotkeyListener prayerToggle = new HotkeyListener(() -> config.toggle())
    {
        @Override
        public void hotkeyPressed()
        {
            toggleFlicker();
        }
    };

    public void toggleFlicker()
    {
        toggle = !toggle;
        if (client.getGameState() != GameState.LOGGED_IN)
        {
            return;
        }
        if (!toggle)
        {
            clientThread.invoke(() ->
            {
                if (Prayers.isQuickPrayerEnabled())
                {
                    togglePrayer();
                }
            });
        }
    }

    public void toggleFlicker(boolean on)
    {
        toggle = on;
        if (client.getGameState() != GameState.LOGGED_IN)
        {
            return;
        }
        if (!toggle)
        {
            clientThread.invoke(() ->
            {
                if (Prayers.isQuickPrayerEnabled())
                {
                    togglePrayer();
                }
            });
        }
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged event)
    {
        if (event.getGameState() != GameState.LOGGED_IN)
        {
            keyManager.unregisterKeyListener(prayerToggle);
            return;
        }
        keyManager.registerKeyListener(prayerToggle, this.getClass());
    }

    @Subscribe
    public void onClientTick(ClientTick event)
    {
        var menuEntries = client.getMenuEntries();
        for (var entry : menuEntries)
        {
            if (entry.getActionParam1() == quickPrayerWidgetID)
            {
                addMenuEntry();
                return;
            }
        }
    }

    String target = ColorUtil.wrapWithColorTag("Prayer Flicker", Color.GREEN);

    private void addMenuEntry()
    {
        //Stop action takes priority via opcode
        String action;
        int opcode;
        if (toggle)
        {
            action = "Stop";
            opcode = MenuAction.UNKNOWN.getId();
        } else
        {
            action = "Start";
            opcode = MenuAction.RUNELITE.getId();
        }
        client.insertMenuItem(
                action,
                target,
                opcode,
                1,
                -1,
                -1,
                false
        );
    }

    @Subscribe
    public void onMenuOptionClicked(MenuOptionClicked event)
    {
        if ((event.getMenuAction() == MenuAction.RUNELITE || event.getMenuAction() == MenuAction.UNKNOWN) && event.getMenuTarget().equals(target))
        {
            if (event.getMenuOption().equals("Start"))
            {
                toggleFlicker(true);
            }
            if (event.getMenuOption().equals("Stop"))
            {
                toggleFlicker(false);
            }
            event.consume();
        }
    }
}
