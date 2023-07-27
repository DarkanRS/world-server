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
package com.rs.game.content.world.areas.al_kharid;

import com.rs.game.content.skills.agility.Agility;
import com.rs.game.content.world.AgilityShortcuts;
import com.rs.game.content.world.doors.Doors;
import com.rs.game.model.entity.pathing.Direction;
import com.rs.game.model.entity.player.Player;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Tile;
import com.rs.lib.game.WorldObject;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ObjectClickHandler;
import com.rs.plugin.handlers.PlayerStepHandler;

@PluginEventHandler
public class AlKharid {

	public static PlayerStepHandler shantayPass = new PlayerStepHandler(new Tile[] { Tile.of(3303, 3116, 0), Tile.of(3303, 3117, 0), Tile.of(3305, 3116, 0), Tile.of(3305, 3117, 0) }, e -> {
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
	});

	public static ObjectClickHandler clickShantayPass = new ObjectClickHandler(new Object[] { 12774 }, e -> e.getPlayer().sendMessage("Walk on through with a pass!"));

	public static ObjectClickHandler handleGates = new ObjectClickHandler(new Object[] { 35549, 35551 }, e -> {
		if (e.getPlayer().getInventory().hasCoins(10)) {
			e.getPlayer().getInventory().removeCoins(10);
			Doors.handleDoubleDoor(e.getPlayer(), e.getObject());
		} else
			e.getPlayer().sendMessage("You need 10 gold to pass through this gate.");
	});

	public static ObjectClickHandler handleStrykewyrmStile = new ObjectClickHandler(new Object[] { 48208 }, e -> {
		AgilityShortcuts.climbOver(e.getPlayer(), e.getPlayer().transform(e.getPlayer().getX() < e.getObject().getX() ? 3 : -3, 0, 0));
	});

	public static ObjectClickHandler handleMiningSiteShortcut = new ObjectClickHandler(new Object[] { 9331, 9332 }, e -> {
		if (!Agility.hasLevel(e.getPlayer(), 38)) {
			e.getPlayer().sendMessage("You need 38 agility");
			return;
		}

		Player p = e.getPlayer();
		WorldObject obj = e.getObject();

		if(obj.getTile().matches(Tile.of(3306, 3315, 0)))//above
			p.forceMove(Tile.of(3303, 3315, 0), 2050, 10, 60);
		if(obj.getTile().matches(Tile.of(3304, 3315, 0)))//below
			p.forceMove(Tile.of(3307, 3315, 0), 2049, 10, 60);
	});
	//brimhaven
	public static ObjectClickHandler handlebrimhavenstairs = new ObjectClickHandler(new Object[] { 45, 46 }, e -> {
		if (e.getObjectId() == 45)
			e.getPlayer().setNextTile(e.getPlayer().transform(e.getObject().getRotation() == 2 ? -0 : e.getObject().getRotation() == 0 ? -0 : 0, e.getObject().getRotation() == 2 ? -4 : e.getObject().getRotation() == 0 ? 4 : 0, 1));
		else if (e.getObjectId() == 46)
			e.getPlayer().setNextTile(e.getPlayer().transform(e.getObject().getRotation() == 2 ? 0 : e.getObject().getRotation() == 0 ? -0 : 0, e.getObject().getRotation() == 2 ? 4 : e.getObject().getRotation() == 0 ? -4 : 0, -1));
	});
}
