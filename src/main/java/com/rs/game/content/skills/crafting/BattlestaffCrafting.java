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
public class BattlestaffCrafting  {

	private static Item[][] materials = { { new Item(1391), new Item(571) }, { new Item(1391), new Item(575) }, { new Item(1391), new Item(569) }, { new Item(1391), new Item(573) } };
	private static Item[][] products = { { new Item(1395) }, { new Item(1399) }, { new Item(1393) }, { new Item(1397) } };
	private static int[] reqs = { 54, 58, 62, 66 };
	private static double[] xp = { 100, 112.5, 125, 137.5 };
	private static int[] anims = { 16448, 16447, 16449, 16446 };

	public static ItemOnItemHandler craftStaves = new ItemOnItemHandler(1391, new int[] { 569, 571, 573, 575 }, e -> e.getPlayer().startConversation(new CreateActionD(e.getPlayer(), materials, products, xp, anims, reqs, Constants.CRAFTING, 2)));
}