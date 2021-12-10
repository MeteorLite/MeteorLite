package meteor.ui.client;

import com.jfoenix.controls.JFXButton;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.TextAlignment;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import meteor.plugins.Plugin;
import meteor.ui.MeteorUI;
import meteor.util.MeteorConstants;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


@Singleton
@Slf4j
public class Toolbar extends JFXPanel {

	private final Map<String, ToolbarButton> buttons;

	@Inject
	private MeteorUI meteorUI;

	@Inject
	private RightPanel rightPanel;

	@Getter
	private String position;

	@Getter
	private Parent currentRoot;

	@Getter
	private Map<String, Parent> roots;

	public Toolbar() {
		buttons = new HashMap<>();

		ToolbarButton pluginsButton = new ToolbarButton(null, "Plugins", FontAwesomeIcon.PLUG);

		pluginsButton.setOnMouseClicked(event -> {
			meteorUI.showPlugins();
		});

		buttons.put("plugins", pluginsButton);

		position = BorderLayout.NORTH;
		Platform.runLater(() -> createScene(true));
	}

	private void createScene(boolean vertical) {
		Scene scene;
		if (vertical) {
			currentRoot = initVerticalToolbar();
			scene = new Scene(currentRoot, 32,1080);
		} else {
			currentRoot = initHorizontalToolbar();
			scene = new Scene(currentRoot, 1920,32);
		}
		setScene(scene);
	}

	public void setPosition(String position) {
		this.position = position;
		refresh();
	}

	public boolean isVertical() {
		return position.equals(BorderLayout.EAST) || position.equals(BorderLayout.WEST);
	}

	public void refresh() {
		if (position.equals(BorderLayout.EAST)) {
			rightPanel.addToolbar();
		} else {
			Platform.runLater(() -> createScene(isVertical()));
		}
	}

	private HBox initHorizontalToolbar() {
		HBox horizontalToolbar = new HBox();
		horizontalToolbar.setMinHeight(32);
		horizontalToolbar.setMaxHeight(32);
		horizontalToolbar.setMinWidth(MeteorConstants.CLIENT_WIDTH);
		horizontalToolbar.setPrefWidth(MeteorConstants.CLIENT_WIDTH);
		horizontalToolbar.setAlignment(Pos.CENTER_RIGHT);
		horizontalToolbar.setPadding(new Insets(0,4,0,4));
		horizontalToolbar.setBackground(new Background(new BackgroundFill(MeteorConstants.GRAY, null, null)));

		for (ToolbarButton button : buttons.values()) {
			button.swapOrientation(false);
			horizontalToolbar.getChildren().add(button);
		}
		return horizontalToolbar;
	}

	public VBox initVerticalToolbar() {
		VBox verticalToolbar = new VBox();
		verticalToolbar.setMinWidth(32);
		verticalToolbar.setMaxWidth(32);
		verticalToolbar.setBackground(new Background(new BackgroundFill(MeteorConstants.GRAY, null, null)));

		for (ToolbarButton button : buttons.values()) {
			button.swapOrientation(true);
			verticalToolbar.getChildren().add(button);
		}
		return verticalToolbar;
	}

	public void addNavigationButton(FontAwesomeIcon icon, Parent parent, Plugin plugin) {
		ToolbarButton toolbarButton = new ToolbarButton(parent, plugin.getName(), icon);
		toolbarButton.setOnMouseClicked(event -> {
			meteorUI.updateRightPanel(parent);
		});
		buttons.put(plugin.getClass().getSimpleName(), toolbarButton);
		refresh();
	}

	public void addNavigationButton(Image image, Parent parent, Plugin plugin) {
		ToolbarButton toolbarButton = new ToolbarButton(parent, plugin.getName(), image);
		toolbarButton.setOnMouseClicked(event -> {
			meteorUI.updateRightPanel(parent);
		});
		buttons.put(plugin.getClass().getSimpleName(), toolbarButton);
		refresh();
	}

	public void removeNavigationButton(Plugin plugin) {
		buttons.remove(plugin.getClass().getSimpleName());
		refresh();
	}

}
