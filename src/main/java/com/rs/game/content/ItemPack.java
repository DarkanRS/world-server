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
package com.rs.game.content;

import com.rs.game.model.entity.player.Player;
import com.rs.lib.game.Item;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ItemClickHandler;

import java.util.HashMap;
import java.util.Map;

@PluginEventHandler
public enum ItemPack {
	VIAL_OF_WATER(15363, new Item(228, 50)),
	SHARDS(15262, new Item(12183, 5000)),
	BAKRIMNEL_BOLTS(24133, new Item(24127, 50)),
	BASKEET(15366, new Item(5377, 50)),
	SACK(15367, new Item(5419, 50)),
	EYE_OF_NEWT(15364, new Item(222, 50)),
	BIRD_MEAT(15365, new Item(9979, 50)),
	JUJU_VIAL(20047, new Item(19997, 50)),
	VIAL(15362, new Item(230, 50));

	private static Map<Integer, ItemPack> MAP = new HashMap<>();

	static {
		for (ItemPack pack : ItemPack.values())
			MAP.put(pack.itemId, pack);
	}

	private int itemId;
	private Item contents;

	private ItemPack(int itemId, Item contents) {
		this.itemId = itemId;
		this.contents = contents;
	}

	public static ItemPack forId(int itemId) {
		return MAP.get(itemId);
	}

	public int getItemId() {
		return itemId;
	}

	public Item getContents() {
		return contents;
	}

	public static ItemClickHandler onClick = new ItemClickHandler(ItemPack.MAP.keySet().toArray(), e -> {
		ItemPack pack = ItemPack.forId(e.getItem().getId());
		if (pack != null) {
			if (e.getOption().equals("Open"))
				pack.open(e.getPlayer());
			if (e.getOption().equals("Open-All"))
				pack.openAll(e.getPlayer());
		}
	});

	public void open(Player player) {
		if (player.getInventory().containsItem(itemId, 1))
			if (player.getInventory().addItem(contents))
				player.getInventory().deleteItem(itemId, 1);
	}

	public void openAll(Player player) {
		int count = player.getInventory().getNumberOf(itemId);
		if (player.getInventory().containsItem(itemId, count)) {
			player.getInventory().deleteItem(itemId, count);
			player.getInventory().addItem(new Item(contents.getId(), contents.getAmount() * count));
		}
	}
}
