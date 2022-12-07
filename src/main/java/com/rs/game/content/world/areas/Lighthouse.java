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
package com.rs.game.content.world.areas;

import com.rs.game.content.world.doors.Doors;
import com.rs.lib.game.WorldTile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.ObjectClickEvent;
import com.rs.plugin.handlers.ObjectClickHandler;

@PluginEventHandler
public class Lighthouse {

	public static ObjectClickHandler handleEntranceLadders = new ObjectClickHandler(new Object[] { 4383, 4412 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			e.getPlayer().useLadder(e.getObjectId() == 4412 ? WorldTile.of(2510, 3644, 0) : WorldTile.of(2519, 9995, 1));
		}
	};

	public static ObjectClickHandler handleDoors = new ObjectClickHandler(new Object[] { 4545, 4546 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			Doors.handleDoor(e.getPlayer(), e.getObject());
		}
	};

	public static ObjectClickHandler handleLadders = new ObjectClickHandler(new Object[] { 4413, 4485 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			e.getPlayer().useLadder(e.getPlayer().transform(0, e.getObjectId() == 4485 ? 3 : -3, e.getObjectId() == 4485 ? -1 : 1));
		}
	};
}
