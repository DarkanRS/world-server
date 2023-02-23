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

public class GolrieD extends Conversation {
	public GolrieD(Player player) {
		super(player);
		int NPC =  306;
		if(player.getQuestManager().getStage(Quest.WATERFALL_QUEST) == 3) {
			addPlayer(HeadE.HAPPY_TALKING, "Hi, I heard you may be able to help me.");
			addNPC(NPC, HeadE.CALM_TALK, "I might have something of use laying around in here somewhere.");
			addPlayer(HeadE.CONFUSED, "Mind if I take a look?");
			addNPC(NPC, HeadE.HAPPY_TALKING, "No, by all means go ahead.");
			addPlayer(HeadE.HAPPY_TALKING, "Could I have this old pebble?");
			addNPC(NPC, HeadE.CALM_TALK, "Sure, it's of no use to me.");
			addPlayer(HeadE.HAPPY_TALKING, "Thank you very much for your time.", ()->{
				player.getQuestManager().setStage(Quest.WATERFALL_QUEST, 4);
				player.getInventory().addItem(294, 1);
			});
			addNPC(NPC, HeadE.HAPPY_TALKING, "And thanks for saving me from this disgusting hole.");
			addPlayer(HeadE.HAPPY_TALKING, "You're welcome.");
			return;
		}
		if(player.getQuestManager().getStage(Quest.WATERFALL_QUEST) > 3) {
			addNPC(NPC, HeadE.CALM_TALK, "Have you lost Glarial's pebble?");
			if(player.getInventory().containsItem(294)) {
				addPlayer(HeadE.HAPPY_TALKING, "Nope, just passing by.");
				addNPC(NPC, HeadE.CALM_TALK, "Ah, well take care now!");
				return;
			}
			addPlayer(HeadE.SAD, "Yes I have...");
			addNPC(NPC, HeadE.HAPPY_TALKING, "Here. Take this one I just found.", ()->{
				player.getInventory().addItem(294, 1);
			});
			return;
		}
		addNPC(NPC, HeadE.CONFUSED, "What are you doing here?");
		addPlayer(HeadE.HAPPY_TALKING, "No idea...");
	}
}
