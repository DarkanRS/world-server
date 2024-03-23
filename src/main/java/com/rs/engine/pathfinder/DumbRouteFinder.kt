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
package com.rs.engine.pathfinder

import com.rs.engine.pathfinder.collision.CollisionStrategy
import com.rs.game.World
import com.rs.game.model.entity.Entity
import com.rs.lib.game.Tile
import com.rs.utils.WorldUtil
import java.util.*

object DumbRouteFinder {
    @JvmStatic
	fun addDumbPathfinderSteps(entity: Entity, target: Any, collision: CollisionStrategy): Boolean {
        return addDumbPathfinderSteps(entity, target, 25, collision)
    }

    @JvmStatic
	fun addDumbPathfinderSteps(entity: Entity, target: Any, maxSize: Int, collision: CollisionStrategy): Boolean {
        val tiles = find(entity, target, maxSize, collision)
        if (tiles.size > 0) {
            var last = Tile.of(entity.tile)
            //World.sendSpotAnim(null, new SpotAnim(2000), last);
            for (t in tiles) {
                //World.sendSpotAnim(null, new SpotAnim(2000), t);
                entity.addWalkStep(t.x, t.y, last.x, last.y, true)
                last = t
            }
            return true
        }
        return false
    }

    private fun find(origin: Any, target: Any, maxSize: Int, collision: CollisionStrategy): Deque<Tile> {
        val originTile = WorldUtil.targetToTile(origin)
        val targetTile = WorldUtil.targetToTile(target)
        val size = if (origin is Entity) origin.size else 1
        var real = Tile.of(originTile)
        var curr = if (origin is Entity) origin.middleTile else Tile.of(originTile)
        val targ = if (target is Entity) target.middleTile else Tile.of(targetTile)
        val positions: Deque<Tile> = ArrayDeque(maxSize)
        val step = StepValidator(WorldCollision.allFlags)
        while (true) {
            val from = Tile.of(curr)
            if (curr.x < targ.x && curr.y < targ.y) {
                if (step.canTravel(real.plane, real.x, real.y, 1, 1, size, 0, collision)) {
                    real = add(positions, real.transform(1, 1))
                    curr = curr.transform(1, 1)
                } else if (step.canTravel(real.plane, real.x, real.y, 1, 0, size, 0, collision)) {
                    real = add(positions, real.transform(1, 0))
                    curr = curr.transform(1, 0)
                } else if (step.canTravel(real.plane, real.x, real.y, 0, 1, size, 0, collision)) {
                    real = add(positions, real.transform(0, 1))
                    curr = curr.transform(0, 1)
                }
            } else if (curr.x > targ.x && curr.y > targ.y) {
                if (step.canTravel(real.plane, real.x, real.y, -1, -1, size, 0, collision)) {
                    real = add(positions, real.transform(-1, -1))
                    curr = curr.transform(-1, -1)
                } else if (step.canTravel(real.plane, real.x, real.y, -1, 0, size, 0, collision)) {
                    real = add(positions, real.transform(-1, 0))
                    curr = curr.transform(-1, 0)
                } else if (step.canTravel(real.plane, real.x, real.y, 0, -1, size, 0, collision)) {
                    real = add(positions, real.transform(0, -1))
                    curr = curr.transform(0, -1)
                }
            } else if (curr.x < targ.x && curr.y > targ.y) {
                if (step.canTravel(real.plane, real.x, real.y, 1, -1, size, 0, collision)) {
                    real = add(positions, real.transform(1, -1))
                    curr = curr.transform(1, -1)
                } else if (step.canTravel(real.plane, real.x, real.y, 1, 0, size, 0, collision)) {
                    real = add(positions, real.transform(1, 0))
                    curr = curr.transform(1, 0)
                } else if (step.canTravel(real.plane, real.x, real.y, 0, -1, size, 0, collision)) {
                    real = add(positions, real.transform(0, -1))
                    curr = curr.transform(0, -1)
                }
            } else if (curr.x > targ.x && curr.y < targ.y) {
                if (step.canTravel(real.plane, real.x, real.y, -1, 1, size, 0, collision)) {
                    real = add(positions, real.transform(-1, 1))
                    curr = curr.transform(-1, 1)
                } else if (step.canTravel(real.plane, real.x, real.y, -1, 0, size, 0, collision)) {
                    real = add(positions, real.transform(-1, 0))
                    curr = curr.transform(-1, 0)
                } else if (step.canTravel(real.plane, real.x, real.y, 0, 1, size, 0, collision)) {
                    real = add(positions, real.transform(0, 1))
                    curr = curr.transform(0, 1)
                }
            } else if (curr.x < targ.x) {
                if (step.canTravel(real.plane, real.x, real.y, 1, 0, size, 0, collision)) {
                    real = add(positions, real.transform(1, 0))
                    curr = curr.transform(1, 0)
                }
            } else if (curr.x > targ.x) {
                if (step.canTravel(real.plane, real.x, real.y, -1, 0, size, 0, collision)) {
                    real = add(positions, real.transform(-1, 0))
                    curr = curr.transform(-1, 0)
                }
            } else if (curr.y < targ.y) {
                if (step.canTravel(real.plane, real.x, real.y, 0, 1, size, 0, collision)) {
                    real = add(positions, real.transform(0, 1))
                    curr = curr.transform(0, 1)
                }
            } else if (curr.y > targ.y) if (step.canTravel(real.plane, real.x, real.y, 0, -1, size, 0, collision)) {
                real = add(positions, real.transform(0, -1))
                curr = curr.transform(0, -1)
            }
            if (curr.matches(from)) break
        }
        return positions
    }

    private fun add(positions: Deque<Tile>, att: Tile): Tile {
        positions.add(att)
        return att
    }
}