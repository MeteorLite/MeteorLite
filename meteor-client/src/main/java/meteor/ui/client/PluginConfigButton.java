package meteor.ui.client;

import com.jfoenix.controls.JFXButton;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import meteor.MeteorLiteClientModule;
import meteor.plugins.Plugin;

public class PluginConfigButton extends JFXButton {

	public PluginConfigButton(Plugin plugin) {
		FontAwesomeIconView cog = new FontAwesomeIconView(FontAwesomeIcon.COG);
		cog.setFill(MeteorLiteClientModule.METEOR_FONT_COLOR);
		cog.setSize(String.valueOf(MeteorLiteClientModule.METEOR_FONT_SIZE));
		setGraphic(cog);

		addEventHandler(MouseEvent.MOUSE_CLICKED, (e) -> {
			plugin.showConfig();
		});

		setVisible(false);
		AnchorPane.setRightAnchor(this, 48.0);
		AnchorPane.setTopAnchor(this, 0.0);
	}
}
