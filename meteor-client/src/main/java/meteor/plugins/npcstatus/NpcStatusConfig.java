/*
 * Copyright (c) 2019, GeChallengeM <https://github.com/GeChallengeM>
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
package meteor.plugins.npcstatus;

import java.awt.Font;
import lombok.AllArgsConstructor;
import lombok.Getter;
import meteor.config.*;
import meteor.plugins.fightcave.FightCaveConfig.FontStyle;

@ConfigGroup("npcstatus")
public interface NpcStatusConfig extends Config
{
	@ConfigSection(
		keyName = "rangeTitle",
		position = 1,
		name = "Attack range",
		description = ""
	)
	String rangeTitle = "Attack range";

	@Range(
		min = 1,
		max = 20
	)
	@ConfigItem(
		keyName = "AttackRange",
		name = "NPC attack range",
		description = "The attack range of the NPC.",
		position = 2,
		section = rangeTitle
	)
	default int getRange()
	{
		return 1;
	}

	@ConfigSection(
		keyName = "speedTitle",
		position = 3,
		name = "Attack speed",
		description = ""
	)
	String speedTitle = "Attack speed";

	@ConfigItem(
		keyName = "CustomAttSpeedEnabled",
		name = "Custom attack speed",
		description = "Use this if the timer is wrong.",
		position = 4,
		section = speedTitle
	)
	default boolean isCustomAttSpeed()
	{
		return false;
	}

	@Range(
		min = 1,
		max = 9
	)
	@ConfigItem(
		keyName = "CustomAttSpeed",
		name = "Custom NPC att speed",
		description = "The attack speed of the NPC (amount of ticks between their attacks).",
		position = 5,
		hidden = true,
		unhide = "CustomAttSpeedEnabled",
		section = speedTitle
	)
	default int getCustomAttSpeed()
	{
		return 4;
	}

	@ConfigItem(
			keyName = "autoPray",
			name = "Auto Pray",
			description = "Automatically pray based on Pray Style",
			position = 5
	)
	default boolean autoPray()
	{
		return false;
	}

	@Getter
	@AllArgsConstructor
	enum PrayStyle
	{
		MAGIC("Magic"),
		RANGED("Ranged"),
		MELEE("Melee");

		private String name;

		@Override
		public String toString()
		{
			return getName();
		}
	}

	@ConfigItem(
			position = 6,
			keyName = "prayStyle",
			name = "Pray Style",
			description = ""
	)
	default PrayStyle prayStyle()
	{
		return PrayStyle.MELEE;
	}
}
