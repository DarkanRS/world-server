@file:Suppress("DuplicatedCode")

package com.rs.engine.pathfinder.bound

import com.rs.engine.pathfinder.flag.CollisionFlag
import com.rs.engine.pathfinder.getIndexInZone
import com.rs.engine.pathfinder.getZoneIndex

internal fun reachWall(
    flags: Array<IntArray?>,
    x: Int,
    y: Int,
    z: Int,
    destX: Int,
    destY: Int,
    srcSize: Int,
    shape: Int,
    rot: Int
): Boolean = when {
    srcSize == 1 && x == destX && y == destY -> true
    srcSize != 1 && destX >= x && srcSize + x - 1 >= destX &&
        destY >= y && srcSize + y - 1 >= destY -> true

    srcSize == 1 -> reachWall1(
        flags,
        x,
        y,
        z,
        destX,
        destY,
        shape,
        rot
    )

    else -> reachWallN(
        flags,
        x,
        y,
        z,
        destX,
        destY,
        srcSize,
        shape,
        rot
    )
}

private fun reachWall1(
    flags: Array<IntArray?>,
    x: Int,
    y: Int,
    z: Int,
    destX: Int,
    destY: Int,
    shape: Int,
    rot: Int
): Boolean {
    when (shape) {
        0 -> {
            when (rot) {
                0 -> {
                    if (x == destX - 1 && y == destY) {
                        return true
                    }
                    if (x == destX && y == destY + 1 &&
                        (flags[x, y, z] and CollisionFlag.BLOCK_NORTH) == 0
                    ) return true
                    if (x == destX && y == destY - 1 &&
                        (flags[x, y, z] and CollisionFlag.BLOCK_SOUTH) == 0
                    ) return true
                }

                1 -> {
                    if (x == destX && y == destY + 1) {
                        return true
                    }
                    if (x == destX - 1 && y == destY &&
                        (flags[x, y, z] and CollisionFlag.BLOCK_WEST) == 0
                    ) return true
                    if (x == destX + 1 && y == destY &&
                        (flags[x, y, z] and CollisionFlag.BLOCK_EAST) == 0
                    ) return true
                }

                2 -> {
                    if (x == destX + 1 && y == destY) {
                        return true
                    }
                    if (x == destX && y == destY + 1 &&
                        (flags[x, y, z] and CollisionFlag.BLOCK_NORTH) == 0
                    ) return true
                    if (x == destX && y == destY - 1 &&
                        (flags[x, y, z] and CollisionFlag.BLOCK_SOUTH) == 0
                    ) return true
                }

                3 -> {
                    if (x == destX && y == destY - 1) {
                        return true
                    }
                    if (x == destX - 1 && y == destY &&
                        (flags[x, y, z] and CollisionFlag.BLOCK_WEST) == 0
                    ) return true
                    if (x == destX + 1 && y == destY &&
                        (flags[x, y, z] and CollisionFlag.BLOCK_EAST) == 0
                    ) return true
                }
            }
        }

        2 -> {
            when (rot) {
                0 -> {
                    if (x == destX - 1 && y == destY) {
                        return true
                    }
                    if (x == destX && y == destY + 1) {
                        return true
                    }
                    if (x == destX + 1 && y == destY &&
                        (flags[x, y, z] and CollisionFlag.BLOCK_EAST) == 0
                    ) return true
                    if (x == destX && y == destY - 1 &&
                        (flags[x, y, z] and CollisionFlag.BLOCK_SOUTH) == 0
                    ) return true
                }

                1 -> {
                    if (x == destX - 1 && y == destY &&
                        (flags[x, y, z] and CollisionFlag.BLOCK_WEST) == 0
                    ) return true
                    if (x == destX && y == destY + 1) {
                        return true
                    }
                    if (x == destX + 1 && y == destY) {
                        return true
                    }
                    if (x == destX && y == destY - 1 &&
                        (flags[x, y, z] and CollisionFlag.BLOCK_SOUTH) == 0
                    ) return true
                }

                2 -> {
                    if (x == destX - 1 && y == destY &&
                        (flags[x, y, z] and CollisionFlag.BLOCK_WEST) == 0
                    ) return true
                    if (x == destX && y == destY + 1 &&
                        (flags[x, y, z] and CollisionFlag.BLOCK_NORTH) == 0
                    ) return true
                    if (x == destX + 1 && y == destY) {
                        return true
                    }
                    if (x == destX && y == destY - 1) {
                        return true
                    }
                }

                3 -> {
                    if (x == destX - 1 && y == destY) {
                        return true
                    }
                    if (x == destX && y == destY + 1 &&
                        (flags[x, y, z] and CollisionFlag.BLOCK_NORTH) == 0
                    ) return true
                    if (x == destX + 1 && y == destY &&
                        (flags[x, y, z] and CollisionFlag.BLOCK_EAST) == 0
                    ) return true
                    if (x == destX && y == destY - 1) {
                        return true
                    }
                }
            }
        }

        9 -> {
            if (x == destX && y == destY + 1 &&
                (flags[x, y, z] and CollisionFlag.WALL_SOUTH) == 0
            ) return true
            if (x == destX && y == destY - 1 &&
                (flags[x, y, z] and CollisionFlag.WALL_NORTH) == 0
            ) return true
            if (x == destX - 1 && y == destY &&
                (flags[x, y, z] and CollisionFlag.WALL_EAST) == 0
            ) return true

            return x == destX + 1 && y == destY &&
                (flags[x, y, z] and CollisionFlag.WALL_WEST) == 0
        }
    }
    return false
}

