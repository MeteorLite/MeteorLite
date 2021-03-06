/*
 * Copyright (c) 2018, Kamiel
 * Copyright (c) 2019, Adam <Adam@sigterm.info>
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
package meteor.plugins.raids;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum RoomType
{
	START("Start", '#'),
	END("End", '¤'),
	SCAVENGERS("Scavengers", 'S'),
	FARMING("Farming", 'F'),
	EMPTY("Empty", ' '),
	COMBAT("Combat", 'C'),
	PUZZLE("Puzzle", 'P');

	private final String name;
	private final char code;

	RaidRoom getUnsolvedRoom()
	{
		switch (this)
		{
			case START:
				return RaidRoom.START;
			case END:
				return RaidRoom.END;
			case SCAVENGERS:
				return RaidRoom.SCAVENGERS;
			case FARMING:
				return RaidRoom.FARMING;
			case COMBAT:
				return RaidRoom.UNKNOWN_COMBAT;
			case PUZZLE:
				return RaidRoom.UNKNOWN_PUZZLE;
			case EMPTY:
			default:
				return RaidRoom.EMPTY;
		}
	}

	static RoomType fromCode(char code)
	{
		for (RoomType type : values())
		{
			if (type.getCode() == code)
			{
				return type;
			}
		}

		return EMPTY;
	}
}
