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

import com.rs.engine.quest.Quest;
import com.rs.engine.quest.QuestHandler;
import com.rs.engine.quest.QuestOutline;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.entity.player.Skills;
import com.rs.lib.game.Item;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ItemOnObjectHandler;

import java.util.ArrayList;
import java.util.List;

@QuestHandler(Quest.DRUIDIC_RITUAL)
@PluginEventHandler
public class DruidicRitual extends QuestOutline {
	final static int RAW_BEAR_MEAT = 2136;
	final static int RAW_RAT_MEAT = 2134;
	final static int RAW_CHICKEN = 2138;
	final static int RAW_BEEF = 2132;

	final static int ENCHANTED_RAW_BEAR_MEAT = 524;
	final static int ENCHANTED_RAW_RAT_MEAT = 523;
	final static int ENCHANTED_RAW_CHICKEN = 525;
	final static int ENCHANTED_RAW_BEEF = 522;

	@Override
	public int getCompletedStage() {
		return 4;
	}

	@Override
	public List<String> getJournalLines(Player player, int stage) {
		ArrayList<String> lines = new ArrayList<>();
		switch(stage) {
		case 0:
			lines.add("I can start this quest by speaking to Kaqemeex who is at");
			lines.add("the Druids Circle just North of Taverly.");
			lines.add("");
			break;
		case 1:
			lines.add("I must speak to Sanfew south of the monument.");
			lines.add("");
			break;
		case 2:
			lines.add("Sanfew told me to get raw rat, chicken, beef and bear meat");
			lines.add("Im to take them to the cauldron of thunder and dip them in.");
			lines.add("Afterwards I should take all four back to Sanfew");
			lines.add("");
			lines.add("I can find the cauldron of thunder in Taverly Dungeon");
			lines.add("It is at the first gate northeast of the dungeon entrance");
			lines.add("");
			break;
		case 3:
			lines.add("I must speak to Kaqemeex at the monument");
			break;
		case 4:
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
		player.getSkills().addXpQuest(Skills.HERBLORE, 250);
		player.getInventory().addItem(200, 15);
		player.getInventory().addItem(222, 15);
		getQuest().sendQuestCompleteInterface(player, 195, "250 Herblore XP", "15 Grimy Guam and 15 eye of newt");
	}

	public static ItemOnObjectHandler handleCauldron = new ItemOnObjectHandler(new Object[] { 2142 }, e -> {
		Player player = e.getPlayer();
		Item item = e.getItem();
		int itemId = item.getId();
		if (itemId == RAW_BEAR_MEAT) {
			if(player.getInventory().containsItem(RAW_BEAR_MEAT) && !player.getInventory().containsItem(ENCHANTED_RAW_BEAR_MEAT)) {
				player.getInventory().deleteItem(RAW_BEAR_MEAT, 1);
				player.getInventory().addItem(ENCHANTED_RAW_BEAR_MEAT);
				return;
			}
			if(player.getInventory().containsItem(ENCHANTED_RAW_BEAR_MEAT)) {
				player.getPackets().sendGameMessage("Enchanted bear meat is already in your inventory");
				return;
			}
		}
		if (itemId == RAW_RAT_MEAT) {
			if(player.getInventory().containsItem(RAW_RAT_MEAT) && !player.getInventory().containsItem(ENCHANTED_RAW_RAT_MEAT)) {
				player.getInventory().deleteItem(RAW_RAT_MEAT, 1);
				player.getInventory().addItem(ENCHANTED_RAW_RAT_MEAT);
				return;
			}
			if(player.getInventory().containsItem(ENCHANTED_RAW_RAT_MEAT)) {
				player.getPackets().sendGameMessage("Enchanted rat meat is already in your inventory");
				return;
			}
		}
		if (itemId == RAW_CHICKEN) {
			if(player.getInventory().containsItem(RAW_CHICKEN) && !player.getInventory().containsItem(ENCHANTED_RAW_CHICKEN)) {
				player.getInventory().deleteItem(RAW_CHICKEN, 1);
				player.getInventory().addItem(ENCHANTED_RAW_CHICKEN);
				return;
			}
			if(player.getInventory().containsItem(ENCHANTED_RAW_CHICKEN))  {
				player.getPackets().sendGameMessage("Enchanted chicken meat is already in your inventory");
				return;
			}
		}
		if (itemId == RAW_BEEF) {
			if(player.getInventory().containsItem(RAW_BEEF) && !player.getInventory().containsItem(ENCHANTED_RAW_BEEF)) {
				player.getInventory().deleteItem(RAW_BEEF, 1);
				player.getInventory().addItem(ENCHANTED_RAW_BEEF);
				return;
			}
			if(player.getInventory().containsItem(ENCHANTED_RAW_BEEF))  {
				player.getPackets().sendGameMessage("Enchanted beef meat is already in your inventory");
				return;
			}
		}

		player.getPackets().sendGameMessage("There is no reason to put this in the cauldron");
	});
}
