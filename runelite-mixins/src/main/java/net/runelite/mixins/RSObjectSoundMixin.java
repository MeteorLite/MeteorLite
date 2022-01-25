package net.runelite.mixins;

import net.runelite.api.coords.LocalPoint;
import net.runelite.api.mixins.Inject;
import net.runelite.api.mixins.Mixin;
import net.runelite.rs.api.RSObjectSound;

@Mixin(RSObjectSound.class)
public abstract class RSObjectSoundMixin implements RSObjectSound {
	@Inject
	public LocalPoint getMinPosition()
	{
		return new LocalPoint(getX(), getY());
	}

	@Inject
	public LocalPoint getMaxPosition()
	{
		return new LocalPoint(getMaxX(), getMaxY());
	}
}
