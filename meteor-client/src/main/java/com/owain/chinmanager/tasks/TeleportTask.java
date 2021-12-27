package com.owain.chinmanager.tasks;

import static com.owain.automation.ContainerUtils.*;
import com.owain.chinmanager.ChinManager;
import com.owain.chinmanager.ChinManagerPlugin;
import static com.owain.chinmanager.ChinManagerPlugin.*;
import static com.owain.chinmanager.ChinManagerState.stateMachine;
import com.owain.chinmanager.ChinManagerStates;
import com.owain.chinmanager.Location;
import com.owain.chinmanager.Runes;
import com.owain.chinmanager.ui.teleports.TeleportsConfig;
import com.owain.chinmanager.ui.teleports.config.AmuletOfGlory;
import com.owain.chinmanager.ui.teleports.config.CombatBracelet;
import com.owain.chinmanager.ui.teleports.config.DigsitePendant;
import com.owain.chinmanager.ui.teleports.config.GamesNecklace;
import com.owain.chinmanager.ui.teleports.config.Poh;
import com.owain.chinmanager.ui.teleports.config.RingOfDueling;
import com.owain.chinmanager.ui.teleports.config.RingOfWealth;
import com.owain.chinmanager.ui.teleports.config.SkillsNecklace;
import com.owain.chinmanager.ui.teleports.config.XericsTalisman;
import com.owain.chintasks.Task;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.disposables.Disposable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.inject.Inject;

import net.runelite.api.Client;
import net.runelite.api.EquipmentInventorySlot;
import net.runelite.api.GameState;
import net.runelite.api.InventoryID;
import net.runelite.api.Item;
import net.runelite.api.ItemComposition;
import net.runelite.api.ItemContainer;
import net.runelite.api.ItemID;
import net.runelite.api.MenuAction;
import net.runelite.api.NPC;
import net.runelite.api.NullObjectID;
import net.runelite.api.ObjectComposition;
import net.runelite.api.ObjectID;
import net.runelite.api.TileObject;
import net.runelite.api.VarClientInt;
import net.runelite.api.Varbits;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import meteor.eventbus.EventBus;
import meteor.eventbus.Subscribe;
import meteor.game.ItemManager;
import meteor.Logger;

public class TeleportTask implements Task<Void>
{
	enum TeleportState
	{
		NONE,
		OPEN_BANK,
		OPENING_BANK,
		QUANTITY,
		SETTINGS,
		ITEM_OPTIONS,
		CLOSE_BANK,
		FETCH_POH,
		FETCH_TELEPORT,
		SELECT_MINIGAME,
		MINIGAME_TELEPORT,
		MINIGAME_TELEPORT_WAIT,
		TELEPORT_POH,
		TELEPORT_POH_WAIT,
		TELEPORT_MENU,
		TELEPORT_MENU_CLICK,
		TELEPORT_DIALOG,
		TELEPORT_WAIT,
		EQUIPMENT,
		TELEPORT_EQUIPMENT,
		TELEPORT_EQUIPMENT_CLICK,
		TELEPORTING,
		HANDLE_POH,
		CLICK_POH_TELEPORT,
		POH_WAIT_TELEPORT_MENU,
		POH_TELEPORT_MENU,

		;
	}

	private final ChinManager chinManager;
	private final ChinManagerPlugin chinManagerPlugin;
	private final TeleportsConfig teleportsConfig;
	private final Client client;
	private final EventBus eventBus;
	private final ItemManager itemManager;
	private Logger log = Logger.Companion.getLogger(TeleportTask.class);

	private final List<Disposable> disposables = new ArrayList<>();

	private static final Varbits[] AMOUNT_VARBITS =
		{
			Varbits.RUNE_POUCH_AMOUNT1, Varbits.RUNE_POUCH_AMOUNT2, Varbits.RUNE_POUCH_AMOUNT3
		};
	private static final Varbits[] RUNE_VARBITS =
		{
			Varbits.RUNE_POUCH_RUNE1, Varbits.RUNE_POUCH_RUNE2, Varbits.RUNE_POUCH_RUNE3
		};

	private TeleportState teleportState;
	private TeleportState cachedTeleportState;

	private int tikkie = 10;

	@Inject
	TeleportTask(ChinManager chinManager, ChinManagerPlugin chinManagerPlugin, TeleportsConfig teleportsConfig, EventBus eventBus, ItemManager itemManager)
	{
		this.chinManager = chinManager;
		this.chinManagerPlugin = chinManagerPlugin;
		this.teleportsConfig = teleportsConfig;
		this.client = chinManagerPlugin.getClient();
		this.eventBus = eventBus;
		this.itemManager = itemManager;
	}

	@Override
	public void routine(ObservableEmitter<Void> emitter)
	{
		teleportState = TeleportState.NONE;
		cachedTeleportState = TeleportState.NONE;

		tikkie = 10;

		eventBus.register(this);
	}

	public void unsubscribe()
	{
		eventBus.unregister(this);
		tikkie = 10;

		teleportState = TeleportState.NONE;
		cachedTeleportState = TeleportState.NONE;

		chinManager.teleportingDone();

		for (Disposable disposable : disposables)
		{
			if (!disposable.isDisposed())
			{
				disposable.dispose();
			}
		}
	}

