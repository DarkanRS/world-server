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
package com.rs.game.model;

import com.rs.game.model.entity.Entity;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.object.GameObject;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Projectile;
import com.rs.lib.game.Tile;
import com.rs.lib.util.Utils;

import java.util.function.Consumer;

public class WorldProjectile extends Projectile {

	public WorldProjectile(Object from, Object to, int spotAnimId, int startHeight, int endHeight, int startDelayClientCycles, int inAirClientCycles, int offset, int angle, Consumer<WorldProjectile> task) {
		super(switch(from) {
			case Tile t -> t;
			case Entity e -> e.getMiddleTile();
			case GameObject g -> g.getTile();
			default -> throw new IllegalArgumentException("Unexpected value: " + from);
		}, from instanceof Entity e ? e.getIndex() : -1, switch(to) {
			case Tile t -> t;
			case Entity e -> e.getMiddleTile();
			case GameObject g -> g.getTile();
			default -> throw new IllegalArgumentException("Unexpected value: " + to);
		}, to instanceof Entity e ? e.getIndex() : -1, spotAnimId, startHeight, endHeight, startDelayClientCycles, inAirClientCycles, offset, angle);
		Entity fromE = from instanceof Entity e ? e : null;
		sourceId = fromE == null ? 0 : (fromE instanceof Player ? -(fromE.getIndex() + 1) : fromE.getIndex() + 1);
		Entity toE = to instanceof Entity e ? e : null;
		lockOnId = toE == null ? 0 : (toE instanceof Player ? -(toE.getIndex() + 1) : toE.getIndex() + 1);
		if (task != null)
			WorldTasks.schedule(getTaskDelay(), () -> task.accept(WorldProjectile.this));
	}

	public int getTaskDelay() {
		return Utils.clampI(Utils.projInAirToTicks(getStartDelayClientCycles(), getInAirClientCycles()), 0, Integer.MAX_VALUE);
	}
}
