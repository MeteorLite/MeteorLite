package meteor.ui.client;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import lombok.Getter;
import meteor.ui.MeteorUI;
import meteor.util.MeteorConstants;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.*;

@Singleton
public class RightPanel extends JFXPanel {

	private Parent panel = new Pane();
	private HBox root = new HBox(panel);

	@Inject
	private MeteorUI meteorUI;

	@Inject
	private Toolbar toolbar;

	@Getter
	private boolean isOpen = false;

	public RightPanel() {
		Scene scene = new Scene(root, 0, 1080);
		setScene(scene);
	}

	private void updateScene(int width) {
		Platform.runLater(() -> {
			root = new HBox();
			if (isOpen) {
				root.getChildren().add(panel);
			}
			if (hasToolbar()) {
				VBox tb = toolbar.initVerticalToolbar();
				tb.setFillWidth(true);
				root.getChildren().add(tb);
			}
			Scene scene = new Scene(root, width, 1080);
			setScene(scene);
		});
	}

	public void addToolbar() {
		Platform.runLater(() -> {
			root = new HBox(panel, toolbar.initVerticalToolbar());
			updateScene(MeteorConstants.PANEL_WIDTH + MeteorConstants.TOOLBAR_SIZE);
		});
	}

	public void removeToolbar() {
		Platform.runLater(() -> {
			root = new HBox(panel);
			toolbar.refresh();
			updateScene(MeteorConstants.PANEL_WIDTH);
		});
	}

	private boolean hasToolbar() {
		return toolbar.getPosition().equals(BorderLayout.EAST);
	}

	private void changePanel(Parent parent) {
		if (parent instanceof ScrollPane) {
			((ScrollPane) parent).setMinWidth(MeteorConstants.PANEL_WIDTH);
		}
		if (parent instanceof Pane) {
			((Pane) parent).setMinWidth(MeteorConstants.PANEL_WIDTH);
		}
		panel = parent;
		if (root.getChildren().size() > 1) {
			root.getChildren().remove(0);
		}
		root.getChildren().add(0, panel);
	}

	private void open(Parent parent) {
		isOpen = true;
		if (hasToolbar()) {
			updateScene(MeteorConstants.PANEL_WIDTH + MeteorConstants.TOOLBAR_SIZE);
		} else {
			updateScene(MeteorConstants.PANEL_WIDTH);
			meteorUI.showRightPanel();
		}
		changePanel(parent);
		meteorUI.updateClientSize();
	}

	public void close() {
		Platform.runLater(() -> {
			isOpen = false;
			if (hasToolbar()) {
				updateScene(MeteorConstants.TOOLBAR_SIZE);
			} else {
				updateScene(0);
				meteorUI.hideRightPanel();
			}
			meteorUI.updateClientSize();
		});
	}

	public void update(Parent parent) {
		Platform.runLater(() -> {
			if (isOpen()) {
				if (parent == null) {
					return;
				}
				if (panel == null) {
					return;
				}
				if (parent.equals(panel)) {
					close();
				} else {
					changePanel(parent);
				}
			} else {
				open(parent);
			}
		});
	}
}
