package com.rs.engine.pathfinder

import com.rs.engine.pathfinder.collision.CollisionStrategies
import com.rs.engine.pathfinder.collision.CollisionStrategy
import com.rs.engine.pathfinder.flag.CollisionFlag.BLOCK_EAST
import com.rs.engine.pathfinder.flag.CollisionFlag.BLOCK_NORTH
import com.rs.engine.pathfinder.flag.CollisionFlag.BLOCK_NORTH_AND_SOUTH_EAST
import com.rs.engine.pathfinder.flag.CollisionFlag.BLOCK_NORTH_AND_SOUTH_WEST
import com.rs.engine.pathfinder.flag.CollisionFlag.BLOCK_NORTH_EAST
import com.rs.engine.pathfinder.flag.CollisionFlag.BLOCK_NORTH_EAST_AND_WEST
import com.rs.engine.pathfinder.flag.CollisionFlag.BLOCK_NORTH_WEST
import com.rs.engine.pathfinder.flag.CollisionFlag.BLOCK_SOUTH
import com.rs.engine.pathfinder.flag.CollisionFlag.BLOCK_SOUTH_EAST
import com.rs.engine.pathfinder.flag.CollisionFlag.BLOCK_SOUTH_EAST_AND_WEST
import com.rs.engine.pathfinder.flag.CollisionFlag.BLOCK_SOUTH_WEST
import com.rs.engine.pathfinder.flag.CollisionFlag.BLOCK_WEST

/**
 * @author Kris | 16/03/2022
 */
public class StepValidator(private val flags: Array<IntArray?>) {

    public fun canTravel(
        level: Int,
        x: Int,
        y: Int,
        offsetX: Int,
        offsetY: Int,
        size: Int = 1,
        extraFlag: Int,
        collision: CollisionStrategy = CollisionStrategies.Normal,
    ): Boolean {
        val blocked = when {
            offsetX == 0 && offsetY == -1 -> isBlockedSouth(flags, level, x, y, size, extraFlag, collision)
            offsetX == 0 && offsetY == 1 -> isBlockedNorth(flags, level, x, y, size, extraFlag, collision)
            offsetX == -1 && offsetY == 0 -> isBlockedWest(flags, level, x, y, size, extraFlag, collision)
            offsetX == 1 && offsetY == 0 -> isBlockedEast(flags, level, x, y, size, extraFlag, collision)
            offsetX == -1 && offsetY == -1 -> isBlockedSouthWest(flags, level, x, y, size, extraFlag, collision)
            offsetX == -1 && offsetY == 1 -> isBlockedNorthWest(flags, level, x, y, size, extraFlag, collision)
            offsetX == 1 && offsetY == -1 -> isBlockedSouthEast(flags, level, x, y, size, extraFlag, collision)
            offsetX == 1 && offsetY == 1 -> isBlockedNorthEast(flags, level, x, y, size, extraFlag, collision)
            else -> error("Invalid offsets: $offsetX, $offsetY")
        }
        return !blocked
    }

    @Suppress("NOTHING_TO_INLINE")
    private inline operator fun Array<IntArray?>.get(x: Int, y: Int, level: Int): Int {
        val zone = this[getZoneIndex(x, y, level)] ?: return -1
        return zone[getIndexInZone(x, y)]
    }

    private fun isBlockedSouth(
        flags: Array<IntArray?>,
        level: Int,
        x: Int,
        y: Int,
        size: Int,
        extraFlag: Int,
        collision: CollisionStrategy
    ): Boolean {
        return when (size) {
            1 -> !collision.canMove(flags[x, y - 1, level], BLOCK_SOUTH or extraFlag)
            2 -> !collision.canMove(flags[x, y - 1, level], BLOCK_SOUTH_WEST or extraFlag) ||
                !collision.canMove(flags[x + 1, y - 1, level], BLOCK_SOUTH_EAST or extraFlag)
            else -> {
                if (
                    !collision.canMove(flags[x, y - 1, level], BLOCK_SOUTH_WEST or extraFlag) ||
                    !collision.canMove(flags[x + size - 1, y - 1, level], BLOCK_SOUTH_EAST or extraFlag)
                ) {
                    return true
                }
                for (midX in x + 1 until x + size - 1) {
                    if (!collision.canMove(
                            flags[midX, y - 1, level],
                            BLOCK_NORTH_EAST_AND_WEST or extraFlag
                        )
                    ) {
                        return true
                    }
                }
                return false
            }
        }
    }

