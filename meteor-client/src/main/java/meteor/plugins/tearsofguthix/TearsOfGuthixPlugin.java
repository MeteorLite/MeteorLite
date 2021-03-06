/*
 * Copyright (c) 2018, Infinitay <https://github.com/Infinitay>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package meteor.plugins.tearsofguthix;

import com.google.inject.Provides;
import lombok.Getter;
import meteor.config.ConfigManager;
import meteor.eventbus.Subscribe;
import meteor.plugins.Plugin;
import meteor.plugins.PluginDescriptor;
import meteor.ui.overlay.OverlayManager;
import net.runelite.api.Client;
import net.runelite.api.DecorativeObject;
import net.runelite.api.ObjectID;
import net.runelite.api.events.DecorativeObjectDespawned;
import net.runelite.api.events.DecorativeObjectSpawned;
import net.runelite.api.events.GameStateChanged;

import javax.inject.Inject;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@PluginDescriptor(
	name = "Tears Of Guthix",
	description = "Show timers for the Tears Of Guthix streams",
	tags = {"minigame", "overlay", "skilling", "timers", "tog"}
)
public class TearsOfGuthixPlugin extends Plugin
{
	private static final int TOG_REGION = 12948;

	@Inject
	private Client client;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private TearsOfGuthixOverlay overlay;

	@Getter
	private final Map<DecorativeObject, Instant> streams = new HashMap<>();

	@Provides
	public TearsOfGuthixConfig getConfig(ConfigManager configManager)
	{
		return configManager.getConfig(TearsOfGuthixConfig.class);
	}

	@Override
	public void startup()
	{
		overlayManager.add(overlay);
	}

	@Override
	public void shutdown()
	{
		overlayManager.remove(overlay);
		streams.clear();
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged event)
	{
		switch (event.getGameState())
		{
			case LOADING:
			case LOGIN_SCREEN:
			case HOPPING:
				streams.clear();
		}
	}

	@Subscribe
	public void onDecorativeObjectSpawned(DecorativeObjectSpawned event)
	{
		DecorativeObject object = event.getDecorativeObject();

		if (object.getId() == ObjectID.BLUE_TEARS ||
			object.getId() == ObjectID.BLUE_TEARS_6665 ||
			object.getId() == ObjectID.GREEN_TEARS ||
			object.getId() == ObjectID.GREEN_TEARS_6666)
		{
			if (client.getLocalPlayer().getWorldLocation().getRegionID() == TOG_REGION)
			{
				streams.put(event.getDecorativeObject(), Instant.now());
			}
		}
	}

	@Subscribe
	public void onDecorativeObjectDespawned(DecorativeObjectDespawned event)
	{
		if (streams.isEmpty())
		{
			return;
		}

		DecorativeObject object = event.getDecorativeObject();
		streams.remove(object);
	}
}
