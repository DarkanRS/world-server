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
package com.rs.game.content.quests;

import java.util.ArrayList;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.quest.Quest;
import com.rs.engine.quest.QuestHandler;
import com.rs.engine.quest.QuestOutline;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.Constants;
import com.rs.lib.game.Item;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;

@QuestHandler(Quest.COOKS_ASSISTANT)
@PluginEventHandler
public class CooksAssistant extends QuestOutline {

	@Override
	public int getCompletedStage() {
		return 2;
	}

	@Override
	public ArrayList<String> getJournalLines(Player player, int stage) {
		ArrayList<String> lines = new ArrayList<>();
		switch(stage) {
		case 0:
			lines.add("I can start this quest by speaking to the cook");
			lines.add("in the Lumbridge castle kitchen.");
			break;
		case 1:
			lines.add("The cook is having problems baking a cake for the Duke's");
			lines.add("birthday party. He needs the following 3 items:");
			lines.add((player.getInventory().containsItem(1944, 1) ? "<str>":"")+"An egg");
			lines.add((player.getInventory().containsItem(1927, 1) ? "<str>":"")+"A bucket of milk");
			lines.add((player.getInventory().containsItem(1933, 1) ? "<str>":"")+"A pot of flour");
			break;
		case 2:
			lines.add("");
			lines.add("");
			lines.add("QUEST COMPLETE!");
			break;
		default:
			lines.add("Invalid quest stage. Report this to an administrator.");
			break;
		}
		return lines;
	}

	@Override
	public void complete(Player player) {
		player.getSkills().addXpQuest(Constants.COOKING, 300);
		getQuest().sendQuestCompleteInterface(player, 1891, "300 Cooking XP");
	}

	static class CookD extends Conversation {

		public CookD(Player player, int npcId) {
			super(player);

			if (player.getQuestManager().getStage(Quest.COOKS_ASSISTANT) == 0) {
				addNPC(npcId, HeadE.SAD_MILD, "What am I to do?");
				addPlayer(HeadE.CONFUSED, "What's wrong?");
				addNPC(npcId, HeadE.UPSET, "Oh dear, oh dear, I'm in a terrible, terrible mess!");
				addNPC(npcId, HeadE.UPSET, "It's the duke's birthday today, and I should be making him a big birthday cake using special ingredients... but I've forgotten to get the ingredients.");
				addNPC(npcId, HeadE.SAD_MILD, "I'll never get them in time now. He'll sack me!<br>Whatever will I do?");
				addNPC(npcId, HeadE.UPSET, "I have four children and a goat to look after. Would you help me? Please?");
				addOption("What would you like to say?", "Of course. What is it you are looking for?", "Sorry, I can't help right now.");
				addPlayer(HeadE.HAPPY_TALKING, "Of course. What is it you are looking for?");
				addNPC(npcId, HeadE.HAPPY_TALKING, "Oh, thank you, thank you. I must tell you that this is no ordinary cake, though - only the best ingredients will do!");
				addNPC(npcId, HeadE.HAPPY_TALKING, "I need an egg, some milk, and a pot of flour.");
				addPlayer(HeadE.CONFUSED, "Where can I find those, then?");
				addNPC(npcId, HeadE.CONFUSED, "That's the problem: I don't exactly know. I usually send my assistant to get them for me but he quit.");
				addPlayer(HeadE.HAPPY_TALKING, "Well don't worry then, I'll be back with the ingredients soon.", () -> {
					player.getQuestManager().setStage(Quest.COOKS_ASSISTANT, 1, true);
				});
			} else if (player.getQuestManager().getStage(Quest.COOKS_ASSISTANT) == 1) {
				addNPC(npcId, HeadE.CONFUSED, "How are you getting with finding the ingredients?");
				if (!player.getInventory().containsItems(new Item(1933, 1), new Item(1944, 1), new Item(1927, 1)))
					addPlayer(HeadE.WORRIED, "I haven't quite gotten them all yet. I'll be back when I have the rest of them.");
				else {
					addPlayer(HeadE.HAPPY_TALKING, "I have all of the items right here!");
					addNPC(npcId, HeadE.HAPPY_TALKING, "You've brought me everything I need! I am saved! Thank you!");
					addPlayer(HeadE.CONFUSED, "So, do I get to go to the Duke's party?");
					addNPC(npcId, HeadE.CALM_TALK, "I'm afraid not. Only the big cheeses get to dine with the Duke.");
					addPlayer(HeadE.CALM_TALK, "Well, maybe one day, I'll be important enough to sit at the Duke's table.");
					addNPC(npcId, HeadE.CALM_TALK, "Maybe, but I won't be holding my breath.", () -> {
						player.getInventory().deleteItem(new Item(1933, 1));
						player.getInventory().deleteItem(new Item(1944, 1));
						player.getInventory().deleteItem(new Item(1927, 1));
						player.getQuestManager().completeQuest(Quest.COOKS_ASSISTANT);
					});
				}
			} else
				addNPC(npcId, HeadE.HAPPY_TALKING, "Thank you for the help! Feel free to use my range!");

			create();
		}

	}

	public static NPCClickHandler talkCook = new NPCClickHandler(new Object[] { 278 }, e -> e.getPlayer().startConversation(new CookD(e.getPlayer(), e.getNPC().getId())));
}
