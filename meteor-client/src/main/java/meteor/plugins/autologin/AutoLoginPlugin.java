package meteor.plugins.autologin;

import com.google.inject.Provides;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import meteor.config.ConfigManager;
import meteor.eventbus.Subscribe;
import meteor.plugins.Plugin;
import meteor.plugins.PluginDescriptor;
import dev.hoot.api.game.Game;
import dev.hoot.api.input.Keyboard;
import net.runelite.api.GameState;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.LoginStateChanged;

@PluginDescriptor(
				name = "Auto Login",
				description = "Automatically fills in your saved details and logs in",
				enabledByDefault = false
)
public class AutoLoginPlugin extends Plugin {
	@Inject
	private AutoLoginConfig config;

	@Inject
	private ScheduledExecutorService executor;

	@Provides
	public AutoLoginConfig getConfig(ConfigManager configManager) {
		return configManager.getConfig(AutoLoginConfig.class);
	}

	@Subscribe
	private void onGameStateChanged(GameStateChanged e) {
		if (e.getGameState() == GameState.LOGIN_SCREEN && Game.getClient().getLoginIndex() == 0) {
			executor.schedule(() -> client.setLoginIndex(2), 2000, TimeUnit.MILLISECONDS);
			executor.schedule(this::login, 2000, TimeUnit.MILLISECONDS);
		}
	}

	@Subscribe
	private void onLoginStateChanged(LoginStateChanged e) {
		if (e.getIndex() == 2) {
			login();
		}
	}

	private void login() {
		client.setUsername(config.username());
		client.setPassword(config.password());
		Keyboard.sendEnter();
		Keyboard.sendEnter();
	}
}
