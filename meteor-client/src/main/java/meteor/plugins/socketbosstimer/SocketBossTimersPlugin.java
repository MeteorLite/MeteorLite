package meteor.plugins.socketbosstimer;

import com.google.inject.Provides;
import meteor.config.ConfigManager;
import meteor.eventbus.EventBus;
import meteor.eventbus.Subscribe;
import meteor.game.ItemManager;
import meteor.plugins.Plugin;
import meteor.plugins.PluginDescriptor;
import meteor.plugins.socket.org.json.JSONObject;
import meteor.plugins.socket.packet.SocketBroadcastPacket;
import meteor.plugins.socket.packet.SocketReceivePacket;
import meteor.ui.overlay.infobox.InfoBox;
import meteor.ui.overlay.infobox.InfoBoxManager;
import meteor.util.ColorUtil;
import net.runelite.api.Client;
import net.runelite.api.NPC;
import net.runelite.api.events.NpcDespawned;
import meteor.Logger;

import javax.inject.Inject;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

@PluginDescriptor(
	name = "Socket - Boss Timers",
	description = "Show boss spawn timer overlays. Zhuri made the multiple worlds possible. I just made it socket.",
	tags = {"combat", "pve", "overlay", "spawn"},
	enabledByDefault = false
)
public class SocketBossTimersPlugin extends Plugin {
	private static final Logger log = Logger.Companion.getLogger(SocketBossTimersPlugin.class);

	@Inject
	private Client client;

	@Inject
	private InfoBoxManager infoBoxManager;

	@Inject
	private ItemManager itemManager;

/*	@Inject
	private Notifier notifier;*/

	@Inject
	private SocketBossTimersConfig config;

	@Inject
	private EventBus eventBus;

	public ArrayList<Integer> worldList = new ArrayList<>();

	@Provides
	public SocketBossTimersConfig getConfig(ConfigManager configManager) {
		return (SocketBossTimersConfig)configManager.getConfig(SocketBossTimersConfig.class);
	}

	public void shutdown() {
		this.infoBoxManager.removeIf(t -> t instanceof SocketRespawnTimer);
	}

/*	@Subscribe
	private void onGameTick(GameTick e) {
		if (this.config.notifyOnTime())
			for (InfoBox infoBox : this.infoBoxManager.getInfoBoxes()) {
				if (infoBox instanceof SocketRespawnTimer) {
					Instant endTime = ((SocketRespawnTimer)infoBox).getEndTime();
					Instant now = Instant.now();
					long delta = now.until(endTime, ChronoUnit.SECONDS);
					if (delta <= this.config.notifyTime() && delta > 0L)
						this.notifier.notify("Boss Spawning");
				}
			}
	}*/

	@Subscribe
	public void onNpcDespawned(NpcDespawned npcDespawned) {
		NPC npc = npcDespawned.getNpc();
		int world = this.client.getWorld();
		if (!npc.isDead())
			return;
		int npcId = npc.getId();
		Boss boss = Boss.find(npcId);
		if (boss == null)
			return;

		if (this.config.socketBossKill()) {
			JSONObject data = new JSONObject();
			data.put("bossId", npc.getId());
			data.put("bossName", npc.getName());
			data.put("world", this.client.getWorld());
			data.put("name", this.client.getLocalPlayer().getName());
			JSONObject payload = new JSONObject();
			payload.put("socketrespawn", data);
			this.eventBus.post(new SocketBroadcastPacket(payload));
		}

		if (!this.config.multiWorldTimers())
			this.infoBoxManager.removeIf(t -> (t instanceof SocketRespawnTimer && ((SocketRespawnTimer)t).getBoss() == boss));

		SocketRespawnTimer timer = new SocketRespawnTimer(boss, (BufferedImage)this.itemManager.getImage(boss.getItemSpriteId()), this, world);
		timer.setTooltip(ColorUtil.wrapWithColorTag(npc.getName(), Color.YELLOW) + "</br>" + ColorUtil.wrapWithColorTag("World: " + world, Color.YELLOW));
		this.infoBoxManager.addInfoBox((InfoBox)timer);
	}

	@Subscribe
	public void onSocketReceivePacket(SocketReceivePacket event) {
		try {
			JSONObject payload = event.getPayload();
			if (payload.has("socketrespawn")) {
				JSONObject data = payload.getJSONObject("socketrespawn");
				String name = data.getString("name");
				int bossId = data.getInt("bossId");
				String bossName = data.getString("bossName");
				int world = data.getInt("world");
				Boss boss = Boss.find(bossId);

				if (!name.equals(this.client.getLocalPlayer().getName())){
					if (!this.config.multiWorldTimers()) {
						this.infoBoxManager.removeIf(t -> (t instanceof SocketRespawnTimer && ((SocketRespawnTimer) t).getBoss() == boss));
					}

					boolean alreadyExists = false;
					for(InfoBox rTimer : this.infoBoxManager.getInfoBoxes()){
						if (rTimer.getTooltip().contains("World: " + world)) {
							alreadyExists = true;
							break;
						}
					}
					if(!alreadyExists) {
						SocketRespawnTimer timer = new SocketRespawnTimer(boss, (BufferedImage) this.itemManager.getImage(boss.getItemSpriteId()), this, world);
						timer.setTooltip(ColorUtil.wrapWithColorTag(bossName, Color.YELLOW) + "</br>" + ColorUtil.wrapWithColorTag("World: " + world, Color.YELLOW));
						this.infoBoxManager.addInfoBox(timer);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
