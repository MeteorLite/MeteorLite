package net.runelite.mixins;

import net.runelite.api.mixins.Inject;
import net.runelite.api.mixins.Mixin;
import net.runelite.api.util.Text;
import net.runelite.rs.api.RSUser;

@Mixin(RSUser.class)
public abstract class UserMixin implements RSUser {
	@Inject
	@Override
	public String getName$api() {
		return Text.sanitize(getRsName().getName$api());
	}
}
