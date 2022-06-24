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
package com.rs.game.content.skills;

import java.util.Arrays;

import com.rs.cache.loaders.ItemDefinitions;
import com.rs.game.content.dialogue.Conversation;
import com.rs.game.content.dialogue.Dialogue;
import com.rs.game.content.dialogue.statements.MakeXStatement;
import com.rs.game.content.dialogue.statements.MakeXStatement.MakeXType;
import com.rs.game.content.skills.Fletching.Fletch;
import com.rs.game.model.entity.player.Player;

public class FletchingD extends Conversation {

	public FletchingD(Player player, Fletch items) {
		super(player);
		
		boolean maxQuantityTen = Fletching.maxMakeQuantityTen(items) && items.getProduct()[0] != 52;
		Dialogue makeX = addNext(new MakeXStatement(maxQuantityTen ? MakeXType.MAKE_SET : MakeXType.MAKE, maxQuantityTen ? 10 : 28, "Choose how many you wish to make,<br>then click on the item to begin.", items.getProduct(), maxQuantityTen ? null : (String[]) Arrays.stream(items.getProduct()).mapToObj(i -> ItemDefinitions.getDefs(i).name.replace(" (u)", "")).toArray()));
		
		for (int i = 0;i < items.getProduct().length;i++) {
			final int option = i;
			makeX.addNext(() -> {
				int quantity = MakeXStatement.getQuantity(player);
				int invQuantity = player.getInventory().getItems().getNumberOf(items.getId());
				if (quantity > invQuantity)
					quantity = invQuantity;
				player.getActionManager().setAction(new Fletching(items, option, quantity));
			});
		}
		
		create();
	}
}
