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
package com.rs.game.player.dialogues;

import com.rs.game.player.quests.Quest;

public class LunarAltar extends Dialogue {

	@Override
	public void start() {
		if (!Quest.LUNAR_DIPLOMACY.meetsRequirements(player, "to use the Lunar Spellbook."))
			end();
		else
			sendOptionsDialogue("Change spellbooks?", "Yes, replace my spellbook.", "Never mind.");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		if (componentId == OPTION_1) {
			if (!Quest.LUNAR_DIPLOMACY.meetsRequirements(player, "to use the Lunar Spellbook."))
				return;
			if (player.getCombatDefinitions().getSpellBook() != 430) {
				sendDialogue("Your mind clears and you switch", "back to the lunar spellbook.");
				player.getCombatDefinitions().setSpellBook(2);
			} else {
				sendDialogue("Your mind clears and you switch", "back to the normal spellbook.");
				player.getCombatDefinitions().setSpellBook(0);
			}
		} else
			end();
	}

	@Override
	public void finish() {

	}

}
