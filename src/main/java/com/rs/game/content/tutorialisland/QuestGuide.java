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

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.dialogue.statements.NPCStatement;
import com.rs.game.content.tutorialisland.TutorialIslandController.Stage;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.player.Player;

public class QuestGuide extends Conversation {

	public QuestGuide(Player player, NPC npc, TutorialIslandController ctrl) {
		super(player);
		npc.faceEntity(player);
		npc.resetWalkSteps();

		if (ctrl.getStage().ordinal() >= Stage.TALK_TO_QUEST_GUIDE.ordinal() && ctrl.getStage().ordinal() < Stage.TALK_TO_QUEST_GUIDE_2.ordinal())
			addNext(new Dialogue(new NPCStatement(npc.getId(), HeadE.NO_EXPRESSION, "Ah. Welcome, adventurer. I'm here to tell you all about", "quests. Let's start by opening the quest side panel."), () -> ctrl.nextStage(Stage.OPEN_QUEST_TAB)));
		else {
			addNext(new NPCStatement(npc.getId(), HeadE.NO_EXPRESSION, "Now you have the journal open I'll tell you a bit about", "it. At the moment all the quests are shown in red, which", "means you have not started them yet."));
			addNext(new NPCStatement(npc.getId(), HeadE.NO_EXPRESSION, "When you start a quest it will change colour to yellow,", "and to green when you've finished. This is so you can", "easily see what's complete, what's started, and what's left", "to begin."));
			addNext(new NPCStatement(npc.getId(), HeadE.NO_EXPRESSION, "The start of quests are easy to find. Look out for the", "star icons on the minimap, just like the one you should", "see marking my house."));
			addNext(new NPCStatement(npc.getId(), HeadE.NO_EXPRESSION, "The quests themselves can vary greatly from collecting", "beads to hunting down dragons. Generally quests are", "started by talking to a non-player character like me,", "and will involve a series of tasks."));
			addNext(new NPCStatement(npc.getId(), HeadE.NO_EXPRESSION, "There's a lot more I can tell you about questing.", "You have to experience the thrill of it yourself to fully", "understand. You may find some adventure in the caves", "under my house."));
			addNext(new Dialogue().setFunc(() -> ctrl.nextStage(Stage.LEAVE_QUEST_GUIDE_HOUSE)));
		}

		create();
	}
}
