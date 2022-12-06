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
package com.rs.game.model.entity.pathing;

import java.util.ArrayDeque;
import java.util.Deque;

import com.rs.game.World;
import com.rs.game.model.entity.Entity;
import com.rs.lib.game.WorldTile;
import com.rs.utils.WorldUtil;

public final class DumbRouteFinder {

	public static boolean addDumbPathfinderSteps(Entity entity, Object target, ClipType type) {
		return addDumbPathfinderSteps(entity, target, 25, type);
	}

	public static boolean addDumbPathfinderSteps(Entity entity, Object target, int maxSize, ClipType type) {
		Deque<WorldTile> tiles = find(entity, target, maxSize, type);
		if (tiles.size() > 0) {
			WorldTile last = WorldTile.of(entity.getTile());
			//World.sendSpotAnim(null, new SpotAnim(2000), last);
			for (WorldTile t : tiles) {
				//World.sendSpotAnim(null, new SpotAnim(2000), t);
				entity.addWalkStep(t.getX(), t.getY(), last.getX(), last.getY(), true);
				last = t;
			}
			return true;
		}
		return false;
	}

	private static Deque<WorldTile> find(Object origin, Object target, int maxSize, ClipType type) {
		WorldTile originTile = WorldUtil.targetToTile(origin);
		WorldTile targetTile = WorldUtil.targetToTile(target);
		int size = origin instanceof Entity e ? e.getSize() : 1;
		WorldTile real = WorldTile.of(originTile);
		WorldTile curr = origin instanceof Entity e ? e.getMiddleWorldTile() : WorldTile.of(originTile);
		WorldTile targ = target instanceof Entity e ? e.getMiddleWorldTile() : WorldTile.of(targetTile);
		Deque<WorldTile> positions = new ArrayDeque<>(maxSize);
		while (true) {
			WorldTile from = WorldTile.of(curr);
			if (curr.getX() < targ.getX() && curr.getY() < targ.getY()) {
				if (World.checkWalkStep(real, real.transform(1, 1), size, type)) {
					real = add(positions, real.transform(1, 1));
					curr = curr.transform(1, 1);
				} else if (World.checkWalkStep(real, real.transform(1, 0), size, type)) {
					real = add(positions, real.transform(1, 0));
					curr = curr.transform(1, 0);
				} else if (World.checkWalkStep(real, real.transform(0, 1), size, type)) {
					real = add(positions, real.transform(0, 1));
					curr = curr.transform(0, 1);
				}
			} else if (curr.getX() > targ.getX() && curr.getY() > targ.getY()) {
				if (World.checkWalkStep(real, real.transform(-1, -1), size, type)) {
					real = add(positions, real.transform(-1, -1));
					curr = curr.transform(-1, -1);
				} else if (World.checkWalkStep(real, real.transform(-1, 0), size, type)) {
					real = add(positions, real.transform(-1, 0));
					curr = curr.transform(-1, 0);
				} else if (World.checkWalkStep(real, real.transform(0, -1), size, type)) {
					real = add(positions, real.transform(0, -1));
					curr = curr.transform(0, -1);
				}
			} else if (curr.getX() < targ.getX() && curr.getY() > targ.getY()) {
				if (World.checkWalkStep(real, real.transform(1, -1), size, type)) {
					real = add(positions, real.transform(1, -1));
					curr = curr.transform(1, -1);
				} else if (World.checkWalkStep(real, real.transform(1, 0), size, type)) {
					real = add(positions, real.transform(1, 0));
					curr = curr.transform(1, 0);
				} else if (World.checkWalkStep(real, real.transform(0, -1), size, type)) {
					real = add(positions, real.transform(0, -1));
					curr = curr.transform(0, -1);
				}
			} else if (curr.getX() > targ.getX() && curr.getY() < targ.getY()) {
				if (World.checkWalkStep(real, real.transform(-1, 1), size, type)) {
					real = add(positions, real.transform(-1, 1));
					curr = curr.transform(-1, 1);
				} else if (World.checkWalkStep(real, real.transform(-1, 0), size, type)) {
					real = add(positions, real.transform(-1, 0));
					curr = curr.transform(-1, 0);
				} else if (World.checkWalkStep(real, real.transform(0, 1), size, type)) {
					real = add(positions, real.transform(0, 1));
					curr = curr.transform(0, 1);
				}
			} else if (curr.getX() < targ.getX()) {
				if (World.checkWalkStep(real, real.transform(1, 0), size, type)) {
					real = add(positions, real.transform(1, 0));
					curr = curr.transform(1, 0);
				}
			} else if (curr.getX() > targ.getX()) {
				if (World.checkWalkStep(real, real.transform(-1, 0), size, type)) {
					real = add(positions, real.transform(-1, 0));
					curr = curr.transform(-1, 0);
				}
			} else if (curr.getY() < targ.getY()) {
				if (World.checkWalkStep(real, real.transform(0, 1), size, type)) {
					real = add(positions, real.transform(0, 1));
					curr = curr.transform(0, 1);
				}
			} else if (curr.getY() > targ.getY())
				if (World.checkWalkStep(real, real.transform(0, -1), size, type)) {
					real = add(positions, real.transform(0, -1));
					curr = curr.transform(0, -1);
				}
			if (curr.matches(from))
				break;
		}
		return positions;
	}

	private static WorldTile add(Deque<WorldTile> positions, WorldTile att) {
		positions.add(att);
		return att;
	}
}