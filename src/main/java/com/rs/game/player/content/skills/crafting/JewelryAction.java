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
import com.rs.game.player.actions.Action;
import com.rs.game.player.content.skills.crafting.Jewelry.Bling;
import com.rs.lib.Constants;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Item;

public class JewelryAction extends Action {

	Bling bling;
	int numberToMake;

	public JewelryAction(Bling bling, int number) {
		this.bling = bling;
		numberToMake = number;
	}

	public boolean checkAll(Player player) {
		if (bling == null || player == null)
			return false;
		if (!player.getInventory().containsItem(bling.getMouldRequired().getId(), 1)) {
			player.sendMessage("You need one " + ItemDefinitions.getDefs(bling.getMouldRequired().getId()).getName().toLowerCase() + " to make that.");
			return false;
		}
		if (!player.getInventory().containsItems(bling.getItemsRequired())) {
			player.sendMessage("You don't have the items required to make that.");
			return false;
		}
		if (player.getSkills().getLevel(Constants.CRAFTING) < bling.getLevelRequired()) {
			player.sendMessage("You need " + bling.getLevelRequired() + " crafting to make that.");
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
			if (player.getTempAttribs().getO("jewelryObject") != null)
				player.faceObject(player.getTempAttribs().getO("jewelryObject"));
			return true;
		}
		return false;
	}

	@Override
	public int processWithDelay(Player player) {
		numberToMake--;
		player.setNextAnimation(new Animation(3243));
		player.getSkills().addXp(Constants.CRAFTING, bling.getExperience());
		for (Item required : bling.getItemsRequired())
			player.getInventory().deleteItem(required.getId(), required.getAmount());
		player.getInventory().addItem(bling.getProduct());
		player.sendMessage("You make a " + bling.getProduct().getDefinitions().getName().toLowerCase() + ".", true);

		if (numberToMake > 0)
			return 2;
		return -1;
	}

	@Override
	public void stop(Player player) {

	}

}
