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
package com.rs.game.content.quests.dwarfcannon;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.quest.Quest;
import com.rs.game.model.entity.player.Player;

public class LollkD extends Conversation {

	private static final int Lollk = 207;

	public LollkD(Player player) {
		super(player);
		int currentStage = player.getQuestManager().getStage(Quest.DWARF_CANNON);
		switch(currentStage) {
		case 5:
			addNPC(Lollk, HeadE.HAPPY_TALKING, "Thank the heavens, you saved me! I thought I'd be goblin lunch for sure!");
			addPlayer(HeadE.NO_EXPRESSION, "Are you okay?");
			addNPC(Lollk, HeadE.HAPPY_TALKING, "I think so, I'd better run off home.");
			addPlayer(HeadE.NO_EXPRESSION, "That's right, you get going. I'll catch up.");
			addNPC(Lollk, HeadE.HAPPY_TALKING, "Thanks again, brave adventurer.", () -> {
				player.sendMessage("The dwarf child runs off into the caverns.");
				player.getQuestManager().setStage(Quest.DWARF_CANNON, 6);
			});
			break;
		default:
			addPlayer(HeadE.NERVOUS, "Hello.");
			addNPC(Lollk, HeadE.HAPPY_TALKING, "Hello.");
		}
		create();
	}
}
