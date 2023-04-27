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
package com.rs.game.content.skills.prayer.cremation;

import com.rs.game.model.entity.player.Player;
import com.rs.lib.game.Item;
import com.rs.utils.drop.Drop;
import com.rs.utils.drop.DropSet;
import com.rs.utils.drop.WeightedSet;
import com.rs.utils.drop.WeightedTable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum Corpse {
	LOAR(new int[] { 3396 }, 35,
			new WeightedSet(
					new WeightedTable(6, new Drop(995, 400, 500)),
					new WeightedTable(1, new Drop(3450)),
					new WeightedTable(1, new Drop(3451)),
					new WeightedTable(1, new Drop(3452))
					)),
	PHRIN(new int[] { 3398 }, 46.5,
			new WeightedSet(
					new WeightedTable(10, new Drop(995, 600, 700)),
					new WeightedTable(1, new Drop(3453)),
					new WeightedTable(1, new Drop(3454)),
					new WeightedTable(1, new Drop(3455)),
					new WeightedTable(1, new Drop(3456)),
					new WeightedTable(1, new Drop(3457))
					)),
	RIYL(new int[] { 3400 }, 61.5,
			new WeightedSet(
					new WeightedTable(10, new Drop(995, 700, 800)),
					new WeightedTable(1, new Drop(3458)),
					new WeightedTable(1, new Drop(3459)),
					new WeightedTable(1, new Drop(3460)),
					new WeightedTable(1, new Drop(3461)),
					new WeightedTable(1, new Drop(3462))
					)),
	ASYN(new int[] { 3402 }, 80,
			new WeightedSet(
					new WeightedTable(10, new Drop(995, 800, 900)),
					new WeightedTable(1, new Drop(3463)),
					new WeightedTable(1, new Drop(3464)),
					new WeightedTable(1, new Drop(3465)),
					new WeightedTable(1, new Drop(3466)),
					new WeightedTable(1, new Drop(3467))
					)),
	FIYR(new int[] { 3404 }, 100,
			new WeightedSet(
					new WeightedTable(20, new Drop(995, 1500, 5000)),
					new WeightedTable(2, new Drop(3465)),
					new WeightedTable(2, new Drop(3466)),
					new WeightedTable(2, new Drop(3467)),
					new WeightedTable(2, new Drop(3468)),
					new WeightedTable(2, new Drop(3469)),
					new WeightedTable(1, new Drop(21511))
					)),
	VYRE(new int[] { 21454, 21608 }, 79.5,
			new WeightedSet(
					new WeightedTable(2, new Drop(995, 600, 700)),
					new WeightedTable(1, new Drop(13158))
					));

	private static Map<Integer, Corpse> ID_MAP = new HashMap<>();

	static {
		for (Corpse corpse : Corpse.values())
			for (int id : corpse.itemIds)
				ID_MAP.put(id, corpse);
	}

	public static Corpse forId(int id) {
		return ID_MAP.get(id);
	}

	public final int[] itemIds;
	public final double xp;
	public final DropSet keyDropSet;

	private Corpse(int[] itemIds, double xp, DropSet keyDropSet) {
		this.itemIds = itemIds;
		this.xp = xp;
		this.keyDropSet = keyDropSet;
	}

	public List<Item> getKeyDrop(Player player, PyreLog log) {
		double modifier = 1.0;
		switch(log) {
		case NORMAL:
		case OAK:
			modifier -= 0.03;
			break;
		case WILLOW:
		case TEAK:
			modifier -= 0.06;
			break;
		case ARCTIC:
		case MAPLE:
			modifier -= 0.09;
			break;
		case MAHOGANY:
		case EUCALYPTUS:
			modifier -= 0.12;
			break;
		case YEW:
			modifier -= 0.15;
			break;
		case MAGIC:
			modifier -= 0.18;
			break;
		}
		return keyDropSet.getDropList().genDrop(player, modifier);
	}
}
