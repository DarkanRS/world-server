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
package com.rs.game.content.skills.smithing;

import com.rs.game.content.quests.Quest;
import com.rs.game.content.skills.util.CreateActionD;
import com.rs.lib.Constants;
import com.rs.lib.game.Item;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.ItemOnObjectEvent;
import com.rs.plugin.handlers.ItemOnObjectHandler;

@PluginEventHandler
public class CannonBallSmelting  {

	public static int AMMO_MOULD = 4;
	public static int STEEL_BAR = 2353;

	private static Item[][] mats = { { new Item(2353) } };
	private static Item[][] prods = { { new Item(2, 4) } };
	private static int[] reqs = { 35 };
	private static double[] xp = { 25.6 };
	private static int[] anims = { 3243 }; //827, 899

	public static ItemOnObjectHandler handleCreate = new ItemOnObjectHandler(new Object[] { "Furnace" }) {
		@Override
		public void handle(ItemOnObjectEvent e) {
			if ((e.getItem().getId() == AMMO_MOULD || e.getItem().getId() == STEEL_BAR) && e.getPlayer().getInventory().containsItem(AMMO_MOULD))
				if (e.getPlayer().isQuestComplete(Quest.DWARF_CANNON, "before you can smith cannonballs."))
					e.getPlayer().startConversation(new CreateActionD(e.getPlayer(), mats, prods, xp, anims, reqs, Constants.SMITHING, 7));
		}
	};

}
