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
package com.rs.game.player.content.skills.smithing;

import com.rs.game.player.content.minigames.ectofuntus.Ectofuntus;
import com.rs.game.player.dialogues.CreateActionD;
import com.rs.lib.Constants;
import com.rs.lib.game.Item;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.ItemOnObjectEvent;
import com.rs.plugin.handlers.ItemOnObjectHandler;

@PluginEventHandler
public class MoltenGlassMaking {

	public static int SODA_ASH = 1781;
	public static int BUCKET_OF_SAND = 1783;
	public static int MOLTEN_GLASS = 1775;

	private static Item[][] materials = { { new Item(SODA_ASH), new Item(BUCKET_OF_SAND) } };
	private static Item[][] products = { { new Item(MOLTEN_GLASS), new Item(Ectofuntus.EMPTY_BUCKET) } };
	private static int[] reqs = { 1 };
	private static double[] xp = { 20 };
	private static int[] anims = { 3243 };

	public static ItemOnObjectHandler handleCreate = new ItemOnObjectHandler(new Object[] { "Furnace" }) {
		@Override
		public void handle(ItemOnObjectEvent e) {
			if (e.getItem().getId() == SODA_ASH || e.getItem().getId() == BUCKET_OF_SAND)
				e.getPlayer().getDialogueManager().execute(new CreateActionD(materials, products, xp, anims, reqs, Constants.CRAFTING, 2));
		}
	};
}
