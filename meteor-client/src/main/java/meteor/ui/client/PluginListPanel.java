package meteor.ui.client;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import org.sponge.util.Logger;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class PluginListPanel extends BorderPane {

    private static final String MAIN_CATEGORY_NAME = "Plugins";
    private static final String EXTERNAL_CATEGORY_NAME = "Externals";

    private final Logger logger = new Logger("PluginList");
    private final ObservableList<Plugin> plugins;
    private Category main;
    private Category externals;
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

        loadPlugins();

        ToolBar toolBar = initSearchBar();
        ScrollPane pluginListPane = initPluginListPane();

        initCategories();

        setBackground(new Background(new BackgroundFill(MeteorConstants.LIGHT_GRAY, null, null)));

        setMinWidth(MeteorConstants.PANEL_WIDTH);
        setMaxWidth(MeteorConstants.PANEL_WIDTH);

        setTop(toolBar);
        setCenter(pluginListPane);
    }

    public void loadPlugins() {
        plugins.setAll(pluginManager.getPlugins());
        plugins.sort(Comparator.comparing(pl -> pl.getName().toLowerCase()));
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

        return scrollPane;
    }

    private ToolBar initSearchBar() {
        ToolBar toolBar = new ToolBar();
        toolBar.setBackground(new Background(new BackgroundFill(MeteorConstants.LIGHT_GRAY, null, null)));

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
        });

        searchBar.textProperty().addListener(obs -> {
            String filter = searchBar.getText().toLowerCase();
            if (filter.length() == 0) {
                main.getFilteredPlugins().setPredicate(s -> true);
            } else {
                main.getFilteredPlugins().setPredicate(s -> s.getPluginName().toLowerCase().contains(filter));
                main.setExpanded(true);
            }
        });

        HBox.setHgrow(searchBar, Priority.ALWAYS);
        toolBar.getItems().addAll(searchBar, addCategoryButton);
        return toolBar;
    }

    private PluginListCell createPluginListCell(Plugin plugin) {
        PluginListCell panel = new PluginListCell(plugin, configManager, eventBus);
        panel.setOnContextMenuRequested(new EventHandler<ContextMenuEvent>() {
            @Override
            public void handle(ContextMenuEvent event) {
                List<Category> categoriesWithPlugin = getCategoriesWithPlugin(plugin);
                List<Category> categoriesWithoutPlugin = getCategoriesWithoutPlugin(plugin);

                if (categories.size() > 2) {
                    ContextMenu contextMenu = new ContextMenu();
                    contextMenu.setStyle("-fx-background-color: #121212; -fx-highlight-fill: #424242");

                    Menu addToCategory = new Menu("Add to Category");
                    addToCategory.setStyle("-fx-background-color: #121212; -fx-highlight-fill: #424242; -fx-text-fill: cyan");
                    if (!categoriesWithoutPlugin.isEmpty()) {
                        categoriesWithoutPlugin
                                .forEach(cat -> {
                                    MenuItem add = new MenuItem(cat.getText());
                                    add.setStyle("-fx-background-color: #121212; -fx-text-fill: cyan; -fx-highlight-fill: #424242");
                                    add.setOnAction(new EventHandler<ActionEvent>() {
                                        @Override
                                        public void handle(ActionEvent event) {
                                            cat.addPlugin(createPluginListCell(plugin));
                                            saveCategories();
                                        }
                                    });
                                    addToCategory.getItems().add(add);
                                });
                        contextMenu.getItems().add(addToCategory);
                    }

                    Menu removeFromCategory = new Menu("Remove from Category");
                    removeFromCategory.setStyle("-fx-background-color: #121212; -fx-highlight-fill: #424242; -fx-text-fill: cyan; -fx-border-color: #121212;");
                    if (!categoriesWithPlugin.isEmpty()) {
                        categoriesWithPlugin
                                .forEach(cat -> {
                                    MenuItem remove = new MenuItem(cat.getText());
                                    remove.setStyle("-fx-background-color: #121212; -fx-text-fill: cyan; -fx-highlight-fill: #424242; -fx-border-color: #121212;");
                                    remove.setOnAction(new EventHandler<ActionEvent>() {
                                        @Override
                                        public void handle(ActionEvent event) {
                                            cat.removePlugin(panel);
                                            saveCategories();
                                        }
                                    });
                                    removeFromCategory.getItems().add(remove);
                                });
                        contextMenu.getItems().add(removeFromCategory);
                    }

                    if (contextMenu.getItems().size() > 0) {
                        contextMenu.show(panel, event.getScreenX(), event.getScreenY());
                    }
                }
            }
        });

        return panel;
    }

    public Category createCategory(String name) {
        Category category = new Category(name);
        ContextMenu contextMenu = new ContextMenu();
        contextMenu.setStyle("-fx-background-color: #121212; -fx-highlight-fill: #424242");
        MenuItem moveUp = new MenuItem("Move Up");
        moveUp.setStyle("-fx-background-color: #121212; -fx-text-fill: cyan; -fx-highlight-fill: #424242");
        moveUp.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                moveUpCategory(category);
            }
        });

        MenuItem moveDown = new MenuItem("Move Down");
        moveDown.setStyle("-fx-background-color: #121212; -fx-text-fill: cyan; -fx-highlight-fill: #424242");
        moveDown.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                moveDownCategory(category);
            }
        });

        contextMenu.getItems().addAll(moveUp, moveDown);

        if (!category.getText().equalsIgnoreCase(MAIN_CATEGORY_NAME) && !category.getText().equalsIgnoreCase(EXTERNAL_CATEGORY_NAME)) {
            MenuItem remove = new MenuItem("Remove Category");
            remove.setStyle("-fx-background-color: #121212; -fx-text-fill: cyan; -fx-highlight-fill: #424242");
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
                            getPlugin(s).ifPresent(plugin -> {
                                category.addPlugin(createPluginListCell(plugin));
                            });
                        }
                    }
                }

                categories.add(category);
            }
        }

        categories.stream()
                .filter(cat -> cat.getText().equals(MAIN_CATEGORY_NAME)).findFirst().ifPresentOrElse(cat -> {
                            if (cat instanceof Category c) {
                                main = c;
                                plugins.forEach(pl -> {
                                    main.addPlugin(createPluginListCell(pl));
                                });
                            }
                        },
                        () -> {
                            main = createCategory(MAIN_CATEGORY_NAME);
                            plugins.forEach(pl -> {
                                main.addPlugin(createPluginListCell(pl));
                            });
                            categories.add(0, main);
                        });

        categories.stream()
                .filter(cat -> cat.getText().equals(EXTERNAL_CATEGORY_NAME)).findFirst().ifPresentOrElse(cat -> {
                            if (cat instanceof Category c) {
                                externals = c;
                                plugins.forEach(pl -> {
                                    if (pl.isExternal()) {
                                        externals.addPlugin(createPluginListCell(pl));
                                    }
                                });
                            }
                        },
                        () -> {
                            externals = createCategory(EXTERNAL_CATEGORY_NAME);
                            plugins.forEach(pl -> {
                                if (pl.isExternal()) {
                                    externals.addPlugin(createPluginListCell(pl));
                                }
                            });
                            categories.add(1, externals);
                        });

        saveCategories();
    }

    public void saveCategories() {
        categories.forEach(cat -> {
            if (cat instanceof Category c) {
                if (!c.getText().equals(MAIN_CATEGORY_NAME) && !c.getText().equals(EXTERNAL_CATEGORY_NAME)) {
                    configManager.setConfiguration("category",
                            c.getText(),
                            c.getPluginListCells().stream()
                                    .map(PluginListCell::getPluginName)
                                    .distinct()
                                    .collect(Collectors.joining(",")));
                }
            }
        });

        configManager.setConfiguration("meteorlite", "categories",
                categories.stream()
                        .map(TitledPane::getText)
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
        // Needs styling
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
        return categories.stream()
                .filter(Category.class::isInstance)
                .map(Category.class::cast)
                .filter(cat -> cat.getText().equalsIgnoreCase(name))
                .findFirst();
    }

    private Optional<Plugin> getPlugin(String name) {
        return plugins.stream()
                .filter(plugin -> plugin.getName().equalsIgnoreCase(name))
                .findFirst();
    }

    private List<Category> getCategoriesWithoutPlugin(Plugin plugin) {
        List<Category> pluginCategories = getCategoriesWithPlugin(plugin);
        return categories.stream()
                .filter(Category.class::isInstance)
                .map(Category.class::cast)
                .filter(cat -> !cat.getText().equalsIgnoreCase(MAIN_CATEGORY_NAME) && !cat.getText().equalsIgnoreCase(EXTERNAL_CATEGORY_NAME))
                .filter(Predicate.not(pluginCategories::contains))
                .collect(Collectors.toList());
    }

    private List<Category> getCategoriesWithPlugin(Plugin plugin) {
        return categories.stream()
                .filter(Category.class::isInstance)
                .map(Category.class::cast)
                .filter(cat -> !cat.getText().equalsIgnoreCase(MAIN_CATEGORY_NAME) && !cat.getText().equalsIgnoreCase(EXTERNAL_CATEGORY_NAME))
                .filter(cat -> cat.getPlugins().contains(plugin))
                .collect(Collectors.toList());
    }

    @Subscribe
    public void onExternalsReloaded(ExternalsReloaded e) {
        loadPlugins();
        main.clearExternals();
        externals.clearExternals();
        plugins.forEach(pl -> {
            if (pl.isExternal()) {
                main.addPlugin(createPluginListCell(pl));
                externals.addPlugin(createPluginListCell(pl));
            }
        });
    }
}
