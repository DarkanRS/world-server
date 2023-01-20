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
package com.rs.game.content.minigames.pest;

import com.rs.cache.loaders.ItemDefinitions;
import com.rs.game.content.skills.herblore.HerbCleaning.Herbs;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.Constants;
import com.rs.lib.game.Item;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ButtonClickHandler;
import com.rs.utils.DropSets;
import com.rs.utils.drop.DropTable;

@PluginEventHandler
public class CommendationExchange {// 1875 TODO

	// can be static :) 1513 cs for names (useless really)

	private static final int INTERFACE = 1011, RATE_ONE = 1, RATE_TEN = 10, RATE_HUNDRED = 100;
	/**
	 * EXP related stuff
	 */
	private static final int[] SKILL_BASE_COMPONENTS = { 100, 116, 132, 148, 164, 180 };
	private static final int[] SKILLS = { Constants.STRENGTH, Constants.DEFENSE, Constants.CONSTRUCTION, Constants.RANGE, Constants.MAGIC, Constants.PRAYER };

	/**
	 * Void related stuff
	 */
	private static final int[] VOID_BASE_COMPONENTS = { 15, 196, 208, 220, 232, 166, 256, 268, 280 };
	private static final int[] VOID = { 11676, 11675, 11674, 8839, 8840, 8842, 8841, 19711, 11666 };
	private static final int[] VOID_POINTS_COST = { 200, 200, 200, 250, 250, 150, 250, 150, 10 };

	/**
	 * Charm related stuff
	 */
	private static final int[] CHARM_BASE_COMPONENTS = { 324, 339, 354, 369 };
	private static final int[] CHARMS = { 12166, 12167, 12164, 12165 };

	public static void openExchangeShop(Player player) {
		player.getInterfaceManager().sendInterface(INTERFACE);
		player.getVars().setVar(1875, 1250);
		refreshPoints(player);
	}

	private static void refreshPoints(Player player) {
		player.getVars().setVarBit(2086, player.getPestPoints());
	}

	private static boolean exchangeCommendation(Player player, int price) {
		int currentPoints = player.getPestPoints();
		if (currentPoints - price < 0) {
			player.sendMessage("You don't have enough Commendations remaining to complete this exchange.");
			return false;
		}
		player.setPestPoints(currentPoints - price);
		refreshPoints(player);
		return true;
	}

	public static ButtonClickHandler handleButtonOptions = new ButtonClickHandler(1011, e -> {
		if (e.getComponentId() == 68)
			addXPForSkill(e.getPlayer(), Constants.ATTACK, RATE_ONE);
		else if (e.getComponentId() == 86)
			addXPForSkill(e.getPlayer(), Constants.ATTACK, RATE_TEN);
		else if (e.getComponentId() == 88)
			addXPForSkill(e.getPlayer(), Constants.ATTACK, RATE_HUNDRED);
		else if (e.getComponentId() == 29)
			e.getPlayer().getPackets().setIFHidden(INTERFACE, 69, false);
		else if (e.getComponentId() == 75) {
			e.getPlayer().getPackets().setIFHidden(INTERFACE, 70, true);
			e.getPlayer().getPackets().setIFHidden(INTERFACE, 69, false);
		} else if (e.getComponentId() == 20 || e.getComponentId() == 73)
			openExchangeShop(e.getPlayer());
		else if (e.getComponentId() == 24 || e.getComponentId() == 31) {
			e.getPlayer().getPackets().setIFHidden(INTERFACE, 70, false);
			e.getPlayer().getPackets().setIFHidden(INTERFACE, 69, true);
		} else if (e.getComponentId() == 291) {
			if (e.getPlayer().getSkills().getLevelForXp(Constants.HERBLORE) < 25) {
				e.getPlayer().sendMessage("You need a herblore level of 25 in order to purchase a herblore pack.");
				return;
			}
			if (!exchangeCommendation(e.getPlayer(), 30))
				return;
			e.getPlayer().getInventory().addItem(Herbs.values()[Utils.random(5)].getHerbId() + 1, Utils.random(4), true);
			e.getPlayer().getInventory().addItem(Herbs.values()[Herbs.values().length - 1].getHerbId() + 1, Utils.random(2), true);
			e.getPlayer().sendMessage("You exchange 30 commendation points for a herblore pack.");
		} else if (e.getComponentId() == 302) {
			if (e.getPlayer().getSkills().getLevelForXp(Constants.MINING) < 25) {
				e.getPlayer().sendMessage("You need a mining level of 25 in order to purchase a herblore pack.");
				return;
			}
			if (!exchangeCommendation(e.getPlayer(), 15))
				return;
			e.getPlayer().getInventory().addItem(441, Utils.random(20), true);
			e.getPlayer().getInventory().addItem(454, Utils.random(30), true);
			e.getPlayer().sendMessage("You exchange 15 commendation points for a mineral pack.");
		} else if (e.getComponentId() == 313) {
			if (e.getPlayer().getSkills().getLevelForXp(Constants.FARMING) < 25) {
				e.getPlayer().sendMessage("You need a farming level of 25 in order to purchase a herblore pack.");
				return;
			}
			if (!exchangeCommendation(e.getPlayer(), 15))
				return;
			for (int i = 0;i < 6;i++)
				for (Item rew : DropTable.calculateDrops(e.getPlayer(), DropSets.getDropSet("nest_shit_seed")))
					e.getPlayer().getInventory().addItemDrop(rew);
			e.getPlayer().sendMessage("You exchange 15 commendation points for a seed pack.");
		} else {
			for (int index = 0; index < SKILL_BASE_COMPONENTS.length; index++) {
				int skillComponent = SKILL_BASE_COMPONENTS[index];
				for (int i = 0; i < 6; i += 2)
					if (skillComponent + i == e.getComponentId())
						addXPForSkill(e.getPlayer(), SKILLS[index], getRateForIndex(i / 2));
			}

			for (int index = 0; index < VOID_BASE_COMPONENTS.length; index++)
				if (VOID_BASE_COMPONENTS[index] == e.getComponentId())
					addVoidItem(e.getPlayer(), index);

			for (int index = 0; index < CHARM_BASE_COMPONENTS.length; index++) {
				int charmComponent = CHARM_BASE_COMPONENTS[index];
				for (int i = 0; i < 6; i += 2)
					if (charmComponent + i == e.getComponentId())
						addCharm(e.getPlayer(), CHARMS[index], getRateForIndex(i / 2));
			}
		}
	});

