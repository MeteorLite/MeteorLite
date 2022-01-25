/*
 * Copyright (c) 2017, Adam <Adam@sigterm.info>
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
package meteor.plugins.hiscorewise;

import com.google.common.collect.ImmutableList;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.regex.Pattern;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Provider;

import meteor.eventbus.EventBus;
import meteor.menus.MenuManager;
import meteor.plugins.Plugin;
import meteor.plugins.PluginDescriptor;
import meteor.ui.MeteorUI;
import meteor.ui.client.Toolbar;
import net.runelite.api.Client;

@PluginDescriptor(
	name = "WiseOldMan Tracker",
	description = "Enable the WiseOldMan tracker panel",
	tags = {"panel", "players"},
	cantDisable = true
)
public class HiscoreWisePlugin extends Plugin
{
	private static final String LOOKUP = "Lookup";
	private static final String KICK_OPTION = "Kick";
	private static final ImmutableList<String> AFTER_OPTIONS = ImmutableList.of("Message", "Add ignore", "Remove friend", "Delete", KICK_OPTION);
	private static final Pattern BOUNTY_PATTERN = Pattern.compile("<col=ff0000>You've been assigned a target: (.*)</col>");
	public static HiscoreWisePlugin INSTANCE;

	@Inject
	@Nullable
	private Client client;

	@Inject
	private Provider<MenuManager> menuManager;

	@Inject
	public Toolbar toolbar;

	@Inject
	private MeteorUI meteorUI;

	@Inject
	private EventBus eventBus;

	private static Parent wiseOldManPanel;
	public static Parent skillOverviewPanel;
	{
		try {
			INSTANCE = this;
			wiseOldManPanel = FXMLLoader.load(Objects.requireNonNull(getClass().getClassLoader()
					.getResource("meteor/plugins/hiscorewise/hiscorewise.fxml")));
			skillOverviewPanel =FXMLLoader.load(Objects.requireNonNull(getClass().getClassLoader()
					.getResource("meteor/plugins/hiscorewise/skilloverview.fxml")));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void showPanel() {
		INSTANCE.meteorUI.updateRightPanel(wiseOldManPanel);
	}

	@Override
	public void startup()
	{
		InputStream input = ClassLoader.getSystemClassLoader().getResourceAsStream("wom.png");
		Image image = new Image(input, 20, 20, false, false);
		ImageView imageView = new ImageView(image);
		imageView.setPreserveRatio(true);

		toolbar.addNavigationButton(image, wiseOldManPanel, this);
	}

	@Override
	public void shutdown()
	{
		toolbar.removeNavigationButton(this);

		if (client != null)
		{
			menuManager.get().removePlayerMenuItem(LOOKUP);
		}
	}
}
