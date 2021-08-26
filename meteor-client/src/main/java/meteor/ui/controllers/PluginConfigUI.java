package meteor.ui.controllers;

import static meteor.MeteorLiteClientModule.mainWindow;
import static meteor.ui.controllers.PluginListUI.lastPluginInteracted;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXColorPicker;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXSlider;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXTooltip;
import com.sun.javafx.collections.ObservableListWrapper;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Orientation;
import javafx.scene.control.ScrollBar;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javax.inject.Inject;
import meteor.MeteorLiteClientLauncher;
import meteor.MeteorLiteClientModule;
import meteor.config.Button;
import meteor.config.Config;
import meteor.config.ConfigDescriptor;
import meteor.config.ConfigItemDescriptor;
import meteor.config.ConfigManager;
import meteor.plugins.Plugin;
import meteor.plugins.PluginDescriptor;
import meteor.ui.components.PluginToggleButton;
import net.runelite.api.Client;
import net.runelite.api.events.ConfigButtonClicked;
import org.sponge.util.Logger;

public class PluginConfigUI {

  Logger logger = new Logger("PluginConfigController");

  @FXML
  private AnchorPane pluginConfigPanel ;

  @FXML
  private VBox nodeList;

  private Plugin plugin;

  @Inject
  Client client;

  @Inject
  ConfigManager configManager;

  private double configItemViewOffset = 0;
  AnchorPane configPanel;
  boolean fakeEvent = false;

  @FXML
  public void initialize() {
    MeteorLiteClientModule.instanceInjectorStatic.injectMembers(this);
    plugin = lastPluginInteracted;

    FontAwesomeIconView plug = new FontAwesomeIconView(FontAwesomeIcon.PLUG);
    plug.setFill(Paint.valueOf("CYAN"));
    plug.setLayoutY(25);
    AnchorPane.setLeftAnchor(plug, 10.0);

    pluginConfigPanel.getChildren().add(plug);
    pluginConfigPanel.setMinHeight(8000);

    Text pluginsString = new Text();
    pluginsString.setText(plugin.getName());
    pluginsString.setFill(Paint.valueOf("WHITE"));
    pluginsString.setLayoutY(28);
    pluginsString.setWrappingWidth(300);
    pluginsString.setFont(Font.font(18));
    AnchorPane.setLeftAnchor(pluginsString, 35.0);

    pluginConfigPanel.getChildren().add(pluginsString);
    nodeList = new VBox();
    nodeList.setLayoutY(45);

    configPanel = new AnchorPane();
    configPanel.setStyle("-fx-border-color: #102027");
    configPanel.setPrefHeight(25);
    configPanel.setPrefWidth(280);

    configManager = MeteorLiteClientLauncher.mainClientInstance.instanceInjector.getInstance(ConfigManager.class);
    Config config = plugin.getConfig(configManager);
    ConfigDescriptor descriptor = configManager.getConfigDescriptor(config);
    if (descriptor != null)
    {
      for (ConfigItemDescriptor configItemDescriptor : descriptor.getItems())
      {
        AnchorPane nodePanel = createNode();
        nodePanel.setStyle("-fx-background-color: #212121; -fx-border-style: solid;  -fx-border-color: #121212; -fx-border-width: 1;");
        AnchorPane.setRightAnchor(nodePanel, 2.0);
        AnchorPane.setLeftAnchor(nodePanel, 2.0);
        if (configItemDescriptor.getType() == int.class)
        {
          if (configItemDescriptor.getRange() != null)
          {
            if (configItemDescriptor.getRange().textInput()) {
              createIntegerTextNode(descriptor, nodePanel, configItemDescriptor);
            } else {
              createIntegerSliderNode(descriptor, nodePanel, configItemDescriptor);
            }
          }
          else
            createIntegerSliderNode(descriptor, nodePanel, configItemDescriptor);
        }
        if (configItemDescriptor.getType() == boolean.class)
        {
          createBooleanNode(descriptor, nodePanel, configItemDescriptor);
        }
        if (configItemDescriptor.getType() == String.class)
        {
          createStringNode(descriptor, nodePanel, configItemDescriptor);
        }
        if (configItemDescriptor.getType() == Color.class)
        {
          createColorPickerNode(descriptor, nodePanel, configItemDescriptor);
        }
        if (configItemDescriptor.getType() == double.class)
        {
          createdDoubleTextNode(descriptor, nodePanel, configItemDescriptor);
        }
        if (configItemDescriptor.getType() == Button.class)
        {
          createButtonNode(descriptor, nodePanel, configItemDescriptor);
        }
        if (configItemDescriptor.getType().isEnum())
        {
          createEnumNode(descriptor, nodePanel, configItemDescriptor);
        }
        if (nodePanel.getChildren().size() > 0)
        {
          nodeList.getChildren().add(nodePanel);
        }
      }
    }
    pluginConfigPanel.getChildren().add(nodeList);

    ScrollBar scrollBar = new ScrollBar();
    scrollBar.setOrientation(Orientation.VERTICAL);
    scrollBar.setMinSize(5, mainWindow.getHeight());
    AnchorPane.setRightAnchor(scrollBar, 0.0);
    scrollBar.setMin(0);
    scrollBar.setMax(100);
    scrollBar.setValue(0);
    scrollBar.getStylesheets().add("css/plugins/jfx-scrollbar.css");

    scrollBar.valueProperty().addListener((observable, oldValue, newValue) -> {
      if (fakeEvent)
      {
        fakeEvent = false;
        return;
      }
      configItemViewOffset =  newValue.doubleValue();
      configItemViewOffset = (configItemViewOffset - (configItemViewOffset *= 45));
      nodeList.setLayoutY(configItemViewOffset + 45);
      pluginsString.setLayoutY(configItemViewOffset + 28);
      plug.setLayoutY(configItemViewOffset + 25);
    });

    pluginConfigPanel.getChildren().add(scrollBar);
    pluginConfigPanel.addEventHandler(ScrollEvent.SCROLL, (e) -> {
      configItemViewOffset = configItemViewOffset + e.getDeltaY();
      if (configItemViewOffset > 0)
        configItemViewOffset = 0;

      nodeList.setLayoutY(configItemViewOffset + 45);
      pluginsString.setLayoutY(configItemViewOffset + 28);
      plug.setLayoutY(configItemViewOffset + 25);

      fakeEvent = true;
      scrollBar.setValue((configItemViewOffset * -1) / 200);
    });
  }

