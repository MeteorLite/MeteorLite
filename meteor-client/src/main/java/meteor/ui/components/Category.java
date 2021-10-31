package meteor.ui.components;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.VBox;
import lombok.Getter;
import lombok.Setter;
import meteor.ui.client.PluginListCell;
import meteor.util.MeteorConstants;

public class Category extends TitledPane {

	private final VBox pluginList;

	@Getter
	private final ObservableList<PluginListCell> plugins;

	@Getter
	private final FilteredList<PluginListCell> filteredPlugins;

	public Category(String name) {
		setText(name);
		setBackground(new Background(new BackgroundFill(MeteorConstants.LIGHT_GRAY, null, null)));

		plugins = FXCollections.observableArrayList();

		pluginList = new VBox();
		pluginList.setBackground(new Background(new BackgroundFill(MeteorConstants.LIGHT_GRAY, null, null)));
		filteredPlugins = new FilteredList<>(plugins, s -> true);

		setContent(pluginList);

		filteredPlugins.addListener((ListChangeListener.Change<? extends PluginListCell> c) -> {
			pluginList.getChildren().setAll(filteredPlugins);
			if (c.getList().size() > 0) {
				setExpanded(true);
			}
		});
	}

	public void addPlugin(PluginListCell plugin) {
		if (plugins.stream().noneMatch(plc -> plc.getPluginName().equalsIgnoreCase(plugin.getPluginName()))) {
			plugins.add(plugin);
		}
	}

	public void removePlugin(PluginListCell plugin) {
		plugins.remove(plugin);
	}

}
