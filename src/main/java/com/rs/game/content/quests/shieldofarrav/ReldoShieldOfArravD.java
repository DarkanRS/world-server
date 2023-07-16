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
package com.rs.game.content.quests.shieldofarrav;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.quest.Quest;
import com.rs.game.model.entity.player.Player;

public class ReldoShieldOfArravD extends Conversation {
	final int RELDO = 647;

	public ReldoShieldOfArravD(Player p) {
		super(p);
		switch(p.getQuestManager().getStage(Quest.SHIELD_OF_ARRAV)) {
		case ShieldOfArrav.NOT_STARTED_STAGE:
			addPlayer(HeadE.SECRETIVE, "I'm in search of a quest");
			addNPC(RELDO, HeadE.SKEPTICAL_THINKING, "Hmmm, I don't... believe there are any here... let me think actually...");

			//options
			addOption("Shield Of Arrav", "Accept quest", "Not right now");
			addNPC(RELDO, HeadE.CALM_TALK, " Ah, yes. I know. If you look in a book called 'The Shield of Arrav', you'll find a quest in there. I'm not " +
					"sure where the book is mind you... but I'm sure it's around here somewhere.")
			.addNext(() -> {
				ShieldOfArrav.setStage(p, ShieldOfArrav.FIND_BOOK_STAGE);
			});
			addPlayer(HeadE.CALM_TALK, "On second thoughts, I don't want a quest after all.")
			.addNPC(RELDO, HeadE.CALM_TALK, "Oh. But you said you're in search of a quest.")
			.addPlayer(HeadE.CALM_TALK, "I was. I am. I changed my mind.")
			.addNPC(RELDO, HeadE.CALM_TALK, "You perplex me. Well, come back if you change your mind again.");
			break;
		case ShieldOfArrav.FIND_BOOK_STAGE:
			addPlayer(HeadE.SECRETIVE, "Where is that book...'The Shield of Arrav'?");
			if (p.getInventory().containsItem(757)) {
				addNPC(RELDO, HeadE.SECRETIVE, "Did you find the book?");
				addPlayer(HeadE.SECRETIVE, "Yes");
				addNPC(RELDO, HeadE.FRUSTRATED, "Why are you asking me where to find a book you've already found? Have you read it?");
				addPlayer(HeadE.SECRETIVE, "No");
				addNPC(RELDO, HeadE.SECRETIVE, "Try reading it.");
				addPlayer(HeadE.SECRETIVE, "Read 'The Shield of Arrav'. Got it.");
			} else
				addNPC(RELDO, HeadE.SECRETIVE, "I'm not sure where it is, exactly...but I'm sure it's somewhere around the library.");
			break;
		case ShieldOfArrav.BOOK_IS_READ_STAGE:
			addPlayer(HeadE.SECRETIVE, "I've read the book. Do you know where I can find the Phoenix Gang?");
			addNPC(RELDO, HeadE.SECRETIVE, "No, I don't. I think I know someone who might, however. If I were you I would talk to Baraek, the fur trader " +
					"in the market place. I've heard he has connections with the Phoenix Gang.");
			addPlayer(HeadE.HAPPY_TALKING, "Thanks. I'll try that!");
			addNext(() -> {
				ShieldOfArrav.setStage(p, ShieldOfArrav.TALK_TO_BARAEK_STAGE);
			});
			break;
		case ShieldOfArrav.TALK_TO_BARAEK_STAGE:
			addPlayer(HeadE.SECRETIVE, "Remind me again where I can find the Phoenix Gang?");
			addNPC(RELDO, HeadE.SECRETIVE, "If I were you I would talk to Baraek, the fur trader in the market place. I've heard he has connections with " +
					"the Phoenix Gang.");
			break;
		default:
			addNPC(RELDO, HeadE.HAPPY_TALKING, "You seem to have this covered, good luck!");
			break;
		}

	}

}