	@Subscribe
	private void onGameTick(GameTick gameTick)
	{
		if (teleportState != cachedTeleportState)
		{
			cachedTeleportState = teleportState;
			tikkie = 0;
		}

		if (tikkie >= 10)
		{
			tikkie = 0;
			teleportState = TeleportState.NONE;
		}

		if (teleportState == TeleportState.MINIGAME_TELEPORT_WAIT || client.getLocalPlayer().isMoving())
		{
			tikkie = 0;
		}

		Widget bankTitleBar = client.getWidget(WidgetInfo.BANK_TITLE_BAR);

		if (client.getItemContainer(InventoryID.BANK) != null && client.getVarbitValue(6590) != 0)
		{
			teleportState = TeleportState.QUANTITY;
			disposables.add(chinManagerPlugin.getTaskExecutor().prepareTask(new ClickTask(chinManagerPlugin)).ignoreElements().subscribe());
		}
		else if (client.getItemContainer(InventoryID.BANK) != null && client.getVarbitValue(10079) != 0)
		{
			if (bankTitleBar != null)
			{
				if (!bankTitleBar.getText().equals("Bank settings menu"))
				{
					teleportState = TeleportState.SETTINGS;
				}
				else
				{
					teleportState = TeleportState.ITEM_OPTIONS;
				}

				disposables.add(chinManagerPlugin.getTaskExecutor().prepareTask(new ClickTask(chinManagerPlugin)).ignoreElements().subscribe());
			}
		}
		else if (bankTitleBar != null && bankTitleBar.getText().equals("Bank settings menu"))
		{
			teleportState = TeleportState.SETTINGS;
			disposables.add(chinManagerPlugin.getTaskExecutor().prepareTask(new ClickTask(chinManagerPlugin)).ignoreElements().subscribe());
		}
		else if (teleportState == TeleportState.TELEPORT_POH_WAIT && client.getWidget(WidgetInfo.DIALOG_OPTION_OPTION1) != null)
		{
			teleportState = TeleportState.HANDLE_POH;
		}
		else if (teleportState == TeleportState.OPEN_BANK && ChinManagerPlugin.isAtBank(client))
		{
			disposables.add(chinManagerPlugin.getTaskExecutor().prepareTask(new ClickTask(chinManagerPlugin)).ignoreElements().subscribe());
		}
		else if (teleportState == TeleportState.OPENING_BANK)
		{
			if (client.getItemContainer(InventoryID.BANK) != null)
			{
				teleportState = TeleportState.NONE;
			}
		}
		else if (teleportState == TeleportState.NONE)
		{
			TileObject exitPortal = chinManagerPlugin.getObject(ObjectID.PORTAL_4525);

			if (exitPortal != null)
			{
				teleportState = TeleportState.HANDLE_POH;
			}
			else
			{
				try
				{
					Location location = chinManager.getTeleportingLocation();

					if (needRingOfDueling(location) ||
						needGamesNecklace(location) ||
						needCombatBracelet(location) ||
						needSkillsNecklace(location) ||
						needRingOfWealth(location) ||
						needAmuletOfGlory(location) ||
						needXericsTalisman(location) ||
						needDigsitePendant(location))
					{
						jewelleryLogic(getTeleportJewellery(location));
					}
					else if (needPohTeleport(location))
					{
						pohLogic();
					}
					else if (isMinigameTeleport(location))
					{
						minigameLogic();
					}
				}
				catch (Exception ignored)
				{
					teleportState = TeleportState.NONE;
				}
			}
		}
		else if (teleportState == TeleportState.SELECT_MINIGAME)
		{
			if (client.getVar(Varbits.QUEST_TAB) != 2)
			{
				disposables.add(chinManagerPlugin.getTaskExecutor().prepareTask(new ClickTask(chinManagerPlugin)).ignoreElements().subscribe());
			}
			else
			{
				int id;

				switch (chinManager.getTeleportingLocation())
				{
					case NMZ:
						id = 10;
						break;

					case TITHE_FARM:
						id = 19;
						break;

					case LMS:
						id = 9;
						break;

					case PEST_CONTROL:
						id = 11;
						break;

					case SOUL_WARS:
						id = 16;
						break;

					default:
						id = -1;
				}

				if (id == -1)
				{
					teleportState = TeleportState.NONE;
					return;
				}

				client.runScript(124, id);
				teleportState = TeleportState.MINIGAME_TELEPORT;
			}
		}
		else if (teleportState == TeleportState.MINIGAME_TELEPORT)
		{
			disposables.add(chinManagerPlugin.getTaskExecutor().prepareTask(new ClickTask(chinManagerPlugin)).ignoreElements().subscribe());
		}
		else if (teleportState == TeleportState.EQUIPMENT)
		{
			if (client.getVar(VarClientInt.INVENTORY_TAB) != 4)
			{
				client.runScript(915, 4);
			}
			teleportState = TeleportState.TELEPORT_EQUIPMENT;
		}
		else if (teleportState == TeleportState.TELEPORT_EQUIPMENT)
		{
			disposables.add(chinManagerPlugin.getTaskExecutor().prepareTask(new ClickTask(chinManagerPlugin)).ignoreElements().subscribe());
		}
		else if (teleportState == TeleportState.TELEPORT_DIALOG)
		{
			Location location = chinManager.getTeleportingLocation();

			Widget option;

			if (needSkillsNecklace(location) || needXericsTalisman(location))
			{
				option = client.getWidget(187, 3);
			}
			else
			{
				option = client.getWidget(WidgetInfo.DIALOG_OPTION_OPTION1);
			}

			if (option == null)
			{
				teleportState = TeleportState.NONE;
				return;
			}

			Widget[] optionChildren = option.getDynamicChildren();
			for (Widget child : optionChildren)
			{
				if (location == Location.DUEL_ARENA && child.getText().contains("Duel Arena"))
				{
					disposables.add(chinManagerPlugin.getTaskExecutor().prepareTask(new KeyTask(chinManagerPlugin, String.valueOf(child.getIndex()))).ignoreElements().subscribe());
					teleportState = TeleportState.TELEPORT_WAIT;
				}
				else if (location == Location.CASTLE_WARS && child.getText().contains("Castle Wars"))
				{
					disposables.add(chinManagerPlugin.getTaskExecutor().prepareTask(new KeyTask(chinManagerPlugin, String.valueOf(child.getIndex()))).ignoreElements().subscribe());
					teleportState = TeleportState.TELEPORT_WAIT;
				}
				else if (location == Location.FEROX_ENCLAVE && child.getText().contains("Ferox Enclave"))
				{
					disposables.add(chinManagerPlugin.getTaskExecutor().prepareTask(new KeyTask(chinManagerPlugin, String.valueOf(child.getIndex()))).ignoreElements().subscribe());
					teleportState = TeleportState.TELEPORT_WAIT;
				}
				else if (location == Location.BURTHORPE && child.getText().contains("Burthorpe"))
				{
					disposables.add(chinManagerPlugin.getTaskExecutor().prepareTask(new KeyTask(chinManagerPlugin, String.valueOf(child.getIndex()))).ignoreElements().subscribe());
					teleportState = TeleportState.TELEPORT_WAIT;
				}
				else if (location == Location.BARBARIAN_OUTPOST && child.getText().contains("Barbarian Outpost"))
				{
					disposables.add(chinManagerPlugin.getTaskExecutor().prepareTask(new KeyTask(chinManagerPlugin, String.valueOf(child.getIndex()))).ignoreElements().subscribe());
					teleportState = TeleportState.TELEPORT_WAIT;
				}
				else if (location == Location.CORPOREAL_BEAST && child.getText().contains("Corporeal Beast"))
				{
					disposables.add(chinManagerPlugin.getTaskExecutor().prepareTask(new KeyTask(chinManagerPlugin, String.valueOf(child.getIndex()))).ignoreElements().subscribe());
					teleportState = TeleportState.TELEPORT_WAIT;
				}
				else if (location == Location.WINTERTODT && child.getText().contains("Wintertodt Camp"))
				{
					disposables.add(chinManagerPlugin.getTaskExecutor().prepareTask(new KeyTask(chinManagerPlugin, String.valueOf(child.getIndex()))).ignoreElements().subscribe());
					teleportState = TeleportState.TELEPORT_WAIT;
				}
				else if (location == Location.WARRIORS_GUILD && child.getText().contains("Warriors' Guild"))
				{
					disposables.add(chinManagerPlugin.getTaskExecutor().prepareTask(new KeyTask(chinManagerPlugin, String.valueOf(child.getIndex()))).ignoreElements().subscribe());
					teleportState = TeleportState.TELEPORT_WAIT;
				}
				else if (location == Location.CHAMPIONS_GUILD && child.getText().contains("Champions' Guild"))
				{
					disposables.add(chinManagerPlugin.getTaskExecutor().prepareTask(new KeyTask(chinManagerPlugin, String.valueOf(child.getIndex()))).ignoreElements().subscribe());
					teleportState = TeleportState.TELEPORT_WAIT;
				}
				else if (location == Location.EDGEVILLE_MONASTERY && child.getText().contains("Monastery"))
				{
					disposables.add(chinManagerPlugin.getTaskExecutor().prepareTask(new KeyTask(chinManagerPlugin, String.valueOf(child.getIndex()))).ignoreElements().subscribe());
					teleportState = TeleportState.TELEPORT_WAIT;
				}
				else if (location == Location.RANGING_GUILD && child.getText().contains("Ranging Guild"))
				{
					disposables.add(chinManagerPlugin.getTaskExecutor().prepareTask(new KeyTask(chinManagerPlugin, String.valueOf(child.getIndex()))).ignoreElements().subscribe());
					teleportState = TeleportState.TELEPORT_WAIT;
				}
				else if (location == Location.MISCELLANIA && child.getText().contains("Miscellania"))
				{
					disposables.add(chinManagerPlugin.getTaskExecutor().prepareTask(new KeyTask(chinManagerPlugin, String.valueOf(child.getIndex()))).ignoreElements().subscribe());
					teleportState = TeleportState.TELEPORT_WAIT;
				}
				else if (location == Location.GRAND_EXCHANGE && child.getText().contains("Grand Exchange"))
				{
					disposables.add(chinManagerPlugin.getTaskExecutor().prepareTask(new KeyTask(chinManagerPlugin, String.valueOf(child.getIndex()))).ignoreElements().subscribe());
					teleportState = TeleportState.TELEPORT_WAIT;
				}
				else if (location == Location.FALADOR_PARK && child.getText().contains("Falador Park"))
				{
					disposables.add(chinManagerPlugin.getTaskExecutor().prepareTask(new KeyTask(chinManagerPlugin, String.valueOf(child.getIndex()))).ignoreElements().subscribe());
					teleportState = TeleportState.TELEPORT_WAIT;
				}
				else if (location == Location.DONDAKANS_ROCK && child.getText().contains("Dondakan's Rock"))
				{
					disposables.add(chinManagerPlugin.getTaskExecutor().prepareTask(new KeyTask(chinManagerPlugin, String.valueOf(child.getIndex()))).ignoreElements().subscribe());
					teleportState = TeleportState.TELEPORT_WAIT;
				}
				else if (location == Location.EDGEVILLE && child.getText().contains("Edgeville"))
				{
					disposables.add(chinManagerPlugin.getTaskExecutor().prepareTask(new KeyTask(chinManagerPlugin, String.valueOf(child.getIndex()))).ignoreElements().subscribe());
					teleportState = TeleportState.TELEPORT_WAIT;
				}
				else if (location == Location.KARAMJA && child.getText().contains("Karamja"))
				{
					disposables.add(chinManagerPlugin.getTaskExecutor().prepareTask(new KeyTask(chinManagerPlugin, String.valueOf(child.getIndex()))).ignoreElements().subscribe());
					teleportState = TeleportState.TELEPORT_WAIT;
				}
				else if (location == Location.DRAYNOR_VILLAGE && child.getText().contains("Draynor Village"))
				{
					disposables.add(chinManagerPlugin.getTaskExecutor().prepareTask(new KeyTask(chinManagerPlugin, String.valueOf(child.getIndex()))).ignoreElements().subscribe());
					teleportState = TeleportState.TELEPORT_WAIT;
				}
				else if (location == Location.AL_KHARID && child.getText().contains("Al Kharid"))
				{
					disposables.add(chinManagerPlugin.getTaskExecutor().prepareTask(new KeyTask(chinManagerPlugin, String.valueOf(child.getIndex()))).ignoreElements().subscribe());
					teleportState = TeleportState.TELEPORT_WAIT;
				}
				else if (location == Location.DIGSITE && child.getText().contains("Digsite"))
				{
					disposables.add(chinManagerPlugin.getTaskExecutor().prepareTask(new KeyTask(chinManagerPlugin, String.valueOf(child.getIndex()))).ignoreElements().subscribe());
					teleportState = TeleportState.TELEPORT_WAIT;
				}
				else if (location == Location.FOSSIL_ISLAND && child.getText().contains("Fossil Island"))
				{
					disposables.add(chinManagerPlugin.getTaskExecutor().prepareTask(new KeyTask(chinManagerPlugin, String.valueOf(child.getIndex()))).ignoreElements().subscribe());
					teleportState = TeleportState.TELEPORT_WAIT;
				}
				else if (location == Location.FISHING_GUILD && child.getText().contains("Fishing Guild"))
				{
					disposables.add(chinManagerPlugin.getTaskExecutor().prepareTask(new KeyTask(chinManagerPlugin, String.valueOf(child.getIndex() + 1))).ignoreElements().subscribe());
					teleportState = TeleportState.TELEPORT_WAIT;
				}
				else if (location == Location.MINING_GUILD && child.getText().contains("Mining Guild"))
				{
					disposables.add(chinManagerPlugin.getTaskExecutor().prepareTask(new KeyTask(chinManagerPlugin, String.valueOf(child.getIndex() + 1))).ignoreElements().subscribe());
					teleportState = TeleportState.TELEPORT_WAIT;
				}
				else if (location == Location.CRAFTING_GUILD && child.getText().contains("Crafting Guild"))
				{
					disposables.add(chinManagerPlugin.getTaskExecutor().prepareTask(new KeyTask(chinManagerPlugin, String.valueOf(child.getIndex() + 1))).ignoreElements().subscribe());
					teleportState = TeleportState.TELEPORT_WAIT;
				}
				else if (location == Location.COOKS_GUILD && child.getText().contains("Cooking Guild"))
				{
					disposables.add(chinManagerPlugin.getTaskExecutor().prepareTask(new KeyTask(chinManagerPlugin, String.valueOf(child.getIndex() + 1))).ignoreElements().subscribe());
					teleportState = TeleportState.TELEPORT_WAIT;
				}
				else if (location == Location.WOODCUTTING_GUILD && child.getText().contains("Woodcutting Guild"))
				{
					disposables.add(chinManagerPlugin.getTaskExecutor().prepareTask(new KeyTask(chinManagerPlugin, String.valueOf(child.getIndex() + 1))).ignoreElements().subscribe());
					teleportState = TeleportState.TELEPORT_WAIT;
				}
				else if (location == Location.FARMING_GUILD && child.getText().contains("Farming Guild"))
				{
					disposables.add(chinManagerPlugin.getTaskExecutor().prepareTask(new KeyTask(chinManagerPlugin, String.valueOf(child.getIndex() + 1))).ignoreElements().subscribe());
					teleportState = TeleportState.TELEPORT_WAIT;
				}
				else if (location == Location.XERICS_LOOKOUT && child.getText().contains("Xeric's Look-out"))
				{
					disposables.add(chinManagerPlugin.getTaskExecutor().prepareTask(new KeyTask(chinManagerPlugin, String.valueOf(child.getIndex() + 1))).ignoreElements().subscribe());
					teleportState = TeleportState.TELEPORT_WAIT;
				}
				else if (location == Location.XERICS_GLADE && child.getText().contains("Xeric's Glade"))
				{
					disposables.add(chinManagerPlugin.getTaskExecutor().prepareTask(new KeyTask(chinManagerPlugin, String.valueOf(child.getIndex() + 1))).ignoreElements().subscribe());
					teleportState = TeleportState.TELEPORT_WAIT;
				}
				else if (location == Location.XERICS_INFERNO && child.getText().contains("Xeric's Inferno"))
				{
					disposables.add(chinManagerPlugin.getTaskExecutor().prepareTask(new KeyTask(chinManagerPlugin, String.valueOf(child.getIndex() + 1))).ignoreElements().subscribe());
					teleportState = TeleportState.TELEPORT_WAIT;
				}
				else if (location == Location.XERICS_HEART && child.getText().contains("Xeric's Heart"))
				{
					disposables.add(chinManagerPlugin.getTaskExecutor().prepareTask(new KeyTask(chinManagerPlugin, String.valueOf(child.getIndex() + 1))).ignoreElements().subscribe());
					teleportState = TeleportState.TELEPORT_WAIT;
				}
				else if (location == Location.XERICS_HONOUR && child.getText().contains("Xeric's Honour"))
				{
					disposables.add(chinManagerPlugin.getTaskExecutor().prepareTask(new KeyTask(chinManagerPlugin, String.valueOf(child.getIndex() + 1))).ignoreElements().subscribe());
					teleportState = TeleportState.TELEPORT_WAIT;
				}
			}
		}
		else if (teleportState == TeleportState.TELEPORTING)
		{
			Widget loadingHome = client.getWidget(71, 7);

			if (loadingHome != null && !loadingHome.isHidden())
			{
				return;
			}

			if (client.getVar(VarClientInt.INVENTORY_TAB) != 3)
			{
				client.runScript(915, 3);
			}

			TileObject exitPortal = chinManagerPlugin.getObject(ObjectID.PORTAL_4525);

			if (exitPortal != null)
			{
				teleportState = TeleportState.HANDLE_POH;
			}
			else
			{
				stateMachine.accept(ChinManagerStates.IDLE);
			}
		}
		else if (teleportState == TeleportState.HANDLE_POH)
		{
			teleportState = TeleportState.CLICK_POH_TELEPORT;
			disposables.add(chinManagerPlugin.getTaskExecutor().prepareTask(new ClickTask(chinManagerPlugin)).ignoreElements().subscribe());
		}
		else if (teleportState == TeleportState.POH_WAIT_TELEPORT_MENU)
		{
			if (client.getWidget(590, 1) != null)
			{
				teleportState = TeleportState.POH_TELEPORT_MENU;
			}
			else if (client.getWidget(187, 3) != null)
			{
				teleportState = TeleportState.POH_TELEPORT_MENU;
			}
		}
		else if (teleportState == TeleportState.POH_TELEPORT_MENU)
		{
			Location location = chinManager.getTeleportingLocation();

			Widget option = null;

			if (client.getWidget(187, 3) != null)
			{
				option = client.getWidget(187, 3);
			}
			else if (client.getWidget(590, 1) != null)
			{
				switch (location)
				{
					case DUEL_ARENA:
					case CASTLE_WARS:
					case FEROX_ENCLAVE:
						option = client.getWidget(WidgetInfo.JEWELLERY_BOX_DUEL_RING);
						break;

					case BURTHORPE:
					case BARBARIAN_OUTPOST:
					case CORPOREAL_BEAST:
					case CHASM_OF_TEARS:
					case WINTERTODT:
						option = client.getWidget(WidgetInfo.JEWELLERY_BOX_GAME_NECK);
						break;

					case WARRIORS_GUILD:
					case CHAMPIONS_GUILD:
					case EDGEVILLE_MONASTERY:
					case RANGING_GUILD:
						option = client.getWidget(WidgetInfo.JEWELLERY_BOX_COMB_BRAC);
						break;

					case FISHING_GUILD:
					case MINING_GUILD:
					case CRAFTING_GUILD:
					case COOKS_GUILD:
					case WOODCUTTING_GUILD:
					case FARMING_GUILD:
						option = client.getWidget(WidgetInfo.JEWELLERY_BOX_SKIL_NECK);
						break;

					case MISCELLANIA:
					case GRAND_EXCHANGE:
					case FALADOR_PARK:
					case DONDAKANS_ROCK:
						option = client.getWidget(WidgetInfo.JEWELLERY_BOX_RING_OFGP);
						break;

					case EDGEVILLE:
					case KARAMJA:
					case DRAYNOR_VILLAGE:
					case AL_KHARID:
						option = client.getWidget(WidgetInfo.JEWELLERY_BOX_AMUL_GLOR);
						break;
				}
			}

			if (option == null)
			{
				teleportState = TeleportState.NONE;
				return;
			}

			Widget[] optionChildren = option.getDynamicChildren();
			for (Widget child : optionChildren)
			{
				if (location == Location.DIGSITE && child.getText().contains("Digsite"))
				{
					disposables.add(chinManagerPlugin.getTaskExecutor().prepareTask(new KeyTask(chinManagerPlugin, String.valueOf(child.getIndex() + 1))).ignoreElements().subscribe());
					teleportState = TeleportState.TELEPORT_POH_WAIT;
				}
				else if (location == Location.FOSSIL_ISLAND && child.getText().contains("Fossil Island"))
				{
					disposables.add(chinManagerPlugin.getTaskExecutor().prepareTask(new KeyTask(chinManagerPlugin, String.valueOf(child.getIndex() + 1))).ignoreElements().subscribe());
					teleportState = TeleportState.TELEPORT_POH_WAIT;
				}
				else if (location == Location.XERICS_LOOKOUT && child.getText().contains("Xeric's Look-out"))
				{
					disposables.add(chinManagerPlugin.getTaskExecutor().prepareTask(new KeyTask(chinManagerPlugin, String.valueOf(child.getIndex() + 1))).ignoreElements().subscribe());
					teleportState = TeleportState.TELEPORT_POH_WAIT;
				}
				else if (location == Location.XERICS_GLADE && child.getText().contains("Xeric's Glade"))
				{
					disposables.add(chinManagerPlugin.getTaskExecutor().prepareTask(new KeyTask(chinManagerPlugin, String.valueOf(child.getIndex() + 1))).ignoreElements().subscribe());
					teleportState = TeleportState.TELEPORT_POH_WAIT;
				}
				else if (location == Location.XERICS_INFERNO && child.getText().contains("Xeric's Inferno"))
				{
					disposables.add(chinManagerPlugin.getTaskExecutor().prepareTask(new KeyTask(chinManagerPlugin, String.valueOf(child.getIndex() + 1))).ignoreElements().subscribe());
					teleportState = TeleportState.TELEPORT_POH_WAIT;
				}
				else if (location == Location.XERICS_HEART && child.getText().contains("Xeric's Heart"))
				{
					disposables.add(chinManagerPlugin.getTaskExecutor().prepareTask(new KeyTask(chinManagerPlugin, String.valueOf(child.getIndex() + 1))).ignoreElements().subscribe());
					teleportState = TeleportState.TELEPORT_POH_WAIT;
				}
				else if (location == Location.XERICS_HONOUR && child.getText().contains("Xeric's Honour"))
				{
					disposables.add(chinManagerPlugin.getTaskExecutor().prepareTask(new KeyTask(chinManagerPlugin, String.valueOf(child.getIndex() + 1))).ignoreElements().subscribe());
					teleportState = TeleportState.TELEPORT_POH_WAIT;
				}
				else if (location == Location.DUEL_ARENA && child.getText().contains("Duel Arena"))
				{
					String text = child.getText().replace("<col=ccccff>", "").substring(0, 1);
					disposables.add(chinManagerPlugin.getTaskExecutor().prepareTask(new KeyTask(chinManagerPlugin, text)).ignoreElements().subscribe());
					teleportState = TeleportState.TELEPORT_POH_WAIT;
				}
				else if (location == Location.CASTLE_WARS && child.getText().contains("Castle Wars"))
				{
					String text = child.getText().replace("<col=ccccff>", "").substring(0, 1);
					disposables.add(chinManagerPlugin.getTaskExecutor().prepareTask(new KeyTask(chinManagerPlugin, text)).ignoreElements().subscribe());
					teleportState = TeleportState.TELEPORT_POH_WAIT;
				}
				else if (location == Location.FEROX_ENCLAVE && child.getText().contains("Ferox Enclave"))
				{
					String text = child.getText().replace("<col=ccccff>", "").substring(0, 1);
					disposables.add(chinManagerPlugin.getTaskExecutor().prepareTask(new KeyTask(chinManagerPlugin, text)).ignoreElements().subscribe());
					teleportState = TeleportState.TELEPORT_POH_WAIT;
				}
				else if (location == Location.BURTHORPE && child.getText().contains("Burthorpe"))
				{
					String text = child.getText().replace("<col=ccccff>", "").substring(0, 1);
					disposables.add(chinManagerPlugin.getTaskExecutor().prepareTask(new KeyTask(chinManagerPlugin, text)).ignoreElements().subscribe());
					teleportState = TeleportState.TELEPORT_POH_WAIT;
				}
				else if (location == Location.BARBARIAN_OUTPOST && child.getText().contains("Barbarian Outpost"))
				{
					String text = child.getText().replace("<col=ccccff>", "").substring(0, 1);
					disposables.add(chinManagerPlugin.getTaskExecutor().prepareTask(new KeyTask(chinManagerPlugin, text)).ignoreElements().subscribe());
					teleportState = TeleportState.TELEPORT_POH_WAIT;
				}
				else if (location == Location.CORPOREAL_BEAST && child.getText().contains("Corporeal Beast"))
				{
					String text = child.getText().replace("<col=ccccff>", "").substring(0, 1);
					disposables.add(chinManagerPlugin.getTaskExecutor().prepareTask(new KeyTask(chinManagerPlugin, text)).ignoreElements().subscribe());
					teleportState = TeleportState.TELEPORT_POH_WAIT;
				}
				else if (location == Location.WINTERTODT && child.getText().contains("Wintertodt Camp"))
				{
					String text = child.getText().replace("<col=ccccff>", "").substring(0, 1);
					disposables.add(chinManagerPlugin.getTaskExecutor().prepareTask(new KeyTask(chinManagerPlugin, text)).ignoreElements().subscribe());
					teleportState = TeleportState.TELEPORT_POH_WAIT;
				}
				else if (location == Location.WARRIORS_GUILD && child.getText().contains("Warriors' Guild"))
				{
					String text = child.getText().replace("<col=ccccff>", "").substring(0, 1);
					disposables.add(chinManagerPlugin.getTaskExecutor().prepareTask(new KeyTask(chinManagerPlugin, text)).ignoreElements().subscribe());
					teleportState = TeleportState.TELEPORT_POH_WAIT;
				}
				else if (location == Location.CHAMPIONS_GUILD && child.getText().contains("Champions' Guild"))
				{
					String text = child.getText().replace("<col=ccccff>", "").substring(0, 1);
					disposables.add(chinManagerPlugin.getTaskExecutor().prepareTask(new KeyTask(chinManagerPlugin, text)).ignoreElements().subscribe());
					teleportState = TeleportState.TELEPORT_POH_WAIT;
				}
				else if (location == Location.EDGEVILLE_MONASTERY && child.getText().contains("Monastery"))
				{
					String text = child.getText().replace("<col=ccccff>", "").substring(0, 1);
					disposables.add(chinManagerPlugin.getTaskExecutor().prepareTask(new KeyTask(chinManagerPlugin, text)).ignoreElements().subscribe());
					teleportState = TeleportState.TELEPORT_POH_WAIT;
				}
				else if (location == Location.RANGING_GUILD && child.getText().contains("Ranging Guild"))
				{
					String text = child.getText().replace("<col=ccccff>", "").substring(0, 1);
					disposables.add(chinManagerPlugin.getTaskExecutor().prepareTask(new KeyTask(chinManagerPlugin, text)).ignoreElements().subscribe());
					teleportState = TeleportState.TELEPORT_POH_WAIT;
				}
				else if (location == Location.MISCELLANIA && child.getText().contains("Miscellania"))
				{
					String text = child.getText().replace("<col=ccccff>", "").substring(0, 1);
					disposables.add(chinManagerPlugin.getTaskExecutor().prepareTask(new KeyTask(chinManagerPlugin, text)).ignoreElements().subscribe());
					teleportState = TeleportState.TELEPORT_POH_WAIT;
				}
				else if (location == Location.GRAND_EXCHANGE && child.getText().contains("Grand Exchange"))
				{
					String text = child.getText().replace("<col=ccccff>", "").substring(0, 1);
					disposables.add(chinManagerPlugin.getTaskExecutor().prepareTask(new KeyTask(chinManagerPlugin, text)).ignoreElements().subscribe());
					teleportState = TeleportState.TELEPORT_POH_WAIT;
				}
				else if (location == Location.FALADOR_PARK && child.getText().contains("Falador Park"))
				{
					String text = child.getText().replace("<col=ccccff>", "").substring(0, 1);
					disposables.add(chinManagerPlugin.getTaskExecutor().prepareTask(new KeyTask(chinManagerPlugin, text)).ignoreElements().subscribe());
					teleportState = TeleportState.TELEPORT_POH_WAIT;
				}
				else if (location == Location.DONDAKANS_ROCK && child.getText().contains("Dondakan's Rock"))
				{
					String text = child.getText().replace("<col=ccccff>", "").substring(0, 1);
					disposables.add(chinManagerPlugin.getTaskExecutor().prepareTask(new KeyTask(chinManagerPlugin, text)).ignoreElements().subscribe());
					teleportState = TeleportState.TELEPORT_POH_WAIT;
				}
				else if (location == Location.EDGEVILLE && child.getText().contains("Edgeville"))
				{
					String text = child.getText().replace("<col=ccccff>", "").substring(0, 1);
					disposables.add(chinManagerPlugin.getTaskExecutor().prepareTask(new KeyTask(chinManagerPlugin, text)).ignoreElements().subscribe());
					teleportState = TeleportState.TELEPORT_POH_WAIT;
				}
				else if (location == Location.KARAMJA && child.getText().contains("Karamja"))
				{
					String text = child.getText().replace("<col=ccccff>", "").substring(0, 1);
					disposables.add(chinManagerPlugin.getTaskExecutor().prepareTask(new KeyTask(chinManagerPlugin, text)).ignoreElements().subscribe());
					teleportState = TeleportState.TELEPORT_POH_WAIT;
				}
				else if (location == Location.DRAYNOR_VILLAGE && child.getText().contains("Draynor Village"))
				{
					String text = child.getText().replace("<col=ccccff>", "").substring(0, 1);
					disposables.add(chinManagerPlugin.getTaskExecutor().prepareTask(new KeyTask(chinManagerPlugin, text)).ignoreElements().subscribe());
					teleportState = TeleportState.TELEPORT_POH_WAIT;
				}
				else if (location == Location.AL_KHARID && child.getText().contains("Al Kharid"))
				{
					String text = child.getText().replace("<col=ccccff>", "").substring(0, 1);
					disposables.add(chinManagerPlugin.getTaskExecutor().prepareTask(new KeyTask(chinManagerPlugin, text)).ignoreElements().subscribe());
					teleportState = TeleportState.TELEPORT_POH_WAIT;
				}
				else if (location == Location.FISHING_GUILD && child.getText().contains("Fishing Guild"))
				{
					String text = child.getText().replace("<col=ccccff>", "").substring(0, 1);
					disposables.add(chinManagerPlugin.getTaskExecutor().prepareTask(new KeyTask(chinManagerPlugin, text)).ignoreElements().subscribe());
					teleportState = TeleportState.TELEPORT_POH_WAIT;
				}
				else if (location == Location.MINING_GUILD && child.getText().contains("Mining Guild"))
				{
					String text = child.getText().replace("<col=ccccff>", "").substring(0, 1);
					disposables.add(chinManagerPlugin.getTaskExecutor().prepareTask(new KeyTask(chinManagerPlugin, text)).ignoreElements().subscribe());
					teleportState = TeleportState.TELEPORT_POH_WAIT;
				}
				else if (location == Location.CRAFTING_GUILD && child.getText().contains("Crafting Guild"))
				{
					String text = child.getText().replace("<col=ccccff>", "").substring(0, 1);
					disposables.add(chinManagerPlugin.getTaskExecutor().prepareTask(new KeyTask(chinManagerPlugin, text)).ignoreElements().subscribe());
					teleportState = TeleportState.TELEPORT_POH_WAIT;
				}
				else if (location == Location.COOKS_GUILD && child.getText().contains("Cooking Guild"))
				{
					String text = child.getText().replace("<col=ccccff>", "").substring(0, 1);
					disposables.add(chinManagerPlugin.getTaskExecutor().prepareTask(new KeyTask(chinManagerPlugin, text)).ignoreElements().subscribe());
					teleportState = TeleportState.TELEPORT_POH_WAIT;
				}
				else if (location == Location.WOODCUTTING_GUILD && child.getText().contains("Woodcutting Guild"))
				{
					String text = child.getText().replace("<col=ccccff>", "").substring(0, 1);
					disposables.add(chinManagerPlugin.getTaskExecutor().prepareTask(new KeyTask(chinManagerPlugin, text)).ignoreElements().subscribe());
					teleportState = TeleportState.TELEPORT_POH_WAIT;
				}
				else if (location == Location.FARMING_GUILD && child.getText().contains("Farming Guild"))
				{
					String text = child.getText().replace("<col=ccccff>", "").substring(0, 1);
					disposables.add(chinManagerPlugin.getTaskExecutor().prepareTask(new KeyTask(chinManagerPlugin, text)).ignoreElements().subscribe());
					teleportState = TeleportState.TELEPORT_POH_WAIT;
				}
			}
		}

		tikkie += 1;
	}

