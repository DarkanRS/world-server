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
package com.rs.game.player.dialogues;

import com.rs.game.player.content.SkillsDialogue;
import com.rs.game.player.content.SkillsDialogue.ItemNameFilter;
import com.rs.game.player.content.skills.Fletching;
import com.rs.game.player.content.skills.Fletching.Fletch;

public class FletchingD extends Dialogue {

	private Fletch items;

	@Override
	public void start() {
		items = (Fletch) parameters[0];
		boolean maxQuantityTen = Fletching.maxMakeQuantityTen(items) && items.getProduct()[0] != 52;
		SkillsDialogue.sendSkillsDialogue(player, maxQuantityTen ? SkillsDialogue.MAKE_INTERVAL : SkillsDialogue.MAKE_ALL, "Choose how many you wish to make,<br>then click on the item to begin.", maxQuantityTen ? 10 : 28, items.getProduct(),
				maxQuantityTen ? null : new ItemNameFilter() {
					@Override
					public String rename(String name) {
						return name.replace(" (u)", "");
					}
				});
	}

	@Override
	public void run(int interfaceId, int componentId) {
		int option = SkillsDialogue.getItemSlot(componentId);
		if (option > items.getProduct().length) {
			end();
			return;
		}
		int quantity = SkillsDialogue.getQuantity(player);
		int invQuantity = player.getInventory().getItems().getNumberOf(items.getId());
		if (quantity > invQuantity)
			quantity = invQuantity;
		end();
		player.getActionManager().setAction(new Fletching(items, option, quantity));

	}

	@Override
	public void finish() {
	}

}