  private void createButtonNode(ConfigDescriptor config, AnchorPane root, ConfigItemDescriptor configItem) {
    root.setMinSize(350, 56);

    JFXButton button = new JFXButton();
    button.setMinSize(100, 50);
    AnchorPane.setTopAnchor(button, 2.0);
    AnchorPane.setRightAnchor(button, 125.0);
    AnchorPane.setLeftAnchor(button, 125.0);
    AtomicReference<FontAwesomeIcon> icon = new AtomicReference<>();

    if (configItem.getIcon().canToggle())
      if (plugin.isRunning()) {
        icon.set(configItem.getIcon().stop());
        button.setText("Stop");
      }
      else {
        icon.set(configItem.getIcon().start());
        button.setText("Start");
      }
    else {
      icon.set(configItem.getIcon().value());
      button.setText(configItem.name());
    }

    FontAwesomeIconView buttonIcon = new FontAwesomeIconView(icon.get());
    buttonIcon.setSize("11");
    buttonIcon.setFill(javafx.scene.paint.Color.valueOf("CYAN"));
    button.setGraphic(buttonIcon);
    button.autosize();
    button.setStyle("-fx-background-color: #252525; -fx-text-fill: CYAN; -jfx-button-type: RAISED;");


    button.pressedProperty().addListener((options, oldValue, pressed) -> {
      if (pressed)
      if (configItem.getIcon().canToggle())
        if (plugin.isRunning()) {
          icon.set(configItem.getIcon().start());
          button.setText("Start");
          client.getCallbacks().post(new ConfigButtonClicked(config.getGroup().value(), configItem.key()));
          plugin.setRunning(false);
        }
        else {
          icon.set(configItem.getIcon().stop());
          button.setText("Stop");
          client.getCallbacks().post(new ConfigButtonClicked(config.getGroup().value(), configItem.key()));
          plugin.setRunning(true);
        }
      else
        icon.set(configItem.getIcon().value());
    });
    root.getChildren().add(button);
  }

