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
package com.rs.game.content.world.regions;

import com.rs.game.content.dialogue.Conversation;
import com.rs.game.content.dialogue.Dialogue;
import com.rs.game.content.dialogue.HeadE;
import com.rs.game.content.dialogue.Options;
import com.rs.game.content.minigames.ectofuntus.Ectofuntus;
import com.rs.game.content.quests.Quest;
import com.rs.lib.game.WorldTile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.ItemClickEvent;
import com.rs.plugin.events.NPCClickEvent;
import com.rs.plugin.handlers.ItemClickHandler;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler
public class PortPhasmatys {

	public static ItemClickHandler handleEctophial = new ItemClickHandler(4251) {
		@Override
		public void handle(ItemClickEvent e) {
			if (!Quest.GHOSTS_AHOY.meetsRequirements(e.getPlayer(), "to use the ectophial."))
				return;
			Ectofuntus.sendEctophialTeleport(e.getPlayer(), new WorldTile(3659, 3523, 0));
		}
	};

	public static NPCClickHandler handleVelorina = new NPCClickHandler(new Object[] { 1683 }) {
		@Override
		public void handle(NPCClickEvent e) {
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
									if (Quest.GHOSTS_AHOY.meetsRequirements(player, "to obtain an ectophial."))
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
		}
	};
}
