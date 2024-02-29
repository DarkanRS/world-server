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
package com.rs.game.content.quests

import com.rs.engine.dialogue.HeadE
import com.rs.engine.dialogue.startConversation
import com.rs.engine.quest.Quest
import com.rs.engine.quest.QuestHandler
import com.rs.engine.quest.QuestOutline
import com.rs.game.model.entity.player.Player
import com.rs.lib.Constants
import com.rs.lib.game.Item
import com.rs.lib.util.Utils.strikeThroughIf
import com.rs.plugin.annotations.PluginEventHandler
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.kts.onNpcClick

@QuestHandler(
		quest = Quest.COOKS_ASSISTANT,
		startText = "Speak to the cook in the kitchen of Lumbridge Castle.",
		itemsText = "Empty pot (can be found in flour mill). Empty bucket (can be found near dairy cow).",
		combatText = "None.",
		rewardsText = "300 Cooking XP<br>500 coins<br>20 sardines<br>Access to the cook's range",
		completedStage = 2
)
@PluginEventHandler
class CooksAssistant : QuestOutline() {
	override fun getJournalLines(player: Player, stage: Int) = when (stage) {
		0 -> listOf("I can start this quest by speaking to the cook in the Lumbridge castle kitchen.")
		1 -> listOf("The cook is having problems baking a cake for the Duke's birthday party. He needs the following 3 items:",
			strikeThroughIf("An egg") { player.inventory.containsItem(1944, 1) },
			strikeThroughIf("A bucket of milk") { player.inventory.containsItem(1927, 1) },
			strikeThroughIf("A pot of flour") { player.inventory.containsItem(1933, 1) }
		)
		2 -> listOf("QUEST COMPLETE!")
		else -> listOf("Invalid quest stage. Report this to an administrator.")
	}

	override fun complete(player: Player) {
		player.skills.addXpQuest(Constants.COOKING, 300.0)
		player.inventory.addCoins(500)
		player.inventory.addItemDrop(326, 20)
		sendQuestCompleteInterface(player, 1891)
	}
}

@ServerStartupEvent
fun mapCooksAssistant() {
	onNpcClick(278) { (player, n) ->
		when(player.getQuestStage(Quest.COOKS_ASSISTANT)) {
			0 -> player.startConversation {
				npc(n, HeadE.SAD_MILD, "What am I to do?")
				player(HeadE.CONFUSED, "What's wrong?")
				npc(n, HeadE.UPSET, "Oh dear, oh dear, I'm in a terrible, terrible mess!")
				npc(n, HeadE.UPSET, "It's the duke's birthday today, and I should be making him a big birthday cake using special ingredients... but I've forgotten to get the ingredients.")
				npc(n, HeadE.SAD_MILD, "I'll never get them in time now. He'll sack me!<br>Whatever will I do?")
				npc(n, HeadE.UPSET, "I have four children and a goat to look after. Would you help me? Please?")
				options {
					op("Of course. What is it you are looking for?") {
						player(HeadE.HAPPY_TALKING, "Of course. What is it you are looking for?")
						npc(n, HeadE.HAPPY_TALKING, "Oh, thank you, thank you. I must tell you that this is no ordinary cake, though - only the best ingredients will do!")
						npc(n, HeadE.HAPPY_TALKING, "I need an egg, some milk, and a pot of flour.")
						player(HeadE.CONFUSED, "Where can I find those, then?")
						npc(n, HeadE.CONFUSED, "That's the problem: I don't exactly know. I usually send my assistant to get them for me but he quit.")
						player(HeadE.HAPPY_TALKING, "Well don't worry then, I'll be back with the ingredients soon.") {
							player.questManager.setStage(Quest.COOKS_ASSISTANT, 1)
						}
					}
					op("Sorry, I can't help right now.")
				}
			}
			1 -> player.startConversation {
				npc(n, HeadE.CONFUSED, "How are you getting with finding the ingredients?")
				if (!player.inventory.containsItems(Item(1933, 1), Item(1944, 1), Item(1927, 1))) {
					player(HeadE.WORRIED, "I haven't quite gotten them all yet. I'll be back when I have the rest of them.")
					return@startConversation
				}
				player(HeadE.HAPPY_TALKING, "I have all of the items right here!")
				npc(n, HeadE.HAPPY_TALKING, "You've brought me everything I need! I am saved! Thank you!")
				player(HeadE.CONFUSED, "So, do I get to go to the Duke's party?")
				npc(n, HeadE.CALM_TALK, "I'm afraid not. Only the big cheeses get to dine with the Duke.")
				player(HeadE.CALM_TALK, "Well, maybe one day, I'll be important enough to sit at the Duke's table.")
				npc(n, HeadE.CALM_TALK, "Maybe, but I won't be holding my breath.") {
					player.inventory.removeItems(Item(1933, 1), Item(1944, 1), Item(1927, 1))
					player.questManager.completeQuest(Quest.COOKS_ASSISTANT)
				}
			}
			else -> player.npcDialogue(n, HeadE.HAPPY_TALKING, "Thank you for the help! Feel free to use my range!")
		}
	}
}