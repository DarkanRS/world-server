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
package com.rs.game.content.skills.farming;

import com.rs.game.model.entity.player.Player;

public class TreeSaplings {

	private static final int[] SEEDS = { 5312, 5313, 5314, 5315, 5316, 5317, 5283, 5284, 5285, 5286, 5287, 5288, 5289, 5290 };
	private static final int[] SAPLINGS = { 5370, 5371, 5372, 5373, 5374, 5375, 5496, 5497, 5498, 5499, 5500, 5501, 5502, 5503 };

	public static boolean hasSaplingRequest(Player player, int itemUsed, int itemUsedWith) {
		boolean hasSeed = false;
		for (int seedId : SEEDS)
            if (itemUsed == seedId || itemUsedWith == seedId) {
                hasSeed = true;
                break;
            }
		return hasSeed && (itemUsed == 5354 || itemUsedWith == 5354);
	}

	public static void plantSeed(Player player, int seedId, int slot) {
		int index = getIndex(seedId);
		if (index == -1)
			return;
		player.lock(1);
		player.getInventory().replaceItem(SAPLINGS[index], 1, slot);
		player.getInventory().deleteItem(seedId, 1);
	}

	private static int getIndex(int itemId) {
		for (int index = 0; index < SEEDS.length; index++)
			if (itemId == SEEDS[index])
				return index;
		return -1;
	}
}
