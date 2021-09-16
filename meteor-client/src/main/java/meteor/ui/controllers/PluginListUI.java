package meteor.ui.controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXTooltip;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TitledPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import meteor.MeteorLiteClientLauncher;
import meteor.MeteorLiteClientModule;
import meteor.PluginManager;
import meteor.config.ConfigGroup;
import meteor.config.ConfigManager;
import meteor.eventbus.EventBus;
import meteor.eventbus.Subscribe;
import meteor.events.ExternalsReloaded;
import meteor.plugins.Plugin;
import meteor.plugins.PluginDescriptor;
import meteor.ui.components.*;
import org.sponge.util.Logger;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static meteor.MeteorLiteClientModule.pluginsPanelVisible;

public class PluginListUI {
  public static final String MAIN_CATEGORY_NAME = "Plugins";
  public static final String EXTERNAL_CATEGORY_NAME = "Externals";

  @FXML
  private FontAwesomeIconView addCategory;

  @FXML
  private VBox pluginList;

  @FXML
  public ScrollPane scrollPane;

  public static Plugin lastPluginInteracted;

  public static Map<String, PluginToggleButton> configGroupPluginMap = new HashMap<>();

  public static Map<Plugin, AnchorPane> pluginPanels = new HashMap<>();

  public static boolean overrideToggleListener = false;

  @Inject
  private ConfigManager configManager;

  public static PluginListUI INSTANCE;

  @Inject
  private EventBus eventBus;

  public static ArrayList<Category> categories = new ArrayList<>();

  @FXML
  public void initialize() {
    MeteorLiteClientModule.instanceInjectorStatic.injectMembers(this);
    eventBus.register(this);
    INSTANCE = this;
    categories.clear();

    addCategory.addEventHandler(MouseEvent.MOUSE_CLICKED, (e) -> {
      createCategory();
      refreshPlugins();
    });

    String[] categoriesConfigArray = null;
    String categoriesConfig = configManager.getConfiguration("meteorlite", "categories");
    if (categoriesConfig != null) {
      categoriesConfigArray = categoriesConfig.split(",");
    }

    if (categoriesConfigArray != null) {
      for (String name : categoriesConfigArray) {
        if (name.length() <= 0) {
          continue;
        }

        Category category = new Category(name);
        String categoryPluginsConfig = configManager.getConfiguration("category", name);

        if (categoryPluginsConfig != null) {
          String[] categoryPlugins = categoryPluginsConfig.split(",");
          for (String s : categoryPlugins) {
            if (!s.equals("")) {
              category.plugins.add(s);
            }
          }
        }

        categories.add(category);
      }
    }

    Category all = new Category(MAIN_CATEGORY_NAME);
    for (Plugin p : PluginManager.plugins) {
      all.plugins.add(p.getName());
    }

    categories.add(all);

    saveCategories();
    refreshPlugins();
  }

  public void saveCategories() {
    StringBuilder categoriesConfig = new StringBuilder();
    int i = 0;

    for (Category c : categories) {
      StringBuilder pluginsConfig = new StringBuilder();

      if (!c.name.equals(MAIN_CATEGORY_NAME)) {
        int k = 0;

        for (String p : c.plugins) {
          if (k != 0) {
            pluginsConfig.append(",");
          }

          pluginsConfig.append(p);
          k++;
        }

        configManager.setConfiguration("category", c.name, pluginsConfig.toString());
        if (i != 0) {
          categoriesConfig.append(",");
        }

        categoriesConfig.append(c.name);
        i++;
      }
    }

    configManager.setConfiguration("meteorlite", "categories", categoriesConfig.toString());
    configManager.saveProperties(true);
  }
  
  public void refreshPlugins() {
    categories.forEach(Category::clear);
    pluginList.getChildren().clear();
    pluginPanels.clear();
    configGroupPluginMap.clear();

    for (Category c : categories) {
      ContextMenu contextMenu = new ContextMenu();
      contextMenu.setStyle("-fx-highlight-fill: #424242");
      int position = getCategoryPosition(c);

      if (position > 0) {
        contextMenu.setStyle("-fx-background-color: #121212;");
        CategoryMenuItem moveUp = new CategoryMenuItem(c, null, "Move up");
        moveUp.setStyle("-fx-background-color: #121212; -fx-text-fill: cyan; -fx-highlight-fill: #424242");
        moveUp.setOnAction((event) -> {
          moveUp(c);
          saveCategories();
        });

        contextMenu.getItems().add(moveUp);
      }

      if (position < categories.size() - 1) {
        contextMenu.setStyle("-fx-background-color: #121212;");
        CategoryMenuItem moveDown = new CategoryMenuItem(c, null, "Move Down");
        moveDown.setStyle("-fx-background-color: #121212; -fx-text-fill: cyan; -fx-highlight-fill: #424242");
        moveDown.setOnAction((event) -> {
          moveDown(c);
          saveCategories();
        });

        contextMenu.getItems().add(moveDown);
      }

      if (!c.name.equals(MAIN_CATEGORY_NAME)) {
        contextMenu.setStyle("-fx-background-color: #121212;");
        CategoryMenuItem moveDown = new CategoryMenuItem(c, null, "Remove");
        moveDown.setStyle("-fx-background-color: #121212; -fx-text-fill: cyan; -fx-highlight-fill: #424242");
        moveDown.setOnAction((event) -> {
          remove(c);
          saveCategories();
        });
        contextMenu.getItems().add(moveDown);
      }

      TitledPane titlePane = c.getTitlePane();
      Text title = c.getSectionPane().getTitle();
      title.setOnContextMenuRequested(e -> contextMenu.show(titlePane, e.getScreenX(), e.getScreenY()));

      pluginList.getChildren().add(titlePane);

      for (String s : c.plugins) {
        Plugin p = PluginManager.getInstance(s);

        if (p != null) {
          addPlugin(p, c);
        }
      }
    }
  }

