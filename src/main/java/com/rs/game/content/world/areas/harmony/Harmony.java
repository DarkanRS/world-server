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
package com.rs.game.content.world.areas.harmony;

import com.rs.engine.quest.Quest;
import com.rs.lib.game.Tile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ObjectClickHandler;

@PluginEventHandler
public class Harmony {
	//Harmony
	public static ObjectClickHandler handleharmonystairs = new ObjectClickHandler(new Object[] { 22247, 22253 }, e -> {
		if (e.getObjectId() == 22247)
			e.getPlayer().setNextTile(e.getPlayer().transform(e.getObject().getRotation() == 1 ? 3 : e.getObject().getRotation() == 3 ? -3 : 0, e.getObject().getRotation() == 2 ? -3 : e.getObject().getRotation() == 0 ? 3 : 0,  1));
		else if (e.getObjectId() == 22253)
			e.getPlayer().setNextTile(e.getPlayer().transform(e.getObject().getRotation() == 1 ? -3 : e.getObject().getRotation() == 3 ? 3 : 0, e.getObject().getRotation() == 2 ? 3 : e.getObject().getRotation() == 0 ? -3 : 0, -1));
	});
	public static ObjectClickHandler handleharmonystairs2 = new ObjectClickHandler(new Object[] { 22248, 22254 }, e -> {
		if (e.getObjectId() == 22248)
			e.getPlayer().setNextTile(e.getPlayer().transform(e.getObject().getRotation() == 1 ? -3 : e.getObject().getRotation() == 3 ? -3 : 0, e.getObject().getRotation() == 2 ? -3 : e.getObject().getRotation() == 0 ? 3 : 0,  1));
		else if (e.getObjectId() == 22254)
			e.getPlayer().setNextTile(e.getPlayer().transform(e.getObject().getRotation() == 1 ? 3 : e.getObject().getRotation() == 3 ? 3 : 0, e.getObject().getRotation() == 2 ? 3 : e.getObject().getRotation() == 0 ? -3 : 0, -1));
	});

	public static ObjectClickHandler handlepirateshipladders = new ObjectClickHandler(new Object[] { 16945, 16946, 16947 }, e -> {
		if (e.getObjectId() == 16945)
			e.getPlayer().setNextTile(e.getPlayer().transform(e.getObject().getRotation() == 0 ? 3 : e.getObject().getRotation() == 2 ? -3 : 0, e.getObject().getRotation() == 3 ? 3 : e.getObject().getRotation() == 1 ? -3 : 0, 1));
		else if (e.getObjectId() == 16947)
			e.getPlayer().setNextTile(e.getPlayer().transform(e.getObject().getRotation() == 0 ? -3 : e.getObject().getRotation() == 2 ? 3 : 0, e.getObject().getRotation() == 3 ? -3 : e.getObject().getRotation() == 1 ? 3 : 0, -1));
		else if (e.getObjectId() == 16946)
			e.getPlayer().setNextTile(e.getPlayer().transform(e.getObject().getRotation() == 2 ? -3 : e.getObject().getRotation() == 0 ? 3 : 0, e.getObject().getRotation() == 1 ? -3 : e.getObject().getRotation() == 3 ? 3 : 0, 1));

	});



}
