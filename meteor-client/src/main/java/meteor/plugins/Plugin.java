package meteor.plugins;

import com.google.inject.Binder;
import com.google.inject.Injector;
import com.google.inject.Module;
import java.io.IOException;
import java.util.Objects;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javax.inject.Inject;
import lombok.Getter;
import lombok.Setter;
import meteor.MeteorLiteClientModule;
import meteor.PluginManager;
import meteor.config.Config;
import meteor.config.ConfigManager;
import meteor.eventbus.EventBus;
import meteor.task.Scheduler;
import meteor.ui.components.PluginToggleButton;
import meteor.ui.controllers.PluginListUI;
import meteor.ui.overlay.OverlayManager;
import net.runelite.api.Client;
import org.sponge.util.Logger;

import static meteor.MeteorLiteClientModule.RIGHT_PANEL_WIDTH;

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

  public Plugin() {
    logger.name = this.getClass().getAnnotation(PluginDescriptor.class).name();
  }

  public void startup() { }
  public void shutdown() { }
  
  public void updateConfig() { }
  public void resetConfiguration(){ }

  @Override
  public void configure(Binder binder) { }

  public String getName() {
    return getClass().getAnnotation(PluginDescriptor.class).name();
  }

  public Config getConfig(ConfigManager configManager)
  {
    return config;
  }

  public void showConfig() {
    Parent configRoot = null;
    try {
      configRoot = FXMLLoader.load(Objects.requireNonNull(ClassLoader.getSystemClassLoader()
              .getResource("plugin-config.fxml")));
    } catch (IOException e) {
      e.printStackTrace();
    }

    MeteorLiteClientModule.updateRightPanel(configRoot, RIGHT_PANEL_WIDTH);
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

    PluginListUI.overrideToggleListener = true;
    for (PluginToggleButton ptb : PluginListUI.configGroupPluginMap.values()) {
      if (ptb.plugin == this) {
        ptb.setSelected(enabled);
      }
    }

    PluginListUI.overrideToggleListener = false;
  }

  public void toggle() {
    boolean conflict = false;
    for (Class<?> p : PluginManager.conflicts.keySet())
      if (p == getClass()) {
        conflict = true;
        break;
      }
    for (Class<?> p : PluginManager.conflicts.values())
      if (p == getClass()) {
        conflict = true;
        break;
      }

      if (conflict) {
        Class<? extends Plugin> conflictingClass = null;
        for (Class<? extends Plugin> p : PluginManager.conflicts.keySet()) {
          if (p == this.getClass()) {
            conflictingClass = PluginManager.conflicts.get(p);
            break;
          }
        }
        if (conflictingClass == null) {
          for (Class<? extends Plugin> p : PluginManager.conflicts.keySet()) {
            if (PluginManager.conflicts.get(p) == this.getClass()) {
              conflictingClass = p;
              break;
            }
          }
        }
        if (conflictingClass != null) {
          Plugin instance = PluginManager.getInstance(conflictingClass);
          if (instance.isEnabled())
            instance.toggle();
        }
      }
    toggle(!enabled);
  }


  // These should NOT be used as they are not called
  // This will create errors in plugins that try to use the protected variant
  public void startUp() {

  }

  public void shutDown() {

  }
}
