package net.runelite.mixins;

import net.runelite.api.mixins.Inject;
import net.runelite.api.mixins.Mixin;
import net.runelite.rs.api.RSBuddy;

@Mixin(RSBuddy.class)
public abstract class BuddyMixin implements RSBuddy {
	@Inject
	@Override
	public int getCurrentWorld() {
		return getWorld$api();
	}
}
