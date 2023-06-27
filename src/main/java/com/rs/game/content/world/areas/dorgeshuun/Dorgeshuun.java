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
package com.rs.game.content.world.areas.dorgeshuun;

import com.rs.lib.game.Tile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ObjectClickHandler;

@PluginEventHandler
public class Dorgeshuun {

	//Dorgeshuun
	public static ObjectClickHandler handleDorgeshuunboilerstairs = new ObjectClickHandler(new Object[] { 22651, 22650 }, e -> {
		if (e.getObjectId() == 22651)
			e.getPlayer().setNextTile(e.getPlayer().transform(e.getObject().getRotation() == 0 ? -0 : e.getObject().getRotation() == 3 ? -3 : 0, e.getObject().getRotation() == 0 ? -3 : e.getObject().getRotation() == 3 ? -0 : 0, -1));
		else if (e.getObjectId() == 22650)
			e.getPlayer().setNextTile(e.getPlayer().transform(e.getObject().getRotation() == 0 ? 3 : e.getObject().getRotation() == 3 ? -0 : 0, e.getObject().getRotation() == 0 ? 0 : e.getObject().getRotation() == 3 ? 3 : 0, 1));
	});
	public static ObjectClickHandler handleDorgeshuunboilerstairs2 = new ObjectClickHandler(new Object[] { 22608, 22609 }, e -> {
		if (e.getObjectId() == 22608)
			e.getPlayer().setNextTile(e.getPlayer().transform(e.getObject().getRotation() == 1 ? -0 : e.getObject().getRotation() == 0 ? -3 : 0, e.getObject().getRotation() == 1 ? -3 : e.getObject().getRotation() == 0 ? -0 : 0, 1));
		else if (e.getObjectId() == 22609)
			e.getPlayer().setNextTile(e.getPlayer().transform(e.getObject().getRotation() == 1 ? 3 : e.getObject().getRotation() == 0 ? -0 : 0, e.getObject().getRotation() == 1 ? 0 : e.getObject().getRotation() == 0 ? 3 : 0, -1));
	});

}