  public void createCategory() {
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
      findOrCreateCategory(nameField.getText());
    });
    rootPanel.getChildren().add(nameField);
    rootPanel.getChildren().add(okButton);
    AnchorPane.setLeftAnchor(okButton, 75.0);
    AnchorPane.setTopAnchor(okButton, 27.0);
    Scene stageScene = new Scene(rootPanel, 200, 65);
    newStage.setScene(stageScene);
    newStage.show();
  }

  public Category findOrCreateCategory(final String name) {
    Optional<Category> categoryOptional =
            categories.stream()
              .filter(c -> c.name.equals(name))
              .findFirst();

    if (categoryOptional.isPresent()) {
      return categoryOptional.get();
    }

    ArrayList<Category> oldCategories = categories;
    ArrayList<Category> newCategories = new ArrayList<>();
    Category c = new Category(name);
    newCategories.add(c);
    newCategories.addAll(oldCategories);
    categories = newCategories;
    saveCategories();
    refreshPlugins();
    return c;
  }

  private int getCategoryPosition(Category category) {
    int i = 0;
    for (Category c : categories) {
      if (c == category) {
        return i;
      }

      i++;
    }
    return -1;
  }

  private int getPluginPosition(Category category, String plugin) {
    int i = 0;
    for (String c : category.plugins) {
      if (c.equals(plugin)) {
        return i;
      }

      i++;
    }
    return -1;
  }

  private void remove(Category category) {
    categories.remove(category);
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
      } else if (currentPos == categoryPosition) {
        temp.add(replaced);
      } else {
        temp.add(c);
      }

      currentPos++;
    }

    categories = temp;
    refreshPlugins();
  }

  private void moveUp(Category category, String plugin) {
    ArrayList<String> temp = new ArrayList<>();
    int categoryPosition = getPluginPosition(category, plugin);
    int currentPos = 0;
    String replaced = null;
    for (String p : category.plugins) {
      if (currentPos == categoryPosition - 1) {
        replaced = p;
        temp.add(plugin);
      } else if (currentPos == categoryPosition) {
        temp.add(replaced);
      } else {
        temp.add(p);
      }

      currentPos++;
    }

    category.plugins = temp;
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
      if (currentPos == categoryPosition) {
        temp.add(replaced);
      } else if (currentPos == categoryPosition + 1) {
        replaced = c;
        temp.add(category);
      } else {
        temp.add(c);
      }

      currentPos++;
    }

    categories = temp;
    refreshPlugins();
  }

  private void moveDown(Category category, String plugin) {
    ArrayList<String> temp = new ArrayList<>();
    int categoryPosition = getPluginPosition(category, plugin);
    int currentPos = 0;
    String replaced = null;
    for (String c : category.plugins) {
      if (currentPos == categoryPosition + 1) {
        replaced = c;
      }

      currentPos++;
    }

    currentPos = 0;
    for (String c : category.plugins) {
      if (currentPos == categoryPosition) {
        temp.add(replaced);
      } else if (currentPos == categoryPosition + 1) {
        replaced = c;
        temp.add(plugin);
      } else {
        temp.add(c);
      }

      currentPos++;
    }
    category.plugins = temp;
    refreshPlugins();
  }

  private void addPlugin(Plugin p, Category category) {
    PanelItem pluginPanel = new PanelItem();
    pluginPanels.put(p, pluginPanel);

    String configGroup = p.getConfig(configManager).getClass().getInterfaces()[0].getAnnotation(ConfigGroup.class).value();
    PluginToggleButton toggleButton = null;
    if (configGroup != null) {
      toggleButton = configGroupPluginMap.get(configGroup);
    }

    if (!p.getClass().getAnnotation(PluginDescriptor.class).cantDisable()) {
      toggleButton = new PluginToggleButton(p);
      AnchorPane.setTopAnchor(toggleButton, 6.0);
      AnchorPane.setRightAnchor(toggleButton, 8.0);

      PluginToggleButton finalToggleButton = toggleButton;
      toggleButton.selectedProperty().addListener((options, oldValue, newValue) -> {
        if (newValue == finalToggleButton.plugin.isEnabled()) {
          return;
        }
        if (!overrideToggleListener) {
          p.toggle();
        }
      });

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

    if (toggleButton != null) {
      toggleButton.selectedProperty().addListener((options, oldValue, newValue) -> {
        if (p.getConfig(configManager).getClass().getInterfaces()[0].getAnnotation(ConfigGroup.class) != null) {
          configManager.setConfiguration(p.getConfig(configManager).getClass().getInterfaces()[0].getAnnotation(ConfigGroup.class).value(), "pluginEnabled", newValue);
        }
      });
    }

    PluginConfigButton configButton = new PluginConfigButton(p);
    if (p.getConfig(MeteorLiteClientLauncher.mainClientInstance.instanceInjector.getInstance(ConfigManager.class)) != null) {
      if (p.getConfig(MeteorLiteClientLauncher.mainClientInstance.instanceInjector.getInstance(ConfigManager.class)).getClass().getDeclaredMethods().length > 4) {
        AnchorPane.setRightAnchor(configButton, 48.0);
        AnchorPane.setTopAnchor(configButton, 0.0);

        FontAwesomeIconView cog = new FontAwesomeIconView(FontAwesomeIcon.COG);
        cog.setFill(MeteorLiteClientModule.METEOR_FONT_COLOR);
        cog.setSize(String.valueOf(MeteorLiteClientModule.METEOR_FONT_SIZE));
        configButton.setGraphic(cog);
        configButton.addEventHandler(MouseEvent.MOUSE_CLICKED, (e) -> {
          lastPluginInteracted = p;
          p.showConfig();
          pluginsPanelVisible = !pluginsPanelVisible;
        });
      }
    }

    MeteorText pluginName = new MeteorText(p);
    pluginName.setFill(Paint.valueOf("WHITE"));

    AnchorPane.setLeftAnchor(pluginName, 2.0);
    AnchorPane.setTopAnchor(pluginName, 0.0);

    if (p.getClass().getAnnotation(PluginDescriptor.class).description().length() > 0) {
      JFXTooltip tooltip = new JFXTooltip();
      tooltip.setText(p.getClass().getAnnotation(PluginDescriptor.class).description());

      pluginName.addEventHandler(MouseEvent.MOUSE_ENTERED, (e) -> {
            tooltip.showOnAnchors(pluginName, 0, -50);
      });

      pluginName.addEventHandler(MouseEvent.MOUSE_EXITED, (e) -> {
          tooltip.hide();
      });
    }

    pluginPanel.getChildren().add(pluginName);
    pluginPanel.getChildren().add(configButton); //Order matters here! Very Important! uwu
    if (toggleButton != null) {
      pluginPanel.getChildren().add(toggleButton);
    }

    ContextMenu contextMenu = new ContextMenu();
    contextMenu.setStyle("-fx-background-color: #121212;");
    for (Category c : categories) {
      if (c.name.equals(MAIN_CATEGORY_NAME)) {
        continue;
      }

      if (!c.plugins.contains(p.getName())) {
        CategoryMenuItem menuItem = new CategoryMenuItem(c, p, "Add : " + c.name);
        menuItem.setStyle("-fx-background-color: #121212; -fx-text-fill: cyan;");
        menuItem.setOnAction((event) -> {
          if (!c.plugins.contains(p.getName())) {
            c.plugins.add(p.getName());
            refreshPlugins();
            saveCategories();
          }
        });
        contextMenu.getItems().add(menuItem);
      } else {
        CategoryMenuItem menuItem = new CategoryMenuItem(c, p, "Remove : " + c.name);
        menuItem.setStyle("-fx-background-color: #121212; -fx-text-fill: cyan;");
        menuItem.setOnAction((event) -> {
          if (c.plugins.contains(p.getName())) {
            c.plugins.remove(p.getName());
            refreshPlugins();
            saveCategories();
          }
        });
        contextMenu.getItems().add(menuItem);
      }
    }

    if (!category.name.equals(MAIN_CATEGORY_NAME)) {
      if (getPluginPosition(category, p.getName()) > 0) {
        CategoryMenuItem moveUpMenuItem = new CategoryMenuItem(category, p, "Move up");
        moveUpMenuItem.setStyle("-fx-background-color: #121212; -fx-text-fill: cyan;");
        moveUpMenuItem.setOnAction((event) -> {
          moveUp(category, p.getName());
          refreshPlugins();
          saveCategories();
        });
        contextMenu.getItems().add(moveUpMenuItem);
      }

      if (getPluginPosition(category, p.getName()) < category.plugins.size() - 1) {
        CategoryMenuItem moveDownMenuItem = new CategoryMenuItem(category, p, "Move Down");
        moveDownMenuItem.setStyle("-fx-background-color: #121212; -fx-text-fill: cyan;");
        moveDownMenuItem.setOnAction((event) -> {
          if (category.plugins.contains(p.getName())) {
            moveDown(category, p.getName());
            refreshPlugins();
            saveCategories();
          }
        });
        contextMenu.getItems().add(moveDownMenuItem);
      }
    }

    pluginName.setOnContextMenuRequested( e -> {
      contextMenu.show(pluginName, e.getScreenX(), e.getScreenY());
    });

    category.addNode(pluginPanel);
  }

  @Subscribe
  public void onExternalsReloaded(ExternalsReloaded e) {
    refreshPlugins();
  }
}
