package meteor.plugins.meteorlite;

import meteor.config.Config;
import meteor.config.ConfigManager;
import meteor.config.MeteorLiteConfig;
import meteor.eventbus.Subscribe;
import meteor.eventbus.events.ConfigChanged;
import meteor.input.MouseManager;
import meteor.plugins.Plugin;
import meteor.plugins.PluginDescriptor;
import meteor.plugins.api.game.Game;
import meteor.plugins.meteorlite.interaction.InteractionManager;
import meteor.plugins.meteorlite.interaction.InteractionOverlay;
import meteor.plugins.meteorlite.loginscreen.MeteorLiteLoginScreen;
import net.runelite.api.events.ConfigButtonClicked;
import org.sponge.util.Logger;

import javax.inject.Inject;


@PluginDescriptor(
        name = "MeteorLite",
        cantDisable = true
)
public class MeteorLitePlugin extends Plugin {

    @Inject
    private MeteorLiteConfig config;

    @Inject
    private InteractionManager interactionManager;

    @Inject
    private InteractionOverlay interactionOverlay;

    @Inject
    private MeteorLiteLoginScreen meteorLiteLoginScreen;

    @Inject
    private MouseManager mouseManager;

    @Inject
    private MeteorLiteConfig meteorLiteConfig;

    @Inject
    private ConfigManager configManager;

    @Override
    public void updateConfig() {
        Logger.setDebugEnabled(config.debugEnabled());
    }

    @Override
    public void startup() {
        overlayManager.add(interactionOverlay);
        mouseManager.registerMouseListener(interactionOverlay);
        eventBus.register(interactionManager);
        eventBus.register(meteorLiteLoginScreen);

        if (config.meteorLoginScreen()) {
            meteorLiteLoginScreen.setCustom();
        } else {
            meteorLiteLoginScreen.setDefault();
        }
    }

    @Override
    public void shutdown() {
        overlayManager.remove(interactionOverlay);
        mouseManager.unregisterMouseListener(interactionOverlay);
        eventBus.unregister(interactionManager);
    }

    @Subscribe
    public void onConfigButtonClicked(ConfigButtonClicked event) {
        if (!event.getGroup().equals(MeteorLiteConfig.GROUP_NAME)) {
            return;
        }
    }

    @Subscribe
    public void onConfigChanged(ConfigChanged event) {
        if (!event.getGroup().equals(MeteorLiteConfig.GROUP_NAME)) {
            return;
        }

        if (event.getKey().equals("meteorLoginScreen")) {
            if (config.meteorLoginScreen()) {
                meteorLiteLoginScreen.setCustom();
            } else {
                meteorLiteLoginScreen.setDefault();
            }
        }
    }

    @Override
    public Config getConfig(ConfigManager configManager) {
        return meteorLiteConfig;
    }
}
