/*
 * Copyright (c) 2018, Magic fTail
 * Copyright (c) 2018, Adam <Adam@sigterm.info>
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
package meteor.plugins.timestamp;

import com.google.inject.Provides;
import java.awt.Color;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import javax.inject.Inject;
import lombok.Getter;
import meteor.config.ConfigManager;
import meteor.eventbus.Subscribe;
import meteor.eventbus.events.ConfigChanged;
import meteor.plugins.Plugin;
import meteor.plugins.PluginDescriptor;
import meteor.util.ColorUtil;
import net.runelite.api.Client;
import net.runelite.api.MessageNode;
import net.runelite.api.ScriptID;
import net.runelite.api.Varbits;
import net.runelite.api.events.ScriptPreFired;
import net.runelite.api.events.ScriptCallbackEvent;

@PluginDescriptor(
	name = "Chat Timestamps",
	description = "Add timestamps to chat messages",
	tags = {"timestamp"},
	enabledByDefault = false
)
public class ChatTimestampPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private TimestampConfig config;

	@Getter
	private SimpleDateFormat formatter;

	private MessageNode currentlyBuildingMessage = null;

	@Provides
	public TimestampConfig getConfig(final ConfigManager configManager)
	{
		return configManager.getConfig(TimestampConfig.class);
	}

	@Override
	public void startup()
	{
		updateFormatter();
	}

	@Override
	public void shutdown()
	{
		formatter = null;
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged event)
	{
		if (event.getGroup().equals("timestamp") && event.getKey().equals("format"))
		{
			updateFormatter();
		}
	}

	@Subscribe
	private void onScriptCallbackEvent(ScriptCallbackEvent event)
	{
		if (!"chatMessageBuilding".equals(event.getEventName()))
		{
			return;
		}

		int uid = client.getIntStack()[client.getIntStackSize() - 1];
		currentlyBuildingMessage = client.getMessages().get$api(uid);
	}

	@Subscribe
	private void onScriptPreFired(ScriptPreFired ev)
	{
		int numStringArgs;
		int messagePrefixArg = 0;
		switch (ev.getScriptId())
		{
			case ScriptID.CHATBOX_BUILD_LINE_WITHOUT_USER:
				numStringArgs = 1;
				break;
			case ScriptID.CHATBOX_BUILD_LINE_WITH_USER:
				numStringArgs = 2;
				break;
			case ScriptID.CHATBOX_BUILD_LINE_WITH_CLAN:
				numStringArgs = 3;
				break;
			default:
				return;
		}

		if (currentlyBuildingMessage == null)
		{
			return;
		}

		MessageNode messageNode = currentlyBuildingMessage;
		currentlyBuildingMessage = null;

		String[] stringStack = client.getStringStack();
		int stringArgStart = client.getStringStackSize() - numStringArgs;

		String timestamp = generateTimestamp(messageNode.getTimestamp(), ZoneId.systemDefault()) + " ";
		
		Color timestampColour = getTimestampColour();
		if (timestampColour != null)
		{
			timestamp = ColorUtil.wrapWithColorTag(timestamp, timestampColour);
		}
		
		String segment = stringStack[stringArgStart + messagePrefixArg];
		segment = timestamp + segment;
		stringStack[stringArgStart + messagePrefixArg] = segment;
	}

	private Color getTimestampColour()
	{
		boolean isChatboxTransparent = client.isResized() && client.getVar(Varbits.TRANSPARENT_CHATBOX) == 1;

		return isChatboxTransparent ? config.transparentTimestamp() : config.opaqueTimestamp();
	}

	String generateTimestamp(int timestamp, ZoneId zoneId)
	{
		final ZonedDateTime time = ZonedDateTime.ofInstant(
			Instant.ofEpochSecond(timestamp), zoneId);

		return formatter.format(Date.from(time.toInstant()));
	}

	private void updateFormatter()
	{
		try
		{
			formatter = new SimpleDateFormat(config.timestampFormat());
		}
		catch (IllegalArgumentException e)
		{
			formatter = new SimpleDateFormat("[HH:mm]");
		}
	}
}