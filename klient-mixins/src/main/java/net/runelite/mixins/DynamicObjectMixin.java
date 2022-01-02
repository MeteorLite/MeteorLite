package net.runelite.mixins;

import net.runelite.api.events.DynamicObjectAnimationChanged;
import net.runelite.api.mixins.*;
import net.runelite.rs.api.RSClient;
import net.runelite.rs.api.RSDynamicObject;
import net.runelite.rs.api.RSModel;
import net.runelite.rs.api.RSRenderable;

@Mixin(RSDynamicObject.class)
public abstract class DynamicObjectMixin implements RSDynamicObject {
	@Shadow("client")
	private static RSClient client;

	@SuppressWarnings("InfiniteRecursion")
	@Copy("getModel")
	@Replace("getModel")
	public RSModel copy$getModel()
	{
		try
		{
			// reset frame because it may have been set from the constructor
			// it should be set again inside the getModel method
			int animFrame = getAnimFrame();
			if (animFrame < 0)
			{
				setAnimFrame((animFrame ^ Integer.MIN_VALUE) & 0xFFFF);
			}
			return copy$getModel();
		}
		finally
		{
			int animFrame = getAnimFrame();
			if (animFrame < 0)
			{
				setAnimFrame((animFrame ^ Integer.MIN_VALUE) & 0xFFFF);
			}
		}
	}

	@FieldHook("cycleStart")
	@Inject
	public void onAnimCycleCountChanged(int idx)
	{
		if (client.isInterpolateObjectAnimations())
		{
			// sets the packed anim frame with the frame cycle
			int objectFrameCycle = client.getGameCycle() - getAnimCycleCount();
			setAnimFrame(Integer.MIN_VALUE | objectFrameCycle << 16 | getAnimFrame());
		}
	}

	@MethodHook(value = "<init>", end = true)
	@Inject
	public void rl$init(int id, int type, int orientation, int plane, int x, int y, int animationID, boolean var8, RSRenderable var9)
	{
		if (animationID != -1)
		{
			DynamicObjectAnimationChanged dynamicObjectAnimationChanged = new DynamicObjectAnimationChanged();
			dynamicObjectAnimationChanged.setObject(id);
			dynamicObjectAnimationChanged.setAnimation(animationID);
			client.getCallbacks().post(dynamicObjectAnimationChanged);
		}
	}

	@Inject
	@Override
	public int getAnimationID()
	{
		return (int) (getSequenceDefinition() == null ? -1 : getSequenceDefinition().getHash());
	}
}