private fun reachWallN(
    flags: Array<IntArray?>,
    x: Int,
    y: Int,
    z: Int,
    destX: Int,
    destY: Int,
    srcSize: Int,
    shape: Int,
    rot: Int
): Boolean {
    val east = x + srcSize - 1
    val north = y + srcSize - 1
    when (shape) {
        0 -> {
            when (rot) {
                0 -> {
                    if (x == destX - srcSize && y <= destY && north >= destY) {
                        return true
                    }
                    if (destX in x..east && y == destY + 1 &&
                        (flags[destX, y, z] and CollisionFlag.BLOCK_NORTH) == 0
                    ) return true
                    if (destX in x..east && y == destY - srcSize &&
                        (flags[destX, north, z] and CollisionFlag.BLOCK_SOUTH) == 0
                    ) return true
                }

                1 -> {
                    if (destX in x..east && y == destY + 1) {
                        return true
                    }
                    if (x == destX - srcSize && y <= destY && north >= destY &&
                        (flags[east, destY, z] and CollisionFlag.BLOCK_WEST) == 0
                    ) return true
                    if (x == destX + 1 && y <= destY && north >= destY &&
                        (flags[x, destY, z] and CollisionFlag.BLOCK_EAST) == 0
                    ) return true
                }

                2 -> {
                    if (x == destX + 1 && y <= destY && north >= destY) {
                        return true
                    }
                    if (destX in x..east && y == destY + 1 &&
                        (flags[destX, y, z] and CollisionFlag.BLOCK_NORTH) == 0
                    ) return true
                    if (destX in x..east && y == destY - srcSize &&
                        (flags[destX, north, z] and CollisionFlag.BLOCK_SOUTH) == 0
                    ) return true
                }

                3 -> {
                    if (destX in x..east && y == destY - srcSize) {
                        return true
                    }
                    if (x == destX - srcSize && y <= destY && north >= destY &&
                        (flags[east, destY, z] and CollisionFlag.BLOCK_WEST) == 0
                    ) return true
                    if (x == destX + 1 && y <= destY && north >= destY &&
                        (flags[x, destY, z] and CollisionFlag.BLOCK_EAST) == 0
                    ) return true
                }
            }
        }

        2 -> {
            when (rot) {
                0 -> {
                    if (x == destX - srcSize && y <= destY && north >= destY) {
                        return true
                    }
                    if (destX in x..east && y == destY + 1) {
                        return true
                    }
                    if (x == destX + 1 && y <= destY && north >= destY &&
                        (flags[x, destY, z] and CollisionFlag.BLOCK_EAST) == 0
                    ) return true
                    if (destX in x..east && y == destY - srcSize &&
                        (flags[destX, north, z] and CollisionFlag.BLOCK_SOUTH) == 0
                    ) return true
                }

                1 -> {
                    if (x == destX - srcSize && y <= destY && north >= destY &&
                        (flags[east, destY, z] and CollisionFlag.BLOCK_WEST) == 0
                    ) return true
                    if (destX in x..east && y == destY + 1) {
                        return true
                    }
                    if (x == destX + 1 && y <= destY && north >= destY) {
                        return true
                    }
                    if (destX in x..east && y == destY - srcSize &&
                        (flags[destX, north, z] and CollisionFlag.BLOCK_SOUTH) == 0
                    ) return true
                }

                2 -> {
                    if (x == destX - srcSize && y <= destY && north >= destY &&
                        (flags[east, destY, z] and CollisionFlag.BLOCK_WEST) == 0
                    ) return true
                    if (destX in x..east && y == destY + 1 &&
                        (flags[destX, y, z] and CollisionFlag.BLOCK_NORTH) == 0
                    ) return true
                    if (x == destX + 1 && y <= destY && north >= destY) {
                        return true
                    }
                    if (destX in x..east && y == destY - srcSize) {
                        return true
                    }
                }

                3 -> {
                    if (x == destX - srcSize && y <= destY && north >= destY) {
                        return true
                    }
                    if (destX in x..east && y == destY + 1 &&
                        (flags[destX, y, z] and CollisionFlag.BLOCK_NORTH) == 0
                    ) return true
                    if (x == destX + 1 && y <= destY && north >= destY &&
                        (flags[x, destY, z] and CollisionFlag.BLOCK_EAST) == 0
                    ) return true
                    if (destX in x..east && y == destY - srcSize) {
                        return true
                    }
                }
            }
        }

        9 -> {
            if (destX in x..east && y == destY + 1 &&
                (flags[destX, y, z] and CollisionFlag.BLOCK_NORTH) == 0
            ) return true
            if (destX in x..east && y == destY - srcSize &&
                (flags[destX, north, z] and CollisionFlag.BLOCK_SOUTH) == 0
            ) return true
            if (x == destX - srcSize && y <= destY && north >= destY &&
                (flags[east, destY, z] and CollisionFlag.BLOCK_WEST) == 0
            ) return true

            return x == destX + 1 && y <= destY && north >= destY &&
                (flags[x, destY, z] and CollisionFlag.BLOCK_EAST) == 0
        }
    }
    return false
}

@Suppress("NOTHING_TO_INLINE")
private inline operator fun Array<IntArray?>.get(
    x: Int,
    y: Int,
    z: Int
): Int {
    val zone = this[getZoneIndex(x, y, z)] ?: return -1
    return zone[getIndexInZone(x, y)]
}
