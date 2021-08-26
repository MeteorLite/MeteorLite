package meteor;

import com.google.inject.Module;
import com.google.inject.*;
import com.owain.chinLogin.ChinLoginPlugin;
import com.owain.chinmanager.ChinManagerPlugin;
import com.questhelper.QuestHelperPlugin;
import meteor.config.Config;
import meteor.config.ConfigGroup;
import meteor.config.ConfigManager;
import meteor.eventbus.EventBus;
import meteor.plugins.Plugin;
import meteor.plugins.PluginDependency;
import meteor.plugins.achievementdiary.DiaryRequirementsPlugin;
import meteor.plugins.agility.AgilityPlugin;
import meteor.plugins.alchemicalhydra.AlchemicalHydraPlugin;
import meteor.plugins.ammo.AmmoPlugin;
import meteor.plugins.animsmoothing.AnimationSmoothingPlugin;
import meteor.plugins.aoewarnings.AoeWarningPlugin;
import meteor.plugins.api.example.chickenkiller.ChickenKillerPlugin;
import meteor.plugins.api.example.deathevent.DeathEventPlugin;
import meteor.plugins.api.example.walking.WalkerPlugin;
import meteor.plugins.autoclicker.AutoClickerPlugin;
import meteor.plugins.autologhop.AutoLogHop;
import meteor.plugins.autorun.AutoRun;
import meteor.plugins.bank.BankPlugin;
import meteor.plugins.banktags.BankTagsPlugin;
import meteor.plugins.barbassault.BAPlugin;
import meteor.plugins.barrows.BarrowsPlugin;
import meteor.plugins.betterantidrag.BetterAntiDragPlugin;
import meteor.plugins.betterroguesden.BetterRougesDenPlugin;
import meteor.plugins.blastfurnace.BlastFurnacePlugin;
import meteor.plugins.boosts.BoostsPlugin;
import meteor.plugins.bosstimer.BossTimersPlugin;
import meteor.plugins.camera.CameraPlugin;
import meteor.plugins.cannon.CannonPlugin;
import meteor.plugins.cannonreloader.CannonReloaderPlugin;
import meteor.plugins.cerberus.CerberusPlugin;
import meteor.plugins.chat.sChatPlugin;
import meteor.plugins.chatchannel.ChatChannelPlugin;
import meteor.plugins.chatcommands.ChatCommandsPlugin;
import meteor.plugins.chatfilter.ChatFilterPlugin;
import meteor.plugins.cluescrolls.ClueScrollPlugin;
import meteor.plugins.combatlevel.CombatLevelPlugin;
import meteor.plugins.coxhelper.CoxPlugin;
import meteor.plugins.dagannothkings.DagannothKingsPlugin;
import meteor.plugins.defaultworld.DefaultWorldPlugin;
import meteor.plugins.demonicgorilla.DemonicGorillaPlugin;
import meteor.plugins.devtools.DevToolsPlugin;
import meteor.plugins.discord.DiscordPlugin;
import meteor.plugins.environmentaid.EnvironmentAidPlugin;
import meteor.plugins.fairyring.FairyRingPlugin;
import meteor.plugins.fightcave.FightCavePlugin;
import meteor.plugins.fishing.FishingPlugin;
import meteor.plugins.fps.FpsPlugin;
import meteor.plugins.gauntlet.GauntletPlugin;
import meteor.plugins.gpu.GpuPlugin;
import meteor.plugins.grotesqueguardians.GrotesqueGuardiansPlugin;
import meteor.plugins.grounditems.GroundItemsPlugin;
import meteor.plugins.groundmarkers.GroundMarkerPlugin;
import meteor.plugins.herbiboars.HerbiboarPlugin;
import meteor.plugins.hunter.HunterPlugin;
import meteor.plugins.implings.ImplingsPlugin;
import meteor.plugins.inferno.InfernoPlugin;
import meteor.plugins.interacthighlight.InteractHighlightPlugin;
import meteor.plugins.inventorygrid.InventoryGridPlugin;
import meteor.plugins.itemcharges.ItemChargePlugin;
import meteor.plugins.itemidentification.ItemIdentificationPlugin;
import meteor.plugins.itemprices.ItemPricesPlugin;
import meteor.plugins.itemstats.ItemStatPlugin;
import meteor.plugins.kalphitequeen.KQPlugin;
import meteor.plugins.keyremapping.KeyRemappingPlugin;
import meteor.plugins.kourendlibrary.KourendLibraryPlugin;
import meteor.plugins.leftclickcast.LeftClickCastPlugin;
import meteor.plugins.lizardmenshaman.LizardmanShamanPlugin;
import meteor.plugins.lowdetail.LowDetailPlugin;
import meteor.plugins.menuentrymodifier.MenuEntryModifierPlugin;
import meteor.plugins.menuentryswapper.MenuEntrySwapperPlugin;
import meteor.plugins.menuentryswapperextended.MenuEntrySwapperExtendedPlugin;
import meteor.plugins.minimap.MinimapPlugin;
import meteor.plugins.mining.MiningPlugin;
import meteor.plugins.motherlode.MotherlodePlugin;
import meteor.plugins.mousetooltips.MouseTooltipPlugin;
import meteor.plugins.mta.MTAPlugin;
import meteor.plugins.neverlog.NeverLogoutPlugin;
import meteor.plugins.nightmare.NightmarePlugin;
import meteor.plugins.npcindicators.NpcIndicatorsPlugin;
import meteor.plugins.npcstatus.NpcStatusPlugin;
import meteor.plugins.npcunaggroarea.NpcAggroAreaPlugin;
import meteor.plugins.objectindicators.ObjectIndicatorsPlugin;
import meteor.plugins.oneclick.OneClickPlugin;
import meteor.plugins.oneclick3t4g.OneClick3t4g;
import meteor.plugins.oneclickagility.OneClickAgilityPlugin;
import meteor.plugins.oneclickdropper.OneClickDropperPlugin;
import meteor.plugins.oneclickthieving.OneClickThievingPlugin;
import meteor.plugins.playerattacktimer.PlayerAttackTimerPlugin;
import meteor.plugins.playerindicators.PlayerIndicatorsPlugin;
import meteor.plugins.playerstatus.PlayerStatusPlugin;
import meteor.plugins.poh.PohPlugin;
import meteor.plugins.poison.PoisonPlugin;
import meteor.plugins.prayer.PrayerPlugin;
import meteor.plugins.prayerpotdrinker.PrayerPotDrinkerPlugin;
import meteor.plugins.puzzlesolver.PuzzleSolverPlugin;
import meteor.plugins.questlist.QuestListPlugin;
import meteor.plugins.randomevents.RandomEventPlugin;
import meteor.plugins.regenmeter.RegenMeterPlugin;
import meteor.plugins.reportbutton.ReportButtonPlugin;
import meteor.plugins.rsnhider.RsnHiderPlugin;
import meteor.plugins.runecraft.RunecraftPlugin;
import meteor.plugins.runenergy.RunEnergyPlugin;
import meteor.plugins.runepouch.RunepouchPlugin;
import meteor.plugins.slayer.SlayerPlugin;
import meteor.plugins.smithing.SmithingPlugin;
import meteor.plugins.socket.SocketPlugin;
import meteor.plugins.socketDPS.SocketDpsCounterPlugin;
import meteor.plugins.socketbosstimer.SocketBossTimersPlugin;
import meteor.plugins.socketdefence.SocketDefencePlugin;
import meteor.plugins.sockethealing.SocketHealingPlugin;
import meteor.plugins.socketicedemon.SocketIceDemonPlugin;
import meteor.plugins.socketplanks.SocketPlanksPlugin;
import meteor.plugins.socketthieving.SocketThievingPlugin;
import meteor.plugins.sotetseg.SotetsegPlugin;
import meteor.plugins.specialcounterextended.SpecialCounterExtendedPlugin;
import meteor.plugins.statusbars.StatusBarsPlugin;
import meteor.plugins.stretchedmode.StretchedModePlugin;
import meteor.plugins.tearsofguthix.TearsOfGuthixPlugin;
import meteor.plugins.theatre.TheatrePlugin;
import meteor.plugins.ticktimers.TickTimersPlugin;
import meteor.plugins.tileindicators.TileIndicatorsPlugin;
import meteor.plugins.timers.TimersPlugin;
import meteor.plugins.timestamp.ChatTimestampPlugin;
import meteor.plugins.tithefarm.TitheFarmPlugin;
import meteor.plugins.vetion.VetionPlugin;
import meteor.plugins.void3tFishing.Void3tFishingPlugin;
import meteor.plugins.void3tteaks.Void3tTeaksPlugin;
import meteor.plugins.vorkath.VorkathPlugin;
import meteor.plugins.woodcutting.WoodcuttingPlugin;
import meteor.plugins.worldmap.WorldMapPlugin;
import meteor.plugins.worldmapwalker.WorldMapWalkerPlugin;
import meteor.plugins.xpdrop.XpDropPlugin;
import meteor.plugins.xpglobes.XpGlobesPlugin;
import meteor.plugins.xptracker.XpTrackerPlugin;
import meteor.plugins.zulrah.ZulrahPlugin;

