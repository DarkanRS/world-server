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

import com.rs.game.content.skills.agility.Agility;
import com.rs.game.content.world.AgilityShortcuts;
import com.rs.lib.game.Tile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ObjectClickHandler;

@PluginEventHandler
public class FremennikSlayerDungeon {

	public static ObjectClickHandler handleChasm = new ObjectClickHandler(false, new Object[] { 44339 }, e -> {
		if (!Agility.hasLevel(e.getPlayer(), 81))
			return;
		final Tile toTile = e.getPlayer().getX() < 2772 ? Tile.of(2775, 10002, 0) : Tile.of(2768, 10002, 0);
		e.getPlayer().walkToAndExecute(e.getPlayer().getX() > 2772 ? Tile.of(2775, 10002, 0) : Tile.of(2768, 10002, 0), () -> {
			AgilityShortcuts.forceMovement(e.getPlayer(), toTile, 4721, 1, 1);
		});
	});

	public static ObjectClickHandler handleShortcut2 = new ObjectClickHandler(new Object[] { 9321 }, e -> {
		if (!Agility.hasLevel(e.getPlayer(), 62))
			return;
		AgilityShortcuts.forceMovement(e.getPlayer(), e.getPlayer().transform(e.getObject().getRotation() == 0 ? 5 : -5, 0), 3844, 1, 1);
	});
}
