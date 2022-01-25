package meteor.plugins;

import com.google.inject.Binder;
import com.google.inject.Injector;
import com.google.inject.Module;
import javafx.scene.Parent;

import javax.inject.Inject;

import lombok.Getter;
import lombok.Setter;
import meteor.PluginManager;
import meteor.config.Config;
import meteor.config.ConfigManager;
import meteor.eventbus.EventBus;
import meteor.eventbus.events.PluginChanged;
import meteor.task.Scheduler;
import meteor.ui.MeteorUI;
import meteor.ui.client.PluginConfigPanel;
import meteor.ui.overlay.OverlayManager;
import net.runelite.api.Client;
import org.sponge.util.Logger;

public class Plugin implements Module {

  public Logger logger = new Logger("");

  @Inject
  public Client client;

  @Inject
  public EventBus eventBus;

  @Getter @Setter
  public Injector injector;

  @Inject
  public Scheduler scheduler;

  @Getter @Setter
  public boolean enabled = false;

  @Setter
  private Config config;

  @Getter @Setter
  private boolean external;

  @Inject
  public OverlayManager overlayManager;

  @Inject
  private MeteorUI meteorUI;

  public Plugin() {
    logger.name = getDescriptor().name();
  }

  public void startup() { }
  public void shutdown() { }
  
  public void updateConfig() { }
  public void resetConfiguration() { }

  @Override
  public void configure(Binder binder) { }

  public PluginDescriptor getDescriptor() {
    return getClass().getAnnotation(PluginDescriptor.class);
  }

  public String getName() {
    return getDescriptor().name();
  }

  public Config getConfig(ConfigManager configManager)
  {
    return config;
  }

  private Parent configRoot = null;

  public void showConfig() {
    if (configRoot == null) {
      configRoot = new PluginConfigPanel(this);
    }
    meteorUI.updateRightPanel(configRoot);
  }

  public void unload() {
    scheduler.unregister(this);
    eventBus.unregister(this);
    shutdown();
  }

  public void toggle(boolean on) {
    if (!on) {
      shutdown();
      setEnabled(false);
      scheduler.unregister(this);
      eventBus.unregister(this);
    } else {
      startup();
      setEnabled(true);
      scheduler.register(this);
      eventBus.register(this);
    }

    updateConfig();

    eventBus.post(new PluginChanged(this, enabled));
  }

  public void toggle() {
    toggle(!enabled);
  }

  // These should NOT be used as they are not called
  // This will create errors in plugins that try to use the protected variant
  public void startUp() {

  }

  public void shutDown() {

  }

  public boolean isToggleable() {
    return !getDescriptor().cantDisable();
  }
}