	private static void addCharm(Player player, int itemId, int rate) {
		if (rate == 100)
			rate = player.getInventory().getFreeSlots();
		for (int i = 0; i < rate; i++)
			if (!exchangeCommendation(player, 2)) {
				rate = i;
				break;
			}
		player.getInventory().addItem(itemId, rate, true);
		player.sendMessage("You exchange " + rate * 2 + " Commendations for a charm.");
	}

	private static int getRateForIndex(int index) {
		if (index == 0)
			return RATE_ONE;
		if (index == 1)
			return RATE_TEN;
		if (index == 2)
			return RATE_HUNDRED;
		return 0;
	}

	private static void addXPForSkill(Player player, int skill, int rate) {
		if (player.getSkills().getLevelForXp(skill) < 25) {
			player.sendMessage("You need a " + Constants.SKILL_NAME[skill] + " of at least 25 in order to gain experience.");
			return;
		}
		for (int i = 0; i < rate; i++)
			if (!exchangeCommendation(player, 1)) {
				rate = i;
				break;
			}
		double experience = calculateExperience(player, skill) * rate;
		player.getSkills().addXp(skill, experience / 18);
		player.simpleDialogue("You gain " + Utils.getFormattedNumber((int) experience) + " experience in " + Constants.SKILL_NAME[skill] + ".");
	}

	private static void addVoidItem(Player player, int index) {
		if (!player.getSkills().hasRequirements(Constants.ATTACK, 42, Constants.STRENGTH, 42, Constants.DEFENSE, 42, Constants.HITPOINTS, 42, Constants.RANGE, 42, Constants.MAGIC, 42, Constants.PRAYER, 22)) {
			player.sendMessage("You need an attack, strength, defence, constitution, range, and magic level of 42, and a prayer level of 22 in order to purchase void equipment.");
			return;
		}
		int cost = VOID_POINTS_COST[index];
		if (!exchangeCommendation(player, cost))
			return;
		int voidItem = VOID[index];
		player.getInventory().addItem(voidItem, 1, true);
		player.itemDialogue(voidItem, "You exchange " + cost + " commendation points for a " + ItemDefinitions.getDefs(voidItem).getName().toLowerCase() + ".");
	}

	private static double calculateExperience(Player player, int skill) {
		int level = player.getSkills().getLevelForXp(skill);
		int constant = 35;
		if (skill == Constants.MAGIC || skill == Constants.RANGE)
			constant = 32;
		else if (skill == Constants.PRAYER)
			constant = 18;
		return (Math.ceil(((level + 25) * (level - 24)) / 606) * constant) + constant;
	}
}