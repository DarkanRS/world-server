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
package com.rs.game.content.world.areas.meiyerditch;

import com.rs.lib.game.Tile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.LoginHandler;
import com.rs.plugin.handlers.ObjectClickHandler;

@PluginEventHandler
public class Meiyerditch {

	public static LoginHandler unlockVars = new LoginHandler(e -> {
		e.getPlayer().getVars().setVarBit(2587, 1); //boat
		e.getPlayer().getVars().setVarBit(2589, 1); //kick down floor
	});

	public static ObjectClickHandler handleBoat = new ObjectClickHandler(false, new Object[] { 12945, 17955 }, e -> {
		if (e.getObjectId() == 12945)
			e.getPlayer().walkToAndExecute(Tile.of(3525, 3170, 0), () -> {
				e.getPlayer().faceObject(e.getObject());
				e.getPlayer().fadeScreen(() -> e.getPlayer().setNextTile(Tile.of(3605, 3163, 0)));
			});
		else
			e.getPlayer().walkToAndExecute(Tile.of(3605, 3163, 0), () -> {
				e.getPlayer().faceObject(e.getObject());
				e.getPlayer().fadeScreen(() -> e.getPlayer().setNextTile(Tile.of(3525, 3170, 0)));
			});
	});

	public static ObjectClickHandler handleRocks1 = new ObjectClickHandler(new Object[] { 17960, 17679 }, e -> {
		e.getPlayer().useLadder(e.getPlayer().transform(e.getObjectId() == 17960 ? 4 : -4, 0, e.getObjectId() == 17960 ? -1 : 1));
	});

	public static ObjectClickHandler handleFloorClimb = new ObjectClickHandler(new Object[] { 18122, 18124 }, e -> {
		e.getPlayer().useLadder(e.getObject().getTile().transform(e.getObjectId() == 18122 ? -1 : 1, 0, e.getObjectId() == 18122 ? -1 : 1));
	});

	public static ObjectClickHandler handleRubble = new ObjectClickHandler(new Object[] { 18037, 18038 }, e -> {
		e.getPlayer().useLadder(e.getObject().getTile().transform(e.getObjectId() == 18037 ? 3 : -3, 0, 0));
	});

}
