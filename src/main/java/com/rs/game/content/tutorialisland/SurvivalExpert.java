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
import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.dialogue.statements.*;
import com.rs.game.content.tutorialisland.TutorialIslandController.Stage;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.player.Player;

public class SurvivalExpert extends Conversation {

	public SurvivalExpert(Player player, NPC npc, TutorialIslandController ctrl) {
		super(player);
		npc.faceEntity(player);
		npc.resetWalkSteps();

		if (ctrl.inSection(Stage.TALK_TO_SURVIVAL_EXPERT, Stage.TALK_TO_SURVIVAL_EXPERT_2))
			addNext(new NPCStatement(npc.getId(), HeadE.NO_EXPRESSION, "Hello there, newcomer. My name is Brynna. My job is", "to teach you a few survival tips and tricks. First off", "we're going to start with the most basic survival skill of", "all: making a fire."));
		else if (ctrl.inSection(Stage.TALK_TO_SURVIVAL_EXPERT_2, Stage.LEAVE_SURVIVAL_EXPERT))
			addNext(new NPCStatement(npc.getId(), HeadE.NO_EXPRESSION, "Well done! Next we need to get some food in our", "bellies. We'll need something to cook. There are shrimp", "in the pond there. So let's catch and cook some."));
		else
			addNext(new NPCStatement(npc.getId(), HeadE.NO_EXPRESSION, "Hello again."));
		if (player.getInventory().missingItems(590, 1351) && ctrl.pastStage(Stage.TALK_TO_SURVIVAL_EXPERT))
			addNext(new Dialogue(new LegacyItemStatement(590, 1351, "", "The Survival Guide gives you a <col=0000FF>tinderbox</col> and a <col=0000FF>bronze</col>", "<col=0000FF>axe</col>!"), () -> {
				player.getInventory().addItem(1351, 1);
				player.getInventory().addItem(590, 1);
				ctrl.nextStage(Stage.OPEN_INVENTORY);
			}));
		if (player.getInventory().missingItems(303) && ctrl.pastStage(Stage.TALK_TO_SURVIVAL_EXPERT_2))
			addNext(new Dialogue(new ItemStatement(303, "The Survival Guide gives you a <col=0000FF>net</col>!"), () -> {
				player.getInventory().addItem(303, 1);
				ctrl.nextStage(Stage.CATCH_SHRIMP);
			}));
		if (ctrl.pastStage(Stage.LEAVE_SURVIVAL_EXPERT)) {
			addNext("Recap", new OptionStatement("What would you like to hear more about?", "Woodcutting.", "Firemaking.", "Fishing.", "Cooking.", "My stats."));

			getStage("Recap").addNext(new PlayerStatement(HeadE.NO_EXPRESSION, "Tell me about Woodcutting again."))
			.addNext(new NPCStatement(npc.getId(), HeadE.CHEERFUL, "Woodcutting, eh? Don't worry, newcomer, it's really", "very easy. Simply equip your axe and click on a", "nearby tree to chop away."))
			.addNext(new NPCStatement(npc.getId(), HeadE.CHEERFUL, "As you explore the mainland you will discover many", "different kinds of trees that will require different levels", "of Woodcutting ability to chop down."))
			.addNext(new NPCStatement(npc.getId(), HeadE.CHEERFUL, "Logs are not only useful for making fires. Many", "archers use the skill known as Fletching to craft their", "own bows and arrows from trees."))
			.addNext(getStage("Recap"));

			getStage("Recap").addNext(new PlayerStatement(HeadE.NO_EXPRESSION, "Tell me about Firemaking again."))
			.addNext(new NPCStatement(npc.getId(), HeadE.CHEERFUL, "Certainly, newcomer. When you have logs simply use", "your tinderbox on them. If successful, you will start a", "fire."))
			.addNext(new NPCStatement(npc.getId(), HeadE.CHEERFUL, "You can also set fire to logs you find lying on the floor", "already, and some other things can also be set alight..."))
			.addNext(new NPCStatement(npc.getId(), HeadE.CHEERFUL, "A tinderbox is always a useful item to keep around!", "Was there anything else you wished to hear again?"))
			.addNext(getStage("Recap"));

			getStage("Recap").addNext(new PlayerStatement(HeadE.NO_EXPRESSION, "Tell me about Fishing again."))
			.addNext(new NPCStatement(npc.getId(), HeadE.CHEERFUL, "Ah, yes. Fishing!", "Fishing is undoubtedly one of the", "more popular hobbies here in "+Settings.getConfig().getServerName()+"!"))
			.addNext(new NPCStatement(npc.getId(), HeadE.CHEERFUL, "Whenever you see sparkling waters, you can be sure", "there's probably some good fishing to be had there!"))
			.addNext(new NPCStatement(npc.getId(), HeadE.CHEERFUL, "Not only are fish absolutely delicious when cooked, there", "are always fighters willing to buy a well cooked fish", "when they're low on health."))
			.addNext(new NPCStatement(npc.getId(), HeadE.CHEERFUL, "I would recommend everybody has a go at Fishing at least once in their lives! Was there anything else you", "wished to hear again?"))
			.addNext(getStage("Recap"));

			getStage("Recap").addNext(new PlayerStatement(HeadE.NO_EXPRESSION, "Tell me about Cooking again."))
			.addNext(new NPCStatement(npc.getId(), HeadE.CHEERFUL, "Yes, the most basic of survival techniques. Most simple", "meals can be cooked on a fire by right-clicking on the", "food, selecting use, then left clicking on the fire."))
			.addNext(new NPCStatement(npc.getId(), HeadE.CHEERFUL, "Eating food will restore a little health. The harder", "something is to cook, the more it will heal you.", "Somewhere around here is a chef who will tell you more", "about food and cooking it."))
			.addNext(getStage("Recap"));

			getStage("Recap").addNext(new PlayerStatement(HeadE.NO_EXPRESSION, "Tell me about my stats again."))
			.addNext(new NPCStatement(npc.getId(), HeadE.CHEERFUL, "To look at your stats, click on the bar graph icon found", "near your backpack icon. In this side panel you can", "check your skill level."))
			.addNext(new NPCStatement(npc.getId(), HeadE.CHEERFUL, "As you move your mouse over any of the icons in this", "panel, the small yellow popup box will show you the", "exact amount of experience you have and how much is", "needed to get to the next level."))
			.addNext(getStage("Recap"));
		}
		create();
	}
}
