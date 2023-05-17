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
package com.rs.game.content.skills.cooking;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.statements.MakeXStatement;
import com.rs.game.content.skills.cooking.FruitCutting.CuttableFruit;
import com.rs.game.model.entity.player.Player;

public class FruitCuttingD extends Conversation {

	public FruitCuttingD(Player player, CuttableFruit fruit) {
		super(player);
		addNext(new MakeXStatement("Choose how many you wish to cut,<br>then click on the item to begin.", fruit.getProductIds(), player.getInventory().getItems().getNumberOf(fruit.getFruitId())));
		for (int i = 0;i < fruit.getProductIds().length;i++) {
			final int op = i;
			addNext(() -> {
				int quantity = MakeXStatement.getQuantity(player);
				int invQuantity = player.getInventory().getItems().getNumberOf(fruit.getFruitId());
				if (quantity > invQuantity)
					quantity = invQuantity;
				player.getActionManager().setAction(new FruitCutting(fruit, op, quantity));
			});
		}
	}
}
