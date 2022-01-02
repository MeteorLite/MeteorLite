package net.runelite.mixins;

import net.runelite.api.RuneLiteObject;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.events.GraphicsObjectCreated;
import net.runelite.api.mixins.*;
import net.runelite.rs.api.RSClient;
import net.runelite.rs.api.RSGraphicsObject;
import net.runelite.rs.api.RSModel;
import net.runelite.rs.api.RSRuneLiteObject;

@Mixin(RSGraphicsObject.class)
public abstract class GraphicsObjectMixin implements RSGraphicsObject
{
	@Shadow("client")
	private static RSClient client;

	@Inject
	GraphicsObjectMixin()
	{
		client.getCallbacks().post(GraphicsObjectCreated.class, new meteor.eventbus.events.GraphicsObjectCreated(this));
	}

	@Override
	@Inject
	public LocalPoint getLocation()
	{
		return new LocalPoint(this.getX(), this.getY());
	}


	@Copy("advance")
	@Replace("advance")
	public void copy$advance(int var1)
	{
		if (this instanceof RuneLiteObject)
		{
			((RSRuneLiteObject) this).advanceRL(var1);
		}
		else
		{
			copy$advance(var1);
		}
	}


	@Copy("getModel")
	@Replace("getModel")
	public RSModel copy$getModel()
	{
		if (this instanceof RuneLiteObject)
		{
			return ((RSRuneLiteObject) this).getModelRl();
		}
		else
		{
			return copy$getModel();
		}
	}
}