	@Subscribe
	private void onGameStateChanged(GameStateChanged gameStateChanged)
	{
		if (gameStateChanged.getGameState() == GameState.LOGGED_IN && (teleportState == TeleportState.TELEPORT_POH_WAIT || teleportState == TeleportState.MINIGAME_TELEPORT_WAIT || teleportState == TeleportState.TELEPORT_WAIT))
		{
			if (teleportState == TeleportState.MINIGAME_TELEPORT_WAIT)
			{
				if (client.getVar(VarClientInt.INVENTORY_TAB) != 6)
				{
					client.runScript(915, 6);
				}
			}

			teleportState = TeleportState.TELEPORTING;
		}
	}

	@Subscribe
	private void onMenuOptionClicked(MenuOptionClicked menuOptionClicked)
	{
		if (teleportState == TeleportState.QUANTITY)
		{
			Widget withdrawOne = client.getWidget(12, 27);

			chinManagerPlugin.menuAction(
				menuOptionClicked,
				"Default quantity: 1",
				"",
				1,
				MenuAction.CC_OP,
				-1,
				withdrawOne.getId()
			);

			teleportState = TeleportState.NONE;
		}
		else if (teleportState == TeleportState.SETTINGS)
		{
			Widget settingsButton = client.getWidget(WidgetInfo.BANK_SETTINGS_BUTTON);

			chinManagerPlugin.menuAction(
				menuOptionClicked,
				"menu",
				"",
				1,
				MenuAction.CC_OP,
				-1,
				settingsButton.getId()
			);

			teleportState = TeleportState.NONE;
		}
		else if (teleportState == TeleportState.ITEM_OPTIONS)
		{
			Widget inventoryOptions = client.getWidget(12, 50);

			chinManagerPlugin.menuAction(
				menuOptionClicked,
				"Show",
				"<col=ff9040>Inventory item options</col>",
				1,
				MenuAction.CC_OP,
				-1,
				inventoryOptions.getId()
			);

			teleportState = TeleportState.NONE;
		}
		else if (teleportState == TeleportState.OPEN_BANK)
		{
			NPC banker = ChinManagerPlugin.getBankNpc(client);
			TileObject bank = ChinManagerPlugin.getBankObject(client);

			log.debug("Banker: {}", banker);
			log.debug("Bank: {}", bank);

			if (bank != null && !bank.getName().contains("Grand"))
			{
				ObjectComposition objectComposition = client.getObjectComposition(bank.getId());
				int[] ids = objectComposition.getImpostorIds();

				if (ids != null && ids.length > 0)
				{
					ObjectComposition imposter = objectComposition.getImpostor();

					if (imposter != null)
					{
						objectComposition = imposter;
					}
				}

				int index = 0;
				for (String action : objectComposition.getActions())
				{
					if (action != null && (action.equalsIgnoreCase("bank") || action.equalsIgnoreCase("use")))
					{
						break;
					}

					index++;
				}

				chinManagerPlugin.menuAction(
					menuOptionClicked,
					"Bank",
					"",
					bank.getId(),
					MenuAction.of(MenuAction.GAME_OBJECT_FIRST_OPTION.getId() + index),
					ChinManagerPlugin.getLocation(bank).getX(),
					ChinManagerPlugin.getLocation(bank).getY()
				);

				teleportState = TeleportState.OPENING_BANK;
			}
			else if (banker != null)
			{
				int index = 0;
				for (String action : client.getNpcComposition(banker.getId()).getActions())
				{
					if (action != null && action.equalsIgnoreCase("bank"))
					{
						break;
					}

					index++;
				}

				chinManagerPlugin.menuAction(
					menuOptionClicked,
					"Bank",
					"",
					banker.getIndex(),
					MenuAction.of(MenuAction.NPC_FIRST_OPTION.getId() + index),
					0,
					0
				);

				teleportState = TeleportState.OPENING_BANK;
			}
			else
			{
				menuOptionClicked.consume();
			}
		}
		else if (teleportState == TeleportState.CLOSE_BANK)
		{
			Widget bankContainerChild = client.getWidget(12, 2);

			teleportState = TeleportState.NONE;

			if (bankContainerChild == null)
			{
				return;
			}

			chinManagerPlugin.menuAction(
				menuOptionClicked,
				"Close",
				"",
				1,
				MenuAction.CC_OP,
				11,
				bankContainerChild.getId()
			);
		}
		else if (teleportState == TeleportState.FETCH_POH)
		{
			Widget bankContainer = client.getWidget(WidgetInfo.BANK_ITEM_CONTAINER);

			teleportState = TeleportState.NONE;

			if (bankContainer == null)
			{
				return;
			}

			Set<Integer> items = new HashSet<>();

			if (teleportsConfig.pohTeleport() == Poh.TELEPORT_TABLET)
			{
				items.add(ItemID.TELEPORT_TO_HOUSE);
			}
			else if (teleportsConfig.pohTeleport() == Poh.RUNES)
			{
				for (Runes rune : getRunesForTeleport(Location.POH))
				{
					items.add(runeOrRunepouch(rune));
				}
			}
			else if (teleportsConfig.pohTeleport() == Poh.CONSTRUCTION_CAPE)
			{
				items.add(
					chinManagerPlugin.getLowestItemMatch(
						CONSTRUCT_CAPE
					)
				);
			}

			log.debug("All items: {}", items);

			for (int item : items)
			{
				if (hasBankInventoryItem(item, client))
				{
					continue;
				}

				log.debug("Fetch item: {}", item);

				ItemComposition itemComposition = itemManager.getItemComposition(item);

				log.debug("Fetch item: {} - stack: {}", itemComposition.getName(), itemComposition.isStackable());

				chinManagerPlugin.menuAction(
					menuOptionClicked,
					"Withdraw-All",
					"Withdraw-All",
					itemComposition.isStackable() ? 7 : 1,
					itemComposition.isStackable() ? MenuAction.CC_OP_LOW_PRIORITY : MenuAction.CC_OP,
					getFirstBankItemsPos(item, client),
					bankContainer.getId()
				);

				break;
			}
		}
		else if (teleportState == TeleportState.FETCH_TELEPORT)
		{
			Widget bankContainer = client.getWidget(WidgetInfo.BANK_ITEM_CONTAINER);

			teleportState = TeleportState.NONE;

			if (bankContainer == null)
			{
				return;
			}

			chinManagerPlugin.menuAction(
				menuOptionClicked,
				"Withdraw-1",
				"Withdraw-1",
				1,
				MenuAction.CC_OP,
				getFirstBankItemsPos(chinManagerPlugin.getLowestItemMatch(getTeleportJewellery(chinManager.getTeleportingLocation())), client),
				bankContainer.getId()
			);
		}
		else if (teleportState == TeleportState.TELEPORT_MENU)
		{
			teleportState = TeleportState.TELEPORT_MENU_CLICK;

			Widget inventory = client.getWidget(WidgetInfo.INVENTORY);

			if (inventory == null)
			{
				teleportState = TeleportState.NONE;
				return;
			}

			if (needRingOfDueling(chinManager.getTeleportingLocation()))
			{
				chinManagerPlugin.menuAction(
					menuOptionClicked,
					"Rub",
					"<col=ff9040>Ring of dueling",
					getInventoryItemsMap(RINGS_OF_DUELING, client).keySet().stream().findFirst().get(),
					MenuAction.ITEM_FOURTH_OPTION,
					getFirstInventoryItemsPos(RINGS_OF_DUELING, client),
					inventory.getId()
				);
			}
			else if (needGamesNecklace(chinManager.getTeleportingLocation()))
			{
				chinManagerPlugin.menuAction(
					menuOptionClicked,
					"Rub",
					"<col=ff9040>Games of necklace",
					getInventoryItemsMap(GAMES_NECKLACES, client).keySet().stream().findFirst().get(),
					MenuAction.ITEM_FOURTH_OPTION,
					getFirstInventoryItemsPos(GAMES_NECKLACES, client),
					inventory.getId()
				);
			}
			else if (needCombatBracelet(chinManager.getTeleportingLocation()))
			{
				chinManagerPlugin.menuAction(
					menuOptionClicked,
					"Rub",
					"<col=ff9040>Combat bracelet",
					getInventoryItemsMap(COMBAT_BRACELETS, client).keySet().stream().findFirst().get(),
					MenuAction.ITEM_FOURTH_OPTION,
					getFirstInventoryItemsPos(COMBAT_BRACELETS, client),
					inventory.getId()
				);
			}
			else if (needSkillsNecklace(chinManager.getTeleportingLocation()))
			{
				chinManagerPlugin.menuAction(
					menuOptionClicked,
					"Rub",
					"<col=ff9040>Skills necklace",
					getInventoryItemsMap(SKILLS_NECKLACES, client).keySet().stream().findFirst().get(),
					MenuAction.ITEM_FOURTH_OPTION,
					getFirstInventoryItemsPos(SKILLS_NECKLACES, client),
					inventory.getId()
				);
			}
			else if (needRingOfWealth(chinManager.getTeleportingLocation()))
			{
				chinManagerPlugin.menuAction(
					menuOptionClicked,
					"Rub",
					"<col=ff9040>Ring of wealth",
					getInventoryItemsMap(RINGS_OF_WEALTH, client).keySet().stream().findFirst().get(),
					MenuAction.ITEM_FOURTH_OPTION,
					getFirstInventoryItemsPos(RINGS_OF_WEALTH, client),
					inventory.getId()
				);
			}
			else if (needAmuletOfGlory(chinManager.getTeleportingLocation()))
			{
				chinManagerPlugin.menuAction(
					menuOptionClicked,
					"Rub",
					"<col=ff9040>Amulet of glory",
					getInventoryItemsMap(AMULETS_OF_GLORY, client).keySet().stream().findFirst().get(),
					MenuAction.ITEM_FOURTH_OPTION,
					getFirstInventoryItemsPos(AMULETS_OF_GLORY, client),
					inventory.getId()
				);
			}
			else if (needDigsitePendant(chinManager.getTeleportingLocation()))
			{
				chinManagerPlugin.menuAction(
					menuOptionClicked,
					"Rub",
					"<col=ff9040>Digsite pendant",
					getInventoryItemsMap(DIGSIDE_PENDANTS, client).keySet().stream().findFirst().get(),
					MenuAction.ITEM_FOURTH_OPTION,
					getFirstInventoryItemsPos(DIGSIDE_PENDANTS, client),
					inventory.getId()
				);
			}
			else if (needXericsTalisman(chinManager.getTeleportingLocation()))
			{
				chinManagerPlugin.menuAction(
					menuOptionClicked,
					"Rub",
					"<col=ff9040>Keric's talisman",
					getInventoryItemsMap(XERICS_TALISMAN, client).keySet().stream().findFirst().get(),
					MenuAction.ITEM_THIRD_OPTION,
					getFirstInventoryItemsPos(XERICS_TALISMAN, client),
					inventory.getId()
				);
			}

			teleportState = TeleportState.TELEPORT_DIALOG;
		}
		else if (teleportState == TeleportState.TELEPORT_POH)
		{
			if (teleportsConfig.pohTeleport() == Poh.TELEPORT_TABLET)
			{
				Widget inventory = client.getWidget(WidgetInfo.INVENTORY);

				if (inventory == null)
				{
					teleportState = TeleportState.NONE;
					return;
				}

				chinManagerPlugin.menuAction(
					menuOptionClicked,
					"Break",
					"Break",
					ItemID.TELEPORT_TO_HOUSE,
					MenuAction.ITEM_FIRST_OPTION,
					getFirstInventoryItemsPos(ItemID.TELEPORT_TO_HOUSE, client),
					inventory.getId()
				);
			}
			else if (teleportsConfig.pohTeleport() == Poh.RUNES)
			{
				Widget teleportToHouse = client.getWidget(WidgetInfo.SPELL_TELEPORT_TO_HOUSE);

				if (teleportToHouse == null)
				{
					teleportState = TeleportState.NONE;
					return;
				}

				chinManagerPlugin.menuAction(
					menuOptionClicked,
					"Cast",
					"<col=00ff00>Teleport to House</col>",
					1,
					MenuAction.CC_OP,
					-1,
					teleportToHouse.getId()
				);
			}
			else if (teleportsConfig.pohTeleport() == Poh.CONSTRUCTION_CAPE)
			{
				if (client.getVar(VarClientInt.INVENTORY_TAB) == 3)
				{
					Widget inventory = client.getWidget(WidgetInfo.INVENTORY);

					if (inventory == null)
					{
						menuOptionClicked.consume();
						teleportState = TeleportState.NONE;
						return;
					}

					chinManagerPlugin.menuAction(
						menuOptionClicked,
						"Teleport",
						"<col=ff9040>Construct. cape",
						chinManagerPlugin.getLowestItemMatch(CONSTRUCT_CAPE),
						MenuAction.ITEM_FOURTH_OPTION,
						getFirstInventoryItemsPos(CONSTRUCT_CAPE, client),
						inventory.getId()
					);
				}
				else if (client.getVar(VarClientInt.INVENTORY_TAB) == 4)
				{
					Widget equipmentCape = client.getWidget(WidgetInfo.EQUIPMENT_CAPE);

					if (equipmentCape == null)
					{
						menuOptionClicked.consume();
						teleportState = TeleportState.NONE;
						return;
					}

					chinManagerPlugin.menuAction(
						menuOptionClicked,
						"Tele to POH",
						"<col=ff9040>Construct. cape",
						4,
						MenuAction.CC_OP,
						-1,
						equipmentCape.getId()
					);
				}
				else
				{
					menuOptionClicked.consume();
				}
			}

			teleportState = TeleportState.TELEPORT_POH_WAIT;
		}
		else if (teleportState == TeleportState.TELEPORT_EQUIPMENT)
		{
			teleportState = TeleportState.TELEPORT_EQUIPMENT_CLICK;

			Widget widget = null;

			if (needGamesNecklace(chinManager.getTeleportingLocation()) ||
				needSkillsNecklace(chinManager.getTeleportingLocation()) ||
				needAmuletOfGlory(chinManager.getTeleportingLocation()) ||
				needXericsTalisman(chinManager.getTeleportingLocation()) ||
				needDigsitePendant(chinManager.getTeleportingLocation()))
			{
				widget = client.getWidget(WidgetInfo.EQUIPMENT_AMULET);
			}
			else if (needRingOfDueling(chinManager.getTeleportingLocation()) ||
				needRingOfWealth(chinManager.getTeleportingLocation()))
			{
				widget = client.getWidget(WidgetInfo.EQUIPMENT_RING);
			}
			else if (needCombatBracelet(chinManager.getTeleportingLocation()))
			{
				widget = client.getWidget(WidgetInfo.EQUIPMENT_GLOVES);
			}

			if (widget == null)
			{
				menuOptionClicked.consume();
				teleportState = TeleportState.TELEPORT_EQUIPMENT;
				return;
			}

			int identifier = -1;
			MenuAction menuAction = MenuAction.CC_OP;

			Location location = chinManager.getTeleportingLocation();

			if (location == Location.DUEL_ARENA)
			{
				identifier = 2;
			}
			else if (location == Location.CASTLE_WARS)
			{
				identifier = 3;
			}
			else if (location == Location.FEROX_ENCLAVE)
			{
				identifier = 4;
			}
			else if (location == Location.BURTHORPE)
			{
				identifier = 2;
			}
			else if (location == Location.BARBARIAN_OUTPOST)
			{
				identifier = 3;
			}
			else if (location == Location.CORPOREAL_BEAST)
			{
				identifier = 4;
			}
			else if (location == Location.WINTERTODT)
			{
				identifier = 6;
				menuAction = MenuAction.CC_OP_LOW_PRIORITY;
			}
			else if (location == Location.WARRIORS_GUILD)
			{
				identifier = 2;
			}
			else if (location == Location.CHAMPIONS_GUILD)
			{
				identifier = 3;
			}
			else if (location == Location.EDGEVILLE_MONASTERY)
			{
				identifier = 4;
			}
			else if (location == Location.RANGING_GUILD)
			{
				identifier = 5;
			}
			else if (location == Location.MISCELLANIA)
			{
				identifier = 2;
			}
			else if (location == Location.GRAND_EXCHANGE)
			{
				identifier = 3;
			}
			else if (location == Location.FALADOR_PARK)
			{
				identifier = 4;
			}
			else if (location == Location.DONDAKANS_ROCK)
			{
				identifier = 5;
			}
			else if (location == Location.EDGEVILLE)
			{
				identifier = 2;
			}
			else if (location == Location.KARAMJA)
			{
				identifier = 3;
			}
			else if (location == Location.DRAYNOR_VILLAGE)
			{
				identifier = 4;
			}
			else if (location == Location.AL_KHARID)
			{
				identifier = 5;
			}
			else if (location == Location.DIGSITE)
			{
				identifier = 2;
			}
			else if (location == Location.FOSSIL_ISLAND)
			{
				identifier = 3;
			}
			else if (location == Location.FISHING_GUILD)
			{
				identifier = 2;
			}
			else if (location == Location.MINING_GUILD)
			{
				identifier = 3;
			}
			else if (location == Location.CRAFTING_GUILD)
			{
				identifier = 4;
			}
			else if (location == Location.COOKS_GUILD)
			{
				identifier = 5;
			}
			else if (location == Location.WOODCUTTING_GUILD)
			{
				identifier = 6;
				menuAction = MenuAction.CC_OP_LOW_PRIORITY;
			}
			else if (location == Location.FARMING_GUILD)
			{
				identifier = 7;
				menuAction = MenuAction.CC_OP_LOW_PRIORITY;
			}
			else if (location == Location.XERICS_LOOKOUT)
			{
				identifier = 2;
			}
			else if (location == Location.XERICS_GLADE)
			{
				identifier = 3;
			}
			else if (location == Location.XERICS_INFERNO)
			{
				identifier = 4;
			}
			else if (location == Location.XERICS_HEART)
			{
				identifier = 5;
			}
			else if (location == Location.XERICS_HONOUR)
			{
				identifier = 6;
				menuAction = MenuAction.CC_OP_LOW_PRIORITY;
			}

			chinManagerPlugin.menuAction(
				menuOptionClicked,
				"",
				"",
				identifier,
				menuAction,
				-1,
				widget.getId()
			);

			teleportState = TeleportState.TELEPORT_WAIT;
		}
		else if (teleportState == TeleportState.CLICK_POH_TELEPORT)
		{
			TileObject teleportObject = chinManagerPlugin.getObject(getPohTeleportObject(chinManager.getTeleportingLocation()));

			if (teleportObject == null)
			{
				menuOptionClicked.consume();
				teleportState = TeleportState.NONE;
				return;
			}

			teleportState = TeleportState.POH_WAIT_TELEPORT_MENU;

			chinManagerPlugin.menuAction(
				menuOptionClicked,
				"Teleport menu",
				"",
				teleportObject.getId(),
				MenuAction.GAME_OBJECT_SECOND_OPTION,
				ChinManagerPlugin.getLocation(teleportObject).getX(),
				ChinManagerPlugin.getLocation(teleportObject).getY()
			);
		}
		else if (teleportState == TeleportState.SELECT_MINIGAME)
		{
			Widget questTabQuest = client.getWidget(WidgetInfo.QUESTTAB_QUEST_TAB);

			if (questTabQuest == null)
			{
				teleportState = TeleportState.NONE;
				return;
			}

			chinManagerPlugin.menuAction(
				menuOptionClicked,
				"Minigame List",
				"Empty",
				1,
				MenuAction.CC_OP,
				-1,
				questTabQuest.getId() + 10
			);
		}
		else if (teleportState == TeleportState.MINIGAME_TELEPORT)
		{
			int id;

			switch (chinManager.getTeleportingLocation())
			{
				case NMZ:
					id = 10;
					break;

				case TITHE_FARM:
					id = 19;
					break;

				case LMS:
					id = 9;
					break;

				case PEST_CONTROL:
					id = 11;
					break;

				case SOUL_WARS:
					id = 17;
					break;

				default:
					id = -1;
			}

			Widget widget = client.getWidget(WidgetInfo.MINIGAME_TELEPORT_BUTTON);

			teleportState = TeleportState.MINIGAME_TELEPORT_WAIT;

			chinManagerPlugin.menuAction(
				menuOptionClicked,
				"Teleport",
				"",
				1,
				MenuAction.CC_OP,
				id,
				widget.getId()
			);
		}
	}

