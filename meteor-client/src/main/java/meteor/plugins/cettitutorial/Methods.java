package meteor.plugins.cettitutorial;

import dev.hoot.api.commons.Rand;
import dev.hoot.api.entities.TileObjects;
import dev.hoot.api.game.Game;
import net.runelite.api.TileObject;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;

import java.util.ArrayList;
import java.util.List;

public class Methods {

	public enum GameMode {
		REGULAR("Regular"),
		IRONMAN("Ironman"),
		HARDCORE_IRONMAN("Hardcore Ironman"),
		ULTIMATE_IRONMAN("Ultimate Ironman");

		public final String name;

		@Override
		public String toString() {
			return name;
		}

		GameMode(String name) {
			this.name = name;
		}
	}

	public static WorldPoint getRandomPoint(WorldPoint sourcePoint, int radius, boolean avoidObjects) {
		TileObject nearestObject = TileObjects.getNearest(x -> x.getName() != null && !x.getName().equals("null"));
		WorldArea sourceArea = new WorldArea(sourcePoint, 1, 1);
		WorldArea possibleArea = new WorldArea(
				new WorldPoint(sourcePoint.getX() - radius, sourcePoint.getY() - radius, sourcePoint.getPlane()),
				new WorldPoint(sourcePoint.getX() + radius, sourcePoint.getY() + radius, sourcePoint.getPlane())
		);

		List<WorldPoint> possiblePoints = possibleArea.toWorldPointList();
		List<WorldPoint> losPoints = new ArrayList<>();
		losPoints.add(sourcePoint);

		for (WorldPoint point : possiblePoints) {
			if (sourceArea.hasLineOfSightTo(Game.getClient(), point)) {
				if (point.equals(nearestObject.getWorldLocation()) && avoidObjects) {
					continue;
				}
				losPoints.add(point);
			}
		}
		return losPoints.get(Rand.nextInt(0, losPoints.size() - 1));
	}
}
