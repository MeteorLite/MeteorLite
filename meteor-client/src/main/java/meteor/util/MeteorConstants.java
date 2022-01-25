package meteor.util;

import javafx.scene.paint.Paint;
import net.runelite.api.Constants;

import java.awt.*;

public class MeteorConstants {

	// toolbar size can be height or width
	public static final int TOOLBAR_SIZE = 32;
	public static final int PANEL_WIDTH = 350;
	public static final int CLIENT_WIDTH = Constants.GAME_FIXED_WIDTH + (Constants.GAME_FIXED_WIDTH - 749);
	public static final int CLIENT_HEIGHT = Constants.GAME_FIXED_HEIGHT + (Constants.GAME_FIXED_HEIGHT - 464);
	public static final Dimension CLIENT_SIZE = new Dimension(CLIENT_WIDTH, CLIENT_HEIGHT);

	public static final Paint DARK_GRAY = Paint.valueOf("121212");
	public static final Paint LIGHT_GRAY = Paint.valueOf("252525");
	public static final Paint GRAY = Paint.valueOf("212121");
	public static final Color CYAN = Color.CYAN;
	public static final Paint CYAN_PAINT = Paint.valueOf("00FFFF");

}