	private void jewelleryLogic(List<Integer> items)
	{
		ArrayList<Integer> equipmentItems = getEquipment();

		if (client.getItemContainer(InventoryID.BANK) != null)
		{
			if (!hasAnyBankInventoryItem(items, client))
			{
				if (hasAnyBankItem(items, client))
				{
					teleportState = TeleportState.FETCH_TELEPORT;
					disposables.add(chinManagerPlugin.getTaskExecutor().prepareTask(new ClickTask(chinManagerPlugin)).ignoreElements().subscribe());
				}
			}
			else
			{
				teleportState = TeleportState.CLOSE_BANK;
				disposables.add(chinManagerPlugin.getTaskExecutor().prepareTask(new ClickTask(chinManagerPlugin)).ignoreElements().subscribe());
			}
		}
		else if (hasAnyItem(items, client))
		{
			teleportState = TeleportState.TELEPORT_MENU;
			disposables.add(chinManagerPlugin.getTaskExecutor().prepareTask(new ClickTask(chinManagerPlugin)).ignoreElements().subscribe());
		}
		else if (equipmentItems.stream().anyMatch(items::contains))
		{
			teleportState = TeleportState.EQUIPMENT;
		}
		else if (ChinManagerPlugin.isAtBank(client))
		{
			teleportState = TeleportState.OPEN_BANK;
		}
	}

