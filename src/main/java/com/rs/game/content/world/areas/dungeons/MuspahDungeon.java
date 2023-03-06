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

import com.rs.lib.game.Tile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ObjectClickHandler;

@PluginEventHandler
public class MuspahDungeon {

	public static ObjectClickHandler handleExit = new ObjectClickHandler(new Object[] { 42891 }, e -> {
		e.getPlayer().setNextTile(Tile.of(2736, 3729, 0));
	});

	public static ObjectClickHandler handleEntrance = new ObjectClickHandler(new Object[] { 42793 }, e -> {
		//subtract 64 from x if muspah has not escaped
		e.getPlayer().setNextTile(Tile.of(3485, 5511, 0));
	});

	public static ObjectClickHandler handleOpenings = new ObjectClickHandler(new Object[] { 42794, 42795 }, e -> {
		e.getPlayer().setNextTile(e.getPlayer().transform(0, e.getObjectId() == 42794 ? 8 : -8, 0));
	});

	public static ObjectClickHandler handleEnterIceStryke = new ObjectClickHandler(new Object[] { 48188 }, e -> {
		e.getPlayer().setNextTile(Tile.of(3435, 5646, 0));
	});

	public static ObjectClickHandler handleExitIceStryke = new ObjectClickHandler(new Object[] { 48189 }, e -> {
		e.getPlayer().setNextTile(Tile.of(3509, 5515, 0));
	});
}
