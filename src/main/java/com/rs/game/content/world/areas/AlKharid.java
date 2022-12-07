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

import com.rs.game.content.skills.agility.Agility;
import com.rs.game.content.world.AgilityShortcuts;
import com.rs.game.content.world.doors.Doors;
import com.rs.game.model.entity.pathing.Direction;
import com.rs.game.model.entity.player.Player;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.WorldObject;
import com.rs.lib.game.WorldTile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.ObjectClickEvent;
import com.rs.plugin.events.PlayerStepEvent;
import com.rs.plugin.handlers.ObjectClickHandler;
import com.rs.plugin.handlers.PlayerStepHandler;

@PluginEventHandler
public class AlKharid {

	public static PlayerStepHandler shantayPass = new PlayerStepHandler(WorldTile.of(3303, 3116, 0), WorldTile.of(3303, 3117, 0), WorldTile.of(3305, 3116, 0), WorldTile.of(3305, 3117, 0)) {
		@Override
		public void handle(PlayerStepEvent e) {
			if (e.getStep().getY() == 3116 && e.getStep().getDir() == Direction.SOUTH) {
				if (!e.getPlayer().getInventory().containsItem(1854, 1)) {
					e.getPlayer().sendMessage("You should check with Shantay for a pass.");
					return;
				}
				e.getPlayer().getInventory().deleteItem(1854, 1);
			}
			e.getStep().setCheckClip(false);
			e.getPlayer().setRunHidden(false);
			WorldTasks.delay(3, () -> {
				e.getPlayer().setRunHidden(true);
			});
		}
	};

	public static ObjectClickHandler clickShantayPass = new ObjectClickHandler(new Object[] { 12774 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			e.getPlayer().sendMessage("Walk on through with a pass!");
		}
	};

	public static ObjectClickHandler handleGates = new ObjectClickHandler(new Object[] { 35549, 35551 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			if (e.getPlayer().getInventory().containsItem(995, 10)) {
				e.getPlayer().getInventory().deleteItem(995, 10);
				Doors.handleDoubleDoor(e.getPlayer(), e.getObject());
			} else
				e.getPlayer().sendMessage("You need 10 gold to pass through this gate.");
		}
	};

	public static ObjectClickHandler handleStrykewyrmStile = new ObjectClickHandler(new Object[] { 48208 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			AgilityShortcuts.climbOver(e.getPlayer(), e.getPlayer().transform(e.getPlayer().getX() < e.getObject().getX() ? 3 : -3, 0, 0));
		}
	};

	public static ObjectClickHandler handleMiningSiteShortcut = new ObjectClickHandler(new Object[] { 9331, 9332 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			if (!Agility.hasLevel(e.getPlayer(), 38)) {
				e.getPlayer().sendMessage("You need 38 agility");
				return;
			}

			Player p = e.getPlayer();
			WorldObject obj = e.getObject();

			if(obj.getTile().matches(WorldTile.of(3306, 3315, 0)))//above
				AgilityShortcuts.forceMovementInstant(p, WorldTile.of(3303, 3315, 0), 2050, 1, 1, Direction.EAST);
			if(obj.getTile().matches(WorldTile.of(3304, 3315, 0)))//below
				AgilityShortcuts.forceMovementInstant(p, WorldTile.of(3307, 3315, 0), 2049, 1, 1, Direction.EAST);
		}
	};

}
