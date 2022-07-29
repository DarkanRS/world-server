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
package com.rs.game.content.skills.util;

import com.rs.game.model.entity.player.Player;
import com.rs.game.model.entity.player.actions.PlayerAction;
import com.rs.lib.Constants;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Item;

public class CreateAction extends PlayerAction {

	private int[] anims;
	private Item[][] materials;
	private Item[][] products;
	private double[] xp;
	private int[] reqs;
	private int skill;
	private int delay;
	private int choice;
	private int quantity = -1;

	public CreateAction(Item[][] materials, Item[][] products, double[] xp, int[] anim, int[] reqs, int skill, int delay, int choice) {
		anims = anim;
		this.materials = materials;
		this.products = products;
		this.xp = xp;
		this.skill = skill;
		this.delay = delay;
		this.choice = choice;
		this.reqs = reqs;
	}

	public CreateAction setQuantity(int quantity) {
		this.quantity = quantity;
		return this;
	}

	public boolean checkAll(Player player) {
		if ((choice >= materials.length) || !player.getInventory().containsItems(materials[choice]) || !player.getInventory().hasRoomFor(materials[choice], products[choice]))
			return false;
		if (reqs != null) {
			if (skill != -1 && player.getSkills().getLevel(skill) < reqs[choice]) {
				player.sendMessage("You need a " + Constants.SKILL_NAME[skill] + " level of " + reqs[choice] + " to make a " + products[choice][0].getName() + ".");
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean start(Player player) {
		return checkAll(player);
	}

	@Override
	public boolean process(Player player) {
		return checkAll(player);
	}

	@Override
	public int processWithDelay(Player player) {
		if (quantity != -1) {
			quantity--;
			if (quantity < 0)
				return -1;
		}
		if (!player.getInventory().hasRoomFor(materials[choice], products[choice])) {
			player.sendMessage("You don't have enough inventory space.");
			return -1;
		}
		if (anims != null)
			player.setNextAnimation(new Animation(anims[choice]));
		for (int i = 0; i < materials[choice].length; i++)
			player.getInventory().deleteItem(materials[choice][i]);
		for (int i = 0; i < products[choice].length; i++)
			player.getInventory().addItemDrop(products[choice][i]);
		if (xp != null && skill != -1)
			player.getSkills().addXp(skill, xp[choice]);
		return delay;
	}

	@Override
	public void stop(Player player) {

	}

}
