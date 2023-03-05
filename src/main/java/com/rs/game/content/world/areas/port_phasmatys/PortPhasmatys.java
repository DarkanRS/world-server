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
package com.rs.game.content.world.areas.port_phasmatys;

import com.rs.game.content.minigames.ectofuntus.Ectofuntus;
import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.dialogue.Options;
import com.rs.engine.quest.Quest;
import com.rs.lib.game.Tile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ItemClickHandler;
import com.rs.plugin.handlers.NPCClickHandler;
import com.rs.plugin.handlers.ObjectClickHandler;

@PluginEventHandler
public class PortPhasmatys {
	
	public static NPCClickHandler handleBillTeach = new NPCClickHandler(new Object[] { 3157 }, e -> {
		if (!e.getPlayer().isQuestComplete(Quest.CABIN_FEVER, "to travel to Mos' Le Harmless."))
			return;
		e.getPlayer().sendOptionDialogue(ops -> {
			if (e.getPlayer().getRegionId() == 14638)
				ops.add("Travel to Port Phasmatys.", () -> e.getPlayer().setNextTile(Tile.of(3713, 3497, 1)));
			else
				ops.add("Travel to Mos' Le Harmless.", () -> e.getPlayer().setNextTile(Tile.of(3682, 2949, 1)));
			ops.add("Nevermind.");
		});
	});

	public static ItemClickHandler handleEctophial = new ItemClickHandler(new Object[] { 4251 }, e -> {
		if (!e.getPlayer().isQuestComplete(Quest.GHOSTS_AHOY, "to use the ectophial."))
			return;
		Ectofuntus.sendEctophialTeleport(e.getPlayer(), Tile.of(3659, 3523, 0));
	});

	public static NPCClickHandler handleVelorina = new NPCClickHandler(new Object[] { 1683 }, e -> {
		e.getPlayer().startConversation(new Conversation(e.getPlayer()) {
			{
				if (e.getPlayer().getEquipment().getAmuletId() != 552) {
					addNPC(1683, HeadE.CALM_TALK, "Woooowoooooo woooooo!");
					addSimple("You cannot understand a word the ghost is saying.");
				} else
					addOptions("What would you like to say?", new Options() {
						@Override
						public void create() {
							if (!player.containsItems(4251))
								if (player.isQuestComplete(Quest.GHOSTS_AHOY, "to obtain an ectophial."))
									option("Can I have another ectophial?", new Dialogue()
											.addPlayer(HeadE.CONFUSED, "Can I have an ectophial?" + (player.getBool("recTokkulZo") ? " I've lost mine." : ""))
											.addNPC(1683, HeadE.CALM_TALK, "Of course you can, you have helped us more than we could ever have hoped.")
											.addItem(4251, "Velorina gives you a vial of bright green ectoplasm.", () -> {
												if (!player.getInventory().hasFreeSlots()) {
													player.sendMessage("You don't have enough inventory space.");
													return;
												}
												player.getInventory().addItem(4251);
											}));

							option("I thought you were going to pass over to the next world.", new Dialogue()
									.addPlayer(HeadE.CONFUSED, "I thought you were going to pass over to the next world.")
									.addNPC(1683, HeadE.CALM_TALK, "All in good time, " + player.getDisplayName() + ". We stand forever in your debt, and will certainly"
											+ "put in a good word for you when we pass over."));
						}
					});
				create();
			}
		});
	});
	
	public static ObjectClickHandler barTrapdoor = new ObjectClickHandler(new Object[] { 7433, 7434 }, e -> {
		switch(e.getObjectId()) {
		case 7433 -> e.getPlayer().useStairs(828, Tile.of(3681, 3497, 0), 1, 2);
		case 7434 -> e.getPlayer().useStairs(828, Tile.of(3682, 9961, 0), 1, 2);
		}
	});
}
