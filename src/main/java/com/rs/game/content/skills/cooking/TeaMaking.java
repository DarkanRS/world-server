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

import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ItemOnItemHandler;

@PluginEventHandler
public class TeaMaking  {

	static int[] cupsEmpty = {7728, 7732, 7735};
	static int[] cupsFull = {7730, 7733, 7736};

	public static ItemOnItemHandler mixTea = new ItemOnItemHandler(Utils.range(7688, 7739), e -> {
		if (e.getItem1().getDefinitions().getName().contains("Pot of tea"))
			for (int i = 0;i < 3;i++)
				if (e.getItem2().getId() == cupsEmpty[i]) {
					e.getPlayer().getInventory().deleteItem(e.getItem1().getId(), 1);
					e.getPlayer().getInventory().deleteItem(e.getItem2().getId(), 1);
					e.getPlayer().getInventory().addItem(cupsFull[i], 1);
					if (e.getItem1().getDefinitions().getName().contains("(1)"))
						e.getPlayer().getInventory().addItem(e.getItem1().getId()+4, 1);
					else
						e.getPlayer().getInventory().addItem(e.getItem1().getId()+2, 1);
					break;
				}

		if (e.usedWith(7738, 7702)) {
			e.getPlayer().getInventory().deleteItem(7738, 1);
			e.getPlayer().getInventory().deleteItem(7702, 1); //Clay leaves into pot
			e.getPlayer().getInventory().addItem(7700, 1);
		} else if (e.usedWith(7738, 7714)) {
			e.getPlayer().getInventory().deleteItem(7738, 1);
			e.getPlayer().getInventory().deleteItem(7714, 1); //Porcelain leaves into pot
			e.getPlayer().getInventory().addItem(7712, 1);
		} else if (e.usedWith(7738, 7726)) {
			e.getPlayer().getInventory().deleteItem(7738, 1);
			e.getPlayer().getInventory().deleteItem(7726, 1); //Gilded leaves into pot
			e.getPlayer().getInventory().addItem(7724, 1);
		} else if (e.usedWith(7691, 7700)) {
			e.getPlayer().getInventory().deleteItem(7691, 1);
			e.getPlayer().getInventory().deleteItem(7700, 1); //Clay water into pot
			e.getPlayer().getInventory().addItem(7692, 1);
			e.getPlayer().getInventory().addItem(7688, 1);
		} else if (e.usedWith(7691, 7712)) {
			e.getPlayer().getInventory().deleteItem(7691, 1);
			e.getPlayer().getInventory().deleteItem(7712, 1); //Porcelain water into pot
			e.getPlayer().getInventory().addItem(7704, 1);
			e.getPlayer().getInventory().addItem(7688, 1);
		} else if (e.usedWith(7691, 7724)) {
			e.getPlayer().getInventory().deleteItem(7691, 1);
			e.getPlayer().getInventory().deleteItem(7724, 1); //Gilded water into pot
			e.getPlayer().getInventory().addItem(7716, 1);
			e.getPlayer().getInventory().addItem(7688, 1);
		}
	});
}
