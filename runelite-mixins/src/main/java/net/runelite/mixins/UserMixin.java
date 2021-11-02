package net.runelite.mixins;

import net.runelite.api.mixins.Inject;
import net.runelite.api.mixins.Mixin;
import net.runelite.api.util.Text;
import net.runelite.rs.api.RSBuddy;
import net.runelite.rs.api.RSFriend;
import net.runelite.rs.api.RSUser;

@Mixin(RSUser.class)
public abstract class UserMixin implements RSUser {
	@Inject
	@Override
	public String getName() {
		return Text.sanitize(getRsName().getName$api());
	}
}
