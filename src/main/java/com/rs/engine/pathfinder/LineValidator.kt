/*
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
@file:Suppress("MemberVisibilityCanBePrivate", "DuplicatedCode")

package com.rs.engine.pathfinder

import com.rs.engine.pathfinder.flag.CollisionFlag
import com.rs.engine.pathfinder.flag.CollisionFlag.BLOCK_PLAYERS
import com.rs.engine.pathfinder.flag.CollisionFlag.OBJECT_PROJECTILE_BLOCKER
import com.rs.engine.pathfinder.flag.CollisionFlag.WALL_EAST_PROJECTILE_BLOCKER
import com.rs.engine.pathfinder.flag.CollisionFlag.WALL_NORTH_PROJECTILE_BLOCKER
import com.rs.engine.pathfinder.flag.CollisionFlag.WALL_SOUTH_PROJECTILE_BLOCKER
import com.rs.engine.pathfinder.flag.CollisionFlag.WALL_WEST_PROJECTILE_BLOCKER
import kotlin.math.abs

public class LineValidator(
    public val searchMapSize: Int = DEFAULT_SEARCH_MAP_SIZE,
    private val flags: Array<IntArray?>,
) {

    public fun hasLineOfSight(
        srcX: Int,
        srcY: Int,
        z: Int,
        destX: Int,
        destY: Int,
        srcSize: Int = 1,
        destWidth: Int = 0,
        destHeight: Int = 0
    ): Boolean {
        val route = rayCast(
            srcX,
            srcY,
            z,
            destX,
            destY,
            srcSize,
            destWidth,
            destHeight,
            SIGHT_BLOCKED_WEST,
            SIGHT_BLOCKED_EAST,
            SIGHT_BLOCKED_SOUTH,
            SIGHT_BLOCKED_NORTH,
            true
        )
        return route.success
    }

    public fun hasLineOfWalk(
        srcX: Int,
        srcY: Int,
        z: Int,
        destX: Int,
        destY: Int,
        srcSize: Int = 1,
        destWidth: Int = 0,
        destHeight: Int = 0
    ): Boolean {
        val route = rayCast(
            srcX,
            srcY,
            z,
            destX,
            destY,
            srcSize,
            destWidth,
            destHeight,
            WALK_BLOCKED_WEST,
            WALK_BLOCKED_EAST,
            WALK_BLOCKED_SOUTH,
            WALK_BLOCKED_NORTH,
            false
        )
        return route.success
    }

    private fun rayCast(
        srcX: Int,
        srcY: Int,
        z: Int,
        destX: Int,
        destY: Int,
        srcSize: Int = 1,
        destWidth: Int = 0,
        destHeight: Int = 0,
        flagWest: Int,
        flagEast: Int,
        flagSouth: Int,
        flagNorth: Int,
        los: Boolean,
    ): Route {
        val halfMap = searchMapSize / 2
        val baseX = srcX - halfMap
        val baseY = srcY - halfMap
        val localSrcX = srcX - baseX
        val localSrcY = srcY - baseY
        val localDestX = destX - baseX
        val localDestY = destY - baseY

        val startX = coordinate(localSrcX, localDestX, srcSize)
        val startY = coordinate(localSrcY, localDestY, srcSize)

        if (los && flags.isFlagged(baseX, baseY, startX, startY, z, CollisionFlag.OBJECT)) {
            return FAILED_ROUTE
        }

        val endX = coordinate(localDestX, localSrcX, destWidth)
        val endY = coordinate(localDestY, localSrcY, destHeight)

        if (startX == endX && startY == endY) return SUCCESSFUL_ROUTE

        val deltaX = endX - startX
        val deltaY = endY - startY

        val travelEast = deltaX >= 0
        val travelNorth = deltaY >= 0

        var xFlags = if (travelEast) flagWest else flagEast
        var yFlags = if (travelNorth) flagSouth else flagNorth

        if (abs(deltaX) > abs(deltaY)) {
            val offsetX = if (travelEast) 1 else -1
            val offsetY = if (travelNorth) 0 else -1

            var scaledY = scaleUp(startY) + HALF_TILE + offsetY
            val tangent = scaleUp(deltaY) / abs(deltaX)

            var currX = startX
            while (currX != endX) {
                currX += offsetX
                val currY = scaleDown(scaledY)

                if (los && currX == endX && currY == endY) xFlags = xFlags and LAST_TILE_EXCLUDED_FLAGS.inv()
                if (flags.isFlagged(baseX, baseY, currX, currY, z, xFlags)) {
                    return FAILED_ROUTE
                }

                scaledY += tangent

                val nextY = scaleDown(scaledY)
                if (los && currX == endX && nextY == endY) yFlags = yFlags and LAST_TILE_EXCLUDED_FLAGS.inv()
                if (nextY != currY && flags.isFlagged(baseX, baseY, currX, nextY, z, yFlags)) {
                    return FAILED_ROUTE
                }
            }
        } else {
            val offsetX = if (travelEast) 0 else -1
            val offsetY = if (travelNorth) 1 else -1

            var scaledX = scaleUp(startX) + HALF_TILE + offsetX
            val tangent = scaleUp(deltaX) / abs(deltaY)

            var currY = startY
            while (currY != endY) {
                currY += offsetY
                val currX = scaleDown(scaledX)
                if (los && currX == endX && currY == endY) yFlags = yFlags and LAST_TILE_EXCLUDED_FLAGS.inv()
                if (flags.isFlagged(baseX, baseY, currX, currY, z, yFlags)) {
                    return FAILED_ROUTE
                }

                scaledX += tangent

                val nextX = scaleDown(scaledX)
                if (los && nextX == endX && currY == endY) xFlags = xFlags and LAST_TILE_EXCLUDED_FLAGS.inv()
                if (nextX != currX && flags.isFlagged(baseX, baseY, nextX, currY, z, xFlags)) {
                    return FAILED_ROUTE
                }
            }
        }
        return SUCCESSFUL_ROUTE
    }

    private fun coordinate(a: Int, b: Int, size: Int): Int {
        return when {
            a >= b -> a
            a + size - 1 <= b -> a + size - 1
            else -> b
        }
    }

    private fun Array<IntArray?>.isFlagged(
        baseX: Int,
        baseY: Int,
        x: Int,
        y: Int,
        z: Int,
        flags: Int
    ): Boolean {
        return (this[baseX, baseY, x, y, z] and flags) != 0
    }

    @Suppress("NOTHING_TO_INLINE")
    private inline operator fun Array<IntArray?>.get(
        baseX: Int,
        baseY: Int,
        localX: Int,
        localY: Int,
        z: Int
    ): Int {
        val x = baseX + localX
        val y = baseY + localY
        val zone = this[getZoneIndex(x, y, z)] ?: return -1
        return zone[getIndexInZone(x, y)]
    }

    @Suppress("NOTHING_TO_INLINE")
    private inline operator fun IntArray.get(x: Int, y: Int): Int {
        val index = (y * searchMapSize) + x
        return this[index]
    }

    private companion object {
        private val FAILED_ROUTE = Route(ArrayDeque(), alternative = false, success = false)
        private val SUCCESSFUL_ROUTE = Route(ArrayDeque(), alternative = false, success = true)
        private const val LAST_TILE_EXCLUDED_FLAGS = OBJECT_PROJECTILE_BLOCKER or BLOCK_PLAYERS
        private const val SIGHT_BLOCKED_NORTH = OBJECT_PROJECTILE_BLOCKER
            .or(WALL_NORTH_PROJECTILE_BLOCKER)
            .or(BLOCK_PLAYERS)
        private const val SIGHT_BLOCKED_EAST = OBJECT_PROJECTILE_BLOCKER
            .or(WALL_EAST_PROJECTILE_BLOCKER)
            .or(BLOCK_PLAYERS)
        private const val SIGHT_BLOCKED_SOUTH = OBJECT_PROJECTILE_BLOCKER
            .or(WALL_SOUTH_PROJECTILE_BLOCKER)
            .or(BLOCK_PLAYERS)
        private const val SIGHT_BLOCKED_WEST = OBJECT_PROJECTILE_BLOCKER
            .or(WALL_WEST_PROJECTILE_BLOCKER)
            .or(BLOCK_PLAYERS)

        private const val WALK_BLOCKED_NORTH = CollisionFlag.WALL_NORTH
            .or(CollisionFlag.OBJECT)
            .or(CollisionFlag.FLOOR_DECORATION)
            .or(CollisionFlag.FLOOR)
            .or(BLOCK_PLAYERS)
        private const val WALK_BLOCKED_EAST = CollisionFlag.WALL_EAST
            .or(CollisionFlag.OBJECT)
            .or(CollisionFlag.FLOOR_DECORATION)
            .or(CollisionFlag.FLOOR)
            .or(BLOCK_PLAYERS)
        private const val WALK_BLOCKED_SOUTH = CollisionFlag.WALL_SOUTH
            .or(CollisionFlag.OBJECT)
            .or(CollisionFlag.FLOOR_DECORATION)
            .or(CollisionFlag.FLOOR)
            .or(BLOCK_PLAYERS)
        private const val WALK_BLOCKED_WEST = CollisionFlag.WALL_WEST
            .or(CollisionFlag.OBJECT)
            .or(CollisionFlag.FLOOR_DECORATION)
            .or(CollisionFlag.FLOOR)
            .or(BLOCK_PLAYERS)

        private const val SCALE = 16
        private val HALF_TILE = scaleUp(tiles = 1) / 2

        private fun scaleUp(tiles: Int) = tiles shl SCALE

        private fun scaleDown(tiles: Int) = tiles ushr SCALE
    }
}
