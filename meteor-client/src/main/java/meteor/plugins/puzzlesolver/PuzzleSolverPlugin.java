/*
 * Copyright (c) 2018, Lotto <https://github.com/devLotto>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package meteor.plugins.puzzlesolver;

import com.google.inject.Provides;
import meteor.config.ConfigManager;
import meteor.eventbus.Subscribe;
import meteor.plugins.Plugin;
import meteor.plugins.PluginDescriptor;
import meteor.plugins.puzzlesolver.lightbox.*;
import meteor.ui.overlay.OverlayManager;
import meteor.util.ColorUtil;
import meteor.util.Text;
import net.runelite.api.Client;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.events.WidgetLoaded;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetID;
import net.runelite.api.widgets.WidgetInfo;

import javax.inject.Inject;
import java.awt.*;
import java.util.Arrays;

import static net.runelite.api.widgets.WidgetInfo.*;

@PluginDescriptor(
	name = "Puzzle Solver",
	description = "Show you where to click to solve puzzle boxes",
	tags = {"clues", "scrolls", "overlay"}
)
public class PuzzleSolverPlugin extends Plugin
{
	private static final Color CORRECT_MUSEUM_PUZZLE_ANSWER_COLOR = new Color(0, 248, 128);

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private PuzzleSolverOverlay overlay;

	@Inject
	private Client client;

	private LightboxState lightbox;
	private LightboxState[] changes = new LightboxState[LightBox.COMBINATIONS_POWER];
	private Combination lastClick;
	private boolean lastClickInvalid;

	@Override
	public void startup()
	{
		overlayManager.add(overlay);
	}

	@Override
	public void shutdown()
	{
		overlayManager.remove(overlay);
	}

	@Provides
	public PuzzleSolverConfig getConfig(ConfigManager configManager)
	{
		return configManager.getConfig(PuzzleSolverConfig.class);
	}

	@Subscribe
	public void onWidgetLoaded(WidgetLoaded widget)
	{
		if (widget.getGroupId() != WidgetID.VARROCK_MUSEUM_QUIZ_GROUP_ID)
		{
			return;
		}

		final Widget questionWidget = client.getWidget(WidgetInfo.VARROCK_MUSEUM_QUESTION);

		if (questionWidget == null)
		{
			return;
		}

		final Widget answerWidget = VarrockMuseumAnswer.findCorrect(
			client,
			questionWidget.getText(),
			WidgetInfo.VARROCK_MUSEUM_FIRST_ANSWER,
			WidgetInfo.VARROCK_MUSEUM_SECOND_ANSWER,
			WidgetInfo.VARROCK_MUSEUM_THIRD_ANSWER);

		if (answerWidget == null)
		{
			return;
		}

		final String answerText = answerWidget.getText();
		if (answerText.equals(Text.removeTags(answerText)))
		{
			answerWidget.setText(ColorUtil.wrapWithColorTag(answerText, CORRECT_MUSEUM_PUZZLE_ANSWER_COLOR));
		}
	}

	@Subscribe
	public void onMenuOptionClicked(MenuOptionClicked menuOptionClicked)
	{
		int widgetId = menuOptionClicked.getParam1();
		if (TO_GROUP(widgetId) != WidgetID.LIGHT_BOX_GROUP_ID)
		{
			return;
		}

		Combination combination;
		if (widgetId == LIGHT_BOX_BUTTON_A.getPackedId())
		{
			combination = Combination.A;
		}
		else if (widgetId == LIGHT_BOX_BUTTON_B.getPackedId())
		{
			combination = Combination.B;
		}
		else if (widgetId == LIGHT_BOX_BUTTON_C.getPackedId())
		{
			combination = Combination.C;
		}
		else if (widgetId == LIGHT_BOX_BUTTON_D.getPackedId())
		{
			combination = Combination.D;
		}
		else if (widgetId == LIGHT_BOX_BUTTON_E.getPackedId())
		{
			combination = Combination.E;
		}
		else if (widgetId == LIGHT_BOX_BUTTON_F.getPackedId())
		{
			combination = Combination.F;
		}
		else if (widgetId == LIGHT_BOX_BUTTON_G.getPackedId())
		{
			combination = Combination.G;
		}
		else if (widgetId == LIGHT_BOX_BUTTON_H.getPackedId())
		{
			combination = Combination.H;
		}
		else
		{
			return;
		}

		if (lastClick != null)
		{
			lastClickInvalid = true;
		}
		else
		{
			lastClick = combination;
		}
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		Widget lightboxWidget = client.getWidget(WidgetInfo.LIGHT_BOX_CONTENTS);
		if (lightboxWidget == null)
		{
			if (lightbox != null)
			{
				lastClick = null;
				lastClickInvalid = false;
				lightbox = null;
				Arrays.fill(changes, null);
			}
			return;
		}

		// get current state from widget
		LightboxState lightboxState = new LightboxState();
		int index = 0;
		for (Widget light : lightboxWidget.getDynamicChildren())
		{
			boolean lit = light.getItemId() == LightBox.LIGHT_BULB_ON;
			lightboxState.setState(index / LightBox.WIDTH, index % LightBox.HEIGHT, lit);
			index++;
		}

		if (lightboxState.equals(lightbox))
		{
			return; // no change
		}

		LightboxState prev = lightbox;
		lightbox = lightboxState;

		if (lastClick == null || lastClickInvalid)
		{
			lastClick = null;
			lastClickInvalid = false;
			return;
		}

		LightboxState diff = lightboxState.diff(prev);
		changes[lastClick.ordinal()] = diff;

		lastClick = null;

		// try to solve
		LightboxSolver solver = new LightboxSolver();
		solver.setInitial(lightbox);
		int idx = 0;
		for (LightboxState state : changes)
		{
			if (state != null)
			{
				Combination combination = Combination.values()[idx];
				solver.setSwitchChange(combination, state);
			}
			++idx;
		}

		LightboxSolution solution = solver.solve();
		if (solution != null)
		{
		}

		// Set solution to title
		Widget lightbox = client.getWidget(WidgetInfo.LIGHT_BOX);
		if (lightbox != null)
		{
			Widget title = lightbox.getChild(1);
			if (solution != null && solution.numMoves() > 0)
			{
				title.setText("Light box - Solution: " + solution);
			}
			else if (solution != null)
			{
				title.setText("Light box - Solution: solved!");
			}
			else
			{
				title.setText("Light box - Solution: unknown");
			}
		}
	}
}
