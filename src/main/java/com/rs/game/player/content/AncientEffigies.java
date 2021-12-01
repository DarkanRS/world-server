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
package com.rs.game.player.content;

import com.rs.game.player.Inventory;
import com.rs.game.player.Player;
import com.rs.lib.Constants;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Item;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.util.Utils;

/**
 * Handles ancient effigies non-dialogue related stuff.
 * 
 * @author Raghav/Own4g3 <Raghav_ftw@hotmail.com>
 * 
 */
public class AncientEffigies {

	/**
	 * First skill to be nourished.
	 */
	public static int[] SKILL_1 = { Constants.AGILITY, Constants.CONSTRUCTION, Constants.COOKING, Constants.FISHING, Constants.FLETCHING, Constants.HERBLORE, Constants.MINING, Constants.SUMMONING };

	/**
	 * Second skill to be nourished.
	 */
	public static int[] SKILL_2 = { Constants.CRAFTING, Constants.THIEVING, Constants.FIREMAKING, Constants.FARMING, Constants.WOODCUTTING, Constants.HUNTER, Constants.SMITHING, Constants.RUNECRAFTING };

	/**
	 * Ancient effigies' item ids.
	 */
	public static final int STARVED_ANCIENT_EFFIGY = 18778, NOURISHED_ANCIENT_EFFIGY = 18779, SATED_ANCIENT_EFFIGY = 18780, GORGED_ANCIENT_EFFIGY = 18781, DRAGONKIN_LAMP = 18782;

	/**
	 * Getting the required level for each effigy.
	 * 
	 * @param id
	 *            The effigy's item id.
	 * @return Required level.
	 */
	public static int getRequiredLevel(int id) {
		switch (id) {
		case STARVED_ANCIENT_EFFIGY:
			return 91;
		case NOURISHED_ANCIENT_EFFIGY:
			return 93;
		case SATED_ANCIENT_EFFIGY:
			return 95;
		case GORGED_ANCIENT_EFFIGY:
			return 97;
		}
		return -1;
	}

	/**
	 * Getting the message.
	 * 
	 * @param skill
	 *            The skill
	 * @return message
	 */
	public static String getMessage(int skill) {
		switch (skill) {
		case Constants.AGILITY:
			return "deftness and precision";
		case Constants.CONSTRUCTION:
			return "buildings and security";
		case Constants.COOKING:
			return "fire and preparation";
		case Constants.FISHING:
			return "life and cultivation";
		case Constants.FLETCHING:
			return "lumber and woodworking";
		case Constants.HERBLORE:
			return "flora and fuana";
		case Constants.MINING:
			return "metalwork and minerals";
		case Constants.SUMMONING:
			return "binding essence and spirits";
		}
		return null;
	}

	/**
	 * Getting the experience amount.
	 * 
	 * @param itemId
	 *            The effigy's item id.
	 * @return The amount of experience.
	 */
	public static int getExp(int itemId) {
		switch (itemId) {
		case STARVED_ANCIENT_EFFIGY:
			return 15000;
		case NOURISHED_ANCIENT_EFFIGY:
			return 20000;
		case SATED_ANCIENT_EFFIGY:
			return 25000;
		case GORGED_ANCIENT_EFFIGY:
			return 30000;
		}
		return -1;
	}

	/**
	 * Investigation of an effigy.
	 * 
	 * @param player
	 *            The player who is doing investigation.
	 * @param id
	 *            The effigy item id.
	 */
	public static void effigyInvestigation(Player player, Item item) {
		Inventory inv = player.getInventory();
		if (item.getId() == STARVED_ANCIENT_EFFIGY)
			inv.replace(item, new Item(NOURISHED_ANCIENT_EFFIGY, 1).addMetaData("effigyType", (double) Utils.random(7)+1.0));
		else if (item.getId() == NOURISHED_ANCIENT_EFFIGY)
			inv.replace(item, new Item(SATED_ANCIENT_EFFIGY, 1).addMetaData("effigyType", (double) Utils.random(7)+1.0));
		else if (item.getId() == SATED_ANCIENT_EFFIGY)
			inv.replace(item, new Item(GORGED_ANCIENT_EFFIGY, 1).addMetaData("effigyType", (double) Utils.random(7)+1.0));
		else if (item.getId() == GORGED_ANCIENT_EFFIGY) {
			player.incrementCount("Ancient Effigies opened");
			inv.replace(item, new Item(DRAGONKIN_LAMP, 1));
		}
		player.setNextAnimation(new Animation(item.getId() == GORGED_ANCIENT_EFFIGY ? 14177 : 4068));
		if (item.getId() == GORGED_ANCIENT_EFFIGY)
			player.setNextSpotAnim(new SpotAnim(2692));
	}
}
