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

import com.rs.game.content.world.doors.Doors;
import com.rs.lib.game.WorldTile;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.ItemOnObjectEvent;
import com.rs.plugin.events.ObjectClickEvent;
import com.rs.plugin.handlers.ItemOnObjectHandler;
import com.rs.plugin.handlers.ObjectClickHandler;

@PluginEventHandler
public class ShadeCatacombs {

	public static ObjectClickHandler handleEntrance = new ObjectClickHandler(new Object[] { 31294, 34947 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			if (e.getObjectId() == 31294 && !e.getPlayer().getInventory().containsOneItem(Utils.range(3450, 3469)) && !e.getPlayer().getInventory().containsItem(21511)) {
				e.getPlayer().sendMessage("The door seems securely locked.");
				return;
			}
			e.getPlayer().useStairs(e.getObjectId() == 31294 ? WorldTile.of(3493, 9725, 0) : WorldTile.of(3484, 3321, 0));
		}
	};

	public static ObjectClickHandler handleDoors = new ObjectClickHandler(new Object[] { 4106, 4107, 4108, 4109 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			switch(e.getObjectId()) {
			case 4106:
				if (!e.getPlayer().getInventory().containsOneItem(Utils.range(3450, 3469)) && !e.getPlayer().getInventory().containsItem(21511)) {
					e.getPlayer().sendMessage("The door seems securely locked.");
					return;
				}
				Doors.handleDoor(e.getPlayer(), e.getObject());
				break;
			case 4107:
				if (!e.getPlayer().getInventory().containsOneItem(Utils.range(3455, 3469)) && !e.getPlayer().getInventory().containsItem(21511)) {
					e.getPlayer().sendMessage("The door seems securely locked.");
					return;
				}
				Doors.handleDoor(e.getPlayer(), e.getObject());
				break;
			case 4108:
				if (!e.getPlayer().getInventory().containsOneItem(Utils.range(3460, 3469)) && !e.getPlayer().getInventory().containsItem(21511)) {
					e.getPlayer().sendMessage("The door seems securely locked.");
					return;
				}
				Doors.handleDoor(e.getPlayer(), e.getObject());
				break;
			case 4109:
				if (!e.getPlayer().getInventory().containsOneItem(Utils.range(3465, 3469)) && !e.getPlayer().getInventory().containsItem(21511)) {
					e.getPlayer().sendMessage("The door seems securely locked.");
					return;
				}
				Doors.handleDoor(e.getPlayer(), e.getObject());
				break;
			}
		}
	};

	public static ObjectClickHandler handleChestClick = new ObjectClickHandler(new Object[] { 4111, 4112, 4113, 4114, 4115, 4116, 4117, 4118, 4119, 4120, 4121, 4122, 4123, 4124, 4125, 4126, 4127, 4128, 4129, 4130, 59731 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			e.getPlayer().sendMessage("The chest is locked.");
		}
	};

	public static ItemOnObjectHandler handleChests = new ItemOnObjectHandler(new Object[] { 4111, 4112, 4113, 4114, 4115, 4116, 4117, 4118, 4119, 4120, 4121, 4122, 4123, 4124, 4125, 4126, 4127, 4128, 4129, 4130, 59731 }) {
		@Override
		public void handle(ItemOnObjectEvent e) {
			ShadeChest chest = ShadeChest.forId(e.getObjectId());
			chest.open(e.getPlayer(), e.getObject(), e.getItem().getId());
		}
	};
}
