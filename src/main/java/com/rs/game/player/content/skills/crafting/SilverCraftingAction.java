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
package com.rs.game.player.content.skills.crafting;

import com.rs.cache.loaders.ItemDefinitions;
import com.rs.game.player.Player;
import com.rs.game.player.actions.PlayerAction;
import com.rs.game.player.content.skills.crafting.Silver.SilverItems;
import com.rs.lib.Constants;
import com.rs.lib.game.Animation;

public class SilverCraftingAction extends PlayerAction {

	SilverItems itemToMake;
	int numberToMake;

	public SilverCraftingAction(SilverItems bling, int number) {
		itemToMake = bling;
		numberToMake = number;
	}

	public boolean checkAll(Player player) {
		if (itemToMake == null || player == null)
			return false;
		if (itemToMake.getMouldRequired() != -1 && !player.getInventory().containsItem(itemToMake.getMouldRequired(), 1)) {
			player.sendMessage("You need one " + ItemDefinitions.getDefs(itemToMake.getMouldRequired()).getName().toLowerCase() + " to make that.");
			return false;
		}
		if (!player.getInventory().containsItem(Silver.SILVER_BAR, 1)) {
			player.sendMessage("You don't have the items required to make that.");
			return false;
		}
		if (player.getSkills().getLevel(Constants.CRAFTING) < itemToMake.getLevelRequired()) {
			player.sendMessage("You need " + itemToMake.getLevelRequired() + " crafting to make that.");
			return false;
		}
		return true;
	}

	@Override
	public boolean start(Player player) {
		if (checkAll(player))
			return true;
		return false;
	}

	@Override
	public boolean process(Player player) {
		if (checkAll(player)) {
			if (player.getTempAttribs().getO("silverObject") != null)
				player.faceObject(player.getTempAttribs().getO("silverObject"));
			return true;
		}
		return false;
	}

	@Override
	public int processWithDelay(Player player) {
		numberToMake--;
		player.setNextAnimation(new Animation(3243));
		player.getSkills().addXp(Constants.CRAFTING, itemToMake.getExperience());
		player.getInventory().deleteItem(Silver.SILVER_BAR, 1);
		player.getInventory().addItem(itemToMake.getProduct());
		player.sendMessage("You make a " + itemToMake.getProduct().getDefinitions().getName().toLowerCase() + ".", true);

		if (numberToMake > 0)
			return 2;
		return -1;
	}

	@Override
	public void stop(Player player) {

	}

}
