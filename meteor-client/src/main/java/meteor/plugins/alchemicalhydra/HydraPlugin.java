package meteor.plugins.alchemicalhydra;

import com.google.inject.Provides;
import lombok.AccessLevel;
import lombok.Getter;
import meteor.callback.ClientThread;
import meteor.eventbus.events.ConfigChanged;
import net.runelite.api.*;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.*;
import meteor.config.ConfigManager;
import meteor.eventbus.Subscribe;
import meteor.plugins.Plugin;
import meteor.plugins.PluginDescriptor;
import meteor.plugins.alchemicalhydra.Hydra.AttackStyle;
import meteor.ui.overlay.OverlayManager;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.*;
import java.util.Map.Entry;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;

@PluginDescriptor(
	name = "Alchemical Hydra",
	description = "Hydra things",
	tags = {"Hydra", "Lazy", "4 headed asshole"},
	enabledByDefault = false
)
@Singleton
public class HydraPlugin extends Plugin {
	private static final int[] HYDRA_REGIONS = {
		5279, 5280,
		5535, 5536
	};
	private static final int STUN_LENGTH = 7;

	@Getter(AccessLevel.PACKAGE)
	private Map<LocalPoint, Projectile> poisonProjectiles = new HashMap<>();

	@Getter(AccessLevel.PACKAGE)
	public Hydra hydra;
	public boolean immuneActive = true;

	private boolean inHydraInstance = false;
	private int lastAttackTick;

	public ArrayList<GraphicsObject> lightningList = new ArrayList<>();

	public GameObject redVent = null;
	public GameObject greenVent = null;
	public GameObject blueVent = null;
	private boolean inFight = false;
	public int ventTicks = 0;

	@Inject
	private Client client;

	@Inject
	private HydraConfig config;

	@Inject
	private HydraOverlay overlay;

	@Inject
	private HydraSceneOverlay sceneOverlay;

	@Inject
	private HydraExtraOverlay extraOverlay;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private ClientThread clientThread;

	@Provides
    public HydraConfig getConfig(ConfigManager configManager) {
		return configManager.getConfig(HydraConfig.class);
	}

	@Override
	public void startup() {
		reset();
		initConfig();
		inHydraInstance = checkArea();
		lastAttackTick = -1;
		poisonProjectiles.clear();
		this.overlayManager.add(extraOverlay);
	}

	@Override
	public void shutdown() {
		reset();
		inHydraInstance = false;
		hydra = null;
		poisonProjectiles.clear();
		removeOverlays();
		lastAttackTick = -1;
		this.overlayManager.remove(extraOverlay);
	}

	private void reset() {
		immuneActive = true;
		lightningList.clear();
		redVent = null;
		greenVent = null;
		blueVent = null;
		ventTicks = 0;
	}

	private void initConfig() {
		this.overlay.setSafeCol(config.safeCol());
		this.overlay.setMedCol(config.medCol());
		this.overlay.setBadCol(config.badCol());
		this.sceneOverlay.setPoisonBorder(config.poisonBorderCol());
		this.sceneOverlay.setPoisonFill(config.poisonCol());
		this.sceneOverlay.setBadFountain(config.fountainColA());
		this.sceneOverlay.setGoodFountain(config.fountainColB());
	}

	@Subscribe
	private void onConfigChanged(ConfigChanged event) {
		if (!event.getGroup().equals("betterHydra")) {
			return;
		}

		switch (event.getKey()) {
			case "safeCol":
				overlay.setSafeCol(config.safeCol());
				return;
			case "medCol":
				overlay.setMedCol(config.medCol());
				return;
			case "badCol":
				overlay.setBadCol(config.badCol());
				return;
			case "poisonBorderCol":
				sceneOverlay.setPoisonBorder(config.poisonBorderCol());
				break;
			case "poisonCol":
				sceneOverlay.setPoisonFill(config.poisonCol());
				break;
			case "fountainColA":
				sceneOverlay.setBadFountain(config.fountainColA());
				break;
			case "fountainColB":
				sceneOverlay.setGoodFountain(config.fountainColB());
				break;
		}
	}

