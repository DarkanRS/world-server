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
package com.rs.game.content.skills.construction;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.Options;
import com.rs.engine.quest.Quest;
import com.rs.game.content.Skillcapes;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.entity.player.Skills;
import com.rs.lib.Constants;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;
import com.rs.utils.shop.ShopsHandler;

@PluginEventHandler
public class EstateAgentDialogue extends Conversation {

	public static NPCClickHandler handleEstateAgent = new NPCClickHandler(new Object[] { "Estate agent" }, e -> e.getPlayer().startConversation(new EstateAgentDialogue(e.getPlayer(), e.getNPCId())));

	public EstateAgentDialogue(Player player, int npcId) {
		super(player);

		addOptions("What would you like to do?", new Options() {
			@Override
			public void create() {
				option("Open shop", new Dialogue().addNext(() -> ShopsHandler.openShop(player, "construction_goods")));
				option("Can I move my house?", new Dialogue().addOptions("Which town would you like your house moved to?", new Options() {
					@Override
					public void create() {
						for (HouseConstants.POHLocation loc : HouseConstants.POHLocation.values()) {
							if (player.getSkills().getLevelForXp(Skills.CONSTRUCTION) >= loc.getLevelRequired())
								option(Utils.formatPlayerNameForDisplay(loc.name()), new Dialogue().addNext(() -> promptHouseLocation(Utils.formatPlayerNameForDisplay(loc.name()), loc, loc.getLevelRequired(), loc.getCost())));
						}
					}
				}));
				option("Ask about changing house styling.", new Dialogue().addOptions("Which kind of house style would you like?", new Options() {
					@Override
					public void create() {
						option("Basic wood - 2,500 coins", new Dialogue().addNext(() -> promptHouseChange("basic wood", 0, 1, 2500)));
						if (player.getSkills().getLevelForXp(Skills.CONSTRUCTION) >= 10)
							option("Basic stone - 10,000 coins", new Dialogue().addNext(() -> promptHouseChange("basic stone", 1, 10, 10000)));
						if (player.getSkills().getLevelForXp(Skills.CONSTRUCTION) >= 20)
							option("Whitewashed stone - 15,000 coins", new Dialogue().addNext(() -> promptHouseChange("whitewashed stone", 2, 20, 15000)));
						if (player.getSkills().getLevelForXp(Skills.CONSTRUCTION) >= 30)
							option("Fremennik-style wood - 25,000 coins", new Dialogue().addNext(() -> promptHouseChange("fremennik-style wood", 3, 30, 20000)));
						if (player.getSkills().getLevelForXp(Skills.CONSTRUCTION) >= 40)
							option("Tropical wood - 50,000 coins", new Dialogue().addNext(() -> promptHouseChange("tropical wood", 4, 40, 50000)));
						if (player.getSkills().getLevelForXp(Skills.CONSTRUCTION) >= 50)
							option("Fancy stone - 100,000 coins", new Dialogue().addNext(() -> promptHouseChange("fancy stone", 5, 50, 100000)));
						if (player.isQuestComplete(Quest.LOVE_STORY, "to have a dark stone themed house."))
							option("Dark stone - 500,000 coins", new Dialogue().addNext(() -> promptHouseChange("dark stone", 6, 80, 500000)));
					}
				}));
				option("What's that cape you are wearing?", Skillcapes.Construction.getOffer99CapeDialogue(player, npcId));
			}
		});
	}

	public void promptHouseLocation(final String name, final HouseConstants.POHLocation loc, int level, final int cost) {
		if (player.getSkills().getLevelForXp(Constants.CONSTRUCTION) >= level) {
			if (player.getInventory().hasCoins(cost))
				player.sendOptionDialogue("Are you sure?", ops -> {
					ops.add("Yes", () -> {
						player.getInventory().removeCoins(cost);
						player.getHouse().setLocation(loc);
						player.sendMessage("Your house location been set to "+name+".");
					});
					ops.add("No, that's too much money.");
				});
			else {
				player.sendMessage("You don't have enough money.");
			}
		} else {
			player.sendMessage("You don't have the construction level required.");
		}
	}

	public void promptHouseChange(final String name, final int look, int level, final int cost) {
		if (player.getSkills().getLevelForXp(Constants.CONSTRUCTION) >= level) {
			if (player.getInventory().hasCoins(cost))
				player.sendOptionDialogue("Are you sure?", ops -> {
					ops.add("Yes", () -> {
						player.getInventory().removeCoins(cost);
						player.getHouse().changeLook(look);
						player.sendMessage("Your house has been set to "+name+".");
					});
					ops.add("No, that's too much money.");
				});
			else {
				player.sendMessage("You don't have enough money.");
			}
		} else {
			player.sendMessage("You don't have the construction level required.");
		}
	}
}
