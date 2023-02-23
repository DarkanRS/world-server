// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program.  If not, see <http://www.gnu.org/licenses/>.
//
//  Copyright (C) 2021 Trenton Kress
//  This file is part of project: Darkan
//
package com.rs.game.content.skills.dungeoneering.skills;

import java.util.Arrays;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.statements.MakeXStatement;
import com.rs.engine.dialogue.statements.MakeXStatement.MakeXType;
import com.rs.game.model.entity.player.Player;

public class DungeoneeringRCD extends Conversation {
	
	public enum DungRCRune {
		AIR(17780, 1, .10, 11, 2, 22, 3, 34, 4, 44, 5, 55, 6, 66, 7, 77, 8, 88, 9, 99, 10),
		WATER(17781, 5, .12, 19, 2, 38, 3, 57, 4, 76, 5, 95, 6),
		EARTH(17782, 9, .13, 26, 2, 52, 3, 78, 4),
		FIRE(17783, 14, .14, 35, 2, 70, 3),
		
		MIND(17784, 2, .11, 14, 2, 28, 3, 42, 4, 56, 5, 70, 6, 84, 7, 98, 8),
		CHAOS(17785, 35, .17, 74, 2),
		DEATH(17786, 65, .20, 74, 2),
		BLOOD(17787, 77, .21),
		
		BODY(17788, 20, .15, 46, 2, 92, 3),
		COSMIC(17789, 27, .16, 59, 2),
		ASTRAL(17790, 40, .174, 82, 2),
		NATURE(17791, 44, .18, 91, 2),
		LAW(17792, 54, .19);
		
		int runeId;
		int level;
		double xp;
		int[] multipliers;
		
		private DungRCRune(int runeId, int level, double xp, int... multipliers) {
			this.runeId = runeId;
			this.level = level;
			this.xp  = xp;
			this.multipliers = multipliers;
		}
	}
	
	public enum DungRCSet {
		ELEMENTAL(DungRCRune.AIR, DungRCRune.WATER, DungRCRune.EARTH, DungRCRune.FIRE),
		COMBAT(DungRCRune.MIND, DungRCRune.CHAOS, DungRCRune.DEATH, DungRCRune.BLOOD),
		OTHER(DungRCRune.BODY, DungRCRune.COSMIC, DungRCRune.ASTRAL, DungRCRune.NATURE, DungRCRune.LAW),
		STAVES();

		private DungRCRune[] runes;
		
		DungRCSet(DungRCRune... runes) {
			this.runes = runes;
		}
	}
	
	static final int[] STAVES = { 16997, 17001, 17005, 17009, 17013, 16999, 17003, 17007, 17011, 17015 };
	
	public DungeoneeringRCD(Player player, DungRCSet set) {
		super(player);
		
		if (set == DungRCSet.STAVES) {
			addOptions("What would you like to make?", ops -> {
				ops.add("Runes").addNext(addMakeOps(DungRCSet.ELEMENTAL));
				ops.add("Staves").addNext(addMakeOps(null));
			});
			create();
			return;
		}
		addNext(addMakeOps(set));
		create();
	}

	private Dialogue addMakeOps(DungRCSet set) {
		Dialogue dialogue = new Dialogue();
		if (set == null) {
			dialogue = dialogue.addNext(new MakeXStatement(MakeXType.MAKE_INTERVAL, STAVES));
			for (int i = 0;i < STAVES.length;i++) {
				final int index = i;
				dialogue.addNext(() -> player.getActionManager().setAction(new DungeoneeringStaves(index, MakeXStatement.getQuantity(player))));
			}
			return dialogue.getHead();
		}
		int[] items = Arrays.stream(set.runes).mapToInt(rune -> rune.runeId).toArray();
		dialogue = dialogue.addNext(new MakeXStatement(MakeXType.MAKE_INTERVAL, items));
		for (int i = 0;i < items.length;i++) {
			final int index = i;
			dialogue.addNext(() -> player.getActionManager().setAction(new DungeoneeringRunecrafting(MakeXStatement.getQuantity(player), set.runes[index].runeId, set.runes[index].level, set.runes[index].xp, set.runes[index].multipliers)));
		}
		return dialogue.getHead();
	}
}
