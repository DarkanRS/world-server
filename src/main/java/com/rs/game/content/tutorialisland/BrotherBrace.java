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

import com.rs.game.content.tutorialisland.TutorialIslandController.Stage;
import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.dialogue.statements.NPCStatement;
import com.rs.engine.dialogue.statements.OptionStatement;
import com.rs.engine.dialogue.statements.PlayerStatement;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.player.Player;

public class BrotherBrace extends Conversation {

	public BrotherBrace(Player player, NPC npc, TutorialIslandController ctrl) {
		super(player);
		npc.faceEntity(player);
		npc.resetWalkSteps();

		createStage("Intro", new PlayerStatement(HeadE.NO_EXPRESSION, "Good day, brother."))
		.addNext(new NPCStatement(npc.getId(), HeadE.NO_EXPRESSION, "Hello, I'm Brother Brace.", "I'm here to tell you all about Prayer."))
		.addNext(new Dialogue().setFunc(() -> ctrl.nextStage(Stage.OPEN_PRAYER_TAB)));

		createStage("Prayer", new NPCStatement(npc.getId(), HeadE.NO_EXPRESSION, "This is your Prayer list. Prayers can help a lot in", "combat. Click on the prayer you wish to use to activate", "it, and click it again to deactivate it."))
		.addNext(new NPCStatement(npc.getId(), HeadE.NO_EXPRESSION, "Active prayers will drain your Prayer Points, which", "you can recharge by finding an altar or other holy spot", "and praying there."))
		.addNext(new NPCStatement(npc.getId(), HeadE.NO_EXPRESSION, "As you noticed, most enemies will drop bones when", "defeated. Burying bones, by clicking them in your", "inventory, will gain you Prayer experience."))
		.addNext(new NPCStatement(npc.getId(), HeadE.NO_EXPRESSION, "I'm also the community officer 'round here, so it's my", "job to tell you about your friends and ignore list."))
		.addNext(new Dialogue().setFunc(() -> ctrl.nextStage(Stage.OPEN_FRIENDS_TAB)));

		createStage("Friends", new NPCStatement(npc.getId(), HeadE.NO_EXPRESSION, "Good. Now you have both menus open I'll tell you a", "little about each. You can add people to either list by", "clicking the add button then typing their name into the", "box that appears."))
		.addNext(new NPCStatement(npc.getId(), HeadE.NO_EXPRESSION, "You remove people from the lists in the same way. If", "you add someone to your ignore list they will not be", "able to talk to you or send any form of message to", "you."))
		.addNext(new NPCStatement(npc.getId(), HeadE.NO_EXPRESSION, "Your friends list shows the online status of your", "friends. Friends in red are offline, friends in green are", "online and on the same server and friends in yellow", "are online, but on a different server."))
		.addNext(new PlayerStatement(HeadE.NO_EXPRESSION, "Are there rules on in-game behaviour?"))
		.addNext(new NPCStatement(npc.getId(), HeadE.NO_EXPRESSION, "Yes, you should read the rules of conduct on the", "website to make sure you do nothing to get yourself", "banned."))
		.addNext(new NPCStatement(npc.getId(), HeadE.NO_EXPRESSION, "But in general, always try to be courteous to other", "players - remember the people in game are real", "people with real feelings."))
		.addNext(new NPCStatement(npc.getId(), HeadE.NO_EXPRESSION, "If you go 'round being abusive or causing trouble your", "character could end up being the one in trouble."))
		.addNext(new PlayerStatement(HeadE.NO_EXPRESSION, "Okay, thanks, I'll bear that in mind."))
		.addNext(new Dialogue().setFunc(() -> ctrl.nextStage(Stage.LEAVE_CHURCH_AREA)));

		if (ctrl.inSection(Stage.TALK_TO_BROTHER_BRACE, Stage.TALK_TO_BROTHER_BRACE_2))
			create("Intro");
		else if (ctrl.inSection(Stage.TALK_TO_BROTHER_BRACE_2, Stage.TALK_TO_BROTHER_BRACE_3))
			create("Prayer");
		else if (ctrl.inSection(Stage.TALK_TO_BROTHER_BRACE_3, Stage.LEAVE_CHURCH_AREA))
			create("Friends");
		else {
			addNext("Recap", new OptionStatement("What would you like to hear again?", "Explain Prayer to me again.", "Explain Friends and Ignores to me again.", "Nothing, I'm ready to move on."));
			getStage("Recap").addNext(getStage("Prayer"));
			getStage("Recap").addNext(getStage("Friends"));
			create();
		}
	}
}