	private void pohLogic()
	{
		Set<Integer> items = new HashSet<>();

		if (teleportsConfig.pohTeleport() == Poh.TELEPORT_TABLET)
		{
			items.add(ItemID.TELEPORT_TO_HOUSE);
		}
		else if (teleportsConfig.pohTeleport() == Poh.RUNES)
		{
			for (Runes rune : getRunesForTeleport(Location.POH))
			{
				items.add(runeOrRunepouch(rune));
			}
		}
		else if (teleportsConfig.pohTeleport() == Poh.CONSTRUCTION_CAPE)
		{
			items.add(
				chinManagerPlugin.getLowestItemMatch(
					ChinManagerPlugin.CONSTRUCT_CAPE
				)
			);
		}

		if (client.getItemContainer(InventoryID.BANK) != null)
		{
			for (int item : items)
			{
				if (!hasAnyBankInventoryItem(item, client))
				{
					if (hasAnyBankItem(item, client))
					{
						teleportState = TeleportState.FETCH_POH;
						disposables.add(chinManagerPlugin.getTaskExecutor().prepareTask(new ClickTask(chinManagerPlugin)).ignoreElements().subscribe());

						return;
					}
				}
			}

			teleportState = TeleportState.CLOSE_BANK;
			disposables.add(chinManagerPlugin.getTaskExecutor().prepareTask(new ClickTask(chinManagerPlugin)).ignoreElements().subscribe());
		}
		else if (hasItems(List.copyOf(items), client))
		{
			ArrayList<Integer> equipmentItems = getEquipment();

			if (teleportsConfig.pohTeleport() == Poh.RUNES && client.getVar(VarClientInt.INVENTORY_TAB) != 6)
			{
				client.runScript(915, 6);
			}
			else if (teleportsConfig.pohTeleport() == Poh.CONSTRUCTION_CAPE && equipmentItems.stream().anyMatch(CONSTRUCT_CAPE::contains) && client.getVar(VarClientInt.INVENTORY_TAB) != 4)
			{
				client.runScript(915, 4);
			}

			teleportState = TeleportState.TELEPORT_POH;
			disposables.add(chinManagerPlugin.getTaskExecutor().prepareTask(new ClickTask(chinManagerPlugin)).ignoreElements().subscribe());
		}
		else if (ChinManagerPlugin.isAtBank(client))
		{
			teleportState = TeleportState.OPEN_BANK;
		}
	}

