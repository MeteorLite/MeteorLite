package meteor.plugins.PvPKeys;

import com.google.inject.Provides;
import meteor.config.ConfigManager;
import meteor.eventbus.Subscribe;
import meteor.input.KeyManager;
import meteor.plugins.Plugin;
import meteor.plugins.PluginDescriptor;
import dev.hoot.api.items.Inventory;
import dev.hoot.api.magic.Ancient;
import dev.hoot.api.magic.Magic;
import dev.hoot.api.widgets.Prayers;
import dev.hoot.api.widgets.Tab;
import dev.hoot.api.widgets.Tabs;
import meteor.util.HotkeyListener;
import net.runelite.api.*;
import net.runelite.api.events.InteractingChanged;
import javax.inject.Inject;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Stream;

@PluginDescriptor(
        name = "PvPKeys",
        description = "hotkeys for pvp",
        enabledByDefault = false
)
public class PvPKeys extends Plugin {
    Random r = new Random();
    Actor target = null;
    @Inject
    private KeyManager keyManager;
    @Inject
    private PvPKeysConfig config;
    public ExecutorService executor;
    @Provides
    public PvPKeysConfig getConfig(ConfigManager manager)
    {
        return manager.getConfig(PvPKeysConfig.class);
    }
    @Override
    public void startup()
    {
        executor = Executors.newSingleThreadExecutor();
        if (client.getGameState() == GameState.LOGGED_IN)
        {
            keyManager.registerKeyListener(inventory,this.getClass());
            keyManager.registerKeyListener(spectab,this.getClass());
            keyManager.registerKeyListener(praytab,this.getClass());
            keyManager.registerKeyListener(spelltab,this.getClass());
            keyManager.registerKeyListener(geartab,this.getClass());
            keyManager.registerKeyListener(magepray,this.getClass());
            keyManager.registerKeyListener(rangepray,this.getClass());
            keyManager.registerKeyListener(meleepray,this.getClass());
            keyManager.registerKeyListener(smite,this.getClass());
            keyManager.registerKeyListener(brew,this.getClass());
            keyManager.registerKeyListener(restore,this.getClass());
            keyManager.registerKeyListener(quickpray,this.getClass());
            keyManager.registerKeyListener(magegear,this.getClass());
            keyManager.registerKeyListener(rangegear,this.getClass());
            keyManager.registerKeyListener(meleegear,this.getClass());
            keyManager.registerKeyListener(icebarrage,this.getClass());
            keyManager.registerKeyListener(iceblitz,this.getClass());
            keyManager.registerKeyListener(bloodblitz,this.getClass());
            keyManager.registerKeyListener(augury,this.getClass());
            keyManager.registerKeyListener(rigour,this.getClass());
            keyManager.registerKeyListener(piety,this.getClass());
            keyManager.registerKeyListener(lasttarget,this.getClass());
        }
    }
    public void shutdown()
    {
        executor.shutdown();
        if (client.getGameState() == GameState.LOGGED_IN)
        {
            keyManager.unregisterKeyListener(inventory);
            keyManager.unregisterKeyListener(spectab);
            keyManager.unregisterKeyListener(praytab);
            keyManager.unregisterKeyListener(spelltab);
            keyManager.unregisterKeyListener(geartab);
            keyManager.unregisterKeyListener(magepray);
            keyManager.unregisterKeyListener(rangepray);
            keyManager.unregisterKeyListener(meleepray);
            keyManager.unregisterKeyListener(smite);
            keyManager.unregisterKeyListener(brew);
            keyManager.unregisterKeyListener(restore);
            keyManager.unregisterKeyListener(quickpray);
            keyManager.unregisterKeyListener(magegear);
            keyManager.unregisterKeyListener(rangegear);
            keyManager.unregisterKeyListener(meleegear);
            keyManager.unregisterKeyListener(icebarrage);
            keyManager.unregisterKeyListener(iceblitz);
            keyManager.unregisterKeyListener(bloodblitz);
            keyManager.unregisterKeyListener(augury);
            keyManager.unregisterKeyListener(rigour);
            keyManager.unregisterKeyListener(piety);
            keyManager.unregisterKeyListener(lasttarget);
        }
    }
    private final HotkeyListener inventory = new HotkeyListener(() -> config.Inventory())
    {
        @Override
        public void hotkeyPressed()
        {
            Tabs.open(Tab.INVENTORY);
        }
    };
    private final HotkeyListener spectab = new HotkeyListener(() -> config.Spectab())
    {
        @Override
        public void hotkeyPressed()
        {
            Tabs.open(Tab.COMBAT);
        }
    };
    private final HotkeyListener praytab = new HotkeyListener(() -> config.Prayertab())
    {
        @Override
        public void hotkeyPressed()
        {
            Tabs.open(Tab.PRAYER);
        }
    };
    private final HotkeyListener spelltab = new HotkeyListener(() -> config.Spellstab())
    {
        @Override
        public void hotkeyPressed()
        {
            Tabs.open(Tab.MAGIC);
        }
    };
    private final HotkeyListener geartab = new HotkeyListener(() -> config.Gearstab())
    {
        @Override
        public void hotkeyPressed()
        {
            Tabs.open(Tab.EQUIPMENT);
        }
    };
    private final HotkeyListener magepray = new HotkeyListener(() -> config.Magepray())
    {
        @Override
        public void hotkeyPressed()
        {
            Prayers.toggle(Prayer.PROTECT_FROM_MAGIC);
        }
    };
    private final HotkeyListener rangepray = new HotkeyListener(() -> config.Rangepray())
    {
        @Override
        public void hotkeyPressed()
        {
            Prayers.toggle(Prayer.PROTECT_FROM_MISSILES);
        }
    };
    private final HotkeyListener meleepray = new HotkeyListener(() -> config.Meleepray())
    {
        @Override
        public void hotkeyPressed()
        {
            Prayers.toggle(Prayer.PROTECT_FROM_MELEE);
        }
    };
    private final HotkeyListener smite = new HotkeyListener(() -> config.Smite())
    {
        @Override
        public void hotkeyPressed()
        {
            Prayers.toggle(Prayer.SMITE);
        }
    };
    private final HotkeyListener brew = new HotkeyListener(() -> config.Brew())
    {
        @Override
        public void hotkeyPressed()
        {
            if(Inventory.getFirst(6691,6689,6687,6685)!=null) {
                Inventory.getFirst(6691, 6689, 6687, 6685).interact(0);
            }
        }
    };
    private final HotkeyListener restore = new HotkeyListener(() -> config.Restore())
    {
        @Override
        public void hotkeyPressed()
        {
            Inventory.getAll(3030,3028,3026,3024).get(0).interact(0);
            /*if(Inventory.getFirst(3030)!=null) {
                Inventory.getFirst(3030).interact(0);
            }else if(Inventory.getFirst(3028)!=null) {
                Inventory.getFirst(3028).interact(0);
            }else if(Inventory.getFirst(3026)!=null) {
                Inventory.getFirst(3026).interact(0);
            } else if(Inventory.getFirst(3024)!=null) {
                Inventory.getFirst(3024).interact(0);
            }*/
        }

    };
    private final HotkeyListener quickpray = new HotkeyListener(() -> config.Quickpray())
    {
        @Override
        public void hotkeyPressed()
        {
            Prayers.toggleQuickPrayer(!Prayers.isQuickPrayerEnabled());
        }
    };
    private final HotkeyListener magegear = new HotkeyListener(() -> config.Magegear())
    {
        @Override
        public void hotkeyPressed()
        {
            Integer[] mageGear = Stream.of(config.MageIDs().split(",")).map(Integer::valueOf).toArray(Integer[]::new);
            executor.submit(() ->
            {
                for (Integer integer : mageGear) {
                    if(Inventory.getFirst(integer)!=null){
                        Inventory.getFirst(integer).interact(1);
                        int delay;
                        do {
                            double val = r.nextGaussian() * 65 + 15;
                            delay = (int) Math.round(val);
                        } while (delay <= 50);
                        try {
                            Thread.sleep(delay);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
    };
    private final HotkeyListener meleegear = new HotkeyListener(() -> config.Meleegear())
    {
        @Override
        public void hotkeyPressed()
        {
            Integer[] meleegear = Stream.of(config.MeleeIDs().split(",")).map(Integer::valueOf).toArray(Integer[]::new);
            executor.submit(() ->
            {
                for (Integer integer : meleegear) {
                    if(Inventory.getFirst(integer)!=null){
                        Inventory.getFirst(integer).interact(1);
                        int delay;
                        do {
                            double val = r.nextGaussian() * 65 + 15;
                            delay = (int) Math.round(val);
                        } while (delay <= 50);
                        try {
                            Thread.sleep(delay);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
    };
    private final HotkeyListener rangegear = new HotkeyListener(() -> config.Rangegear())
    {
        @Override
        public void hotkeyPressed()
        {
            Integer[] rangegear = Stream.of(config.RangeIDs().split(",")).map(Integer::valueOf).toArray(Integer[]::new);
            executor.submit(() ->
            {
                for (Integer integer : rangegear) {
                    if(Inventory.getFirst(integer)!=null){
                        Inventory.getFirst(integer).interact(1);
                        int delay;
                        do {
                            double val = r.nextGaussian() * 65 + 15;
                            delay = (int) Math.round(val);
                        } while (delay <= 50);
                        try {
                            Thread.sleep(delay);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
    };
    private final HotkeyListener icebarrage = new HotkeyListener(() -> config.Icebarrage())
    {
        @Override
        public void hotkeyPressed()
        {
            Magic.selectSpell(Ancient.ICE_BARRAGE);
            if(target!=null) {
                target.interact(0);
            }
        }
    };
    private final HotkeyListener iceblitz = new HotkeyListener(() -> config.Iceblitz())
    {
        @Override
        public void hotkeyPressed()
        {
            Magic.selectSpell(Ancient.ICE_BLITZ);
            if(target!=null) {
                target.interact(0);
            }
        }
    };
    private final HotkeyListener bloodblitz = new HotkeyListener(() -> config.Bloodblitz())
    {
        @Override
        public void hotkeyPressed()
        {
            Magic.selectSpell(Ancient.BLOOD_BLITZ);
            if(target!=null) {
                target.interact(0);
            }
        }
    };
    private final HotkeyListener augury = new HotkeyListener(() -> config.Augury())
    {
        @Override
        public void hotkeyPressed()
        {
            Prayers.toggle(Prayer.AUGURY);
        }
    };
    private final HotkeyListener rigour = new HotkeyListener(() -> config.Rigour())
    {
        @Override
        public void hotkeyPressed()
        {
            Prayers.toggle(Prayer.RIGOUR);
        }
    };
    private final HotkeyListener piety = new HotkeyListener(() -> config.Piety())
    {
        @Override
        public void hotkeyPressed()
        {
            Prayers.toggle(Prayer.PIETY);
        }
    };
    private final HotkeyListener lasttarget = new HotkeyListener(() -> config.Lasttarget())
    {
        @Override
        public void hotkeyPressed()
        {
            if(lasttarget!=null){
                target.interact(0);
            }
           }
    };
    @Subscribe
    public void onInteractingChanged(InteractingChanged event){
        if(event.getSource()==client.getLocalPlayer()){
            if(event.getTarget()!=null&&event.getTarget() instanceof Actor) {
                target = event.getTarget();
            }
        }
    }
}
