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
package com.rs.game.content.skills.smithing;

import com.rs.game.model.entity.player.Player;
import com.rs.lib.Constants;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ItemClickHandler;

/**
 *
 * @author Humid
 */
@PluginEventHandler
public class GodSwordCreation {

	public static boolean handleGodSword(Player player, int usedWith, int itemUsed) {
		if (!player.getInventory().containsItem(usedWith, 1) || !player.getInventory().containsItem(itemUsed, 1))
			return false;
		if (itemUsed == 11702 && usedWith == 11690 || itemUsed == 11690 && usedWith == 11702) {
			player.getInventory().deleteItem(usedWith, 1);
			player.getInventory().deleteItem(itemUsed, 1);
			player.getInventory().addItem(11694, 1);
			player.sendMessage("You join the hilt and blade into a godsword.");
			return true;
		}
		if (itemUsed == 11704 && usedWith == 11690 || usedWith == 11704 && itemUsed == 11690) {
			player.getInventory().deleteItem(itemUsed, 1);
			player.getInventory().deleteItem(usedWith, 1);
			player.getInventory().addItem(11696, 1);
			player.sendMessage("You join the hilt and blade into a godsword.");
			return true;
		}
		if (itemUsed == 11706 && usedWith == 11690 || usedWith == 11706 && itemUsed == 11690) {
			player.getInventory().deleteItem(itemUsed, 1);
			player.getInventory().deleteItem(usedWith, 1);
			player.getInventory().addItem(11698, 1);
			player.sendMessage("You join the hilt and blade into a godsword.");
			return true;
		} else if (itemUsed == 11708 && usedWith == 11690 || usedWith == 11708 && itemUsed == 11690) {
			player.getInventory().deleteItem(itemUsed, 1);
			player.getInventory().deleteItem(usedWith, 1);
			player.getInventory().addItem(11700, 1);
			player.sendMessage("You join the hilt and blade into a godsword.");
			return true;
		} else if (itemUsed == 11710 && usedWith == 11712 || usedWith == 11710 && itemUsed == 11712) {
			player.getInventory().deleteItem(itemUsed, 1);
			player.getInventory().deleteItem(usedWith, 1);
			player.getInventory().addItem(11686, 1);
			player.sendMessage("You join the shards together.");
			return true;
		} else if (itemUsed == 11712 && usedWith == 11714 || usedWith == 11712 && itemUsed == 11714) {
			player.getInventory().deleteItem(itemUsed, 1);
			player.getInventory().deleteItem(usedWith, 1);
			player.getInventory().addItem(11692, 1);
			player.sendMessage("You join the shards together.");
			return true;
		} else if (itemUsed == 11686 && usedWith == 11714 || usedWith == 11686 && itemUsed == 11714) {
			player.getInventory().deleteItem(itemUsed, 1);
			player.getInventory().deleteItem(usedWith, 1);
			player.getInventory().addItem(11690, 1);
			player.sendMessage("You join the shards together.");
			return true;
		} else if (itemUsed == 11710 && usedWith == 11692 || usedWith == 11710 && itemUsed == 11692) {
			player.getInventory().deleteItem(itemUsed, 1);
			player.getInventory().deleteItem(usedWith, 1);
			player.getInventory().addItem(11690, 1);
			player.sendMessage("You join the shards together.");
			return true;
		} else if (itemUsed == 11712 && usedWith == 11688 || usedWith == 11712 && itemUsed == 11688) {
			player.getInventory().deleteItem(itemUsed, 1);
			player.getInventory().deleteItem(usedWith, 1);
			player.getInventory().addItem(11690, 1);
			player.sendMessage("You join the shards together.");
			return true;
		} else if (itemUsed == 985 && usedWith == 987 || usedWith == 985 && itemUsed == 987) {
			player.getInventory().deleteItem(itemUsed, 1);
			player.getInventory().deleteItem(usedWith, 1);
			player.getInventory().addItem(989, 1);
			return true;
		} else if (itemUsed == 2366 && usedWith == 2368 || usedWith == 2366 && itemUsed == 2368) {
			player.getInventory().deleteItem(itemUsed, 1);
			player.getInventory().deleteItem(usedWith, 1);
			player.getInventory().addItem(1187, 1);
			return true;
		} else if (itemUsed == 11286 && usedWith == 1540 || usedWith == 11286 && itemUsed == 1540) {
			if (player.getSkills().getLevel(Constants.SMITHING) >= 90) {
				player.getInventory().deleteItem(itemUsed, 1);
				player.getInventory().deleteItem(usedWith, 1);
				player.getInventory().addItem(11284, 1);
				player.getSkills().addXp(Constants.SMITHING, 2000);
				return true;
			} else {
				player.sendMessage("You need 90 smithing to create a dragonfire shield.");
				return true;
			}
		} else if (itemUsed == 13734 && usedWith == 13754 || usedWith == 13734 && itemUsed == 13754) {
			if (player.getSkills().getLevel(Constants.PRAYER) >= 85) {
				player.getInventory().deleteItem(itemUsed, 1);
				player.getInventory().deleteItem(usedWith, 1);
				player.getInventory().addItem(13736, 1);
				player.getSkills().addXp(Constants.PRAYER, 1500);
				return true;
			} else {
				player.sendMessage("You need 85 prayer to bless a spirit shield.");
				return true;
			}
		}
		/*
		 * Skull sceptre
		 */
		else if (itemUsed == 9007 && usedWith == 9008 || usedWith == 9007 && itemUsed == 9008) {
			player.getInventory().deleteItem(itemUsed, 1);
			player.getInventory().deleteItem(usedWith, 1);
			player.getInventory().addItem(9009, 1);
			player.sendMessage("You combine the skull halves into a strange looking skull.");
			return true;
		} else if (itemUsed == 9010 && usedWith == 9011 || usedWith == 9010 && itemUsed == 9011) {
			player.getInventory().deleteItem(itemUsed, 1);
			player.getInventory().deleteItem(usedWith, 1);
			player.getInventory().addItem(9012, 1);
			player.sendMessage("You put the two sceptre peices together and create a runed sceptre.");
			return true;
		} else if (itemUsed == 9012 && usedWith == 9009 || usedWith == 9012 && itemUsed == 9009) {
			player.getInventory().deleteItem(itemUsed, 1);
			player.getInventory().deleteItem(usedWith, 1);
			player.getInventory().addItem(9013, 1);
			player.sendMessage("You put the skull onto the sceptre and a strange magic begins to emnate from it.");
			return true;
		}

		/*
		 * START SPIRIT SHIELDS
		 */
		else if (itemUsed == 13736 && usedWith == 13746 || usedWith == 13736 && itemUsed == 13746) {
			if (player.getSkills().getLevel(Constants.PRAYER) >= 90 || player.getSkills().getLevel(Constants.SMITHING) >= 85) {
				player.getInventory().deleteItem(itemUsed, 1);
				player.getInventory().deleteItem(usedWith, 1);
				player.getInventory().addItem(13738, 1);
				player.getSkills().addXp(Constants.SMITHING, 1800);
				return true;
			} else {
				player.sendMessage("You need 85 smithing and 90 prayer to attach the sigil.");
				return true;
			}
		} else if (itemUsed == 13736 && usedWith == 13748 || usedWith == 13736 && itemUsed == 13748) {
			if (player.getSkills().getLevel(Constants.PRAYER) >= 90 || player.getSkills().getLevel(Constants.SMITHING) >= 85) {
				player.getInventory().deleteItem(itemUsed, 1);
				player.getInventory().deleteItem(usedWith, 1);
				player.getInventory().addItem(13740, 1);
				player.getSkills().addXp(Constants.SMITHING, 1800);
				return true;
			} else {
				player.sendMessage("You need 85 smithing and 90 prayer to attach the sigil.");
				return true;
			}
		} else if (itemUsed == 13736 && usedWith == 13750 || usedWith == 13736 && itemUsed == 13750) {
			if (player.getSkills().getLevel(Constants.PRAYER) >= 90 || player.getSkills().getLevel(Constants.SMITHING) >= 85) {
				player.getInventory().deleteItem(itemUsed, 1);
				player.getInventory().deleteItem(usedWith, 1);
				player.getInventory().addItem(13742, 1);
				player.getSkills().addXp(Constants.SMITHING, 1800);
				return true;
			} else {
				player.sendMessage("You need 85 smithing and 90 prayer to attach the sigil.");
				return true;
			}
		} else if (itemUsed == 13736 && usedWith == 13752 || usedWith == 13736 && itemUsed == 13752) {
			if (player.getSkills().getLevel(Constants.PRAYER) >= 90 || player.getSkills().getLevel(Constants.SMITHING) >= 85) {
				player.getInventory().deleteItem(itemUsed, 1);
				player.getInventory().deleteItem(usedWith, 1);
				player.getInventory().addItem(13744, 1);
				player.getSkills().addXp(Constants.SMITHING, 1800);
				return true;
			} else {
				player.sendMessage("You need 85 smithing and 90 prayer to attach the sigil.");
				return true;
			}
		} else if (itemUsed == 21369 && usedWith == 4151 || usedWith == 21369 && itemUsed == 4151) {
			player.getInventory().deleteItem(4151, 1);
			player.getInventory().deleteItem(21369, 1); // WHIP VINE
			player.getInventory().addItem(21371, 1);
			return true;
		} else if (itemUsed == 21358 && usedWith == 21359 || usedWith == 21358 && itemUsed == 21359) {
			if (player.getInventory().containsItem(21359, 2) && player.getSkills().getLevel(Constants.FLETCHING) >= 72) {
				player.getInventory().deleteItem(21358, 1);
				player.getInventory().deleteItem(21359, 2); // BOLAS
				player.getInventory().addItem(21365, 1);
				player.getSkills().addXp(Constants.FLETCHING, 25);
			} else
				player.sendMessage("You need 2 excressence, 1 mutated vine, and 72 fletching to create bolas.");
			return true;
		}
		//spotanim 450 sagie
		return false;
	}
	public static ItemClickHandler handleAbyssalVineWhip = new ItemClickHandler(new Object[] { "Abyssal vine whip" }, new String[] { "Split" }, e -> {
		if (e.getPlayer().getInventory().getFreeSlots() >= 1) {
			e.getPlayer().getInventory().deleteItem(e.getItem());
			e.getPlayer().getInventory().addItem(4151);
			e.getPlayer().getInventory().addItem(21369);
			e.getPlayer().sendMessage("You split the vine from the whip.");
		} else
			e.getPlayer().sendMessage("Not enough space in your inventory.");
	});
}