import java.util.ArrayList;
import java.util.List;

public class PluginManager {

  @Inject
  private EventBus eventBus;

  @Inject
  private ConfigManager configManager;

  @Inject
  private MeteorLiteClientModule meteorLiteClientModule;

  PluginManager() {
  }

  public static List<Plugin> plugins = new ArrayList<>();

  private void initPlugins() {
    plugins.add(new AgilityPlugin());
    plugins.add(new AlchemicalHydraPlugin());
    plugins.add(new AmmoPlugin());
    plugins.add(new AnimationSmoothingPlugin());
    plugins.add(new AoeWarningPlugin());
    plugins.add(new BankPlugin());
    plugins.add(new BankTagsPlugin());
    plugins.add(new BAPlugin());
    plugins.add(new BarrowsPlugin());
    plugins.add(new BetterAntiDragPlugin());
    plugins.add(new BetterRougesDenPlugin());
    plugins.add(new BlastFurnacePlugin());
    plugins.add(new BoostsPlugin());
    plugins.add(new BossTimersPlugin());
    plugins.add(new TickTimersPlugin());
    plugins.add(new CameraPlugin());
    plugins.add(new CannonPlugin());
    plugins.add(new CannonReloaderPlugin());
    plugins.add(new CerberusPlugin());
    plugins.add(new ChatChannelPlugin());
    plugins.add(new ChatCommandsPlugin());
    plugins.add(new ChatFilterPlugin());
    plugins.add(new ChatTimestampPlugin());
    plugins.add(new ChickenKillerPlugin());
    plugins.add(new ChinManagerPlugin());
    plugins.add(new ChinLoginPlugin());
    plugins.add(new ClueScrollPlugin());
    plugins.add(new CombatLevelPlugin());
    plugins.add(new CoxPlugin());
    plugins.add(new DagannothKingsPlugin());
    plugins.add(new DeathEventPlugin());
    plugins.add(new DefaultWorldPlugin());
    plugins.add(new DemonicGorillaPlugin());
    plugins.add(new DevToolsPlugin());
    plugins.add(new DiaryRequirementsPlugin());
    plugins.add(new DiscordPlugin());
    plugins.add(new EnvironmentAidPlugin());
    plugins.add(new FairyRingPlugin());
    plugins.add(new FightCavePlugin());
    plugins.add(new FishingPlugin());
    plugins.add(new FpsPlugin());
    plugins.add(new GauntletPlugin());
    plugins.add(new GpuPlugin());
    plugins.add(new GrotesqueGuardiansPlugin());
    plugins.add(new GroundItemsPlugin());
    plugins.add(new GroundMarkerPlugin());
    plugins.add(new HerbiboarPlugin());
    plugins.add(new HunterPlugin());
    plugins.add(new ImplingsPlugin());
    plugins.add(new InfernoPlugin());
    plugins.add(new InteractHighlightPlugin());
    plugins.add(new InventoryGridPlugin());
    plugins.add(new ItemChargePlugin());
    plugins.add(new ItemPricesPlugin());
    plugins.add(new ItemStatPlugin());
    plugins.add(new ItemIdentificationPlugin());
    plugins.add(new KQPlugin());
    plugins.add(new KeyRemappingPlugin());
    plugins.add(new KourendLibraryPlugin());
    plugins.add(new LeftClickCastPlugin());
    plugins.add(new LizardmanShamanPlugin());
    plugins.add(new LowDetailPlugin());
    plugins.add(new MenuEntryModifierPlugin());
    plugins.add(new MenuEntrySwapperPlugin());
    plugins.add(new MenuEntrySwapperExtendedPlugin());
    plugins.add(new MinimapPlugin());
    plugins.add(new MiningPlugin());
    plugins.add(new MotherlodePlugin());
    plugins.add(new MouseTooltipPlugin());
    plugins.add(new MTAPlugin());
    plugins.add(new NeverLogoutPlugin());
    plugins.add(new NightmarePlugin());
    plugins.add(new NpcAggroAreaPlugin());
    plugins.add(new NpcIndicatorsPlugin());
    plugins.add(new NpcStatusPlugin());
    plugins.add(new ObjectIndicatorsPlugin());
    plugins.add(new OneClickPlugin());
    plugins.add(new OneClick3t4g());
    plugins.add(new OneClickAgilityPlugin());
    plugins.add(new OneClickDropperPlugin());
    plugins.add(new OneClickThievingPlugin());
    plugins.add(new PlayerAttackTimerPlugin());
    plugins.add(new PlayerIndicatorsPlugin());
    plugins.add(new PohPlugin());
    plugins.add(new PoisonPlugin());
    plugins.add(new PrayerPlugin());
    plugins.add(new PrayerPotDrinkerPlugin());
    plugins.add(new PuzzleSolverPlugin());
    plugins.add(new QuestListPlugin());
    plugins.add(new QuestHelperPlugin());
    plugins.add(new RandomEventPlugin());
    plugins.add(new RegenMeterPlugin());
    plugins.add(new ReportButtonPlugin());
    plugins.add(new RsnHiderPlugin());
    plugins.add(new RunEnergyPlugin());
    plugins.add(new RunepouchPlugin());
    plugins.add(new RunecraftPlugin());
    plugins.add(new SlayerPlugin());
    plugins.add(new SmithingPlugin());
    plugins.add(new SocketPlugin());
    plugins.add(new SocketBossTimersPlugin());
    plugins.add(new sChatPlugin());
    plugins.add(new SocketDefencePlugin());
    plugins.add(new SocketDpsCounterPlugin());
    plugins.add(new SocketHealingPlugin());
    plugins.add(new SocketIceDemonPlugin());
    plugins.add(new SocketPlanksPlugin());
    plugins.add(new PlayerStatusPlugin());
    plugins.add(new SotetsegPlugin());
    plugins.add(new SpecialCounterExtendedPlugin());
    plugins.add(new SocketThievingPlugin());
    plugins.add(new AutoClickerPlugin());
    plugins.add(new AutoLogHop());
    plugins.add(new AutoRun());
    plugins.add(new StatusBarsPlugin());
    plugins.add(new StretchedModePlugin());
    plugins.add(new TheatrePlugin());
    plugins.add(new TileIndicatorsPlugin());
    plugins.add(new TimersPlugin());
    plugins.add(new TitheFarmPlugin());
    plugins.add(new TearsOfGuthixPlugin());
    plugins.add(new VetionPlugin());
    plugins.add(new Void3tFishingPlugin());
    plugins.add(new Void3tTeaksPlugin());
    plugins.add(new VorkathPlugin());
    plugins.add(new WalkerPlugin());
    plugins.add(new WoodcuttingPlugin());
    plugins.add(new WorldMapPlugin());
    plugins.add(new WorldMapWalkerPlugin());
    plugins.add(new XpDropPlugin());
    plugins.add(new XpTrackerPlugin());
    plugins.add(new XpGlobesPlugin());
    plugins.add(new ZulrahPlugin());
  }