	@Subscribe
	private void onGameStateChanged(GameStateChanged state) {
		if (state.getGameState() == GameState.LOGGED_IN) {
			inHydraInstance = checkArea();
			if (!inHydraInstance) {
				if (hydra != null) {
					removeOverlays();
					hydra = null;
				}
			}else{
				for (NPC npc : client.getNpcs()) {
					if (npc.getId() == NpcID.ALCHEMICAL_HYDRA) {
						hydra = new Hydra(npc);
						break;
					}
				}
				addOverlays();
			}
		}
		if (state.getGameState() == GameState.LOADING){
			inFight = false;
		}
	}

	@Subscribe
	private void onNpcSpawned(NpcSpawned event) {
		if (inHydraInstance) {
			if (event.getNpc().getId() == NpcID.ALCHEMICAL_HYDRA) {
				hydra = new Hydra(event.getNpc());
				addOverlays();
			}
		}

		if (event.getNpc().getId() == 8615) {
			immuneActive = true;
		}
	}

	@Subscribe
	private void onNpcDespawned(NpcDespawned event) {
		if (event.getNpc().getId() == 8615 || event.getNpc().getId() == 8616 || event.getNpc().getId() == 8617 ||
				event.getNpc().getId() == 8618 || event.getNpc().getId() == 8619 || event.getNpc().getId() == 8620 ||
				event.getNpc().getId() == 8621 || event.getNpc().getId() == 8622) {
			immuneActive = true;

		}
	}

	@Subscribe
	private void onNpcChanged(NpcChanged event) {
		if (event.getNpc().getId() == 8619 || event.getNpc().getId() == 8620) {
			immuneActive = true;
		}
	}

	@Subscribe
	private void onAnimationChanged(AnimationChanged animationChanged) {
		Actor actor = animationChanged.getActor();

		if(this.client.getLocalPlayer() != null)
		{
			if (animationChanged.getActor().getName() != null && animationChanged.getActor() instanceof NPC)
			{
				if (animationChanged.getActor().getName().toLowerCase().contains("alchemical hydra")
						&& (animationChanged.getActor().getAnimation() == 9111 || animationChanged.getActor().getAnimation() == 9112 || animationChanged.getActor().getAnimation() == 9113))
				{
					if (config.attackChange())
					{
						client.playSoundEffect(3924, this.config.attackChangeVolume());
					}
				}
			}

			else if (animationChanged.getActor() instanceof Player && animationChanged.getActor().getAnimation() == 839)
			{
				if(this.client.isInInstancedRegion()) {
					if (WorldPoint.fromLocalInstance(client, this.client.getLocalPlayer().getLocalLocation()).getRegionID() == 5536)
					{
						ventTicks = 10;
					}
				}else{
					ventTicks = 0;
				}
			}
		}

		if (!inHydraInstance || hydra == null || actor == client.getLocalPlayer()) {
			return;
		}

		HydraPhase phase = hydra.getPhase();

		if (actor.getAnimation() == phase.getDeathAnim2() && phase != HydraPhase.THREE || actor.getAnimation() == phase.getDeathAnim1() && phase == HydraPhase.THREE) {
			switch (phase) {
				case ONE:
					hydra.changePhase(HydraPhase.TWO);
					return;
				case TWO:
					hydra.changePhase(HydraPhase.THREE);
					return;
				case THREE:
					hydra.changePhase(HydraPhase.FOUR);
					return;
				case FOUR:
					hydra = null;
					poisonProjectiles.clear();
					removeOverlays();
					return;
			}
		} else if (actor.getAnimation() == phase.getSpecAnimationId() && phase.getSpecAnimationId() != 0) {
			hydra.setNextSpecial(hydra.getNextSpecial() + 9);
		}

		if (poisonProjectiles.isEmpty()) {
			return;
		}

		Set<LocalPoint> exPoisonProjectiles = new HashSet<>();
		for (Entry<LocalPoint, Projectile> entry : poisonProjectiles.entrySet()) {
			if (entry.getValue().getEndCycle() < client.getGameCycle()) {
				exPoisonProjectiles.add(entry.getKey());
			}
		}
		for (LocalPoint toRemove : exPoisonProjectiles) {
			poisonProjectiles.remove(toRemove);
		}
	}

