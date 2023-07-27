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
import com.rs.lib.net.ClientPacket;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ItemOnObjectHandler;
import com.rs.plugin.handlers.ObjectClickHandler;

@PluginEventHandler
public class SpinningWheel {

	private static Item[][] materials = { { new Item(1779) }, { new Item(10814) }, { new Item(1737) }, { new Item(9436) }, { new Item(3693) } };
	private static Item[][] products = { { new Item(1777) }, { new Item(954) }, { new Item(1759) }, { new Item(9438) }, { new Item(3694) } };
	private static int[] reqs = { 1, 1, 1, 1, 1 };
	private static double[] xp = { 15, 15, 15, 1, 1 };
	private static int[] anims = { 883, 883, 883, 883, 883 };

	public static ObjectClickHandler onClick = new ObjectClickHandler(new Object[] { "Spinning wheel" }, e -> {
		if (e.getOpNum() == ClientPacket.OBJECT_OP2)
			e.getPlayer().startConversation(new CreateActionD(e.getPlayer(), materials, products, xp, anims, reqs, Constants.CRAFTING, 2));
	});

	public static ItemOnObjectHandler handleItemOn = new ItemOnObjectHandler(new Object[] { "Spinning wheel" }, null, e -> {
		for (int i = 0; i < materials.length; i++)
			if (materials[i][0].getId() == e.getItem().getId())
				e.getPlayer().startConversation(new CreateActionD(e.getPlayer(), new Item[][] { { materials[i][0] } }, new Item[][] { { products[i][0] } }, new double[] { xp[i] }, new int[] { anims[i] }, Constants.CRAFTING, 2));
	});
}
