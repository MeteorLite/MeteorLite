package meteor;

import com.google.inject.Binder;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Module;
import com.owain.chinLogin.ChinLoginPlugin;
import com.owain.chinmanager.ChinManagerPlugin;
import com.questhelper.QuestHelperPlugin;
import java.io.File;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.jar.JarFile;

import meteor.config.Config;
import meteor.config.ConfigGroup;
import meteor.config.ConfigManager;
import meteor.eventbus.EventBus;
import meteor.eventbus.Subscribe;
import meteor.plugins.NightmareHelper.NightmareHelper;
import meteor.plugins.Plugin;
import meteor.plugins.PluginDependency;
import meteor.plugins.PvPKeys.PvPKeys;
import meteor.plugins.achievementdiary.DiaryRequirementsPlugin;
import meteor.plugins.agility.AgilityPlugin;
import meteor.plugins.alchemicalhydra.AlchemicalHydraPlugin;
import meteor.plugins.ammo.AmmoPlugin;
import meteor.plugins.animsmoothing.AnimationSmoothingPlugin;
import meteor.plugins.aoewarnings.AoeWarningPlugin;
import meteor.plugins.api.example.deathevent.DeathEventPlugin;
import meteor.plugins.hootoneclick.HootOneClickPlugin;
import meteor.plugins.autoclicker.AutoClickerPlugin;
import meteor.plugins.autologhop.AutoLogHop;
import meteor.plugins.autologin.AutoLoginPlugin;
import meteor.plugins.autorun.AutoRun;
import meteor.plugins.bank.BankPlugin;
import meteor.plugins.banktags.BankTagsPlugin;
import meteor.plugins.barbassault.BAPlugin;
import meteor.plugins.barrows.BarrowsPlugin;
import meteor.plugins.betterantidrag.BetterAntiDragPlugin;
import meteor.plugins.betterroguesden.BetterRougesDenPlugin;
import meteor.plugins.blackjack.BlackjackPlugin;
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
import meteor.plugins.chocogrinder.ChocoGrinder;
import meteor.plugins.cluescrolls.ClueScrollPlugin;
import meteor.plugins.combatlevel.CombatLevelPlugin;
import meteor.plugins.continueclicker.ContinueClickerPlugin;
import meteor.plugins.cooking.CookingPlugin;
import meteor.plugins.coxhelper.CoxPlugin;
import meteor.plugins.coxraidscouter.coxraidscouter;
import meteor.plugins.dagannothkings.DagannothKingsPlugin;
import meteor.plugins.defaultworld.DefaultWorldPlugin;
import meteor.plugins.demonicgorilla.DemonicGorillaPlugin;
import meteor.plugins.devtools.DevToolsPlugin;
import meteor.plugins.discord.DiscordPlugin;
import meteor.plugins.entityhider.EntityHiderPlugin;
import meteor.plugins.entityhiderextended.EntityHiderExtendedPlugin;
import meteor.plugins.entityinspector.EntityInspectorPlugin;
import meteor.plugins.environmentaid.EnvironmentAidPlugin;
import meteor.plugins.examine.ExaminePlugin;
import meteor.plugins.fairyring.FairyRingPlugin;
import meteor.plugins.fightcave.FightCavePlugin;
import meteor.plugins.fishing.FishingPlugin;
import meteor.plugins.fps.FpsPlugin;
import meteor.plugins.gauntlet.GauntletPlugin;
import meteor.plugins.gearhelper.GearHelperPlugin;
import meteor.plugins.gpu.GpuPlugin;
import meteor.plugins.grotesqueguardians.GrotesqueGuardiansPlugin;
import meteor.plugins.grounditems.GroundItemsPlugin;
import meteor.plugins.groundmarkers.sGroundMarkerPlugin;
import meteor.plugins.herbiboars.HerbiboarPlugin;
import meteor.plugins.hootagility.HootAgilityPlugin;
import meteor.plugins.hootfighter.HootFighterPlugin;
import meteor.plugins.hootherblore.HootHerblorePlugin;
import meteor.plugins.hunter.HunterPlugin;
import meteor.plugins.implings.ImplingsPlugin;
import meteor.plugins.inferno.InfernoPlugin;
import meteor.plugins.interacthighlight.InteractHighlightPlugin;
import meteor.plugins.inventorygrid.InventoryGridPlugin;
import meteor.plugins.inventorytags.InventoryTagsPlugin;
import meteor.plugins.itemcharges.ItemChargePlugin;
import meteor.plugins.itemidentification.ItemIdentificationPlugin;
import meteor.plugins.itemprices.ItemPricesPlugin;
import meteor.plugins.itemstats.ItemStatPlugin;
import meteor.plugins.kalphitequeen.KQPlugin;
import meteor.plugins.keyremapping.KeyRemappingPlugin;
import meteor.plugins.kourendlibrary.KourendLibraryPlugin;
import meteor.plugins.leftclickcast.LeftClickCastPlugin;
import meteor.plugins.lizardmenshaman.LizardmanShamanPlugin;
import meteor.plugins.lowcpu.LowCpuPlugin;
import meteor.plugins.lowdetail.LowDetailPlugin;
import meteor.plugins.menuentrymodifier.MenuEntryModifierPlugin;
import meteor.plugins.menuentryswapper.MenuEntrySwapperPlugin;
import meteor.plugins.menuentryswapperextended.MenuEntrySwapperExtendedPlugin;
import meteor.plugins.meteorlite.MeteorLitePlugin;
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
import meteor.plugins.objectmarkers.sObjectIndicatorsPlugin;
import meteor.plugins.oneclick.OneClickPlugin;
import meteor.plugins.oneclick3t4g.OneClick3t4g;
import meteor.plugins.oneclickagility.OneClickAgilityPlugin;
import meteor.plugins.oneclickboner.OneClickBonerPlugin;
import meteor.plugins.oneclickcustom.OneClickCustomPlugin;
import meteor.plugins.oneclickdropper.OneClickDropperPlugin;
import meteor.plugins.oneclickthieving.OneClickThievingPlugin;
import meteor.plugins.opponentinfo.OpponentInfoPlugin;
import meteor.plugins.playerattacktimer.PlayerAttackTimerPlugin;
import meteor.plugins.playerindicators.PlayerIndicatorsPlugin;
import meteor.plugins.playerindicatorsextended.PlayerIndicatorsExtendedPlugin;
import meteor.plugins.playerstatus.PlayerStatusPlugin;
import meteor.plugins.poh.PohPlugin;
import meteor.plugins.poison.PoisonPlugin;
import meteor.plugins.prayer.PrayerPlugin;
import meteor.plugins.prayerpotdrinker.PrayerPotDrinkerPlugin;
import meteor.plugins.puzzlesolver.PuzzleSolverPlugin;
import meteor.plugins.questlist.QuestListPlugin;
import meteor.plugins.raids.RaidsPlugin;
import meteor.plugins.randomevents.RandomEventPlugin;
import meteor.plugins.regenmeter.RegenMeterPlugin;
import meteor.plugins.reportbutton.ReportButtonPlugin;
import meteor.plugins.resourcepacks.ResourcePacksPlugin;
import meteor.plugins.rsnhider.RsnHiderPlugin;
import meteor.plugins.runecraft.RunecraftPlugin;
import meteor.plugins.runenergy.RunEnergyPlugin;
import meteor.plugins.runepouch.RunepouchPlugin;
import meteor.plugins.slayer.SlayerPlugin;
import meteor.plugins.smelting.SmeltingPlugin;
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
import meteor.plugins.specbar.SpecBarPlugin;
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
import meteor.plugins.vorkath.VorkathPlugin;
import meteor.plugins.woodcutting.WoodcuttingPlugin;
import meteor.plugins.worldmap.WorldMapPlugin;
import meteor.plugins.worldmapwalker.WorldMapWalkerPlugin;
import meteor.plugins.xpdrop.XpDropPlugin;
import meteor.plugins.xpglobes.XpGlobesPlugin;
import meteor.plugins.xptracker.XpTrackerPlugin;
import meteor.plugins.zulrah.ZulrahPlugin;
import meteor.ui.components.Category;
import meteor.ui.controllers.PluginListUI;
import net.runelite.api.GameState;
import net.runelite.api.events.GameStateChanged;
import org.sponge.util.Logger;
import rs117.hd.GpuHDPlugin;