	private void minigameLogic()
	{
		if (client.getItemContainer(InventoryID.BANK) != null)
		{
			teleportState = TeleportState.CLOSE_BANK;
			disposables.add(chinManagerPlugin.getTaskExecutor().prepareTask(new ClickTask(chinManagerPlugin)).ignoreElements().subscribe());
		}
		else
		{
			if (client.getVar(VarClientInt.INVENTORY_TAB) != 2)
			{
				client.runScript(915, 2);
			}

			teleportState = TeleportState.SELECT_MINIGAME;
		}
	}

	private boolean needRingOfDueling(Location location)
	{
		switch (location)
		{
			case DUEL_ARENA:
			case CASTLE_WARS:
			case FEROX_ENCLAVE:
				return teleportsConfig.ringOfDuelingTeleport() != RingOfDueling.POH;

			default:
				return false;
		}
	}

	private boolean needGamesNecklace(Location location)
	{
		switch (location)
		{
			case BURTHORPE:
			case BARBARIAN_OUTPOST:
			case CORPOREAL_BEAST:
			case CHASM_OF_TEARS:
			case WINTERTODT:
				return teleportsConfig.gamesNecklaceTeleport() != GamesNecklace.POH;

			default:
				return false;
		}
	}

	private boolean needCombatBracelet(Location location)
	{
		switch (location)
		{
			case WARRIORS_GUILD:
			case CHAMPIONS_GUILD:
			case EDGEVILLE_MONASTERY:
			case RANGING_GUILD:
				return teleportsConfig.combatBraceletTeleport() != CombatBracelet.POH;

			default:
				return false;
		}
	}

