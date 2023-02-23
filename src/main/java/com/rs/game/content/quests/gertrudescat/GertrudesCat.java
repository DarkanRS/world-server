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
package com.rs.game.content.quests.gertrudescat;

import java.util.ArrayList;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.dialogue.statements.SimpleStatement;
import com.rs.engine.quest.Quest;
import com.rs.engine.quest.QuestHandler;
import com.rs.engine.quest.QuestOutline;
import com.rs.game.model.entity.ForceTalk;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.Constants;
import com.rs.lib.game.Animation;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ItemOnItemHandler;
import com.rs.plugin.handlers.ItemOnNPCHandler;
import com.rs.plugin.handlers.LoginHandler;
import com.rs.plugin.handlers.NPCClickHandler;

@QuestHandler(Quest.GERTRUDES_CAT)
@PluginEventHandler
public class GertrudesCat extends QuestOutline {

	/**
	 * Defines the completed stage of the quest. Number should always be the final stage.
	 */
	@Override
	public int getCompletedStage() {
		return 9;
	}

	/**
	 * Defines the journal lines that get displayed when the player opens their quest book
	 * based on the stage the player is on.
	 */
	@Override
	public ArrayList<String> getJournalLines(Player player, int stage) {
		ArrayList<String> lines = new ArrayList<>();
		switch(stage) {
		case 0:
			lines.add("I can start this quest by speaking to Gertrude.");
			lines.add("She can be found in a house south of the road");
			lines.add("leading west out of Varrock.");
			break;
		case 1:
			lines.add("I need to speak to Gertrude's Sons, Shilop and");
			lines.add("Wilough, in Varrock Marketplace.");
			break;
		case 2:
			lines.add("I need to go to Shilop and Wilough's secret");
			lines.add("hideout in the Lumber Yard and find Fluffs, then");
			lines.add("return her to Gertrude. The Lumber Yard is");
			lines.add("very close to the north-east wall outside Varrock.");
			lines.add("I can enter it by squeezing through a broken fence.");
			break;
		case 3:
			lines.add("I had a poke around the Lumber Yard by squeezing");
			lines.add("through a broken fence, and found Fluffs up a");
			lines.add("ladder. I now need to return Fluffs to Gertrude.");
			lines.add("I think Fluffs may be hungry or thirsty but I");
			lines.add("am not sure what she wants; perhaps Gertrude can help?");
			break;
		case 4:
			lines.add("I found Fluffs and fed her some milk but she still won't come back.");
			break;
		case 5:
			lines.add("Fluffs seems to be hungry.");
			break;
		case 6:
			lines.add("I fed Fluffs a doogle sardine but she still won't come back.");
			break;
		case 7:
			if (player.getInventory().containsItem(13236, 1))
				lines.add("I found some kittens! Fluffs might want to see them.");
			else
				lines.add("I can hear kittens mewing in the area. I should explore more.");
			break;
		case 8:
			lines.add("I returned the kittens to Fluffs who then ran away.");
			lines.add("I think I should see Gertrude in her house, west of");
			lines.add("Varrock, and explain what has happened.");
			break;
		case 9:
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

	/**
	 * What to do when the quest gets completed. "addXpQuest" must be used here to avoid
	 * bonus XP or other various XP multipliers from processing.
	 */
	@Override
	public void complete(Player player) {
		player.getSkills().addXpQuest(Constants.COOKING, 1525);
		player.getInventory().addItem(1555, 1);
		player.getInventory().addItem(1897, 1);
		player.getInventory().addItem(2003, 1);
		getQuest().sendQuestCompleteInterface(player, 1555, "A kitten!", "1525 Cooking XP", "The ability to raise cats");
	}

	/**
	 * Updates whether Fluffs is visible to the player or not. In this case:
	 * var 180 = 2 = Fluffs is only visible at the Lumber Yard
	 * var 180 = 5 = Fluffs is only visible in Gertrude's house
	 *
	 * NPC ID: 759 - transforms into with v180:
	 * [0: INVISIBLE],
	 * [1: INVISIBLE],
	 * [2: 7742 (Fluffs)],
	 * [3: 7742 (Fluffs)],
	 * [4: 7742 (Fluffs)],
	 * [5: INVISIBLE],
	 *
	 * NPC ID: 7744 - transforms into with v180:
	 * [0: INVISIBLE],
	 * [1: INVISIBLE],
	 * [2: INVISIBLE],
	 * [3: INVISIBLE],
	 * [4: INVISIBLE],
	 * [5: 7742 (Fluffs)],
	 * [6: 7742 (Fluffs)],
	 * [7: INVISIBLE],
	 *
	 * NPC id 759 is spawned at the Lumber Yard upstairs.
	 * NPC id 7744 is spawned in Gertrude's home.
	 *
	 * @param Player to update Fluffs for.
	 */
	public static void updateFluffs(Player player) {
		if (player.getQuestManager().getStage(Quest.GERTRUDES_CAT) >= 2 && player.getQuestManager().getStage(Quest.GERTRUDES_CAT) < 8)
			player.getVars().setVar(180, 2);
		else if (player.getQuestManager().getStage(Quest.GERTRUDES_CAT) >= 8 && !player.isQuestComplete(Quest.GERTRUDES_CAT))
			player.getVars().setVar(180, 5);
	}

	/**
	 * Handles Fluff's Pick-up option. Whether or not Fluffs has this option available and is visible to the player
	 * depending on the vars set above is automatically handled and verified by the server so cheating cannot occur.
	 *
	 * @param The event to handle.
	 */
	public static NPCClickHandler handleFluffsOptions = new NPCClickHandler(new Object[] { 759 }, e -> {
		if (e.getOption().equals("Pick-up"))
			if (e.getPlayer().getQuestManager().getStage(Quest.GERTRUDES_CAT) == 2 || e.getPlayer().getQuestManager().getStage(Quest.GERTRUDES_CAT) == 4) {
				e.getNPC().setNextAnimation(new Animation(9160));
				e.getNPC().setNextForceTalk(new ForceTalk("Hiss!"));
				e.getPlayer().setNextAnimation(new Animation(827));
				e.getPlayer().setNextForceTalk(new ForceTalk("Ouch!"));
				e.getPlayer().startConversation(new Conversation(new SimpleStatement("Fluffs hisses but clearly wants something - " + (e.getPlayer().getQuestManager().getStage(Quest.GERTRUDES_CAT) == 2 ? "maybe she is thirsty?" : "maybe she is hungry too?"))));
				e.getPlayer().getQuestManager().setStage(Quest.GERTRUDES_CAT, e.getPlayer().getQuestManager().getStage(Quest.GERTRUDES_CAT)+1);
				updateFluffs(e.getPlayer());
			} else if (e.getPlayer().getQuestManager().getStage(Quest.GERTRUDES_CAT) == 6) {
				e.getNPC().setNextAnimation(new Animation(9160));
				e.getNPC().setNextForceTalk(new ForceTalk("Hiss!"));
				e.getPlayer().setNextAnimation(new Animation(827));
				e.getPlayer().setNextForceTalk(new ForceTalk("Ouch!"));
				e.getPlayer().startConversation(new Conversation(new SimpleStatement("Fluffs seems afraid to leave. In the Lumber Yard below you can hear kittens mewing.")));
				e.getPlayer().getQuestManager().setStage(Quest.GERTRUDES_CAT, e.getPlayer().getQuestManager().getStage(Quest.GERTRUDES_CAT)+1);
				updateFluffs(e.getPlayer());
			}
	});

	/**
	 * Handles when the player clicks on the shaking crates in the Lumber Yard.
	 * @param The event to handle.
	 */
	public static NPCClickHandler handleShakingCrate = new NPCClickHandler(new Object[] { 7740 }, e -> {
		if (e.getPlayer().getQuestManager().getStage(Quest.GERTRUDES_CAT) == 7) {
			if (e.getPlayer().containsItem(13236))
				return;
			e.getPlayer().startConversation(new Conversation(new Dialogue()
					.addItem(13236, "You find three little kittens! You carefully place them in your backpack. This explains why Fluffs is so agitated.", () -> {
						e.getPlayer().getInventory().addItem(13236, 1);
					})));
		}
	});

	/**
	 * Handles starting Gertrude's dialogue.
	 * @param The event to handle.
	 */
	public static NPCClickHandler handleGertrude = new NPCClickHandler(new Object[] { 780 }, e -> e.getPlayer().startConversation(new GertrudeD(e.getPlayer())));

	/**
	 * Handles starting Shilop and Wilough's dialogue.
	 * @param The event to handle.
	 */
	public static NPCClickHandler handleShilopWilough = new NPCClickHandler(new Object[] { 781, 783 }, e -> e.getPlayer().startConversation(new ShilopWiloughD(e.getPlayer(), e.getNPC())));

	/**
	 * When the player logs in, Fluffs is re-updated to make sure she is still visible.
	 */
	public static LoginHandler onLogin = new LoginHandler(e -> updateFluffs(e.getPlayer()));

	/**
	 * Handles the creation of doogle sardines.
	 * @param The event to handle.
	 */
	public static ItemOnItemHandler handleDoogleSardineCreation = new ItemOnItemHandler(327, 1573, e -> {
		e.getPlayer().startConversation(new Conversation(new Dialogue().addSimple("You rub the doogle leaves over the sardine.", () -> {
			e.getPlayer().getInventory().deleteItem(327, 1);
			e.getPlayer().getInventory().deleteItem(1573, 1);
			e.getPlayer().getInventory().addItem(1552, 1);
		})));
	});

	/**
	 * Handles the various items you must use on Fluffs at the Lumber Yard to get her to trust you.
	 * @param The event to handle.
	 */
	public static ItemOnNPCHandler handleItemOnFluffs = new ItemOnNPCHandler(759, e -> {
		if (e.getItem().getId() == 1927) {
			if (e.getPlayer().getQuestManager().getStage(Quest.GERTRUDES_CAT) == 3) {
				e.getPlayer().setNextAnimation(new Animation(827));
				e.getPlayer().startConversation(new Conversation(e.getPlayer(), new Dialogue()
						.addNPC(e.getNPC().getId(), HeadE.CAT_CALM_TALK, "Mew!")
						.addPlayer(HeadE.CHEERFUL, "Progress at least.")
						.addSimple("Fluffs laps up the milk greedily. Then she mews at you again.", () -> {
							e.getPlayer().getInventory().deleteItem(1927, 1);
							e.getPlayer().getInventory().addItem(1925, 1);
							e.getPlayer().getQuestManager().setStage(Quest.GERTRUDES_CAT, 4);
							updateFluffs(e.getPlayer());
						})));
			}
		} else if (e.getItem().getId() == 1552) {
			if (e.getPlayer().getQuestManager().getStage(Quest.GERTRUDES_CAT) == 5) {
				e.getPlayer().setNextAnimation(new Animation(827));
				e.getPlayer().startConversation(new Conversation(e.getPlayer(), new Dialogue()
						.addNPC(e.getNPC().getId(), HeadE.CAT_CALM_TALK, "Mew!")
						.addPlayer(HeadE.CHEERFUL, "Progress at least.")
						.addSimple("Fluffs devours the doogle sardine greedily. Then she mews at you again.", () -> {
							e.getPlayer().getInventory().deleteItem(1552, 1);
							e.getPlayer().getQuestManager().setStage(Quest.GERTRUDES_CAT, 6);
							updateFluffs(e.getPlayer());
						})));
			}
		} else if (e.getItem().getId() == 13236)
			if (e.getPlayer().getQuestManager().getStage(Quest.GERTRUDES_CAT) == 7) {
				e.getPlayer().setNextAnimation(new Animation(827));
				// TODO cutscene
				// https://i.imgur.com/VEkOx0N.jpg
				// https://i.imgur.com/G6pFoqR.jpg
				// https://i.imgur.com/3f7cvd2.jpg
				e.getPlayer().getInventory().deleteItem(13236, 1);
				e.getPlayer().getQuestManager().setStage(Quest.GERTRUDES_CAT, 8);
				updateFluffs(e.getPlayer());
			}
	});
}
