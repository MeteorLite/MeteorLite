package meteor.ui.controllers;

import static meteor.MeteorLiteClientModule.pluginsPanelVisible;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXTooltip;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import meteor.MeteorLiteClientLauncher;
import meteor.MeteorLiteClientModule;
import meteor.PluginManager;
import meteor.config.ConfigGroup;
import meteor.config.ConfigManager;
import meteor.eventbus.EventBus;
import meteor.eventbus.Subscribe;
import meteor.eventbus.events.ConfigChanged;
import meteor.plugins.Plugin;
import meteor.plugins.PluginDescriptor;
import meteor.ui.components.Category;
import meteor.ui.components.CategoryMenuItem;
import meteor.ui.components.PluginConfigButton;
import meteor.ui.components.PluginToggleButton;
import org.sponge.util.Logger;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

public class PluginListUI {

  @FXML
  private AnchorPane pluginPanel;

  @FXML
  private VBox pluginList;

  public static Plugin lastPluginInteracted;

  private double configItemViewOffset = 0;

  boolean fakeEvent = false;

  public static Map<String, PluginToggleButton> configGroupPluginMap = new HashMap<>();

  public static boolean overrideToggleListener = false;

  @Inject
  ConfigManager configManager;

  @Inject
  EventBus eventBus;
  private ArrayList<Category> categories = new ArrayList<>();

  @FXML
  public void initialize() {
    MeteorLiteClientModule.instanceInjectorStatic.injectMembers(this);
    eventBus.register(this);
    categories.clear();
    String[] categoriesConfigArray = null;
    String categoriesConfig = configManager.getConfiguration("meteorlite", "categories");
    if (categoriesConfig != null) {
      categoriesConfigArray = categoriesConfig.split(",");
    }
    if (categoriesConfigArray != null) {
      for (String name : categoriesConfigArray) {
        if (name.length() > 0) {
          Category category = new Category(name);
          String categoryPluginsConfig = configManager.getConfiguration("category", name);
          if (categoryPluginsConfig != null) {
            String[] categoryPlugins = categoryPluginsConfig.split(",");
            category.plugins.addAll(Arrays.asList(categoryPlugins));
          }
          categories.add(category);
        }
      }
    }
    Category all = new Category("MeteorLite");
    for (Plugin p : PluginManager.plugins) {
      all.plugins.add(p.getName());
    }
    all.open = true;
    categories.add(all);
    saveCategories();
    refreshPlugins();
  }

  public void saveCategories() {
    StringBuilder categoriesConfig = new StringBuilder();
    int i = 0;
    for (Category c : categories) {
      StringBuilder pluginsConfig = new StringBuilder();
      if (!c.name.equals("MeteorLite")) {
        int k = 0;
        for (String p : c.plugins) {
          if (k != 0)
            pluginsConfig.append(",");
          pluginsConfig.append(p);
          k++;
        }
        configManager.setConfiguration("category", c.name, pluginsConfig.toString());
        if (i != 0)
          categoriesConfig.append(",");
        categoriesConfig.append(c.name);
        i++;
      }
    }
    configManager.setConfiguration("meteorlite", "categories", categoriesConfig.toString());
    configManager.saveProperties(true);
  }

