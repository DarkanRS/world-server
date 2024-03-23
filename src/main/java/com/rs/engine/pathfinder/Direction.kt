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

import com.rs.game.model.entity.Entity
import com.rs.game.model.entity.player.Player
import com.rs.lib.game.Tile
import com.rs.lib.util.Utils
import com.rs.lib.util.Vec2
import kotlin.math.atan2

enum class Direction(@JvmField val id: Int, @JvmField val dx: Int, @JvmField val dy: Int) {
    NORTH(0, 0, 1),
    NORTHEAST(1, 1, 1),
    EAST(2, 1, 0),
    SOUTHEAST(3, 1, -1),
    SOUTH(4, 0, -1),
    SOUTHWEST(5, -1, -1),
    WEST(6, -1, 0),
    NORTHWEST(7, -1, 1);

    val angle: Int
        get() = Utils.getAngleTo(dx, dy)

    val isDiagonal: Boolean
        get() = dx != 0 && dy != 0

    companion object {
        @JvmStatic
		fun random(): Direction {
            return entries[Utils.random(entries.size)]
        }

        @JvmStatic
		fun getById(id: Int): Direction {
            return when (id) {
                0 -> NORTH
                1 -> NORTHEAST
                2 -> EAST
                3 -> SOUTHEAST
                4 -> SOUTH
                5 -> SOUTHWEST
                6 -> WEST
                7 -> NORTHWEST
                else -> SOUTH
            }
        }

        @JvmStatic
		fun rotateClockwise(dir: Direction, rotation: Int): Direction {
            return getById((dir.id + rotation) and 0x7)
        }

        @JvmStatic
		fun fromAngle(angle: Int): Direction? {
            val delta = Utils.getDirection(angle) ?: return SOUTH
            return forDelta(delta[0].toInt(), delta[1].toInt())
        }

        @JvmStatic
		fun forDelta(dx: Int, dy: Int): Direction? {
            if (dy >= 1 && dx >= 1) return NORTHEAST
            if (dy <= -1 && dx >= 1) return SOUTHEAST
            return if (dy <= -1 && dx <= -1) SOUTHWEST
            else if (dy >= 1 && dx <= -1) NORTHWEST
            else if (dy >= 1) NORTH
            else if (dx >= 1) EAST
            else if (dy <= -1) SOUTH
            else if (dx <= -1) WEST
            else null
        }

        @JvmStatic
		fun getAngleTo(dir: Direction): Int {
            return ((atan2(-dir.dx.toDouble(), -dir.dy.toDouble()) * 2607.5945876176133).toInt()) and 0x3fff
        }

        fun getFaceDirection(faceTile: Tile, player: Player): Direction {
            if (player.x < faceTile.x) return EAST
            if (player.x > faceTile.x) return WEST
            return if (player.y < faceTile.y) NORTH
            else if (player.y > faceTile.y) SOUTH
            else NORTH
        }

        @JvmStatic
		fun getDirectionTo(entity: Entity, target: Any): Direction? {
            val from = entity.middleTileAsVector
            val to = if (target is Entity) target.middleTileAsVector else Vec2(target as Tile)
            val sub = to.sub(from)
            sub.norm()
            val delta = sub.toTile()
            return forDelta(delta.x, delta.y)
        }
    }
}
