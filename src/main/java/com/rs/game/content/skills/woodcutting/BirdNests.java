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
package com.rs.game.content.skills.woodcutting;

import com.rs.lib.game.Item;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ItemClickHandler;
import com.rs.utils.DropSets;
import com.rs.utils.drop.DropTable;

@PluginEventHandler
public class BirdNests {

	public static ItemClickHandler handleBirdNests = new ItemClickHandler(new Object[] { 5070, 5071, 5072, 11966, 5073, 5074, 7413 }, e -> {
		if (!e.getPlayer().getInventory().hasFreeSlots()) {
			e.getPlayer().sendMessage("You don't have enough inventory space.");
			return;
		}
		e.getPlayer().incrementCount("Nests searched");
		e.getPlayer().getInventory().deleteItem(e.getItem().getId(), 1);
		e.getPlayer().getInventory().addItem(5075, 1);
		switch(e.getItem().getId()) {
		case 5070:
			e.getPlayer().getInventory().addItem(5076, 1, true);
			break;
		case 5071:
			e.getPlayer().getInventory().addItem(5078, 1, true);
			break;
		case 5072:
			e.getPlayer().getInventory().addItem(5077, 1, true);
			break;
		case 11966:
			e.getPlayer().getInventory().addItem(11964, 1, true);
			break;
		case 5073:
			for (Item rew : DropTable.calculateDrops(e.getPlayer(), DropSets.getDropSet("nest_tree_seed")))
				e.getPlayer().getInventory().addItem(rew);
			break;
		case 5074:
			for (Item rew : DropTable.calculateDrops(e.getPlayer(), DropSets.getDropSet("nest_ring")))
				e.getPlayer().getInventory().addItem(rew);
			break;
		case 7413:
			for (Item rew : DropTable.calculateDrops(e.getPlayer(), DropSets.getDropSet("nest_shit_seed")))
				e.getPlayer().getInventory().addItem(rew);
			break;
		}
	});

}
