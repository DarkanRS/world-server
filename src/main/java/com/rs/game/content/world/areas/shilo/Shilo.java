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
package com.rs.game.content.world.areas.shilo;

import com.rs.game.content.skills.agility.Agility;
import com.rs.game.content.world.AgilityShortcuts;
import com.rs.game.model.entity.pathing.Direction;
import com.rs.game.model.entity.pathing.RouteEvent;
import com.rs.game.model.entity.player.Player;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.WorldObject;
import com.rs.lib.game.Tile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ObjectClickHandler;

@PluginEventHandler
public class Shilo {
	public static ObjectClickHandler handleSteppingStone = new ObjectClickHandler(false, new Object[] { 10536 }, e -> {
		if (!Agility.hasLevel(e.getPlayer(), 77)) {
			e.getPlayer().sendMessage("You need 77 agility");
			return;
		}
		Player p = e.getPlayer();
		WorldObject obj = e.getObject();
		Direction dir = Direction.NORTH;
		if(!obj.getTile().matches(Tile.of(2860, 2974, 0)))
			return;
		if(p.getY() > obj.getY())
			dir = Direction.SOUTH;

		final Direction direction = dir;
		p.setRouteEvent(new RouteEvent(direction == Direction.NORTH ? Tile.of(2860, 2971, 0) : Tile.of(2860, 2977, 0), () -> {
			AgilityShortcuts.forceMovementInstant(p, Tile.of(2860, 2974, 0), 741, 1, 0, direction);
			WorldTasks.schedule(2, () -> {
				AgilityShortcuts.forceMovementInstant(p, Tile.of(2860, 2977, 0), 741, 1, 0, direction);
				p.unlock();
			});
		}));
	});
}