	private boolean needSkillsNecklace(Location location)
	{
		switch (location)
		{
			case FISHING_GUILD:
			case MINING_GUILD:
			case CRAFTING_GUILD:
			case COOKS_GUILD:
			case WOODCUTTING_GUILD:
			case FARMING_GUILD:
				return teleportsConfig.skillsNecklaceTeleport() != SkillsNecklace.POH;

			default:
				return false;
		}
	}

	private boolean needRingOfWealth(Location location)
	{
		switch (location)
		{
			case MISCELLANIA:
			case GRAND_EXCHANGE:
			case FALADOR_PARK:
			case DONDAKANS_ROCK:
				return teleportsConfig.ringOfWealthTeleport() != RingOfWealth.POH;

			default:
				return false;
		}
	}

	private boolean needAmuletOfGlory(Location location)
	{
		switch (location)
		{
			case EDGEVILLE:
			case KARAMJA:
			case DRAYNOR_VILLAGE:
			case AL_KHARID:
				return teleportsConfig.amuletOfGloryTeleport() != AmuletOfGlory.POH;

			default:
				return false;
		}
	}

	private boolean needXericsTalisman(Location location)
	{
		switch (location)
		{
			case XERICS_LOOKOUT:
			case XERICS_GLADE:
			case XERICS_INFERNO:
			case XERICS_HEART:
			case XERICS_HONOUR:
				return teleportsConfig.xericsTalismanTeleport() != XericsTalisman.POH;

			default:
				return false;
		}
	}

	private boolean needDigsitePendant(Location location)
	{
		switch (location)
		{
			case DIGSITE:
			case FOSSIL_ISLAND:
				return teleportsConfig.digsitePendantTeleport() != DigsitePendant.POH;

			default:
				return false;
		}
	}

	private List<Integer> getTeleportJewellery(Location location)
	{
		switch (location)
		{
			case DUEL_ARENA:
			case CASTLE_WARS:
			case FEROX_ENCLAVE:
				return RINGS_OF_DUELING;

			case BURTHORPE:
			case BARBARIAN_OUTPOST:
			case CORPOREAL_BEAST:
			case CHASM_OF_TEARS:
			case WINTERTODT:
				return GAMES_NECKLACES;

			case WARRIORS_GUILD:
			case CHAMPIONS_GUILD:
			case EDGEVILLE_MONASTERY:
			case RANGING_GUILD:
				return COMBAT_BRACELETS;

			case FISHING_GUILD:
			case MINING_GUILD:
			case CRAFTING_GUILD:
			case COOKS_GUILD:
			case WOODCUTTING_GUILD:
			case FARMING_GUILD:
				return SKILLS_NECKLACES;

			case MISCELLANIA:
			case GRAND_EXCHANGE:
			case FALADOR_PARK:
			case DONDAKANS_ROCK:
				return RINGS_OF_WEALTH;

			case EDGEVILLE:
			case KARAMJA:
			case DRAYNOR_VILLAGE:
			case AL_KHARID:
				return AMULETS_OF_GLORY;

			case XERICS_LOOKOUT:
			case XERICS_GLADE:
			case XERICS_INFERNO:
			case XERICS_HEART:
			case XERICS_HONOUR:
				return XERICS_TALISMAN;

			case DIGSITE:
			case FOSSIL_ISLAND:
				return DIGSIDE_PENDANTS;

			default:
				return Collections.emptyList();
		}
	}

	private boolean isMinigameTeleport(Location location)
	{
		switch (location)
		{
			case NMZ:
			case TITHE_FARM:
			case LMS:
			case PEST_CONTROL:
			case SOUL_WARS:
				return true;

			default:
				return false;
		}
	}

