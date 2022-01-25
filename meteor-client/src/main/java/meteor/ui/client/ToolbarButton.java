package meteor.ui.client;

import com.jfoenix.controls.JFXButton;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.TextAlignment;
import meteor.util.MeteorConstants;

public class ToolbarButton extends JFXButton {

	private final Parent panel;
	private final String buttonText;

	public ToolbarButton(Parent panel, String buttonText, FontAwesomeIcon icon) {
		this.panel = panel;
		this.buttonText = buttonText;
		addIcon(icon);
		init();
	}

	public ToolbarButton(Parent panel, String buttonText, Image image) {
		this.panel = panel;
		this.buttonText = buttonText;
		addIcon(image);
		init();
	}

	private void init() {
		setMaxSize(32, 32);
		setMinSize(32, 32);
		setAlignment(Pos.CENTER);
		setTextAlignment(TextAlignment.CENTER);
		setTextFill(Color.CYAN);
		setButtonType(ButtonType.FLAT);
		setBackground(new Background(new BackgroundFill(Paint.valueOf("252525"), null, null)));
	}

	private void addIcon(Image image) {
		ImageView imageView = new ImageView(image);
		imageView.resize(24, 24);
		imageView.setPreserveRatio(true);
		setGraphic(imageView);
	}

	private void addIcon(FontAwesomeIcon icon) {
		FontAwesomeIconView panelButtonIcon = new FontAwesomeIconView(icon);
		panelButtonIcon.setFill(Color.AQUA);
		panelButtonIcon.setSize("12");
		setGraphic(panelButtonIcon);
	}

	public void swapOrientation(boolean vertical) {
		if (vertical) {
			setText("");
			setMaxSize(32, 32);
			setMinSize(32, 32);
			setButtonType(ButtonType.FLAT);
			setBackground(new Background(new BackgroundFill(MeteorConstants.GRAY, null, null)));
		} else {
			setText(buttonText);
			setMaxWidth(Double.MAX_VALUE);
			setButtonType(ButtonType.RAISED);
			setBackground(new Background(new BackgroundFill(MeteorConstants.GRAY, new CornerRadii(3), null)));
		}
	}
}
