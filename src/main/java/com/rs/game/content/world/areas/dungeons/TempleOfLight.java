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

import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ObjectClickHandler;

@PluginEventHandler
public class TempleOfLight {

	//Temple of Light
	public static ObjectClickHandler handletempleoflightstairswide = new ObjectClickHandler(new Object[] { 10015, 10016 }, e -> {
		if (e.getObjectId() == 10015)
			e.getPlayer().setNextTile(e.getPlayer().transform(e.getObject().getRotation() == 1 ? 4 : e.getObject().getRotation() == 3 ? -4 : 0, e.getObject().getRotation() == 0 ? 4 : e.getObject().getRotation() == 2 ? -4 : 0, 1));
		else if (e.getObjectId() == 10016)
			e.getPlayer().setNextTile(e.getPlayer().transform(e.getObject().getRotation() == 1 ? -4 : e.getObject().getRotation() == 3 ? 4 : 0, e.getObject().getRotation() == 0 ? -4 : e.getObject().getRotation() == 2 ? 4 : 0, -1));
	});

	public static ObjectClickHandler handletempleoflightstairsthin = new ObjectClickHandler(new Object[] { 10018, 10017 }, e -> {
		if (e.getObjectId() == 10018)
			e.getPlayer().setNextTile(e.getPlayer().transform(e.getObject().getRotation() == 1 ? -4 : e.getObject().getRotation() == 3 ? 4 : 0, e.getObject().getRotation() == 0 ? -4 : e.getObject().getRotation() == 2 ? 4 : 0, -1));
		else if (e.getObjectId() == 10017)
			e.getPlayer().setNextTile(e.getPlayer().transform(e.getObject().getRotation() == 1 ? 4 : e.getObject().getRotation() == 3 ? -4 : 0, e.getObject().getRotation() == 0 ? 4 : e.getObject().getRotation() == 2 ? -4 : 0, 1));
	});



}
