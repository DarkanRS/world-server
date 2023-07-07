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
package com.rs.game.content.world.areas.ape_atoll;

import com.rs.lib.game.Tile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ObjectClickHandler;

@PluginEventHandler
public class ApeAtoll {

	//Ape Atoll
	public static ObjectClickHandler handlefallingdowncratecave = new ObjectClickHandler(new Object[] { 4714 }, e -> {
		e.getPlayer().setNextTile(Tile.of(2803, 9170, 0));
	});
	public static ObjectClickHandler handlebambooladderbridge = new ObjectClickHandler(new Object[] { 4743 }, e -> {
		e.getPlayer().setNextTile(Tile.of(2803, 2725, 0));
	});
	public static ObjectClickHandler handlebambooladder = new ObjectClickHandler(new Object[] { 4773, 4779 }, e -> {
		if (e.getObjectId() == 4773)
			e.getPlayer().setNextTile(e.getPlayer().transform(e.getObject().getRotation() == 0 ? -2 : e.getObject().getRotation() == 3 ? -0 : 0, e.getObject().getRotation() == 0 ? 0 : e.getObject().getRotation() == 3 ? -2 : 0, 1));
		else if (e.getObjectId() == 4779)
			e.getPlayer().setNextTile(e.getPlayer().transform(e.getObject().getRotation() == 0 ? 2 : e.getObject().getRotation() == 3 ? -0 : 0, e.getObject().getRotation() == 0 ? -0 : e.getObject().getRotation() == 3 ? 2 : 0, -1));
	});
	public static ObjectClickHandler handleclimbingropeup = new ObjectClickHandler(new Object[] { 4728 }, e -> {
		e.getPlayer().setNextTile(Tile.of(2765, 2768, 0));
	});

	public static ObjectClickHandler handleclimbingropeup2 = new ObjectClickHandler(new Object[] { 4889 }, e -> {
		e.getPlayer().setNextTile(Tile.of(2748, 2767, 0));
	});



}