    private fun isBlockedNorth(
        flags: Array<IntArray?>,
        level: Int,
        x: Int,
        y: Int,
        size: Int,
        extraFlag: Int,
        collision: CollisionStrategy
    ): Boolean {
        return when (size) {
            1 -> !collision.canMove(flags[x, y + 1, level], BLOCK_NORTH or extraFlag)
            2 -> !collision.canMove(flags[x, y + 2, level], BLOCK_NORTH_WEST or extraFlag) ||
                !collision.canMove(flags[x + 1, y + 2, level], BLOCK_NORTH_EAST or extraFlag)
            else -> {
                if (
                    !collision.canMove(flags[x, y + size, level], BLOCK_NORTH_WEST or extraFlag) ||
                    !collision.canMove(
                        flags[x + size - 1, y + size, level],
                        BLOCK_NORTH_EAST or extraFlag
                    )
                ) {
                    return true
                }
                for (midX in x + 1 until x + size - 1) {
                    if (!collision.canMove(
                            flags[midX, y + size, level],
                            BLOCK_SOUTH_EAST_AND_WEST or extraFlag
                        )
                    ) {
                        return true
                    }
                }
                return false
            }
        }
    }

    private fun isBlockedWest(
        flags: Array<IntArray?>,
        level: Int,
        x: Int,
        y: Int,
        size: Int,
        extraFlag: Int,
        collision: CollisionStrategy
    ): Boolean {
        return when (size) {
            1 -> !collision.canMove(flags[x - 1, y, level], BLOCK_WEST or extraFlag)
            2 -> !collision.canMove(flags[x - 1, y, level], BLOCK_SOUTH_WEST or extraFlag) ||
                !collision.canMove(flags[x - 1, y + 1, level], BLOCK_NORTH_WEST or extraFlag)
            else -> {
                if (
                    !collision.canMove(flags[x - 1, y, level], BLOCK_SOUTH_WEST or extraFlag) ||
                    !collision.canMove(flags[x - 1, y + size - 1, level], BLOCK_NORTH_WEST or extraFlag)
                ) {
                    return true
                }
                for (midY in y + 1 until y + size - 1) {
                    if (!collision.canMove(
                            flags[x - 1, midY, level],
                            BLOCK_NORTH_AND_SOUTH_EAST or extraFlag
                        )
                    ) {
                        return true
                    }
                }
                return false
            }
        }
    }

    private fun isBlockedEast(
        flags: Array<IntArray?>,
        level: Int,
        x: Int,
        y: Int,
        size: Int,
        extraFlag: Int,
        collision: CollisionStrategy
    ): Boolean {
        return when (size) {
            1 -> !collision.canMove(flags[x + 1, y, level], BLOCK_EAST or extraFlag)
            2 -> !collision.canMove(flags[x + 2, y, level], BLOCK_SOUTH_EAST or extraFlag) ||
                !collision.canMove(flags[x + 2, y + 1, level], BLOCK_NORTH_EAST or extraFlag)
            else -> {
                if (
                    !collision.canMove(flags[x + size, y, level], BLOCK_SOUTH_EAST or extraFlag) ||
                    !collision.canMove(
                        flags[x + size, y + size - 1, level],
                        BLOCK_NORTH_EAST or extraFlag
                    )
                ) {
                    return true
                }
                for (midY in y + 1 until y + size - 1) {
                    if (!collision.canMove(
                            flags[x + size, midY, level],
                            BLOCK_NORTH_AND_SOUTH_WEST or extraFlag
                        )
                    ) {
                        return true
                    }
                }
                return false
            }
        }
    }

    private fun isBlockedSouthWest(
        flags: Array<IntArray?>,
        level: Int,
        x: Int,
        y: Int,
        size: Int,
        extraFlag: Int,
        collision: CollisionStrategy
    ): Boolean {
        return when (size) {
            1 -> !collision.canMove(flags[x - 1, y - 1, level], BLOCK_SOUTH_WEST or extraFlag) ||
                !collision.canMove(flags[x - 1, y, level], BLOCK_WEST or extraFlag) ||
                !collision.canMove(flags[x, y - 1, level], BLOCK_SOUTH or extraFlag)
            2 -> !collision.canMove(flags[x - 1, y, level], BLOCK_NORTH_AND_SOUTH_EAST or extraFlag) ||
                !collision.canMove(flags[x - 1, y - 1, level], BLOCK_SOUTH_WEST or extraFlag) ||
                !collision.canMove(flags[x, y - 1, level], BLOCK_NORTH_EAST_AND_WEST or extraFlag)
            else -> {
                if (!collision.canMove(flags[x - 1, y - 1, level], BLOCK_SOUTH_WEST or extraFlag)) {
                    return true
                }
                for (mid in 1 until size) {
                    if (
                        !collision.canMove(
                            flags[x - 1, y + mid - 1, level],
                            BLOCK_NORTH_AND_SOUTH_EAST or extraFlag
                        ) ||
                        !collision.canMove(
                            flags[x + mid - 1, y - 1, level],
                            BLOCK_NORTH_EAST_AND_WEST or extraFlag
                        )
                    ) {
                        return true
                    }
                }
                return false
            }
        }
    }

