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

import com.rs.game.content.dialogues_matrix.CreateActionD;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.Constants;
import com.rs.lib.game.Item;

public class CookingCombos {

	public enum CookingCombo {
		PIE_SHELL(new Item(2315, 1), 1, new Item[] { new Item(2313, 1), new Item(1953, 1) }),
		PART_MUD_PIE(new Item[] { new Item(7164, 1), new Item(1925, 1) }, 29, new Item[] { new Item(2315, 1), new Item(6032, 1) }),
		PART_MUD_PIE2(new Item[] { new Item(7166, 1), new Item(1925, 1) }, 29, new Item[] { new Item(7164, 1), new Item(1929, 1) }),
		RAW_MUD_PIE(new Item(7168, 1), 29, new Item[] { new Item(7166, 1), new Item(434, 1) }),
		PART_GARDEN_PIE(new Item(7172, 1), 34, new Item[] { new Item(2315, 1), new Item(1982, 1) }),
		PART_GARDEN_PIE2(new Item(7174, 1), 34, new Item[] { new Item(7172, 1), new Item(1957, 1) }),
		RAW_GARDEN_PIE(new Item(7176, 1), 34, new Item[] { new Item(7174, 1), new Item(1965, 1) }),
		PART_FISH_PIE(new Item(7182, 1), 47, new Item[] { new Item(2315, 1), new Item(333, 1) }),
		PART_FISH_PIE2(new Item(7184, 1), 47, new Item[] { new Item(7182, 1), new Item(339, 1) }),
		RAW_FISH_PIE(new Item(7186, 1), 47, new Item[] { new Item(7184, 1), new Item(1942, 1) }),
		PART_ADMIRAL_PIE(new Item(7192, 1), 70, new Item[] { new Item(2315, 1), new Item(329, 1) }),
		PART_ADMIRAL_PIE2(new Item(7194, 1), 70, new Item[] { new Item(7192, 1), new Item(361, 1) }),
		RAW_ADMIRAL_PIE(new Item(7196, 1), 70, new Item[] { new Item(7194, 1), new Item(1942, 1) }),
		PART_WILD_PIE(new Item(7202, 1), 85, new Item[] { new Item(2315, 1), new Item(2136, 1) }),
		PART_WILD_PIE2(new Item(7204, 1), 85, new Item[] { new Item(7202, 1), new Item(2876, 1) }),
		RAW_WILD_PIE(new Item(7206, 1), 85, new Item[] { new Item(7204, 1), new Item(3226, 1) }),
		PART_SUMMER_PIE(new Item(7212, 1), 95, new Item[] { new Item(2315, 1), new Item(5504, 1) }),
		PART_SUMMER_PIE2(new Item(7214, 1), 95, new Item[] { new Item(7212, 1), new Item(5982, 1) }),
		RAW_SUMMER_PIE(new Item(7216, 1), 95, new Item[] { new Item(7214, 1), new Item(1955, 1) }),
		UNCOOKED_APPLE_PIE(new Item(2317, 1), 30, new Item[] { new Item(2315, 1), new Item(1955, 1) }),
		UNCOOKED_MEAT_PIE(new Item(2319, 1), 20, new Item[] { new Item(2315, 1), new Item(2140, 1) }),
		UNCOOKED_BERRY_PIE(new Item(2321, 1), 10, new Item[] { new Item(2315, 1), new Item(1951, 1) });

		private Item[] product;
		private int req;
		private Item[] materials;

		private CookingCombo(Item product, int req, Item[] materials) {
			this.product = new Item[] { product };
			this.req = req;
			this.materials = materials;
		}

		private CookingCombo(Item[] product, int req, Item[] materials) {
			this.product = product;
			this.req = req;
			this.materials = materials;
		}

		public static CookingCombo forMaterials(Item i1, Item i2) {
			for (CookingCombo c : CookingCombo.values())
				if ((c.materials[0].getId() == i1.getId() || c.materials[1].getId() == i1.getId()) && (c.materials[0].getId() == i2.getId() || c.materials[1].getId() == i2.getId()))
					return c;
			return null;
		}
	}

	public static boolean handleCombos(Player player, Item used, Item usedWith) {
		CookingCombo combo = CookingCombo.forMaterials(used, usedWith);
		if (combo != null) {
			if (player.getSkills().getLevel(Constants.COOKING) < combo.req) {
				player.sendMessage("You need a cooking level of " + combo.req + " to make that.");
				return true;
			}
			if (!player.getInventory().containsItems(combo.materials))
				return true;
			player.getDialogueManager().execute(new CreateActionD(new Item[][] { combo.materials }, new Item[][] { combo.product }, new double[] { 0.0 }, new int[] { -1 }, new int[] { combo.req }, Constants.COOKING, 0));
			return true;
		}
		return false;
	}

}