	private List<Integer> getPohTeleportObject(Location location)
	{
		switch (location)
		{
			case DUEL_ARENA:
			case CASTLE_WARS:
			case FEROX_ENCLAVE:
				return List.of(NullObjectID.NULL_29154, NullObjectID.NULL_29155, NullObjectID.NULL_29156);

			case BURTHORPE:
			case BARBARIAN_OUTPOST:
			case CORPOREAL_BEAST:
			case CHASM_OF_TEARS:
			case WINTERTODT:
				return List.of(NullObjectID.NULL_29154, NullObjectID.NULL_29155, NullObjectID.NULL_29156);

			case WARRIORS_GUILD:
			case CHAMPIONS_GUILD:
			case EDGEVILLE_MONASTERY:
			case RANGING_GUILD:
				return List.of(NullObjectID.NULL_29154, NullObjectID.NULL_29155, NullObjectID.NULL_29156);

			case FISHING_GUILD:
			case MINING_GUILD:
			case CRAFTING_GUILD:
			case COOKS_GUILD:
			case WOODCUTTING_GUILD:
			case FARMING_GUILD:
				return List.of(NullObjectID.NULL_29154, NullObjectID.NULL_29155, NullObjectID.NULL_29156);

			case MISCELLANIA:
			case GRAND_EXCHANGE:
			case FALADOR_PARK:
			case DONDAKANS_ROCK:
				return List.of(NullObjectID.NULL_29154, NullObjectID.NULL_29155, NullObjectID.NULL_29156);

			case EDGEVILLE:
			case KARAMJA:
			case DRAYNOR_VILLAGE:
			case AL_KHARID:
				return List.of(NullObjectID.NULL_29154, NullObjectID.NULL_29155, NullObjectID.NULL_29156);

			case XERICS_LOOKOUT:
			case XERICS_GLADE:
			case XERICS_INFERNO:
			case XERICS_HEART:
			case XERICS_HONOUR:
				return List.of(ObjectID.XERICS_TALISMAN, ObjectID.XERICS_TALISMAN_33412, ObjectID.XERICS_TALISMAN_33413, ObjectID.XERICS_TALISMAN_33414, ObjectID.XERICS_TALISMAN_33415, ObjectID.XERICS_TALISMAN_33419);

			case DIGSITE:
			case FOSSIL_ISLAND:
				return List.of(ObjectID.DIGSITE_PENDANT, ObjectID.DIGSITE_PENDANT_33417, ObjectID.DIGSITE_PENDANT_33418, ObjectID.DIGSITE_PENDANT_33420);

			default:
				return Collections.emptyList();
		}
	}

	private boolean needPohTeleport(Location location)
	{
		switch (location)
		{
			case DUEL_ARENA:
			case CASTLE_WARS:
			case FEROX_ENCLAVE:
				return teleportsConfig.ringOfDuelingTeleport() == RingOfDueling.POH;

			case BURTHORPE:
			case BARBARIAN_OUTPOST:
			case CORPOREAL_BEAST:
			case CHASM_OF_TEARS:
			case WINTERTODT:
				return teleportsConfig.gamesNecklaceTeleport() == GamesNecklace.POH;

			case WARRIORS_GUILD:
			case CHAMPIONS_GUILD:
			case EDGEVILLE_MONASTERY:
			case RANGING_GUILD:
				return teleportsConfig.combatBraceletTeleport() == CombatBracelet.POH;

			case FISHING_GUILD:
			case MINING_GUILD:
			case CRAFTING_GUILD:
			case COOKS_GUILD:
			case WOODCUTTING_GUILD:
			case FARMING_GUILD:
				return teleportsConfig.skillsNecklaceTeleport() == SkillsNecklace.POH;

			case MISCELLANIA:
			case GRAND_EXCHANGE:
			case FALADOR_PARK:
			case DONDAKANS_ROCK:
				return teleportsConfig.ringOfWealthTeleport() == RingOfWealth.POH;

			case EDGEVILLE:
			case KARAMJA:
			case DRAYNOR_VILLAGE:
			case AL_KHARID:
				return teleportsConfig.amuletOfGloryTeleport() == AmuletOfGlory.POH;

			case XERICS_LOOKOUT:
			case XERICS_GLADE:
			case XERICS_INFERNO:
			case XERICS_HEART:
			case XERICS_HONOUR:
				return teleportsConfig.xericsTalismanTeleport() == XericsTalisman.POH;

			case DIGSITE:
			case FOSSIL_ISLAND:
				return teleportsConfig.digsitePendantTeleport() == DigsitePendant.POH;

			default:
				return false;
		}
	}

	private Set<Runes> getRunesForTeleport(Location location)
	{
		Set<Runes> runes = new HashSet<>();

		switch (location)
		{
			case POH:
				runes.add(Runes.AIR);
				runes.add(Runes.EARTH);
				runes.add(Runes.LAW);

				break;

			case LUMBRIDGE_GRAVEYARD:
				runes.add(Runes.EARTH);
				runes.add(Runes.LAW);

				break;

			case DRAYNOR_MANOR:
				runes.add(Runes.EARTH);
				runes.add(Runes.WATER);
				runes.add(Runes.LAW);

				break;

			case BATTLEFRONT:
				runes.add(Runes.EARTH);
				runes.add(Runes.FIRE);
				runes.add(Runes.LAW);

				break;

			case VARROCK:
				runes.add(Runes.AIR);
				runes.add(Runes.FIRE);
				runes.add(Runes.LAW);

				break;

			case MIND_ALTAR:
				runes.add(Runes.MIND);
				runes.add(Runes.LAW);

				break;

			case LUMBRIDGE:
				runes.add(Runes.AIR);
				runes.add(Runes.EARTH);
				runes.add(Runes.LAW);

				break;

			case FALADOR:
				runes.add(Runes.AIR);
				runes.add(Runes.WATER);
				runes.add(Runes.LAW);

				break;

			case SALVE_GRAVEYARD:
				runes.add(Runes.LAW);
				runes.add(Runes.SOUL);

				break;

			case CAMELOT_TELEPORT:
				runes.add(Runes.AIR);
				runes.add(Runes.LAW);

				break;

			case FENKENSTRAINS_CASTLE:
				runes.add(Runes.EARTH);
				runes.add(Runes.LAW);
				runes.add(Runes.SOUL);

				break;

			case EAST_ARDOUGNE:
				runes.add(Runes.WATER);
				runes.add(Runes.LAW);

				break;

			case WATCHTOWER_TELEPORT:
				runes.add(Runes.EARTH);
				runes.add(Runes.LAW);

				break;

			case SENNTISTEN:
				runes.add(Runes.LAW);
				runes.add(Runes.SOUL);

				break;

			case WEST_ARDOUGNE:
				runes.add(Runes.LAW);
				runes.add(Runes.SOUL);

				break;

			case MARIM:
				runes.add(Runes.FIRE);
				runes.add(Runes.WATER);
				runes.add(Runes.LAW);
				runes.add(Runes.BANANA);

				break;

			case HARMONY_ISLAND:
				runes.add(Runes.LAW);
				runes.add(Runes.NATURE);
				runes.add(Runes.SOUL);

				break;

			case KHARYLL:
				runes.add(Runes.BLOOD);
				runes.add(Runes.LAW);

				break;

			case LUNAR_ISLE:
				runes.add(Runes.EARTH);
				runes.add(Runes.ASTRAL);
				runes.add(Runes.LAW);

				break;

			case KOUREND_CASTLE:
				runes.add(Runes.FIRE);
				runes.add(Runes.WATER);
				runes.add(Runes.LAW);
				runes.add(Runes.SOUL);

				break;

			case CEMETERY:
				runes.add(Runes.BLOOD);
				runes.add(Runes.LAW);
				runes.add(Runes.SOUL);

				break;

			case WATERBIRTH_ISLAND:
				runes.add(Runes.WATER);
				runes.add(Runes.ASTRAL);
				runes.add(Runes.LAW);

				break;

			case BARROWS:
				runes.add(Runes.BLOOD);
				runes.add(Runes.LAW);
				runes.add(Runes.SOUL);

				break;

			case CARRALANGAR:
				runes.add(Runes.LAW);
				runes.add(Runes.SOUL);

				break;

			case CATHERBY:
				runes.add(Runes.WATER);
				runes.add(Runes.ASTRAL);
				runes.add(Runes.LAW);

				break;

			case ANNAKARL:
				runes.add(Runes.BLOOD);
				runes.add(Runes.LAW);

				break;

			case APE_ATOLL_DUNGEON:
				runes.add(Runes.BLOOD);
				runes.add(Runes.LAW);
				runes.add(Runes.SOUL);

				break;

			case GHORROCK:
				runes.add(Runes.WATER);
				runes.add(Runes.LAW);

				break;

			case TROLL_STRONGHOLD:
				runes.add(Runes.STONYY_BASALT);

				break;

			case WEISS:
				runes.add(Runes.ICY_BASALT);

				break;
		}

		return runes;
	}

	private int runeOrRunepouch(Runes runes)
	{
		if (!hasAnyItem(RUNE_POUCHES, client) && !hasAnyBankInventoryItem(RUNE_POUCHES, client) && !hasAnyBankItem(RUNE_POUCHES, client))
		{
			return runes.getItemId();
		}

		for (int i = 0; i < AMOUNT_VARBITS.length; i++)
		{
			Varbits amountVarbit = AMOUNT_VARBITS[i];

			int amount = client.getVar(amountVarbit);
			if (amount <= 0)
			{
				continue;
			}

			Varbits runeVarbit = RUNE_VARBITS[i];
			int runeId = client.getVar(runeVarbit);
			Runes rune = Runes.getRune(runeId);
			if (rune == null)
			{
				continue;
			}

			if (rune.getItemId() == runes.getItemId())
			{
				for (int runePouch : RUNE_POUCHES)
				{
					if (hasItem(runePouch, client) || hasBankInventoryItem(runePouch, client) || hasBankItem(runePouch, client))
					{
						return runePouch;
					}
				}
			}
		}

		return runes.getItemId();
	}

	private ArrayList<Integer> getEquipment()
	{
		ItemContainer itemContainer = client.getItemContainer(InventoryID.EQUIPMENT);
		ArrayList<Integer> equipmentItems = new ArrayList<>();

		if (itemContainer != null)
		{
			for (final EquipmentInventorySlot slot : EquipmentInventorySlot.values())
			{
				int i = slot.getSlotIdx();
				Item item = itemContainer.getItem(i);

				if (item != null)
				{
					equipmentItems.add(item.getId());
				}
			}
		}

		return equipmentItems;
	}
}
