package meteor.ui.client;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToolBar;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import meteor.MeteorLiteClientLauncher;
import meteor.PluginManager;
import meteor.config.ConfigManager;
import meteor.eventbus.EventBus;
import meteor.eventbus.Subscribe;
import meteor.events.ExternalsReloaded;
import meteor.plugins.Plugin;
import meteor.ui.components.Category;
import meteor.util.MeteorConstants;
import org.controlsfx.control.textfield.CustomTextField;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class PluginListPanel extends BorderPane {

    private static final String MAIN_CATEGORY_NAME = "Plugins";
    private static final String EXTERNAL_CATEGORY_NAME = "Externals";

    private final Category main;
    private final Category externals;
    private final ObservableList<PluginListCell> plugins;
    private ObservableList<TitledPane> categories;

    @Inject
    private ConfigManager configManager;

    @Inject
    private PluginManager pluginManager;

    @Inject
    private EventBus eventBus;

    public PluginListPanel() {
        MeteorLiteClientLauncher.injector.injectMembers(this);
        eventBus.register(this);

        plugins = FXCollections.observableArrayList();

        refreshPlugins();

        ToolBar toolBar = initSearchBar();
        main = createCategory(MAIN_CATEGORY_NAME);
        externals = createCategory(EXTERNAL_CATEGORY_NAME);
        ScrollPane pluginListPane = initPluginListPane();

        initCategories();

        setBackground(new Background(new BackgroundFill(MeteorConstants.LIGHT_GRAY, null, null)));

        setMinWidth(MeteorConstants.PANEL_WIDTH);
        setMaxWidth(MeteorConstants.PANEL_WIDTH);

        setTop(toolBar);
        setCenter(pluginListPane);
    }

    public void refreshPlugins() {
        plugins.clear();

        for (Plugin p : pluginManager.getPlugins()) {
            if (p != null) {
                addPlugin(p);
            }
        }
        plugins.sort(Comparator.comparing(plugin -> plugin.getPluginName().toLowerCase()));
    }

    private ScrollPane initPluginListPane() {
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.getStylesheets().add("css/plugins/jfx-scrollbar.css");
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        scrollPane.setBackground(new Background(new BackgroundFill(MeteorConstants.LIGHT_GRAY, null, null)));

        Accordion accordion = new Accordion();
        accordion.setBackground(new Background(new BackgroundFill(MeteorConstants.LIGHT_GRAY, null, null)));

        scrollPane.setContent(accordion);

        categories = accordion.getPanes();

        plugins.forEach(main::addPlugin);
        categories.add(main);
        categories.add(externals);

        saveCategories();

        return scrollPane;
    }

    private ToolBar initSearchBar() {
        ToolBar toolBar = new ToolBar();
        toolBar.setBackground(new Background(new BackgroundFill(MeteorConstants.GRAY, null, null)));

        CustomTextField searchBar = new CustomTextField();
        searchBar.setStyle("-fx-text-inner-color: white;");
        searchBar.setBackground(new Background(new BackgroundFill(MeteorConstants.DARK_GRAY, null, null)));

        FontAwesomeIconView searchIcon = new FontAwesomeIconView(FontAwesomeIcon.SEARCH);
        searchIcon.setFill(Color.CYAN);

        Label searchIconLabel = new Label();
        searchIconLabel.setGraphic(searchIcon);
        searchIconLabel.setPadding(new Insets(0, 2, 0, 7));

        searchBar.setLeft(searchIconLabel);

        FontAwesomeIconView addCategory = new FontAwesomeIconView();
        addCategory.setFill(Color.CYAN);
        addCategory.setGlyphName("PLUS");

        Button addCategoryButton = new Button();
        addCategoryButton.setBackground(new Background(new BackgroundFill(MeteorConstants.DARK_GRAY, null, null)));
        addCategoryButton.setGraphic(addCategory);

        addCategoryButton.setOnMouseClicked((e) -> {
            createCategoryDialog();
            refreshPlugins();
        });

        searchBar.textProperty().addListener(obs -> {
            String filter = searchBar.getText().toLowerCase();
            if (filter.length() == 0) {
                main.getFilteredPlugins().setPredicate(s -> true);
            } else {
                main.getFilteredPlugins().setPredicate(s -> s.getPluginName().toLowerCase().contains(filter));
            }
        });

        HBox.setHgrow(searchBar, Priority.ALWAYS);
        toolBar.getItems().addAll(searchBar, addCategoryButton);
        return toolBar;
    }

    private void addPlugin(Plugin p) {
        PluginListCell panel = new PluginListCell(p, configManager);
        panel.setOnContextMenuRequested(new EventHandler<ContextMenuEvent>() {
            @Override
            public void handle(ContextMenuEvent event) {
                if (categories.size() > 2) {
					ContextMenu contextMenu = new ContextMenu();
					Menu addToCategory = new Menu("Add to Category");
					categories.stream()
							.filter(cat -> !cat.getText().equalsIgnoreCase(MAIN_CATEGORY_NAME) && !cat.getText().equalsIgnoreCase(EXTERNAL_CATEGORY_NAME))
							.forEach(cat -> {
								MenuItem add = new MenuItem(cat.getText());
								add.setOnAction(new EventHandler<ActionEvent>() {
									@Override
									public void handle(ActionEvent event) {
										if (panel.getParent() instanceof Category cat) {
                                            System.out.println("Here");
											cat.addPlugin(panel);
										}
									}
								});
								addToCategory.getItems().add(add);
							});

					Menu removeFromCategory = new Menu("Remove from Category");
					categories.stream()
							.filter(cat -> !cat.getText().equalsIgnoreCase(MAIN_CATEGORY_NAME) && !cat.getText().equalsIgnoreCase(EXTERNAL_CATEGORY_NAME))
							.forEach(cat -> {
								MenuItem remove = new MenuItem(cat.getText());
								remove.setOnAction(new EventHandler<ActionEvent>() {
									@Override
									public void handle(ActionEvent event) {
										if (panel.getParent() instanceof Category cat) {
											cat.removePlugin(panel);
										}
									}
								});
								removeFromCategory.getItems().add(remove);
							});

					contextMenu.getItems().addAll(addToCategory, removeFromCategory);
                    contextMenu.show(panel, event.getScreenX(), event.getScreenY());
                }
            }
        });


        PluginToggleButton tb = panel.getToggleButton();
        if (tb != null) {
            eventBus.register(panel.getToggleButton());
        }
        plugins.add(panel);
    }

    public Category createCategory(String name) {
        Category category = new Category(name);
        ContextMenu contextMenu = new ContextMenu();
        MenuItem moveUp = new MenuItem("Move Up");
        moveUp.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                moveUpCategory(category);
            }
        });

        MenuItem moveDown = new MenuItem("Move Down");
        moveDown.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                moveDownCategory(category);
            }
        });

        contextMenu.getItems().addAll(moveUp, moveDown);

        if (!category.getText().equalsIgnoreCase(MAIN_CATEGORY_NAME) && !category.getText().equalsIgnoreCase(EXTERNAL_CATEGORY_NAME)) {
            MenuItem remove = new MenuItem("Remove Category");
            remove.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    removeCategory(category);
                }
            });
            contextMenu.getItems().add(remove);
        }

        category.setOnContextMenuRequested(new EventHandler<ContextMenuEvent>() {
            @Override
            public void handle(ContextMenuEvent event) {
                Node node = category.lookup(".title");
                if (node != null && node.contains(event.getX(), event.getY())) {
                    contextMenu.show(category, event.getScreenX(), event.getScreenY());
                }
            }
        });

        return category;
    }

    private void initCategories() {
        List<String> categoriesConfigArray = null;
        String categoriesConfig = configManager.getConfiguration("meteorlite", "categories");
        if (categoriesConfig != null) {
            categoriesConfigArray = Arrays.stream(categoriesConfig.split(",")).toList();
        }

        if (categoriesConfigArray != null) {
            for (String name : categoriesConfigArray) {
                if (name.length() <= 0) {
                    continue;
                }

                Category category = createCategory(name);
                String categoryPluginsConfig = configManager.getConfiguration("category", name);

                if (categoryPluginsConfig != null) {
                    String[] categoryPlugins = categoryPluginsConfig.split(",");
                    for (String s : categoryPlugins) {
                        if (!s.equals("")) {
                            getPlugin(s).ifPresent(category::addPlugin);
                        }
                    }
                }

                categories.add(category);
            }
        }
        saveCategories();
    }

    public void saveCategories() {
        categories.forEach(cat -> {
            if (cat instanceof Category c) {
                if (!c.getText().equals(MAIN_CATEGORY_NAME) && !c.getText().equals(EXTERNAL_CATEGORY_NAME)) {
                    configManager.setConfiguration("category",
                            c.getText(),
                            c.getPlugins().stream()
                                    .map(PluginListCell::getPluginName)
                                    .distinct()
                                    .collect(Collectors.joining(",")));
                }
            }

        });

        configManager.setConfiguration("meteorlite", "categories",
                categories.stream()
                        .map(cat -> {
                            if (cat instanceof Category c) {
                                return c.getText();
                            }
                            return null;
                        }).filter(Objects::nonNull)
                        .filter(txt -> !txt.equals(MAIN_CATEGORY_NAME) && !txt.equals(EXTERNAL_CATEGORY_NAME))
                        .distinct()
                        .collect(Collectors.joining(",")));
        configManager.saveProperties(true);
    }

    private void moveUpCategory(Category category) {
        int categoryIndex = categories.indexOf(category);
        categories.remove(category);
        categories.add(Math.max(0, categoryIndex - 1), category);
        saveCategories();
    }

    private void moveDownCategory(Category category) {
        int categoryIndex = categories.indexOf(category);
        categories.remove(category);
        categories.add(Math.min(categories.size(), categoryIndex + 1), category);
        saveCategories();
    }

    public void createCategoryDialog() {
        TextInputDialog dialog = new TextInputDialog("Name");

        dialog.setHeaderText(null);
        dialog.setGraphic(null);

        dialog.setTitle("Create Category");
        dialog.setContentText("Name:");

        Optional<String> result = dialog.showAndWait();

        result.ifPresent(name -> {
            if (categories.stream().noneMatch(cat -> cat.getText().equalsIgnoreCase(name))) {
                addCategory(createCategory(name));
            }
        });
    }

    private void addCategory(Category category) {
        if (categories.stream().noneMatch(cat -> cat.equals(category) || cat.getText().equalsIgnoreCase(category.getText()))) {
            categories.add(category);
            saveCategories();
        }
    }

    private void removeCategory(Category category) {
        categories.remove(category);
        saveCategories();
    }

    private Optional<Category> getCategory(String name) {
        return categories.stream().filter(cat -> cat.getText().equalsIgnoreCase(name)).map(cat -> (Category) cat).findFirst();
    }

    private Optional<PluginListCell> getPlugin(String name) {
        return plugins.stream().filter(pluginListCell -> pluginListCell.getPluginName().equalsIgnoreCase(name)).findFirst();
    }

    @Subscribe
    public void onExternalsReloaded(ExternalsReloaded e) {
        refreshPlugins();
    }
}
