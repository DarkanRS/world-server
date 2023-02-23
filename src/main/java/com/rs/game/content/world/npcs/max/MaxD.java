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
package com.rs.game.content.world.npcs.max;

import com.rs.game.World;
import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.game.model.entity.player.Player;

public class MaxD extends Conversation {

	public MaxD(Player player, Max max) {
		super(player);
		
		if (max.hasWalkSteps()) {
			addNPC(max, HeadE.CHEERFUL, "Can't stop right now! Catch me up at my next stop?");
			player.getTempAttribs().setB("talkedMaxWalking", true);
			create();
			return;
		}
		if (player.getTempAttribs().getB("talkedMaxWalking")) {
			addNPC(max, HeadE.CHEERFUL, "Sorry about that. XP waste if I stopped back there, eh?");
			player.getTempAttribs().removeB("talkedMaxWalking");
		}
		addNPC(max, HeadE.CHEERFUL, "How can I help you?");
		addOptions("startOps", "What would you like to say?", ops -> {
			ops.add("Who are you?", new Dialogue()
					.addPlayer(HeadE.CONFUSED, "Who are you?")
					.addNPC(max, HeadE.CHEERFUL_EXPOSITION, "Oh, sorry - I'm "+max.getCurrName()+". Some say I'm a bit obsessed with skilling.")
					.addPlayer(HeadE.CHEERFUL, "And I'm " + player.getDisplayName() + ", nice to meet you.")
					.addNPC(max, HeadE.CONFUSED, "Indeed, so can I help you with anything?")
					.addGotoStage("startOps", this));
			ops.add("Nice cape you have there...", new Conversation(player) {
				{
					addPlayer(HeadE.AMAZED_MILD, "Nice cape you have there...");
					addNPC(max, HeadE.CHEERFUL_EXPOSITION, "This? Thanks! It's a symbol that I've trained all my skills to level 99.");
					if (player.getSkills().isMaxed(false)) {
						addPlayer(HeadE.CHEERFUL_EXPOSITION, "I've maxed all my skills!");
						addNPC(max, HeadE.AMAZED_MILD, "Indeed so. Would you like to show that fact off? 2,673,000 coins - 99,000 for each skill!");
						addOptions(ops -> {
							ops.add("I'll take one!", () -> {
								int value = 2475000;
								if (player.getInventory().getFreeSlots() < 2) {
									player.npcDialogue(max, HeadE.SHAKING_HEAD, "You don't have enough inventory space for that.");
								} else {
									if (player.getInventory().hasCoins(value)) {
										player.getInventory().removeCoins(value);
										player.getInventory().addItemDrop(20768, 1);
										player.getInventory().addItemDrop(20767, 1);
										player.npcDialogue(max, HeadE.SHAKING_HEAD, "Thanks. Enjoy!");
										if (player.getSkills().isMaxed(false) && !player.isMaxed) {
											player.isMaxed = true;
											World.sendWorldMessage("<col=ff8c38><img=7>News: " + player.getDisplayName() + " has just been awarded the Max cape!" + "</col> ", false);
										}
									} else
										player.npcDialogue(max, HeadE.SHAKING_HEAD, "You don't have enough money for that!");
								}
							});
							ops.add("No, thanks.", new Dialogue().addPlayer(HeadE.CALM_TALK, "No, thanks."));
						});
					} else {
						addPlayer(HeadE.AMAZED_MILD, "Wow, that's quite impressive.");
						addNPC(max, HeadE.CHEERFUL_EXPOSITION, "Thanks. I have faith in you " + player.getDisplayName() + " - one day you'll be here and I'll sell you one myself.");
						addPlayer(HeadE.SKEPTICAL, "We'll see about that, thanks.");
						addNPC(max, HeadE.CHEERFUL, "Farewell and good luck.");
					}
				}
			}.getStart());
			ops.add("Nothing, thanks.", new Dialogue()
					.addPlayer(HeadE.CHEERFUL, "Nothing, thanks.")
					.addNPC(max, HeadE.CHEERFUL, "Farewell, and good luck with your skills."));
		});
	}
}