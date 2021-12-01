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
//  Copyright © 2021 Trenton Kress
//  This file is part of project: Darkan
//
package com.rs.game.player.content.skills.construction;

import com.rs.game.player.Player;
import com.rs.game.player.content.Skillcapes;
import com.rs.game.player.content.dialogue.Conversation;
import com.rs.game.player.dialogues.Dialogue;
import com.rs.lib.Constants;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.DialogueOptionEvent;
import com.rs.plugin.events.NPCClickEvent;
import com.rs.plugin.handlers.NPCClickHandler;
import com.rs.utils.shop.ShopsHandler;

@PluginEventHandler
public class EstateAgentDialogue extends Dialogue {
	
	public static NPCClickHandler handleEstateAgent = new NPCClickHandler("Estate agent") {
		@Override
		public void handle(NPCClickEvent e) {
			e.getPlayer().getDialogueManager().execute(new EstateAgentDialogue());
		}
	};

	@Override
	public void start() {
		stage = 0;
		sendOptionsDialogue(player, "What would you like to do?", "Open shop", "Ask about changing house styling", "What's that cape you are wearing?");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		if (stage == 0) {
			if (componentId == OPTION_1) {
				ShopsHandler.openShop(player, "construction_goods");
				end();
			} else if (componentId == OPTION_2) {
				stage = 1;
				sendOptionsDialogue(player, "Which kind of house style would you like?", 
						"(Level 1) Basic wood - 2,500 coins", 
						"(Level 10) Basic stone - 10,000 coins",
						"(Level 20) Whitewashed stone - 15,000 coins",
						"(Level 30) Fremennik-style wood - 25,000 coins",
						"More...");
			} else if (componentId == OPTION_3) {
				end();
				player.startConversation(new Conversation(player, Skillcapes.Construction.getOffer99CapeDialogue(player, 6715)));
			}
		} else if (stage == 1) {
			if (componentId == OPTION_1) {
				promptHouseChange("basic wood", 0, 1, 2500);
			} else if (componentId == OPTION_2) {
				promptHouseChange("basic stone", 1, 10, 10000);
			} else if (componentId == OPTION_3) {
				promptHouseChange("whitewashed stone", 2, 20, 15000);
			} else if (componentId == OPTION_4) {
				promptHouseChange("fremennik-style wood", 3, 30, 20000);
			} else if (componentId == OPTION_5) {
				stage = 2;
				sendOptionsDialogue(player, "Which kind of house style would you like?", 
						"(Level 40) Tropical wood - 50,000 coins", 
						"(Level 50) Fancy stone - 100,000 coins",
						"(Level 80) Dark stone - 500,000 coins",
						"Back...");
			}
		} else if (stage == 2) {
			if (componentId == OPTION_1) {
				promptHouseChange("tropical wood", 4, 40, 50000);
			} else if (componentId == OPTION_2) {
				promptHouseChange("fancy stone", 5, 50, 100000);
			} else if (componentId == OPTION_3) {
				promptHouseChange("dark stone", 6, 80, 500000);
			} else if (componentId == OPTION_4) {
				stage = 1;
				sendOptionsDialogue(player, "Which kind of house style would you like?", 
						"(Level 1) Basic wood - 2,500 coins", 
						"(Level 10) Basic stone - 10,000 coins",
						"(Level 20) Whitewashed stone - 15,000 coins",
						"(Level 30) Fremennik-style wood - 25,000 coins",
						"More...");
			}
		} else {
			end();
		}
	}
	
	public void promptHouseChange(final String name, final int look, int level, final int cost) {
		if (player.getSkills().getLevelForXp(Constants.CONSTRUCTION) >= level) {
			if (player.getInventory().containsItem(995, cost)) {
				player.sendOptionDialogue("Are you sure?", new String[] {"Yes", "No, that's too much money."}, new DialogueOptionEvent() {
					@Override
					public void run(Player player) {
						if (option == 1) {
							player.getInventory().deleteItem(995, cost);
							player.getHouse().changeLook(look);
							player.sendMessage("Your house has been set to "+name+".");
						}
					}
				});
			} else {
				end();
				player.sendMessage("You don't have enough money.");
			}
		} else {
			end();
			player.sendMessage("You don't have the construction level required.");
		}
	}

	@Override
	public void finish() {
		
	}

}
