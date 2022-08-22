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
package com.rs.game.content.world;

import com.rs.game.content.world.areas.dungeons.UndergroundDungeonController;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.game.Item;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.ItemClickEvent;
import com.rs.plugin.events.ItemOnItemEvent;
import com.rs.plugin.handlers.ItemClickHandler;
import com.rs.plugin.handlers.ItemOnItemHandler;

@PluginEventHandler
public class LightSource {

	private static final int[][] LIGHT_SOURCES = { { 596, 36, 38, 4529, 4522, 4537, 7051, 4548, 5014, 4701 }, { 594, 33, 32, 4534, 4524, 4539, 7053, 4550, 5013, 4702 } };
	
	public static ItemOnItemHandler lightTinder = new ItemOnItemHandler(LIGHT_SOURCES[0], new int[] { 590 }) {
		@Override
		public void handle(ItemOnItemEvent e) {
			lightSource(e.getPlayer(), e.getUsedWith(590).getSlot());
		}
	};
	
	public static ItemClickHandler handleExtinguish = new ItemClickHandler(LIGHT_SOURCES[1], new String[] { "Extinguish" }) {
		@Override
		public void handle(ItemClickEvent e) {
			extinguishSource(e.getPlayer(), e.getSlotId(), false);
		}
	};

	public static boolean hasExplosiveSource(Player player) {
		for (Item item : player.getInventory().getItems().array()) {
			if (item == null)
				continue;
			int slot = getSlot(item.getId(), false);
			if (slot != -1 && (slot == 0 || slot == 1))
				return true;
		}
		return false;
	}

	public static boolean hasPermenantSource(Player player) {
		if (player.getInventory().containsOneItem(14631, 14662, 14663, 19763) || player.getEquipment().containsOneItem(14631, 14662, 14663, 19763))
			return true;
		for (Item item : player.getInventory().getItems().array()) {
			if (item == null)
				continue;
			int slot = getSlot(item.getId(), false);
			if (slot != -1 && slot != 0 && slot != 1)
				return true;
		}
		return false;
	}

	public static boolean hasLightSource(Player player) {
		return hasExplosiveSource(player) || hasPermenantSource(player);
	}

	private static int getSlot(int itemId, boolean extinguished) {
		for (int slot = 0; slot < LIGHT_SOURCES[0].length; slot++) {
			int id = LIGHT_SOURCES[extinguished ? 0 : 1][slot];
			if (id == itemId)
				return slot;
		}
		return -1;
	}

	public static boolean extinguishSource(Player player, int itemSlot, boolean forceExtinguish) {
		Item item = player.getInventory().getItem(itemSlot);
		if (item == null)
			return false;
		int slot = getSlot(item.getId(), false);
		if (slot == -1)
			return false;
		if (!forceExtinguish && player.getControllerManager().getController() != null && player.getControllerManager().getController() instanceof UndergroundDungeonController) {
			player.sendMessage("You cannot extinguish the " + item.getName().toLowerCase() + " as you will not have a light source.");
			return false;
		}
		player.getInventory().replaceItem(LIGHT_SOURCES[0][slot], item.getAmount(), itemSlot);
		player.sendMessage("You extinguish the " + item.getName().toLowerCase() + ".");
		return true;
	}

	public static boolean lightSource(Player player, int itemSlot) {
		Item item = player.getInventory().getItem(itemSlot);
		if (item == null)
			return false;
		int slot = getSlot(item.getId(), true);
		if (slot == -1)
			return false;
		if (!player.getInventory().containsItem(590, 1)) {
			player.sendMessage("You need a tinderbox in order to light the " + item.getName().toLowerCase() + ".");
			return false;
		}
		player.getInventory().replaceItem(LIGHT_SOURCES[1][slot], item.getAmount(), itemSlot);
		player.sendMessage("You light the " + item.getName().toLowerCase() + ".");
		return true;
	}
}
