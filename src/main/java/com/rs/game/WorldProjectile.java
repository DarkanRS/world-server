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
//  Copyright © 2021 Trenton Kress
//  This file is part of project: Darkan
//
package com.rs.game;

import com.rs.cache.loaders.ObjectDefinitions;
import com.rs.game.object.GameObject;
import com.rs.game.player.Player;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Projectile;
import com.rs.lib.game.WorldTile;
import com.rs.lib.util.Utils;

public class WorldProjectile extends Projectile {

	public WorldProjectile(WorldTile from, WorldTile to, int spotAnimId, int startHeight, int endHeight, int startTime, int endTime, int slope, int angle, Runnable task) {
		super(from, to, spotAnimId, startHeight, endHeight, startTime, endTime, slope, angle);
		Entity fromE = from instanceof Entity e ? e : null;
		sourceId = fromE == null ? 0 : (fromE instanceof Player ? -(fromE.getIndex() + 1) : fromE.getIndex() + 1);
		Entity toE = to instanceof Entity e ? e : null;
		lockOnId = toE == null ? 0 : (toE instanceof Player ? -(toE.getIndex() + 1) : toE.getIndex() + 1);

		if (task != null)
			WorldTasks.schedule(new WorldTask() {
				@Override
				public void run() {
					task.run();
				}
			}, getTaskDelay());

		if (from instanceof Entity e)
			fromSizeX = fromSizeY = e.getSize();
		else if (from instanceof GameObject go) {
			ObjectDefinitions defs = go.getDefinitions();
			fromSizeX = defs.getSizeX();
			fromSizeY = defs.getSizeY();
		} else
			fromSizeX = fromSizeY = 1;
		if (to instanceof Entity e)
			toSizeX = toSizeY = e.getSize();
		else if (to instanceof GameObject go) {
			ObjectDefinitions defs = go.getDefinitions();
			toSizeX = defs.getSizeX();
			toSizeY = defs.getSizeY();
		} else
			toSizeX = toSizeY = 1;
		fromSizeX -= 1;
		fromSizeY -= 1;
		toSizeX -= 1;
		toSizeY -= 1;
	}

	public int getTaskDelay() {
		return Utils.projectileTimeToCycles(getEndTime()) - 1;
	}
}
