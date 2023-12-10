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

import com.rs.game.World;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.object.GameObject;
import com.rs.game.tasks.Task;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Item;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ItemOnObjectHandler;
import com.rs.plugin.handlers.ObjectClickHandler;

@PluginEventHandler
public class CrystalChest {

	private final static Item REWARDS[][] = {
			{ new Item(1631), new Item(1969), new Item(995, 2000) },
			{ new Item(1631) }, { new Item(1631), new Item(554, 50), new Item(556, 50), new Item(555, 50), new Item(557, 50), new Item(559, 50), new Item(558, 50), new Item(560, 10), new Item(561, 10), new Item(562, 10), new Item(563, 10), new Item(564, 10) }, { new Item(1631), new Item(2363, 10) },
			{ new Item(1631), new Item(454, 100) },
			{ new Item(1631), new Item(441, 150) },
			{ new Item(1631), new Item(1603, 2), new Item(1601, 2) },
			{ new Item(1631), new Item(371, 5), new Item(995, 1000) },
			{ new Item(1631), new Item(987), new Item(995, 750) },
			{ new Item(1631), new Item(985), new Item(995, 750) },
			{ new Item(1631), new Item(1183) },
			{ new Item(1631), new Item(1079), new Item(1093) } };


	public static ItemOnObjectHandler handleKeyUse = new ItemOnObjectHandler(new Object[] { 172 }, new Object[] { 989 }, e -> openChest(e.getPlayer(), e.getObject()));
	public static ObjectClickHandler handleChest = new ObjectClickHandler(new Object[] { 172 }, e -> openChest(e.getPlayer(), e.getObject()));

	private static void openChest(Player player, GameObject object) {
		if (player.getInventory().containsItem(989)) {
			player.getInventory().deleteItem(989, 1);
			double random = Utils.random(0, 100);
			final int reward = random <= 39.69 ? 0 : random <= 56.41 ? 1 : random <= 64.41 ? 2 : random <= 67.65 ? 3 : random <= 74.2 ? 4 : random <= 76.95 ? 5 : random <= 81.18 ? 6 : random <= 91.75 ? 7 : random <= 95.01 ? 8 : random <= 98.68 ? 9 : random <= 99.74 ? 10 : 11;
			player.setNextAnimation(new Animation(536));
			player.lock(2);
			player.sendMessage("You unlock the chest with your key.");
			WorldTasks.schedule(new Task() {
				@Override
				public void run() {
					GameObject openedChest = new GameObject(object.getId() + 1, object.getType(), object.getRotation(), object.getX(), object.getY(), object.getPlane());
					World.spawnObjectTemporary(openedChest, 1);
					player.incrementCount("Crystal chests opened");
					player.sendMessage("You find some treasure in the chest!");
					for (Item item : REWARDS[reward])
						player.getInventory().addItem(item.getId(), item.getAmount(), true);
				}
			}, 0);
		} else
			player.sendMessage("You need a crystal key to unlock this chest.");
	}
}