  public void refreshPlugins() {
    pluginPanel.getChildren().clear();
    ScrollPane scrollPane = new ScrollPane();
    scrollPane.setMinSize(350, 600);
    AnchorPane.setTopAnchor(scrollPane, 45.0);
    AnchorPane.setBottomAnchor(scrollPane, 0.0);

    FontAwesomeIconView plug = new FontAwesomeIconView(FontAwesomeIcon.PLUG);
    plug.setFill(Paint.valueOf("CYAN"));
    plug.setLayoutY(25);
    AnchorPane.setLeftAnchor(plug, 130.0);

    pluginPanel.getChildren().add(plug);

    Text pluginsString = new Text();
    pluginsString.setText("Plugins");
    pluginsString.setFill(Paint.valueOf("WHITE"));
    pluginsString.setLayoutY(28);
    pluginsString.setWrappingWidth(300);
    pluginsString.setFont(Font.font(18));
    AnchorPane.setLeftAnchor(pluginsString, 148.0);

    pluginPanel.getChildren().add(pluginsString);

    FontAwesomeIconView addCategory = new FontAwesomeIconView(FontAwesomeIcon.PLUS_SQUARE);
    addCategory.setSize("28");
    addCategory.setFill(Paint.valueOf("CYAN"));
    AnchorPane.setTopAnchor(addCategory, 4.0);
    AnchorPane.setRightAnchor(addCategory, 25.0);

    addCategory.addEventHandler(MouseEvent.MOUSE_CLICKED, (e) -> {
      createCategory();
      refreshPlugins();
    });

    pluginPanel.getChildren().add(addCategory);
    
    pluginList = new VBox();
    pluginList.setLayoutY(45);
    pluginList.setMinHeight(MeteorLiteClientModule.mainWindow.getHeight());

    for (Category c : categories) {
      AnchorPane pluginPanel = new AnchorPane();
      pluginPanel.setStyle("-fx-border-color: transparent;");
      FontAwesomeIconView tick;
      if (!c.open)
        tick = new FontAwesomeIconView(FontAwesomeIcon.ANGLE_RIGHT);
      else
        tick = new FontAwesomeIconView(FontAwesomeIcon.ANGLE_DOWN);

      tick.setFill(Paint.valueOf("CYAN"));
      tick.setLayoutY(25);
      tick.setSize("28");
      AnchorPane.setLeftAnchor(tick, 5.0);

      pluginPanel.setPrefHeight(15);
      pluginPanel.setPrefWidth(280);
      pluginPanel.setStyle("-fx-background-color: #212121; -fx-border-style: solid;  -fx-border-color: #121212; -fx-border-width: 1;");

      Text categoryName = new Text();
      categoryName.setText(c.name);
      categoryName.setFill(Paint.valueOf("CYAN"));
      AnchorPane.setLeftAnchor(categoryName, 28.0);
      AnchorPane.setTopAnchor(categoryName, 3.0);
      categoryName.setWrappingWidth(300);
      categoryName.setFont(Font.font(18));

      ContextMenu contextMenu = new ContextMenu();
      int position = getCategoryPosition(c);

      if (position > 0) {
        contextMenu.setStyle("-fx-background-color: #121212;");
        CategoryMenuItem moveUp = new CategoryMenuItem(c, null, "Move up");
        moveUp.setStyle("-fx-background-color: #121212; -fx-text-fill: cyan;");
        moveUp.setOnAction((event) -> {
          moveUp(c);
          saveCategories();
        });
        contextMenu.getItems().add(moveUp);
      }

      if (position < categories.size() - 1) {
        contextMenu.setStyle("-fx-background-color: #121212;");
        CategoryMenuItem moveDown = new CategoryMenuItem(c, null, "Move Down");
        moveDown.setStyle("-fx-background-color: #121212; -fx-text-fill: cyan;");
        moveDown.setOnAction((event) -> {
          moveDown(c);
          saveCategories();
        });
        contextMenu.getItems().add(moveDown);
      }

      if (!c.name.equals("MeteorLite")) {
        contextMenu.setStyle("-fx-background-color: #121212;");
        CategoryMenuItem moveDown = new CategoryMenuItem(c, null, "Remove");
        moveDown.setStyle("-fx-background-color: #121212; -fx-text-fill: cyan;");
        moveDown.setOnAction((event) -> {
          remove(c);
          saveCategories();
        });
        contextMenu.getItems().add(moveDown);
      }

      pluginPanel.getChildren().add(tick);
      pluginPanel.getChildren().add(categoryName);
      pluginPanel.addEventHandler(MouseEvent.MOUSE_CLICKED, (e) -> {
        if (e.getButton() == MouseButton.PRIMARY)
        {
          c.open = !c.open;
          refreshPlugins();
        }
        else if (e.getButton() == MouseButton.SECONDARY) {
          Bounds bounds = categoryName.getBoundsInLocal();
          Bounds screenBounds = categoryName.localToScreen(bounds);
          contextMenu.show(categoryName, screenBounds.getMinX() + 150, screenBounds.getMinY() + 20);
        }
      });

      pluginList.getChildren().add(pluginPanel);

      if (c.open) {
        for (String s : c.plugins) {
          Plugin p = PluginManager.getInstance(s);
          if (p != null)
            addPlugin(p);
        }
      }

    }

    ScrollBar scrollBar = new ScrollBar();
    scrollBar.setOrientation(Orientation.VERTICAL);
    scrollBar.setMinSize(5, MeteorLiteClientModule.mainWindow.getHeight());
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
      plug.setLayoutY(configItemViewOffset + 25);
      pluginsString.setLayoutY(configItemViewOffset + 28);
      pluginList.setLayoutY(configItemViewOffset + 45);
    });
    pluginPanel.getChildren().add(pluginList);
    pluginPanel.getChildren().add(scrollBar);

    pluginPanel.addEventHandler(ScrollEvent.SCROLL, (e) -> {
      configItemViewOffset = configItemViewOffset + e.getDeltaY();
      if (configItemViewOffset > 0)
        configItemViewOffset = 0;

      pluginList.setLayoutY(configItemViewOffset + 45);
      pluginsString.setLayoutY(configItemViewOffset + 28);
      plug.setLayoutY(configItemViewOffset + 25);

      fakeEvent = true;
      scrollBar.setValue((configItemViewOffset * -1) / 200);
    });
  }

  public void createCategory(){
    Stage newStage = new Stage();
    AnchorPane rootPanel = new AnchorPane();
    rootPanel.setStyle("-fx-background-color: #252525;");
    JFXTextField nameField = new JFXTextField("Name");
    nameField.setMinSize(600, 25);
    nameField.getStylesheets().add("css/plugins/jfx-textfield.css");
    JFXButton okButton = new JFXButton("OK");
    okButton.setMinSize(60, 20);
    okButton.setStyle("-fx-background-color: #252525; -fx-text-fill: CYAN; -jfx-button-type: RAISED;");
    okButton.addEventHandler(MouseEvent.MOUSE_CLICKED, (e) -> {
      newStage.close();
      ArrayList<Category> oldCategories = categories;
      ArrayList<Category> newCategories = new ArrayList<>();
      Category c = new Category(nameField.getText());
      newCategories.add(c);
      newCategories.addAll(oldCategories);
      categories = newCategories;
      saveCategories();
      refreshPlugins();
    });
    rootPanel.getChildren().add(nameField);
    rootPanel.getChildren().add(okButton);
    AnchorPane.setLeftAnchor(okButton, 75.0);
    AnchorPane.setTopAnchor(okButton, 27.0);
    Scene stageScene = new Scene(rootPanel, 200, 65);
    newStage.setScene(stageScene);
    newStage.show();
  }

  private int getCategoryPosition(Category category) {
    int i = 0;
    for (Category c : categories) {
      if (c == category)
        return i;
      i++;
    }
    return -1;
  }

  private void remove(Category category) {
    ArrayList<Category> temp = new ArrayList<>();
    for (Category c : categories)
      if (!c.name.equals(category.name))
        temp.add(c);
    categories = temp;
    saveCategories();
    refreshPlugins();
  }

  private void moveUp(Category category) {
    ArrayList<Category> temp = new ArrayList<>();
    int categoryPosition = getCategoryPosition(category);
    int currentPos = 0;
    Category replaced = null;
    for (Category c : categories) {
      if (currentPos == categoryPosition - 1) {
        replaced = c;
        temp.add(category);
      }
      else if (currentPos == categoryPosition)
        temp.add(replaced);
      else
        temp.add(c);
      currentPos++;
    }
    categories = temp;
    refreshPlugins();
  }

  private void moveDown(Category category) {
    ArrayList<Category> temp = new ArrayList<>();
    int categoryPosition = getCategoryPosition(category);
    int currentPos = 0;
    Category replaced = null;
    for (Category c : categories) {
      if (currentPos == categoryPosition + 1) {
        replaced = c;
      }
      currentPos++;
    }
    currentPos = 0;
    for (Category c : categories) {
      if (currentPos == categoryPosition)
        temp.add(replaced);
      else if (currentPos == categoryPosition + 1) {
        replaced = c;
        temp.add(category);
      }
      else
        temp.add(c);
      currentPos++;
    }
    categories = temp;
    refreshPlugins();
  }

  private void addPlugin(Plugin p) {
    AnchorPane pluginPanel = new AnchorPane();
    pluginPanel.setStyle("-fx-border-color: transparent;");

    pluginPanel.setPrefHeight(25);
    pluginPanel.setPrefWidth(280);
    pluginPanel.setStyle("-fx-background-color: #212121; -fx-border-style: solid;  -fx-border-color: #121212; -fx-border-width: 1;");

    PluginConfigButton configButton = new PluginConfigButton(p);
    if (p.getConfig(MeteorLiteClientLauncher.mainClientInstance.instanceInjector.getInstance(ConfigManager.class)) != null)
    {
      if (p.getConfig(MeteorLiteClientLauncher.mainClientInstance.instanceInjector.getInstance(ConfigManager.class)).getClass().getDeclaredMethods().length > 4) {
        configButton.setContentDisplay(ContentDisplay.RIGHT);
        configButton.setLayoutX(260);
        configButton.setPrefSize(40, 40);

        FontAwesomeIconView cog = new FontAwesomeIconView(FontAwesomeIcon.COG);
        cog.setFill(Paint.valueOf("CYAN"));
        cog.setLayoutX(265);
        cog.setSize("18");
        configButton.setGraphic(cog);
        configButton.addEventHandler(MouseEvent.MOUSE_CLICKED, (e) -> {
          lastPluginInteracted = p;
          p.showConfig();
          pluginsPanelVisible = !pluginsPanelVisible;
        });
      }
    }

    PluginToggleButton toggleButton = null;
    if (!p.getClass().getAnnotation(PluginDescriptor.class).cantDisable())
    {
      toggleButton = new PluginToggleButton(p);
      toggleButton.setSize(6);
      toggleButton.setLayoutX(290);
      PluginToggleButton finalToggleButton = toggleButton;
      toggleButton.selectedProperty().addListener((options, oldValue, newValue) -> {
        if (newValue == finalToggleButton.plugin.isEnabled())
          return;
        if (!overrideToggleListener)
          p.toggle();
      });

      toggleButton.setStyle("-fx-text-fill: CYAN;");
      try {
        if (p.getConfig(configManager).getClass().getInterfaces()[0].getAnnotation(ConfigGroup.class) != null) {
          configGroupPluginMap.put(p.getConfig(configManager).getClass().getInterfaces()[0].getAnnotation(ConfigGroup.class).value(), toggleButton);
          toggleButton.setSelected(Boolean.parseBoolean(configManager.getConfiguration(p.getConfig(configManager)
              .getClass().getInterfaces()[0].getAnnotation(ConfigGroup.class).value(), "pluginEnabled")));
        }
      } catch (Exception e) {
        e.printStackTrace();
        new Logger("PluginListUI").error(p.getName() + " has an incorrect getConfig(). Fix it.");
      }
    }

    if (toggleButton != null)
      toggleButton.selectedProperty().addListener((options, oldValue, newValue) -> {
        if (p.getConfig(configManager).getClass().getInterfaces()[0].getAnnotation(ConfigGroup.class) != null) {
          configManager.setConfiguration(p.getConfig(configManager).getClass().getInterfaces()[0].getAnnotation(ConfigGroup.class).value(), "pluginEnabled", newValue);
        }
      });

    Text pluginName = new Text();
    pluginName.setText(p.getClass().getAnnotation(PluginDescriptor.class).name());
    pluginName.setFill(Paint.valueOf("WHITE"));
    AnchorPane.setLeftAnchor(pluginName, 10.0);
    AnchorPane.setTopAnchor(pluginName, 7.0);
    pluginName.setWrappingWidth(300);
    pluginName.setFont(Font.font(18));

    JFXTooltip tooltip = new JFXTooltip();
    if (p.getClass().getAnnotation(PluginDescriptor.class).description().length() > 0)
      tooltip.setText(p.getClass().getAnnotation(PluginDescriptor.class).description());

    pluginName.addEventHandler(MouseEvent.MOUSE_ENTERED, (e) -> {
      if (!tooltip.isShowing())
        if (tooltip.getText().length() > 0)
          tooltip.showOnAnchors(pluginName, 0, -50);
    });

    pluginName.addEventHandler(MouseEvent.MOUSE_EXITED, (e) -> {
      if (tooltip.isShowing())
        tooltip.hide();
    });

    pluginPanel.getChildren().add(pluginName);
    pluginPanel.getChildren().add(configButton); //Order matters here! Very Important! uwu
    if (toggleButton != null)
      pluginPanel.getChildren().add(toggleButton);

    ContextMenu contextMenu = new ContextMenu();
    contextMenu.setStyle("-fx-background-color: #121212;");
    for (Category c : categories) {
      if (!c.plugins.contains(p.getName())) {
        CategoryMenuItem menuItem = new CategoryMenuItem(c, p, "Add to: " + c.name);
        menuItem.setStyle("-fx-background-color: #121212; -fx-text-fill: cyan;");
        menuItem.setOnAction((event) -> {
          if (!c.plugins.contains(p.getName())) {
            c.plugins.add(p.getName());
            refreshPlugins();
            saveCategories();
          }
        });
        contextMenu.getItems().add(menuItem);
      }
    }

    pluginPanel.addEventHandler(MouseEvent.MOUSE_CLICKED, (e) -> {
      Bounds bounds = pluginPanel.getBoundsInLocal();
      Bounds screenBounds = pluginPanel.localToScreen(bounds);
      contextMenu.show(pluginName, screenBounds.getMinX() + 150, screenBounds.getMinY() + 20);
    });
    pluginList.getChildren().add(pluginPanel);
  }
}