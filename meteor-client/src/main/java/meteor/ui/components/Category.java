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
import meteor.plugins.Plugin;
import meteor.ui.client.PluginListCell;
import meteor.util.MeteorConstants;

public class Category extends TitledPane {

    @Getter
    private final ObservableList<Plugin> plugins;

    @Getter
    private final ObservableList<PluginListCell> pluginListCells;

    @Getter
    private final FilteredList<PluginListCell> filteredPlugins;

    public Category(String name) {
        setText(name);
        setBackground(new Background(new BackgroundFill(MeteorConstants.GRAY, null, null)));

        getStylesheets().add("css/plugins/jfx-titledpane.css");

        plugins = FXCollections.observableArrayList();
        pluginListCells = FXCollections.observableArrayList();

        VBox pluginList = new VBox();
        pluginList.setBackground(new Background(new BackgroundFill(MeteorConstants.LIGHT_GRAY, null, null)));
        filteredPlugins = new FilteredList<>(pluginListCells, s -> true);

        setContent(pluginList);

        filteredPlugins.addListener((ListChangeListener.Change<? extends PluginListCell> c) -> {
            pluginList.getChildren().setAll(filteredPlugins);
        });
    }

    public void clearExternals() {
        plugins.removeIf(Plugin::isExternal);
        pluginListCells.removeIf(plc -> plc.getPlugin().isExternal());
    }

    public void addPlugin(PluginListCell plugin) {
        if (pluginListCells.stream().noneMatch(plc -> plc.getPluginName().equalsIgnoreCase(plugin.getPluginName()))) {
            pluginListCells.add(plugin);
            plugins.add(plugin.getPlugin());
        }
    }

    public void removePlugin(PluginListCell plugin) {
        pluginListCells.remove(plugin);
        plugins.remove(plugin.getPlugin());
    }

}