  public void startInternalPlugins() {
    initPlugins();
    for (Plugin plugin : plugins) {
      Injector parent = meteorLiteClientModule.instanceInjector;
      List<Module> depModules = new ArrayList<>();
      if (plugin.getClass().getAnnotation(PluginDependency.class) != null) {
        Class<? extends Plugin> depClass = plugin.getClass().getAnnotation(PluginDependency.class).value();
        Module depModule = (Binder binder) ->
        {
          try {
            Plugin depInstance = depClass.getDeclaredConstructor().newInstance();
            binder.bind((Class<Plugin>) depInstance.getClass()).toInstance(depInstance);
            binder.install(depInstance);
          } catch (Exception e) {
            e.printStackTrace();
          }
        };
        depModules.add(depModule);
        parent = parent.createChildInjector(depModules);
      }

      Module pluginModule = (Binder binder) ->
      {
        // Since the plugin itself is a module, it won't bind itself, so we'll bind it here
        binder.bind((Class<Plugin>) plugin.getClass()).toInstance(plugin);
        binder.install(plugin);
      };
      Injector pluginInjector = parent.createChildInjector(pluginModule);
      pluginInjector.injectMembers(plugin);
      plugin.setInjector(pluginInjector);
      Config finalConfig = null;
      for (Key<?> key : plugin.getInjector().getBindings().keySet())
      {
        Class<?> type = key.getTypeLiteral().getRawType();
        if (Config.class.isAssignableFrom(type))
        {
          Config config = (Config) plugin.getInjector().getInstance(key);
          finalConfig = config;
          configManager.setDefaultConfiguration(plugin, config, false);
        }
      }
      if (finalConfig != null)
      if (Boolean.parseBoolean(configManager.getConfiguration(finalConfig.getClass().getInterfaces()[0].getAnnotation(ConfigGroup.class).value(), "pluginEnabled")))
        plugin.toggle();
    }
  }

  public static<T extends Plugin> T getInstance(Class<? extends Plugin> type)
  {
    for (Plugin p : PluginManager.plugins)
    {
      if (type.isInstance(p))
      {
        return (T) p;
      }
    }
    return null;
  }

  public static Plugin getInstance(String name)
  {
    for (Plugin p : PluginManager.plugins)
      if (p.getName().equals(name))
        return p;
    return null;
  }
}
