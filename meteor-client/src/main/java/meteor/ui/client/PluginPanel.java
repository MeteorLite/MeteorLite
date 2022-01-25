package meteor.ui.client;

import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import meteor.util.MeteorConstants;


public class PluginPanel extends BorderPane {

	public PluginPanel() {
		setWidth(MeteorConstants.PANEL_WIDTH);
		setBackground(new Background(new BackgroundFill(MeteorConstants.LIGHT_GRAY, null, null)));
	}

}
