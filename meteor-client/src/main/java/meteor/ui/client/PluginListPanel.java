package meteor.ui.client;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToolBar;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
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
import java.util.Comparator;

public class PluginListPanel extends BorderPane {

	private final ObservableList<PluginListCell> plugins;
	private final ObservableList<Category> categories;

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
		categories = FXCollections.observableArrayList();

		ToolBar toolBar = initSearchBar();
		ScrollPane pluginListPane = initPluginListPane();


		setBackground(new Background(new BackgroundFill(MeteorConstants.LIGHT_GRAY, null, null)));

		setMinWidth(MeteorConstants.PANEL_WIDTH);
		setMaxWidth(MeteorConstants.PANEL_WIDTH);


		FilteredList<PluginListCell> filteredData = new FilteredList<>(plugins, s -> true);

//		CustomTextField searchBar = new CustomTextField();
//		searchBar.setStyle("-fx-text-inner-color: white;");
//		searchBar.setBackground(new Background(new BackgroundFill(MeteorConstants.DARK_GRAY, null, null)));
//
//		FontAwesomeIconView searchIcon = new FontAwesomeIconView(FontAwesomeIcon.SEARCH);
//		searchIcon.setFill(Color.CYAN);
//		searchIcon.setTranslateX(2);
//		searchBar.setLeft(searchIcon);
//
//		searchBar.textProperty().addListener(obs -> {
//			String filter = searchBar.getText().toLowerCase();
//			if (filter.length() == 0) {
//				filteredData.setPredicate(s -> true);
//			} else {
//				filteredData.setPredicate(s -> s.getPluginName().toLowerCase().contains(filter));
//			}
//		});
		setTop(toolBar);

		VBox pluginListView = new VBox();
		pluginListView.setBackground(new Background(new BackgroundFill(MeteorConstants.LIGHT_GRAY, null, null)));

		filteredData.addListener((ListChangeListener.Change<? extends PluginListCell> c) -> {
			pluginListView.getChildren().clear();
			pluginListView.getChildren().addAll(filteredData);
		});

		pluginListPane.setContent(pluginListView);
		setCenter(pluginListPane);

//		addCategory.addEventHandler(MouseEvent.MOUSE_CLICKED, (e) -> {
//			refreshPlugins();
//		});

		refreshPlugins();
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

	private void addPlugin(Plugin p) {
		PluginListCell panel = new PluginListCell(p, configManager);
		PluginToggleButton tb = panel.getToggleButton();
		if (tb != null) {
			eventBus.register(panel.getToggleButton());
		}
		plugins.add(panel);
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
		searchIconLabel.setPadding(new Insets(0,2,0,7));

		searchBar.setLeft(searchIconLabel);

		FontAwesomeIconView addCategory = new FontAwesomeIconView();
		addCategory.setFill(Color.CYAN);
		addCategory.setGlyphName("PLUS");

		Button addCategoryButton = new Button();
		addCategoryButton.setBackground(new Background(new BackgroundFill(MeteorConstants.DARK_GRAY, null, null)));
		addCategoryButton.setGraphic(addCategory);

		HBox.setHgrow(searchBar, Priority.ALWAYS);
		toolBar.getItems().addAll(searchBar, addCategoryButton);
		return toolBar;
	}

	@Subscribe
	public void onExternalsReloaded(ExternalsReloaded e) {
		refreshPlugins();
	}
}
