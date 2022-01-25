package meteor.ui.components;

import com.jfoenix.controls.JFXButton;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Paint;
import lombok.Getter;
import lombok.Setter;
import meteor.eventbus.events.ToolbarButtonClicked;
import dev.hoot.api.game.Game;

public class ToolbarButton extends JFXButton {

  public double width;
  /*
    This must be manually defined because the size of a node cannot be determined before it is added
    to a screen.
     */
  @Getter
  @Setter
  int realWidth;

  public ToolbarButton(FontAwesomeIcon icon, String text) {
    super(text);
    setTextFill(Paint.valueOf("CYAN"));
    setStyle("-fx-background-color: #252525; -fx-text-fill: CYAN; -jfx-button-type: RAISED;");
    AnchorPane.setTopAnchor(this, -5.0);
    AnchorPane.setBottomAnchor(this, 0.5);

    if (icon != null) {
      setGraphic(icon);
    }

    pressedProperty().addListener((options, oldValue, pressed) -> {
      if (!pressed) {
        return;
      }

      Game.getClient().getCallbacks().post(new ToolbarButtonClicked(text));
    });
  }

  public Runnable getOnSelect() {
    return null;
  }

  public void setGraphic(FontAwesomeIcon icon) {
    FontAwesomeIconView view = new FontAwesomeIconView(icon);
    view.setFill(Paint.valueOf("CYAN"));
    setGraphic(view);
  }
}
