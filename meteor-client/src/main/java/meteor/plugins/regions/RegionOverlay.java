package meteor.plugins.regions;

import dev.hoot.api.movement.Movement;
import dev.hoot.api.movement.pathfinder.GlobalCollisionMap;
import meteor.ui.overlay.Overlay;
import meteor.ui.overlay.OverlayLayer;
import meteor.ui.overlay.OverlayPosition;
import meteor.ui.overlay.OverlayPriority;

import javax.inject.Inject;
import java.awt.*;

public class RegionOverlay extends Overlay {
	private final RegionConfig regionConfig;
	private final GlobalCollisionMap collisionMap;

	@Inject
	public RegionOverlay(RegionConfig regionConfig, GlobalCollisionMap collisionMap) {
		this.regionConfig = regionConfig;
		this.collisionMap = collisionMap;

		setPosition(OverlayPosition.DYNAMIC);
		setLayer(OverlayLayer.ABOVE_WIDGETS);
		setPriority(OverlayPriority.LOW);
	}

	@Override
	public Dimension render(Graphics2D graphics) {
		if (!regionConfig.overlay()) {
			return null;
		}

		Movement.drawCollisions(graphics, collisionMap);
		return null;
	}
}