public class PluginManager {
	private static final Logger logger = new Logger("PluginManager");
	public static final File EXTERNALS_DIR = new File(MeteorLiteClientLauncher.METEOR_DIR, "externals");
	public static HashMap<Class<? extends Plugin>, Class<? extends Plugin>> conflicts = new HashMap<>();

	@Inject
	private EventBus eventBus;

	@Inject
	private ConfigManager configManager;

	@Inject
	private MeteorLiteClientModule meteorLiteClientModule;

	PluginManager() {
		if (!EXTERNALS_DIR.exists()) {
			EXTERNALS_DIR.mkdirs();
		}
	}

	public static List<Plugin> plugins = new ArrayList<>();

  private void initPlugins() {
		// Leave at the top pls, these are not regular plugins
	  plugins.add(new MeteorLitePlugin());
		plugins.add(new AgilityPlugin());
		plugins.add(new AlchemicalHydraPlugin());
		plugins.add(new AmmoPlugin());
		plugins.add(new AnimationSmoothingPlugin());
		plugins.add(new AoeWarningPlugin());
		plugins.add(new AutoLoginPlugin());
		plugins.add(new BankPlugin());
		plugins.add(new BankTagsPlugin());
		plugins.add(new BAPlugin());
		plugins.add(new BarrowsPlugin());
		plugins.add(new BetterAntiDragPlugin());
		plugins.add(new BetterRougesDenPlugin());
		plugins.add(new BlackjackPlugin());
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
		plugins.add(new ChinManagerPlugin());
		plugins.add(new ChinLoginPlugin());
		plugins.add(new ChocoGrinder());
		plugins.add(new ClueScrollPlugin());
		plugins.add(new CombatLevelPlugin());
		plugins.add(new ContinueClickerPlugin());
		plugins.add(new CookingPlugin());
		plugins.add(new coxraidscouter());
		plugins.add(new RaidsPlugin());
		plugins.add(new CoxPlugin());
		plugins.add(new DagannothKingsPlugin());
		plugins.add(new DeathEventPlugin());
		plugins.add(new DefaultWorldPlugin());
		plugins.add(new DemonicGorillaPlugin());
		plugins.add(new DevToolsPlugin());
		plugins.add(new DiaryRequirementsPlugin());
		plugins.add(new DiscordPlugin());
		plugins.add(new EntityHiderPlugin());
		plugins.add(new EntityHiderExtendedPlugin());
		plugins.add(new EntityInspectorPlugin());
		plugins.add(new EnvironmentAidPlugin());
		plugins.add(new ExaminePlugin());
		plugins.add(new FairyRingPlugin());
		plugins.add(new FightCavePlugin());
		plugins.add(new FishingPlugin());
		plugins.add(new FpsPlugin());
		plugins.add(new GauntletPlugin());
		plugins.add(new GearHelperPlugin());
		plugins.add(new GpuPlugin());
		plugins.add(new GpuHDPlugin());
		plugins.add(new GrotesqueGuardiansPlugin());
		plugins.add(new GroundItemsPlugin());
		plugins.add(new sGroundMarkerPlugin());
		plugins.add(new HerbiboarPlugin());
		plugins.add(new HootAgilityPlugin());
		plugins.add(new HootFighterPlugin());
		plugins.add(new HootHerblorePlugin());
		plugins.add(new HootOneClickPlugin());
		plugins.add(new HunterPlugin());
		plugins.add(new ImplingsPlugin());
		plugins.add(new InfernoPlugin());
		plugins.add(new InteractHighlightPlugin());
		plugins.add(new InventoryGridPlugin());
		plugins.add(new InventoryTagsPlugin());
		plugins.add(new ItemChargePlugin());
		plugins.add(new ItemIdentificationPlugin());
		plugins.add(new ItemPricesPlugin());
		plugins.add(new ItemStatPlugin());
		plugins.add(new KQPlugin());
		plugins.add(new KeyRemappingPlugin());
		plugins.add(new KourendLibraryPlugin());
		plugins.add(new LeftClickCastPlugin());
		plugins.add(new LizardmanShamanPlugin());
		plugins.add(new LowCpuPlugin());
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
	  plugins.add(new NightmareHelper());
		plugins.add(new NightmarePlugin());
		plugins.add(new NpcAggroAreaPlugin());
		plugins.add(new NpcIndicatorsPlugin());
		plugins.add(new NpcStatusPlugin());
		plugins.add(new sObjectIndicatorsPlugin());
		plugins.add(new OneClickPlugin());
		plugins.add(new OneClick3t4g());
		plugins.add(new OneClickAgilityPlugin());
		plugins.add(new OneClickBonerPlugin());
		plugins.add(new OneClickCustomPlugin());
		plugins.add(new OneClickDropperPlugin());
		plugins.add(new OneClickThievingPlugin());
		plugins.add(new OpponentInfoPlugin());
		plugins.add(new PlayerAttackTimerPlugin());
		plugins.add(new PlayerIndicatorsPlugin());
		plugins.add(new PohPlugin());
		plugins.add(new PoisonPlugin());
		plugins.add(new PrayerPlugin());
		plugins.add(new PrayerPotDrinkerPlugin());
		plugins.add(new PvPKeys());
		plugins.add(new PuzzleSolverPlugin());
		plugins.add(new QuestListPlugin());
		plugins.add(new QuestHelperPlugin());
		plugins.add(new RandomEventPlugin());
		plugins.add(new ResourcePacksPlugin());
		plugins.add(new RegenMeterPlugin());
		plugins.add(new ReportButtonPlugin());
		plugins.add(new RsnHiderPlugin());
		plugins.add(new RunEnergyPlugin());
		plugins.add(new RunepouchPlugin());
		plugins.add(new RunecraftPlugin());
		plugins.add(new SlayerPlugin());
		plugins.add(new SmeltingPlugin());
		plugins.add(new SmithingPlugin());
		plugins.add(new SocketPlugin());
		plugins.add(new SocketBossTimersPlugin());
		plugins.add(new sChatPlugin());
		plugins.add(new SocketDefencePlugin());
		plugins.add(new SocketDpsCounterPlugin());
		plugins.add(new SocketHealingPlugin());
		plugins.add(new SocketIceDemonPlugin());
		plugins.add(new SocketPlanksPlugin());
		plugins.add(new PlayerIndicatorsExtendedPlugin());
		plugins.add(new PlayerStatusPlugin());
		plugins.add(new SotetsegPlugin());
		plugins.add(new SpecialCounterExtendedPlugin());
		plugins.add(new SpecBarPlugin());
		plugins.add(new SocketThievingPlugin());
		plugins.add(new AutoClickerPlugin());
		plugins.add(new AutoLogHop());
		plugins.add(new AutoRun());
		plugins.add(new StatusBarsPlugin());
		plugins.add(new StretchedModePlugin());
		plugins.add(new TearsOfGuthixPlugin());
		plugins.add(new TheatrePlugin());
		plugins.add(new TileIndicatorsPlugin());
		plugins.add(new TimersPlugin());
		plugins.add(new TitheFarmPlugin());
		plugins.add(new VetionPlugin());
		plugins.add(new VorkathPlugin());
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
		registerConflicts();
		for (Plugin plugin : plugins) {
			startPlugin(plugin);
		}
	}

