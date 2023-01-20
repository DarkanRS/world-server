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
package com.rs.game.content.skills.crafting;

import com.rs.game.content.skills.util.CreateActionD;
import com.rs.lib.Constants;
import com.rs.lib.game.Item;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ItemOnItemHandler;

@PluginEventHandler
public class SnakeskinCrafting  {

	private static Item[][] materials = { { new Item(6287, 6), new Item(1734) }, { new Item(6287, 8), new Item(1734) }, { new Item(6287, 5), new Item(1734) }, { new Item(6287, 12), new Item(1734) }, { new Item(6287, 15), new Item(1734) } };
	private static Item[][] products = { { new Item(6328) }, { new Item(6330) }, { new Item(6326) }, { new Item(6324) }, { new Item(6322) } };
	private static int[] reqs = { 45, 47, 48, 51, 53 };
	private static double[] xp = { 30, 35, 45, 50, 55 };
	private static int[] anims = { -1, -1, -1, -1, -1 };

	public static ItemOnItemHandler craft = new ItemOnItemHandler(6287, 1733, e -> e.getPlayer().startConversation(new CreateActionD(e.getPlayer(), materials, products, xp, anims, reqs, Constants.CRAFTING, 2)));
}
