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
package com.rs.game.content.quests.handlers.restlessghost;

import com.rs.game.content.dialogue.Conversation;
import com.rs.game.content.dialogue.Dialogue;
import com.rs.game.content.dialogue.HeadE;
import com.rs.game.content.dialogue.Options;
import com.rs.game.content.quests.Quest;
import com.rs.game.content.world.GraveStoneSelection;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.util.Utils;

public class FatherAereckD extends Conversation {
	int NPC = 9827;
	public FatherAereckD(Player p) {
		super(p);
		addNPC(NPC, HeadE.CALM_TALK, "Hello there " + p.getPronoun("brother ", "sister ") + Utils.formatPlayerNameForDisplay(p.getDisplayName()) + ". How may I help you today?");
		addOptions("What would you like to say?", new Options() {
			@Override
			public void create() {
				if(p.getQuestManager().getStage(Quest.RESTLESS_GHOST) == 0)
					option("I'm looking for a quest.", new Dialogue()
							.addPlayer(HeadE.HAPPY_TALKING, "I'm looking for a quest.")
							.addNPC(NPC, HeadE.CALM_TALK, "Well that's convenient. I seem to be having a bit of a<br>ghost problem. Could you go speak to " +
									"speak to<br>Father Urhney down in the swamp about how to<br>exorcise the spirit?", () -> {
								player.getQuestManager().setStage(Quest.RESTLESS_GHOST, 1, true);
							})
					);
				option("Can I have a different gravestone?", new Dialogue()
						.addPlayer(HeadE.HAPPY_TALKING, "Can I have a different gravestone?")
						.addNPC(NPC, HeadE.CALM_TALK, "Of course you can. Have a look at this selection of gravestones.")
						.addNext(()->{GraveStoneSelection.openSelectionInterface(p);})
				);
				option("Can you restore my prayer?", new Dialogue()
						.addPlayer(HeadE.HAPPY_TALKING, "Can you restore my prayer?")
						.addNPC(NPC, HeadE.CALM_TALK, "I think the Gods prefer it if you pray<br>to them at an altar dedicated to their name.")
				);
			}
		});
	}
}