package meteor.plugins.socketbosstimer;

import meteor.plugins.Plugin;
import meteor.ui.overlay.infobox.Timer;

import java.awt.image.BufferedImage;
import java.time.temporal.ChronoUnit;

class SocketRespawnTimer extends Timer {
	private final Boss boss;

	public SocketRespawnTimer(Boss boss, BufferedImage bossImage, Plugin plugin, int world) {
		super(boss.getSpawnTime().toMillis(), ChronoUnit.MILLIS, bossImage, plugin);
		this.boss = boss;
	}

	public Boss getBoss() {
		return this.boss;
	}
}
