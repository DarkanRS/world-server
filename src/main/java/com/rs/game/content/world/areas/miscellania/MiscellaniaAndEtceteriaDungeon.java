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
package com.rs.game.content.world.areas.miscellania;

import com.rs.lib.game.Tile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ItemOnObjectHandler;
import com.rs.plugin.handlers.ObjectClickHandler;

@PluginEventHandler
public class MiscellaniaAndEtceteriaDungeon {

	public static ObjectClickHandler secondcreviceentrance = new ObjectClickHandler(new Object[] { 15194 }, e -> {
		e.getPlayer().setNextTile(Tile.of(2585, 10262, 0));
	});
	public static ObjectClickHandler secondcreviceexit = new ObjectClickHandler(new Object[] { 15195 }, e -> {
		e.getPlayer().setNextTile(Tile.of(2585, 10259, 0));
	});
	public static ObjectClickHandler thirdcreviceentrance = new ObjectClickHandler(new Object[] { 15196 }, e -> {
		e.getPlayer().setNextTile(Tile.of(2617, 10274, 0));
	});
	public static ObjectClickHandler thirdcreviceexit = new ObjectClickHandler(new Object[] { 15197 }, e -> {
		e.getPlayer().setNextTile(Tile.of(2617, 10271, 0));
	});
	public static ObjectClickHandler takeropecrate = new ObjectClickHandler(new Object[] { 15245 }, e -> {
		e.getPlayer().setNextTile(Tile.of(2509, 10288, 1));
	});
	public static ObjectClickHandler crawltunnelentrance = new ObjectClickHandler(new Object[] { 15188 }, e -> {
		e.getPlayer().setNextTile(Tile.of(2514, 10291, 0));
	});
	public static ObjectClickHandler crawltunnelexit = new ObjectClickHandler(new Object[] { 15189 }, e -> {
		e.getPlayer().setNextTile(Tile.of(2510, 10287, 1));
	});
	public static ItemOnObjectHandler TEMPneedropeswingrock = new ItemOnObjectHandler(new Object[] { 15252 }, new Object[] { 954 }, e -> {
		if (e.getPlayer().getX() == 2538 && e.getPlayer().getY() == 10299) {
			e.getPlayer().setNextTile(Tile.of(2543, 10299, 0));
		} else
			e.getPlayer().sendMessage("You are too far away to do this.");
	});
	public static ObjectClickHandler ropeswingexit = new ObjectClickHandler(new Object[] { 15216 }, e -> {
		e.getPlayer().setNextTile(Tile.of(2536, 10296, 0));
	});
	public static ItemOnObjectHandler TEMPlightropeup = new ItemOnObjectHandler(new Object[] { 15193 }, new Object[] { 954 }, e -> {
		e.getPlayer().setNextTile(Tile.of(2620, 3864, 0));
	});
	public static ItemOnObjectHandler TEMPlightropedown = new ItemOnObjectHandler(new Object[] { 15200 }, new Object[] { 954 }, e -> {
		e.getPlayer().setNextTile(Tile.of(2617, 10265, 0));
	});



}