  private void createEnumNode(ConfigDescriptor config, AnchorPane root, ConfigItemDescriptor configItem) {
    Text name = new Text();
    name.setText(configItem.name());
    name.setFill(Paint.valueOf("WHITE"));
    name.setLayoutX(18);
    name.setLayoutY(18);
    name.setWrappingWidth(300);
    name.setFont(Font.font(18));

    JFXTooltip tooltip = new JFXTooltip();
    if (configItem.getItem().description().length() > 0)
      tooltip.setText(configItem.getItem().description());

    name.addEventHandler(MouseEvent.MOUSE_ENTERED, (e) -> {
      if (!tooltip.isShowing())
        if (tooltip.getText().length() > 0)
          tooltip.showOnAnchors(name, 0, -50);
    });

    name.addEventHandler(MouseEvent.MOUSE_EXITED, (e) -> {
      if (tooltip.isShowing())
        tooltip.hide();
    });

    root.getChildren().add(name);

    Class<? extends Enum> type = (Class<? extends Enum>) configItem.getType();
    Enum<?> currentConfigEnum = Enum.valueOf(type, configManager.getConfiguration(config.getGroup().value(), configItem.key()));
    List<Enum<?>> list = new ArrayList<>();
    Enum<?> currentToSet = null;
    for (Enum<?> o : type.getEnumConstants())
    {
      if (o.equals(currentConfigEnum))
        currentToSet = o;
      list.add(o);
    }
    ObservableList<Enum<?>> observableList = new ObservableListWrapper<>(list);
    JFXComboBox<Enum<?>> comboBox =new JFXComboBox<>(observableList);
    comboBox.setValue(currentToSet);
    comboBox.setMinSize(150, 15);
    AnchorPane.setLeftAnchor(comboBox, 200.0);
    AnchorPane.setRightAnchor(comboBox, 10.0);
    AnchorPane.setBottomAnchor(comboBox, 2.0);
    comboBox.autosize();
    comboBox.getStylesheets().add("css/plugins/jfx-combobox.css");
    comboBox.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
      updateConfigItemValue(config, configItem, newValue.name());
    });

    root.getChildren().add(comboBox);
  }

  private void createdDoubleTextNode(ConfigDescriptor config, AnchorPane root, ConfigItemDescriptor configItem) {
    Text name = new Text();
    name.setText(configItem.name());
    name.setFill(Paint.valueOf("WHITE"));
    name.setLayoutX(18);
    name.setLayoutY(18);
    name.setWrappingWidth(300);
    name.setFont(Font.font(18));

    JFXTooltip tooltip = new JFXTooltip();
    if (configItem.getItem().description().length() > 0)
      tooltip.setText(configItem.getItem().description());

    name.addEventHandler(MouseEvent.MOUSE_ENTERED, (e) -> {
      if (!tooltip.isShowing())
        if (tooltip.getText().length() > 0)
          tooltip.showOnAnchors(name, 0, -50);
    });

    name.addEventHandler(MouseEvent.MOUSE_EXITED, (e) -> {
      tooltip.hide();
    });

    root.getChildren().add(name);

    JFXTextField textField = new JFXTextField();

    AnchorPane.setLeftAnchor(textField, 200.0);
    AnchorPane.setRightAnchor(textField, 10.0);
    textField.setMinSize(150, 15);
    textField.setFont(Font.font(18));
    textField.autosize();
    textField.setText(configManager.getConfiguration(config.getGroup().value(), configItem.key(), String.class));
    textField.addEventHandler(KeyEvent.KEY_TYPED, (e) ->
    {
      if (isInputValidDouble(configItem, textField.getText()))
        updateConfigItemValue(config, configItem, Double.parseDouble(textField.getText()));
      else
      {
        textField.clear();
        textField.setText("0.0");
        updateConfigItemValue(config, configItem, 0.0);
      }
    });
    textField.getStylesheets().add("css/plugins/jfx-textfield.css");

    root.getChildren().add(textField);
  }

  private void createStringNode(ConfigDescriptor config, AnchorPane root, ConfigItemDescriptor descriptor) {
    Text name = new Text();
    name.setText(descriptor.name());
    name.setFill(Paint.valueOf("WHITE"));
    name.setLayoutX(18);
    name.setLayoutY(18);
    name.setWrappingWidth(300);
    name.setFont(Font.font(18));
    AnchorPane.setBottomAnchor(name, 150.0);

    JFXTooltip tooltip = new JFXTooltip();
    if (descriptor.getItem().description().length() > 0)
      tooltip.setText(descriptor.getItem().description());

    name.addEventHandler(MouseEvent.MOUSE_ENTERED, (e) -> {
      if (!tooltip.isShowing())
        if (tooltip.getText().length() > 0)
          tooltip.showOnAnchors(name, 0, -50);
    });

    name.addEventHandler(MouseEvent.MOUSE_EXITED, (e) -> {
      tooltip.hide();
    });

    root.getChildren().add(name);

    JFXTextArea textArea = new JFXTextArea();
    AnchorPane.setLeftAnchor(textArea, 15.0);
    AnchorPane.setRightAnchor(textArea, 25.0);
    AnchorPane.setBottomAnchor(textArea, 2.0);
    textArea.setFont(Font.font(18));

    textArea.setWrapText(true);
    textArea.setText(configManager.getConfiguration(config.getGroup().value(), descriptor.key(), String.class));
    textArea.setMaxSize(305, 150);
    textArea.setLayoutY(45);
    textArea.getStylesheets().add("css/plugins/jfx-textarea.css");
    textArea.setStyle("-jfx-focus-color: CYAN;");
    textArea.textProperty().addListener((observable, oldValue, newValue) -> updateConfigItemValue(config, descriptor, newValue));

    root.getChildren().add(textArea);
  }

  private AnchorPane createNode()
  {
    AnchorPane node = new AnchorPane();
    node.setStyle("-fx-border-color: #102027");
    node.setPrefHeight(25);
    node.setPrefWidth(280);
    return node;
  }

  private void createBooleanNode(ConfigDescriptor config, AnchorPane root, ConfigItemDescriptor descriptor) {
    Text name = new Text();
    name.setText(descriptor.name());
    name.setFill(Paint.valueOf("WHITE"));
    name.setLayoutX(18);
    name.setLayoutY(30);
    name.setWrappingWidth(300);
    name.setFont(Font.font(18));

    JFXTooltip tooltip = new JFXTooltip();
    if (descriptor.getItem().description().length() > 0)
      tooltip.setText(descriptor.getItem().description());

    name.addEventHandler(MouseEvent.MOUSE_ENTERED, (e) -> {
      if (!tooltip.isShowing())
        if (tooltip.getText().length() > 0)
          tooltip.showOnAnchors(name, 0, -50);
    });

    name.addEventHandler(MouseEvent.MOUSE_EXITED, (e) -> {
      tooltip.hide();
    });

    root.getChildren().add(name);

    PluginToggleButton toggleButton = new PluginToggleButton(plugin);
    toggleButton.setSize(6);
    toggleButton.setLayoutX(305);
    toggleButton.setLayoutY(8);

    Object o = configManager.getConfiguration(config.getGroup().value(), descriptor.key(), descriptor.getType());
    boolean enabled = false;
    if (o instanceof Boolean)
    {
      enabled = (Boolean) o;
    }
    toggleButton.setSelected(enabled);
    toggleButton.setStyle("-fx-text-fill: CYAN;");
    toggleButton.addEventHandler(MouseEvent.MOUSE_CLICKED, (e) -> updateConfigItemValue(config, descriptor, toggleButton.isSelected()));

    root.getChildren().add(toggleButton);
  }

  private void createIntegerSliderNode(ConfigDescriptor config, AnchorPane root, ConfigItemDescriptor descriptor)
  {
    Text name = new Text();
    name.setText(descriptor.name());
    name.setFill(Paint.valueOf("WHITE"));
    AnchorPane.setTopAnchor(name, 5.0);
    name.setLayoutX(18);
    name.setLayoutY(18);
    name.setWrappingWidth(300);
    name.setFont(Font.font(18));

    JFXTooltip tooltip = new JFXTooltip();
    if (descriptor.getItem().description().length() > 0)
      tooltip.setText(descriptor.getItem().description());

    name.addEventHandler(MouseEvent.MOUSE_ENTERED, (e) -> {
      if (!tooltip.isShowing())
        if (tooltip.getText().length() > 0)
          tooltip.showOnAnchors(name, 0, -50);
    });

    name.addEventHandler(MouseEvent.MOUSE_EXITED, (e) -> {
      tooltip.hide();
    });

    root.getChildren().add(name);

    JFXSlider slider = new JFXSlider();
    AnchorPane.setLeftAnchor(slider, 15.0);
    AnchorPane.setRightAnchor(slider, 25.0);
    slider.setMinSize(305, 35);
    slider.setLayoutY(35);
    slider.autosize();
    int min = 0;
    int max = Integer.MAX_VALUE;
    if (descriptor.getRange() != null)
    {
      min = descriptor.getRange().min();
      max = descriptor.getRange().max();
    }
    slider.setValue(configManager.getConfiguration(config.getGroup().value(), descriptor.key(), Double.class));
    slider.setMax(max);
    slider.setMin(min);
    slider.setMajorTickUnit(max / 4.0);
    slider.setMinorTickCount(2);
    slider.getStylesheets().add("css/plugins/jfx-slider.css");
    slider.setShowTickMarks(true);
    slider.setShowTickLabels(true);
    slider.addEventHandler(MouseEvent.MOUSE_DRAGGED, (e) -> updateConfigItemValue(config, descriptor, (int)slider.getValue()));

    root.getChildren().add(slider);
  }

  private void createIntegerTextNode(ConfigDescriptor config, AnchorPane root, ConfigItemDescriptor descriptor)
  {
    root.setMinSize(350, 25);
    root.setMinSize(0, 35);
    Text name = new Text();
    name.setText(descriptor.name());
    name.setFill(Paint.valueOf("WHITE"));
    name.setLayoutX(18);
    name.setLayoutY(30);
    name.setWrappingWidth(300);
    name.setFont(Font.font(18));

    root.getChildren().add(name);

    JFXTextField textField = new JFXTextField();
    AnchorPane.setTopAnchor(textField, 0.0);
    AnchorPane.setBottomAnchor(textField, 0.0);
    AnchorPane.setLeftAnchor(textField, 200.0);
    AnchorPane.setRightAnchor(textField, 10.0);
    textField.setMaxSize(150, 15);
    textField.setFont(Font.font(18));
    textField.setText(configManager.getConfiguration(config.getGroup().value(), descriptor.key(), String.class));
    textField.addEventHandler(KeyEvent.KEY_TYPED, (e) ->
    {
      int min = 0;
      if (descriptor.getRange() != null)
      {
        min = descriptor.getRange().min();
      }
      if (isInputValidInteger(descriptor, textField.getText()))
        updateConfigItemValue(config, descriptor, Integer.parseInt(textField.getText()));
      else
      {
        textField.setText("" + min);
        updateConfigItemValue(config, descriptor, min);
      }
    });
    textField.setStyle("-jfx-focus-color: CYAN;");
    textField.getStylesheets().add("css/plugins/jfx-textfield.css");

    JFXTooltip tooltip = new JFXTooltip();
    if (descriptor.getItem().description().length() > 0)
      tooltip.setText(descriptor.getItem().description());

    textField.addEventHandler(MouseEvent.MOUSE_ENTERED, (e) -> {
      if (!tooltip.isShowing())
        if (tooltip.getText().length() > 0)
          tooltip.showOnAnchors(textField, 0, -50);
    });

    textField.addEventHandler(MouseEvent.MOUSE_EXITED, (e) -> {
      tooltip.hide();
    });

    root.getChildren().add(textField);
  }

  private void createColorPickerNode(ConfigDescriptor config, AnchorPane root, ConfigItemDescriptor configItem)
  {
    Text name = new Text();
    name.setText(configItem.name());
    name.setFill(Paint.valueOf("WHITE"));
    name.setLayoutX(18);
    name.setLayoutY(18);
    name.setWrappingWidth(300);
    name.setFont(Font.font(18));

    JFXTooltip tooltip = new JFXTooltip();
    if (configItem.getItem().description().length() > 0)
      tooltip.setText(configItem.getItem().description());

    name.addEventHandler(MouseEvent.MOUSE_ENTERED, (e) -> {
      if (!tooltip.isShowing())
        if (tooltip.getText().length() > 0)
          tooltip.showOnAnchors(name, 0, -50);
    });

    name.addEventHandler(MouseEvent.MOUSE_EXITED, (e) -> {
      tooltip.hide();
    });

    root.getChildren().add(name);

    JFXColorPicker colorPicker = new JFXColorPicker();
    AnchorPane.setLeftAnchor(colorPicker, 200.0);
    AnchorPane.setRightAnchor(colorPicker, 15.0);
    colorPicker.setMinSize(50, 35);
    colorPicker.autosize();
    Color c = configManager.getConfiguration(config.getGroup().value(), configItem.key(), Color.class);
    if (c == null) {
      logger.warn(config.getGroup().value() + ":" + configItem.key() + " color can't be null");
      c = Color.WHITE;
    }
    double r = c.getRed() / 255.0;
    double g = c.getGreen() / 255.0;
    double b = c.getBlue() / 255.0;
    double a = c.getAlpha() / 255.0;
    colorPicker.setValue(javafx.scene.paint.Color.color(r, g, b, a));
    colorPicker.valueProperty().addListener((observable, oldValue, newValue) -> {
      double nr = newValue.getRed() * 255.0;
      double ng = newValue.getGreen() * 255.0;
      double nb = newValue.getBlue() * 255.0;
      double na = newValue.getOpacity() * 255.0;
      Color colorToSet = new Color((int)nr, (int)ng, (int)nb, (int)na);
      updateConfigItemValue(config, configItem, colorToSet);
    });

    root.getChildren().add(colorPicker);
  }

  private boolean isInputValidInteger(ConfigItemDescriptor descriptor, String input)
  {
    int i;
    try {
      i = Integer.parseInt(input);
    }
    catch (Exception e)
    {
      e.printStackTrace();
      return false;
    }

    if (descriptor.getRange() == null)
    return true;

    if (descriptor.getRange().max() < i)
    return false;

    if (descriptor.getRange().min() > i)
    return false;

    return true;
  }

  private boolean isInputValidDouble(ConfigItemDescriptor descriptor, String input)
  {
    double d;
    try {
      d = Double.parseDouble(input);
    }
    catch (Exception e)
    {
      e.printStackTrace();
      return false;
    }

    if (descriptor.getRange() == null)
      return true;

    if (descriptor.getRange().max() < Double.MIN_VALUE)
      return false;

    if (descriptor.getRange().min() > Double.MAX_VALUE)
      return false;

    return true;
  }

  private void updateConfigItemValue(ConfigDescriptor config, ConfigItemDescriptor configItem, Object value)
  {
    configManager.setConfiguration(config.getGroup().value(), configItem.key(), value);
    lastPluginInteracted.updateConfig();
  }
}