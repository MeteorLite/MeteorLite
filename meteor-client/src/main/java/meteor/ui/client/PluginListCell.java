package meteor.ui.client;

import com.jfoenix.controls.JFXTooltip;
import javafx.scene.control.ContextMenu;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Paint;
import lombok.Getter;
import meteor.config.Config;
import meteor.config.ConfigManager;
import meteor.eventbus.EventBus;
import meteor.plugins.Plugin;
import meteor.plugins.PluginDescriptor;
import meteor.ui.components.MeteorText;
import meteor.util.MeteorConstants;

public class PluginListCell extends AnchorPane {

    @Getter
    private final Plugin plugin;
    @Getter
    private final String pluginName;
    @Getter
    private PluginToggleButton toggleButton;

    public PluginListCell(Plugin plugin, ConfigManager configManager, EventBus eventBus) {
        setBackground(new Background(new BackgroundFill(MeteorConstants.LIGHT_GRAY, null, null)));
        this.plugin = plugin;
        pluginName = plugin.getName();
        ContextMenu contextMenu = new ContextMenu();
        contextMenu.setStyle("-fx-background-color: #121212;");
        if (!plugin.getClass().getAnnotation(PluginDescriptor.class).cantDisable()) {
            toggleButton = new PluginToggleButton(plugin);
            AnchorPane.setTopAnchor(toggleButton, 6.0);
            AnchorPane.setRightAnchor(toggleButton, 8.0);

            toggleButton.selectedProperty().addListener((options, oldValue, newValue) -> {
                if (newValue == plugin.isEnabled()) {
                    return;
                }
                plugin.toggle();
            });

            toggleButton.setSelected(Boolean.parseBoolean(configManager.getConfiguration(plugin.getClass().getSimpleName(), "pluginEnabled")));
        }

        if (toggleButton != null) {
            toggleButton.selectedProperty().addListener((options, oldValue, newValue) -> {
                configManager.setConfiguration(plugin.getClass().getSimpleName(), "pluginEnabled", newValue);
            });
        }

        MeteorText pluginName = new MeteorText(plugin);
        pluginName.setFill(Paint.valueOf("WHITE"));

        AnchorPane.setLeftAnchor(pluginName, 2.0);
        AnchorPane.setTopAnchor(pluginName, 0.0);

        if (plugin.getClass().getAnnotation(PluginDescriptor.class).description().length() > 0) {
            JFXTooltip tooltip = new JFXTooltip();
            tooltip.setText(plugin.getClass().getAnnotation(PluginDescriptor.class).description());

            pluginName.addEventHandler(MouseEvent.MOUSE_ENTERED, (e) -> {
                tooltip.showOnAnchors(pluginName, 0, -50);
            });

            pluginName.addEventHandler(MouseEvent.MOUSE_EXITED, (e) -> {
                tooltip.hide();
            });
        }

        PluginConfigButton configButton = new PluginConfigButton(plugin);
        Config pluginConfig = plugin.getConfig(configManager);
        if (pluginConfig != null && pluginConfig.getClass().getDeclaredMethods().length > 4) {
            configButton.setVisible(true);
        }

        getChildren().addAll(pluginName, configButton);

        if (toggleButton != null) {
            getChildren().add(toggleButton);
            eventBus.register(toggleButton);
        }

        pluginName.setOnContextMenuRequested(e -> {
            contextMenu.show(pluginName, e.getScreenX(), e.getScreenY());
        });

    }

}
