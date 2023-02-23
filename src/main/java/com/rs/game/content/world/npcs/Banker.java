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
package com.rs.game.content.world.npcs;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.dialogue.Options;
import com.rs.game.ge.GE;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.InputIntegerEvent;
import com.rs.plugin.handlers.NPCClickHandler;
import com.rs.plugin.handlers.NPCInteractionDistanceHandler;
import com.rs.plugin.handlers.ObjectClickHandler;

@PluginEventHandler
public class Banker extends Conversation {

	public Banker(Player player, NPC npc) {
		super(player);

		addNPC(npc.getId(), HeadE.CHEERFUL_EXPOSITION, "Good day. How may I help you?");
		addOptions(new Options() {
			@Override
			public void create() {
				option("I'd like to access my bank account, please.", () -> player.getBank().open());
				option("I'd like to check my PIN settings.", () -> player.getBank().openPinSettings());
				option("I'd like to see my collection box.", () -> GE.openCollection(player));
				option("What is this place?", new Dialogue()
						.addNPC(npc.getId(), HeadE.CHEERFUL_EXPOSITION, "This is a branch of the Bank of Gielinor. We have branches in many towns.")
						.addOptions(new Options() {
							@Override
							public void create() {
								option("And what do you do?", new Dialogue()
										.addNPC(npc.getId(), HeadE.CHEERFUL_EXPOSITION, "We will look after your items and money for you. Leave your valuables with us if you want to keep them safe."));
								option("Didn't you used to be called the Bank of Varrock?", new Dialogue()
										.addNPC(npc.getId(), HeadE.CALM_TALK, "Yes we did, but people kept on coming into our branches outside of Varrock and telling us that our signs were wrong. They acted as if we didn't know what town we were in or something."));
							}
						}));
				option("I'd like to change my theshold for valuable loot notifications.", new Dialogue()
						.addPlayer(HeadE.HAPPY_TALKING, "I'd like to change my theshold for valuable loot notifications.")
						.addNPC(npc.getId(), HeadE.HAPPY_TALKING, "Okay, your current threshold is " + player.getI("lootbeamThreshold", 90000) + " GP. What would you like to set it to?")
						.addNext(() -> { 
							player.sendInputInteger("What would you like to set it to?", new InputIntegerEvent() {
								@Override
								public void run(int amount) {
									if (amount < 0)
										return;
									player.save("lootbeamThreshold", amount);
								}
							});
						}));
			}
		});

		create();
	}

	public static NPCInteractionDistanceHandler bankerDistance = new NPCInteractionDistanceHandler(new Object[] { "Banker" }, (p, n) -> 1);

	public static NPCClickHandler bankerHandler = new NPCClickHandler(new Object[] { "Banker", 14707, 2619, 13455, 15194 }, e -> {
		switch(e.getOption()) {
		case "Bank":
			e.getPlayer().getBank().open();
			break;
		case "Collect":
			GE.openCollection(e.getPlayer());
			break;
		case "Talk-to":
			e.getPlayer().startConversation(new Banker(e.getPlayer(), e.getNPC()));
			break;
		}
	});

	public static ObjectClickHandler bankObjHandler = new ObjectClickHandler(new Object[] { "Bank booth", "Bank", "Bank chest", "Bank table", "Counter", "Shantay chest", "Darkmeyer Treasury" }, e -> {
		switch(e.getOption()) {
		case "Bank":
			e.getPlayer().getBank().open();
			break;
		case "Collect":
			GE.openCollection(e.getPlayer());
			break;
		case "Use":
			if (e.getObject().getDefinitions().getName(e.getPlayer().getVars()).equals("Bank chest"))
				e.getPlayer().getBank().open();
			break;
		case "Open":
			if (e.getObject().getDefinitions().getName(e.getPlayer().getVars()).equals("Shantay chest"))
				e.getPlayer().getBank().open();
			break;
		default:
			e.getPlayer().sendMessage("Unhandled bank object option: " + e.getOption());
			break;
		}
	});

	public static ObjectClickHandler depositBoxHandler = new ObjectClickHandler(new Object[] { "Bank deposit box", "Deposit box", "Deposit Box", "Deposit chest", "Pulley lift" }, e -> {
		switch(e.getOption()) {
		case "Deposit":
		case "Use":
			e.getPlayer().getBank().openDepositBox();
			break;
		default:
			e.getPlayer().sendMessage("Unhandled deposit box object option: " + e.getOption());
			break;
		}
	});
}
