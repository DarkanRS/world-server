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

import com.rs.game.model.entity.ForceMovement;
import com.rs.game.model.entity.pathing.RouteEvent;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.Constants;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Tile;
import com.rs.lib.util.Utils;
import com.rs.plugin.handlers.ObjectClickHandler;

public class WerewolfAgility {
	public static ObjectClickHandler handleZipLine = new ObjectClickHandler(false, new Object[] { 5139, 5140, 5141 }, e -> {
		if (!Agility.hasLevel(e.getPlayer(), 60))
			return;
		e.getPlayer().walkToAndExecute(Tile.of(3528, 9909, 0), () -> {
			WorldTasks.schedule(1, 0, new WorldTask() {
				int tick = 0;
				@Override
				public void run() {
					tick++;
				}
			});
		});
	});
}
