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
package com.rs.game.content.world.areas.dungeons;

import com.rs.lib.game.WorldTile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.ObjectClickEvent;
import com.rs.plugin.handlers.ObjectClickHandler;

@PluginEventHandler
public class UndergroundPass {

	public static ObjectClickHandler handleSkullDoorEnter = new ObjectClickHandler(new Object[] { 3220, 3221 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			e.getPlayer().useStairs(WorldTile.of(2173, 4725, 1));
		}
	};

	public static ObjectClickHandler handleSkullDoorExit = new ObjectClickHandler(new Object[] { 34288, 34289 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			e.getPlayer().useStairs(WorldTile.of(2369, 9718, 0));
		}
	};

	public static ObjectClickHandler handleWellDoorEnter = new ObjectClickHandler(new Object[] { 3333, 3334 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			e.getPlayer().useStairs(e.getPlayer().getX() < e.getObject().getX() ? WorldTile.of(2145, 4648, 1) : WorldTile.of(2014, 4712, 1));
		}
	};

}
