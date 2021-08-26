package meteor.plugins.prayerpotdrinker;

import com.google.inject.Provides;
import meteor.callback.ClientThread;
import meteor.config.ConfigManager;
import meteor.eventbus.Subscribe;
import meteor.eventbus.events.ConfigChanged;
import meteor.game.ItemManager;
import meteor.plugins.Plugin;
import meteor.plugins.PluginDescriptor;
import net.runelite.api.*;
import net.runelite.api.events.GameTick;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.api.widgets.WidgetItem;

import javax.inject.Inject;
import java.util.Random;

@PluginDescriptor(
        name = "Prayer Pot Drinker",
        description = "Automatically drink pray pots",
        tags = {"combat", "notifications", "prayer"},
        enabledByDefault = false
)
public class PrayerPotDrinkerPlugin extends Plugin
{
    @Inject
    private Client client;

    @Inject
    private ClientThread clientThread;

    @Inject
    private PrayerPotDrinkerConfig config;

    @Inject
    private ItemManager itemManager;

    private Random r = new Random();
    private int nextRestoreVal = 0;

    @Provides
    public PrayerPotDrinkerConfig getConfig(final ConfigManager configManager)
    {
        return configManager.getConfig(PrayerPotDrinkerConfig.class);
    }

    @Override
    public void startup()
    {
        nextRestoreVal = r.nextInt(config.maxPrayerLevel() - config.minPrayerLevel()) + config.minPrayerLevel();
    }

    @Override
    public void shutdown()
    {
    }

    @Subscribe
    public void onConfigChanged(ConfigChanged event)
    {
        if (!event.getGroup().equals("praypotdrinker"))
        {
            return;
        }

        nextRestoreVal = r.nextInt(config.maxPrayerLevel() - config.minPrayerLevel()) + config.minPrayerLevel();
    }

    @Subscribe
    public void onGameTick(GameTick event)
    {
        if (client.getGameState() != GameState.LOGGED_IN)
        {
            return;
        }

        try
        {
            WidgetItem restoreItem = getRestoreItem();

            if (restoreItem == null)
            {
                return;
            }

            int currentPrayerPoints = client.getBoostedSkillLevel(Skill.PRAYER);
            int prayerLevel = client.getRealSkillLevel(Skill.PRAYER);
            int boostAmount = getBoostAmount(restoreItem, prayerLevel);

            if (currentPrayerPoints + boostAmount > prayerLevel)
            {
                return;
            }

            if (currentPrayerPoints <= nextRestoreVal)
            {
                clientThread.invoke(() ->
                        client.invokeMenuAction(
                                "Drink",
                                "<col=ff9040>Potion",
                                restoreItem.getId(),
                                MenuAction.ITEM_FIRST_OPTION.getId(),
                                restoreItem.getSlot(),
                                WidgetInfo.INVENTORY.getPackedId()
                        )
                );
                nextRestoreVal = r.nextInt(config.maxPrayerLevel() - config.minPrayerLevel()) + config.minPrayerLevel();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private MenuEntry getConsumableEntry(String itemName, int itemId, int itemIndex)
    {
        return new MenuEntry("Drink", "<col=ff9040>" + itemName, itemId, MenuAction.ITEM_FIRST_OPTION.getId(), itemIndex, WidgetInfo.INVENTORY.getPackedId(), false);
    }

    public WidgetItem getRestoreItem()
    {
        WidgetItem item;

        item = PrayerRestoreType.PRAYER_POTION.getItemFromInventory(client);

        if (item != null)
        {
            return item;
        }

        item = PrayerRestoreType.SANFEW_SERUM.getItemFromInventory(client);

        if (item != null)
        {
            return item;
        }

        item = PrayerRestoreType.SUPER_RESTORE.getItemFromInventory(client);

        return item;
    }

    public int getBoostAmount(WidgetItem restoreItem, int prayerLevel)
    {
        if (PrayerRestoreType.PRAYER_POTION.containsId(restoreItem.getId()))
        {
            return 7 + (int) Math.floor(prayerLevel * .25);
        }
        else if (PrayerRestoreType.SANFEW_SERUM.containsId(restoreItem.getId()))
        {
            return 4 + (int) Math.floor(prayerLevel * (double)(3 / 10));
        }
        else if (PrayerRestoreType.SUPER_RESTORE.containsId(restoreItem.getId()))
        {
            return 8 + (int) Math.floor(prayerLevel * .25);
        }

        return 0;
    }
}
