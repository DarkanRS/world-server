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
package com.rs.game.content.minigames.shadesofmortton;

import com.rs.game.model.entity.player.Player;
import com.rs.game.model.object.GameObject;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Item;
import com.rs.lib.util.Utils;
import com.rs.utils.DropSets;

import java.util.HashMap;
import java.util.Map;

public enum ShadeChest {

	BRONZE(Utils.range(4111, 4115), new int[] { 4131, 10607, 10608, 10609, 10610 }, Utils.range(3450, 3454), "shade_bronze_chest"),
	STEEL(Utils.range(4116, 4120), Utils.range(10611, 10615), Utils.range(3455, 3459), "shade_steel_chest"),
	BLACK(Utils.range(4121, 4125), Utils.range(10616, 10620), Utils.range(3460, 3464), "shade_black_chest"),
	SILVER(Utils.range(4126, 4130), Utils.range(10621, 10625), Utils.range(3465, 3469), "shade_silver_chest"),
	GOLD(new int[] { 59731 }, new int[] { 59732 }, new int[] { 21511 }, "shade_gold_chest");

	private static Map<Integer, ShadeChest> OBJECT_MAP = new HashMap<>();

	static {
		for (ShadeChest chest : ShadeChest.values())
			for (int id : chest.objectIds)
				OBJECT_MAP.put(id, chest);
	}

	public static ShadeChest forId(int objectId) {
		return OBJECT_MAP.get(objectId);
	}

	private int[] objectIds;
	private Map<Integer, Integer> opened = new HashMap<>();
	private int[] keyIds;
	private String dropSet;

	private ShadeChest(int[] objectIds, int[] openedIds, int[] keyIds, String dropSet) {
		this.objectIds = objectIds;
		for (int i = 0;i < objectIds.length;i++)
			opened.put(objectIds[i], openedIds[i]);
		this.keyIds = keyIds;
		this.dropSet = dropSet;
	}

	public void open(Player player, GameObject object, int key) {
		int keyIndex = -1;
		for (int i = 0;i < keyIds.length;i++)
			if (keyIds[i] == key) {
				keyIndex = i;
				break;
			}
		if (keyIndex == -1) {
			player.sendMessage("The chest stays remarkably tight.");
			return;
		}
		player.getInventory().deleteItem(key, 1);
		player.setNextAnimation(new Animation(536));
		player.lock(2);
		object.setIdTemporary(opened.get(object.getId()), 1);
		for (Item item : DropSets.getDropSet(dropSet).createDropList().genDrop(player, 1.0 - (keyIndex * 0.05)))
			player.getInventory().addItemDrop(item);
	}
}
