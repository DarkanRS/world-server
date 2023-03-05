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
package com.rs.game.content.skills.agility;

import com.rs.game.model.entity.pathing.Direction;
import com.rs.lib.game.Tile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ObjectClickHandler;

@PluginEventHandler
public class BurthorpeAgility  {
	public static ObjectClickHandler handleLogWalk = new ObjectClickHandler(new Object[] { 66894 }, e -> Agility.walkToAgility(e.getPlayer(), 155, Direction.NORTH, 6, 6, 5.5));
	public static ObjectClickHandler handleClimb1 = new ObjectClickHandler(new Object[] { 66912 }, e -> Agility.handleObstacle(e.getPlayer(), 15765, 7, Tile.of(2919, 3562, 1), 5.5));
	public static ObjectClickHandler handleRopeSwing = new ObjectClickHandler(false, new Object[] { 66904 }, e -> Agility.swingOnRopeSwing(e.getPlayer(), Tile.of(2912, 3562, 1), Tile.of(2916, 3562, 1), e.getObject(), 5.5));
	public static ObjectClickHandler handleMonkeyBars = new ObjectClickHandler(false, new Object[] { 66897 }, e -> Agility.crossMonkeybars(e.getPlayer(), Tile.of(2917, 3561, 1), Tile.of(2917, 3554, 1), 5.5));
	public static ObjectClickHandler handleShimmy = new ObjectClickHandler(new Object[] { 66909 }, e -> Agility.walkToAgility(e.getPlayer(), 2349, Direction.WEST, 4, 4, 5.5));
	public static ObjectClickHandler handleClimb2 = new ObjectClickHandler(new Object[] { 66902 }, e -> Agility.handleObstacle(e.getPlayer(), 15782, 2, Tile.of(2912, 3562, 1), 5.5));

	public static ObjectClickHandler handleJumpDown = new ObjectClickHandler(new Object[] { 66910 }, e -> {
		Agility.handleObstacle(e.getPlayer(), 2588, 1, Tile.of(2916, 3552, 0), 46);
		e.getPlayer().incrementCount("Burthorpe laps");
	});
}