	@Subscribe
	private void onProjectileMoved(ProjectileMoved event) {
		if (!inHydraInstance || hydra == null || client.getGameCycle() >= event.getProjectile().getStartMovementCycle()) {
			return;
		}

		Projectile projectile = event.getProjectile();
		int id = projectile.getId();

		if (hydra.getPhase().getSpecProjectileId() != 0 && hydra.getPhase().getSpecProjectileId() == id) {
			if (hydra.getAttackCount() == hydra.getNextSpecial()) {
				hydra.setNextSpecial(hydra.getNextSpecial() + 9);
			}
			poisonProjectiles.put(event.getPosition(), projectile);
		} else if (client.getTickCount() != lastAttackTick && (id == AttackStyle.MAGIC.getProjectileID() || id == AttackStyle.RANGED.getProjectileID())) {
			hydra.handleAttack(id);
			lastAttackTick = client.getTickCount();
		}
	}

	@Subscribe
	private void onChatMessage(ChatMessage event) {
		if (event.getMessage().equals("The chemicals neutralise the Alchemical Hydra's defences!")) {
			hydra.setWeakened(true);
			immuneActive = false;
		} else if (event.getMessage().equals("The Alchemical Hydra temporarily stuns you.")) {
			if (config.stun()) {
				overlay.setStunTicks(STUN_LENGTH);
			}
		}
	}

	@Subscribe
	private void onGameTick(GameTick tick) {
		if (overlay.getStunTicks() > 0) {
			overlay.setStunTicks(overlay.getStunTicks() - 1);
		}
		if (config.autoPray() && hydra != null && inFight && !client.isPrayerActive(hydra.getNextAttack().getPrayer()))
		{
			activatePrayer(hydra.getNextAttack().getPrayer());
			if (config.offensivePrayerToggle()){
				activatePrayer(config.offensivePrayer().getPrayer());
			}
		}
		if(ventTicks > 0){
			ventTicks--;
			if(ventTicks == 0){
				ventTicks = 8;
			}
		}
	}

	@Subscribe
	private void onGameObjectSpawned(GameObjectSpawned event) {
		if (event.getGameObject().getId() == 34568) {
			redVent = event.getGameObject();
		}else if (event.getGameObject().getId() == 34569) {
			greenVent = event.getGameObject();
		}else if (event.getGameObject().getId() == 34570) {
			blueVent = event.getGameObject();
		}
	}

	@Subscribe
	private void onWallObjestSpawned(WallObjectSpawned event){
		if (event.getWallObject().getId() == 34556){
			inFight = true;
		}
	}

	@Subscribe
	private void onGameObjectDespawned(GameObjectDespawned event) {
		if (event.getGameObject().getId() == 34568) {
			redVent = null;
		}else if (event.getGameObject().getId() == 34569) {
			greenVent = null;
		}else if (event.getGameObject().getId() == 34570) {
			blueVent = null;
		}
	}

	private boolean checkArea() {
		return Arrays.equals(client.getMapRegions(), HYDRA_REGIONS) && client.isInInstancedRegion();
	}

	private void addOverlays() {
		if (config.counting() || config.stun()) {
			overlayManager.add(overlay);
		}

		if (config.counting() || config.fountain()) {
			overlayManager.add(sceneOverlay);
		}
	}

	private void removeOverlays() {
		overlayManager.remove(overlay);
		overlayManager.remove(sceneOverlay);
	}

	public void activatePrayer(Prayer prayer) {
		if (prayer == null) {
			return;
		}

		//check if prayer is already active this tick
		if (client.isPrayerActive(prayer)) {
			return;
		}

		WidgetInfo widgetInfo = prayer.getWidgetInfo();

		if (widgetInfo == null) {
			return;
		}
		Widget prayer_widget = client.getWidget(widgetInfo);

		if (prayer_widget == null) {
			return;
		}

		if (client.getBoostedSkillLevel(Skill.PRAYER) <= 0) {
			return;
		}

		clientThread.invoke(() ->
				client.invokeMenuAction(
						"Activate",
						prayer_widget.getName(),
						1,
						MenuAction.CC_OP.getId(),
						prayer_widget.getItemId(),
						prayer_widget.getId()
				)
		);
	}
}
