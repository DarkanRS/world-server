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
package com.rs.game.content.tutorialisland;

import com.rs.Settings;
import com.rs.game.content.tutorialisland.TutorialIslandController.Stage;
import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.dialogue.statements.NPCStatement;
import com.rs.engine.dialogue.statements.OptionStatement;
import com.rs.engine.dialogue.statements.PlayerStatement;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.player.Player;

public class FinancialAdvisor extends Conversation {

	public FinancialAdvisor(Player player, NPC npc, TutorialIslandController ctrl) {
		super(player);

		if (ctrl.getStage() == Stage.TALK_TO_FINANCIAL_ADVISOR) {
			addNext(new PlayerStatement(HeadE.NO_EXPRESSION, "Hello, Who are you?"));
			addNext(new NPCStatement(npc.getId(), HeadE.CHEERFUL, "I'm the Financial Advisor. I'm here to tell people how to", "make money."));
			addNext(new PlayerStatement(HeadE.SKEPTICAL, "Okay. How can I make money then?"));
			addNext(new NPCStatement(npc.getId(), HeadE.CHEERFUL, "How you can make money? Quite."));
		} else {
			addNext(new OptionStatement("Would you like to hear about making money again?", "Yes!", "No thanks."));
			addNext(new NPCStatement(npc.getId(), HeadE.CHEERFUL, "Okay, making money. Quite."));
		}

		addNext(new NPCStatement(npc.getId(), HeadE.CHEERFUL, "Well, there are three basic ways of making money here:", "combat, quests and trading. I will talk you through each", "of them very quickly."));
		addNext(new NPCStatement(npc.getId(), HeadE.CHEERFUL, "Let's start with combat as it is probably still fresh in", "your mind. Many enemies, both human and monster,", "will drop items when they die."));
		addNext(new NPCStatement(npc.getId(), HeadE.CHEERFUL, "Now, the next way to earn money quickly is by quests.", "Many people on "+ Settings.getConfig().getServerName()+" have things they need", "doing, which they will reward you for."));
		addNext(new NPCStatement(npc.getId(), HeadE.CHEERFUL, "By getting a high level in skills such as Cooking, Mining,", "Smithing or Fishing, you can create or catch your own", "items and sell them for pure profit."));
		addNext(new NPCStatement(npc.getId(), HeadE.CHEERFUL, "Well, that about covers it. Come back if you'd like to go", "over this again."));
		addNext(new Dialogue().setFunc(() -> ctrl.nextStage(Stage.LEAVE_FINANCIAL_ADVISOR_ROOM)));

		create();
	}
}