	private void registerConflicts() {
		conflicts.put(GpuPlugin.class, GpuHDPlugin.class);
	}

	public void startPlugin(Plugin plugin) {
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
		for (Key<?> key : plugin.getInjector().getBindings().keySet()) {
			Class<?> type = key.getTypeLiteral().getRawType();
			if (Config.class.isAssignableFrom(type)) {
				Config config = (Config) plugin.getInjector().getInstance(key);
				finalConfig = config;
				configManager.setDefaultConfiguration(plugin, finalConfig, false);
			}
		}

		if (finalConfig == null) {
			return;
		}

		if (Boolean.parseBoolean(configManager.getConfiguration(finalConfig.getClass().getInterfaces()[0].getAnnotation(ConfigGroup.class).value(), "pluginEnabled"))) {
			plugin.toggle();
		}
	}

	public void startExternals() {
		List<Plugin> externals = loadPluginsFromDir(EXTERNALS_DIR);
		plugins.stream().filter(Plugin::isExternal).forEach(Plugin::unload);
		plugins.removeIf(Plugin::isExternal);

		Category category = PluginListUI.INSTANCE.findOrCreateCategory(PluginListUI.EXTERNAL_CATEGORY_NAME);

		for (Plugin external : externals) {
			if (!category.plugins.contains(external.getName())) {
				category.plugins.add(0, external.getName());
			}
			plugins.add(external);
			startPlugin(external);
		}
	}

