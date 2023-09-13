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
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.dialogue.Options;
import com.rs.engine.quest.Quest;
import com.rs.game.model.entity.player.Player;

public class AlmeraD extends Conversation {
	public AlmeraD(Player player) {
		super(player);
		int NPC = 304;
		addOptions("Choose an option:", new Options() {
			@Override
			public void create() {
				option("How is life on the waterfall?", new Dialogue()
						.addPlayer(HeadE.HAPPY_TALKING, "How is life on the waterfall?")
						.addNPC(NPC, HeadE.CALM_TALK, "I am worried about my son, but other than that everything is fine.")
				);
				option("I am looking for a quest.", new Dialogue()
						.addPlayer(HeadE.HAPPY_TALKING, "I am looking for a quest.")
						.addNPC(NPC, HeadE.CALM_TALK, "I might have one for you. My son Hudon has gone missing on some hunt for treasure in the waterfall.")
						.addNPC(NPC, HeadE.CALM_TALK, "Could you please go make sure he is alright for me?")
						.addQuestStart(Quest.WATERFALL_QUEST)
						.addOptions("Choose an option:", new Options() {
							@Override
							public void create() {
								option("Of course.", new Dialogue()
										.addPlayer(HeadE.HAPPY_TALKING, "Of course I will.", () ->{player.getQuestManager().setStage(Quest.WATERFALL_QUEST, 1);})
										.addNPC(NPC, HeadE.CALM_TALK, "Thank you so much!")
								);
								option("No thanks, I don't have time right now.", new Dialogue()
										.addPlayer(HeadE.HAPPY_TALKING, "Sorry, but I don't have time right now. Bye.")
								);
							}
						})
				);
			}
		});
	}
}
