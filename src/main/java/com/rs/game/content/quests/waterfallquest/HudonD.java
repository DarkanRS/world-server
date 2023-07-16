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
package com.rs.game.content.quests.waterfallquest;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.quest.Quest;
import com.rs.game.model.entity.player.Player;

public class HudonD extends Conversation {

	public HudonD(Player player, int hudonId) {
		super(player);

		if (player.getQuestManager().getStage(Quest.WATERFALL_QUEST) == 1) {
			addPlayer(HeadE.TALKING_ALOT, "Hey Hudon! Your mother is worried about you! Are you doing alright?");
			addNPC(hudonId, HeadE.CHILD_UNSURE, "Who are you and what do you want? You must want my treasure!");
			addPlayer(HeadE.TALKING_ALOT, "Your mother wants you to come home!");
			addNPC(hudonId, HeadE.CHILD_ANGRY, "No! There is too much treasure to be found!");
			addPlayer(HeadE.CONFUSED, "What treasure?");
			addNPC(hudonId, HeadE.CHILD_HAPPY_TALK, "The treasure inside the waterfall!");
			addPlayer(HeadE.TALKING_ALOT, "Can I look for treasure with you?");
			addNPC(hudonId, HeadE.CHILD_ANGRY, "No! You will steal it all!");
			addPlayer(HeadE.TALKING_ALOT, "Very well then..", () -> {
				player.getQuestManager().setStage(Quest.WATERFALL_QUEST, 2);
			});
		} else if (player.getQuestManager().getStage(Quest.WATERFALL_QUEST) > 1)
			addNPC(hudonId, HeadE.CHILD_ANGRY, "I already told you! I am not letting you come with me to find the treasure!");
		else
			addNPC(hudonId, HeadE.CHILD_ANGRY, "Leave me alone! I have treasure to find!");

		create();
	}
}