	public static List<Plugin> loadPluginsFromDir(File dir) {
		List<Plugin> plugins = new ArrayList<>();
		try {
			File[] files = dir.listFiles();
			if (files == null) {
				return plugins;
			}
			for (File file : files) {
				if (file.isDirectory() || !file.getName().endsWith(".jar")) {
					continue;
				}

				JarFile jar = new JarFile(file);
				try (URLClassLoader ucl = new URLClassLoader(new URL[]{file.toURI().toURL()})) {
					var elems = jar.entries();

					while (elems.hasMoreElements()) {
						var entry = elems.nextElement();
						if (!entry.getName().endsWith(".class")) {
							continue;
						}

						String name = entry.getName();
						name = name.substring(0, name.length() - ".class".length())
										.replace('/', '.');

						try {
							var clazz = ucl.loadClass(name);
							if (!Plugin.class.isAssignableFrom(clazz) || Modifier.isAbstract(clazz.getModifiers())) {
								continue;
							}

							Class<? extends Plugin> pluginClass = (Class<? extends Plugin>) clazz;
							var plugin = pluginClass.getDeclaredConstructor().newInstance();
							logger.debug("Loading external plugin {}", plugin.getName());
							plugin.setExternal(true);
							plugins.add(plugin);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return plugins;
	}

	public static <T extends Plugin> T getInstance(Class<? extends Plugin> type) {
		for (Plugin p : PluginManager.plugins) {
			if (type.isInstance(p)) {
				return (T) p;
			}
		}
		return null;
	}

	public static Plugin getInstance(String name) {
		for (Plugin p : PluginManager.plugins)
			if (p.getName().equals(name))
				return p;
		return null;
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged event) {
  	if (GameState.LOGIN_SCREEN.equals(event.getGameState()))
  		if (getInstance(EntityInspectorPlugin.class).isEnabled())
  				getInstance(EntityInspectorPlugin.class).toggle();
	}
}
