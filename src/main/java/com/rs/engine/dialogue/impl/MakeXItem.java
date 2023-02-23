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
package com.rs.engine.dialogue.impl;

import com.rs.game.content.skills.util.CreateAction;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.statements.MakeXStatement;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.game.Item;

public class MakeXItem extends Dialogue {

	private int itemId;

	public MakeXItem(Player player, Item[] materials, Item[] products, double xp, int anim, int req, int skill, int delay) {
		itemId = products[0].getId();
		setFunc(() -> {
			int quantity = MakeXStatement.getQuantity(player);
			for (Item mat : materials) {
				int newQ = player.getInventory().getNumberOf(mat.getId()) / mat.getAmount();
				if (newQ < quantity)
					quantity = newQ;
			}
			player.getActionManager().setAction(new CreateAction(new Item[][] { materials }, new Item[][] { products }, new double [] { xp }, new int[] { anim },  new int[] { req }, skill, delay, 0).setQuantity(quantity));
		});
	}

	public MakeXItem(Player player, Item material, Item product, double xp, int anim, int req, int skill, int delay) {
		this(player, new Item[] { material }, new Item[] { product }, xp, anim, req, skill, delay);
	}

	public int getItemId() {
		return itemId;
	}
}