    private fun isBlockedNorthWest(
        flags: Array<IntArray?>,
        level: Int,
        x: Int,
        y: Int,
        size: Int,
        extraFlag: Int,
        collision: CollisionStrategy
    ): Boolean {
        return when (size) {
            1 -> !collision.canMove(flags[x - 1, y + 1, level], BLOCK_NORTH_WEST or extraFlag) ||
                !collision.canMove(flags[x - 1, y, level], BLOCK_WEST or extraFlag) ||
                !collision.canMove(flags[x, y + 1, level], BLOCK_NORTH or extraFlag)
            2 -> !collision.canMove(flags[x - 1, y + 1, level], BLOCK_NORTH_AND_SOUTH_EAST or extraFlag) ||
                !collision.canMove(flags[x - 1, y + 2, level], BLOCK_NORTH_WEST or extraFlag) ||
                !collision.canMove(flags[x, y + 2, level], BLOCK_SOUTH_EAST_AND_WEST or extraFlag)
            else -> {
                if (!collision.canMove(flags[x - 1, y + size, level], BLOCK_NORTH_WEST or extraFlag)) {
                    return true
                }
                for (mid in 1 until size) {
                    if (
                        !collision.canMove(
                            flags[x - 1, y + mid, level],
                            BLOCK_NORTH_AND_SOUTH_EAST or extraFlag
                        ) ||
                        !collision.canMove(
                            flags[x + mid - 1, y + size, level],
                            BLOCK_SOUTH_EAST_AND_WEST or extraFlag
                        )
                    ) {
                        return true
                    }
                }
                return false
            }
        }
    }

    private fun isBlockedSouthEast(
        flags: Array<IntArray?>,
        level: Int,
        x: Int,
        y: Int,
        size: Int,
        extraFlag: Int,
        collision: CollisionStrategy
    ): Boolean {
        return when (size) {
            1 -> !collision.canMove(flags[x + 1, y - 1, level], BLOCK_SOUTH_EAST or extraFlag) ||
                !collision.canMove(flags[x + 1, y, level], BLOCK_EAST or extraFlag) ||
                !collision.canMove(flags[x, y - 1, level], BLOCK_SOUTH or extraFlag)
            2 -> !collision.canMove(flags[x + 1, y - 1, level], BLOCK_NORTH_EAST_AND_WEST or extraFlag) ||
                !collision.canMove(flags[x + 2, y - 1, level], BLOCK_SOUTH_EAST or extraFlag) ||
                !collision.canMove(flags[x + 2, y, level], BLOCK_NORTH_AND_SOUTH_WEST or extraFlag)
            else -> {
                if (!collision.canMove(
                        flags[x + size, y - 1, level],
                        BLOCK_SOUTH_EAST or extraFlag
                    )
                ) return true
                for (mid in 1 until size) {
                    if (
                        !collision.canMove(
                            flags[x + size, y + mid - 1, level],
                            BLOCK_NORTH_AND_SOUTH_WEST or extraFlag
                        ) ||
                        !collision.canMove(
                            flags[x + mid, y - 1, level],
                            BLOCK_NORTH_EAST_AND_WEST or extraFlag
                        )
                    ) {
                        return true
                    }
                }
                return false
            }
        }
    }

    private fun isBlockedNorthEast(
        flags: Array<IntArray?>,
        level: Int,
        x: Int,
        y: Int,
        size: Int,
        extraFlag: Int,
        collision: CollisionStrategy
    ): Boolean {
        return when (size) {
            1 -> !collision.canMove(flags[x + 1, y + 1, level], BLOCK_NORTH_EAST or extraFlag) ||
                !collision.canMove(flags[x + 1, y, level], BLOCK_EAST or extraFlag) ||
                !collision.canMove(flags[x, y + 1, level], BLOCK_NORTH or extraFlag)
            2 -> !collision.canMove(flags[x + 1, y + 2, level], BLOCK_SOUTH_EAST_AND_WEST or extraFlag) ||
                !collision.canMove(flags[x + 2, y + 2, level], BLOCK_NORTH_EAST or extraFlag) ||
                !collision.canMove(flags[x + 2, y + 1, level], BLOCK_NORTH_AND_SOUTH_WEST or extraFlag)
            else -> {
                if (!collision.canMove(flags[x + size, y + size, level], BLOCK_NORTH_EAST or extraFlag)) {
                    return true
                }
                for (mid in 1 until size) {
                    if (
                        !collision.canMove(
                            flags[x + mid, y + size, level],
                            BLOCK_SOUTH_EAST_AND_WEST or extraFlag
                        ) ||
                        !collision.canMove(
                            flags[x + size, y + mid, level],
                            BLOCK_NORTH_AND_SOUTH_WEST or extraFlag
                        )
                    ) {
                        return true
                    }
                }
                return false
            }
        }
    }
}
