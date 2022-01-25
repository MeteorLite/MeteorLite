package meteor.plugins.stretchedmode;

import com.jfoenix.controls.JFXSlider;
import com.jfoenix.controls.JFXToggleButton;
import javafx.fxml.FXML;
import meteor.PluginManager;
import meteor.plugins.Plugin;

import javax.inject.Inject;

public class ConfigFXMLController {

  StretchedModePlugin plugin;
  @FXML
  private JFXToggleButton keepAspectRatioEnabled;
  @FXML
  private JFXToggleButton increasedPerfEnabled;
  @FXML
  private JFXToggleButton integerScalingEnabled;
  @FXML
  private JFXSlider scalingFactorSlider;

  @Inject
  private PluginManager pluginManager;

  {
    for (Plugin p : pluginManager.getPlugins()) {
      if (p instanceof StretchedModePlugin) {
        plugin = (StretchedModePlugin) p;
      }
    }
  }

  @FXML
  public void initialize() {
  }

}
