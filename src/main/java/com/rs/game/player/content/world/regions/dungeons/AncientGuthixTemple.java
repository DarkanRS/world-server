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
//  Copyright © 2021 Trenton Kress
//  This file is part of project: Darkan
//
package com.rs.game.player.content.world.regions.dungeons;

import com.rs.lib.game.WorldTile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.ObjectClickEvent;
import com.rs.plugin.handlers.ObjectClickHandler;

@PluginEventHandler
public class AncientGuthixTemple {
	
	public static ObjectClickHandler handleClimbWall = new ObjectClickHandler(false, new Object[] { 40261, 40262 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			e.getPlayer().walkToAndExecute(e.getObject().transform(0, 1, 0), new Runnable() {
				@Override
				public void run() {
					e.getPlayer().useStairs(-1, e.getPlayer().transform(0, -1, 1), 1, 1);
				}
			});
		}
	};
	
	public static ObjectClickHandler handleJumpDownWall = new ObjectClickHandler(new Object[] { 40849 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			e.getPlayer().useStairs(-1, e.getPlayer().transform(0, 1, -1), 1, 1);
		}
	};
	
	public static ObjectClickHandler handleSkullEntrance = new ObjectClickHandler(new Object[] { 48248 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			e.getPlayer().useStairs(-1, new WorldTile(2525, 5810, 0), 1, 1);
		}
	};
	
	public static ObjectClickHandler handleSkullExit = new ObjectClickHandler(new Object[] { 41077 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			e.getPlayer().useStairs(-1, new WorldTile(2527, 5830, 2), 1, 1);
		}
	};

}
