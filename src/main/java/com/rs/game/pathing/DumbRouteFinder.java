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
package com.rs.game.pathing;

import java.util.ArrayDeque;
import java.util.Deque;

import com.rs.game.Entity;
import com.rs.game.World;
import com.rs.lib.game.WorldTile;

public final class DumbRouteFinder {

	public static boolean addDumbPathfinderSteps(Entity entity, WorldTile target, ClipType type) {
		return addDumbPathfinderSteps(entity, target, 25, type);
	}

	public static boolean addDumbPathfinderSteps(Entity entity, WorldTile target, int maxSize, ClipType type) {
		Deque<WorldTile> tiles = find(entity, target, maxSize, type);
		if (tiles.size() > 0) {
			WorldTile last = new WorldTile(entity);
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

	private static Deque<WorldTile> find(WorldTile origin, WorldTile target, int maxSize, ClipType type) {
		int size = origin instanceof Entity e ? e.getSize() : 1;
		WorldTile real = new WorldTile(origin);
		WorldTile curr = origin instanceof Entity e ? e.getMiddleWorldTile() : new WorldTile(origin);
		WorldTile targ = target instanceof Entity e ? e.getMiddleWorldTile() : new WorldTile(target);
		Deque<WorldTile> positions = new ArrayDeque<>(maxSize);
		while (true) {
			WorldTile from = new WorldTile(curr);
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