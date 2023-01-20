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
import com.rs.plugin.handlers.ObjectClickHandler;

@PluginEventHandler
public class Weaving  {

	private static Item[][] materials = { { new Item(1759, 4) }, { new Item(5931, 4) }, { new Item(5933, 6) }, { new Item(401, 5), new Item(1794) } };
	private static Item[][] products = { { new Item(3224) }, { new Item(5418) }, { new Item(5376) }, { new Item(14859) } };
	private static int[] reqs = { 10, 21, 36, 52 };
	private static double[] xp = { 12, 38, 56, 83 };
	private static int[] anims = { -1, -1, -1, -1 };

	public static ObjectClickHandler onClick = new ObjectClickHandler(new Object[] { "Loom" }, e -> e.getPlayer().startConversation(new CreateActionD(e.getPlayer(), materials, products, xp, anims, reqs, Constants.CRAFTING, 2)));
}
