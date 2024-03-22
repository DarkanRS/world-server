@file:Suppress("DuplicatedCode")

package com.rs.engine.pathfinder.bound

import com.rs.engine.pathfinder.flag.CollisionFlag
import com.rs.engine.pathfinder.getIndexInZone
import com.rs.engine.pathfinder.getZoneIndex

internal fun reachWallDeco(
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
    srcSize == 1 && x == destX && destY == y -> true
    srcSize != 1 && destX >= x && srcSize + x - 1 >= destX &&
        destY >= y && srcSize + y - 1 >= destY -> true

    srcSize == 1 -> reachWallDeco1(
        flags,
        x,
        y,
        z,
        destX,
        destY,
        shape,
        rot
    )

    else -> reachWallDecoN(
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

private fun reachWallDeco1(
    flags: Array<IntArray?>,
    x: Int,
    y: Int,
    z: Int,
    destX: Int,
    destY: Int,
    shape: Int,
    rot: Int
): Boolean {
    if (shape in 6..7) {
        when (rot.alteredRotation(shape)) {
            0 -> {
                if (x == destX + 1 && y == destY &&
                    (flags[x, y, z] and CollisionFlag.WALL_WEST) == 0
                ) return true
                if (x == destX && y == destY - 1 &&
                    (flags[x, y, z] and CollisionFlag.WALL_NORTH) == 0
                ) return true
            }

            1 -> {
                if (x == destX - 1 && y == destY &&
                    (flags[x, y, z] and CollisionFlag.WALL_EAST) == 0
                ) return true
                if (x == destX && y == destY - 1 &&
                    (flags[x, y, z] and CollisionFlag.WALL_NORTH) == 0
                ) return true
            }

            2 -> {
                if (x == destX - 1 && y == destY &&
                    (flags[x, y, z] and CollisionFlag.WALL_EAST) == 0
                ) return true
                if (x == destX && y == destY + 1 &&
                    (flags[x, y, z] and CollisionFlag.WALL_SOUTH) == 0
                ) return true
            }

            3 -> {
                if (x == destX + 1 && y == destY &&
                    (flags[x, y, z] and CollisionFlag.WALL_WEST) == 0
                ) return true
                if (x == destX && y == destY + 1 &&
                    (flags[x, y, z] and CollisionFlag.WALL_SOUTH) == 0
                ) return true
            }
        }
    } else if (shape == 8) {
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
    return false
}

private fun reachWallDecoN(
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
    if (shape in 6..7) {
        when (rot.alteredRotation(shape)) {
            0 -> {
                if (x == destX + 1 && y <= destY && north >= destY &&
                    (flags[x, destY, z] and CollisionFlag.WALL_WEST) == 0
                ) return true
                if (x <= destX && y == destY - srcSize && east >= destX &&
                    (flags[destX, north, z] and CollisionFlag.WALL_NORTH) == 0
                ) return true
            }

            1 -> {
                if (x == destX - srcSize && y <= destY && north >= destY &&
                    (flags[east, destY, z] and CollisionFlag.WALL_EAST) == 0
                ) return true
                if (x <= destX && y == destY - srcSize && east >= destX &&
                    (flags[destX, north, z] and CollisionFlag.WALL_NORTH) == 0
                ) return true
            }

            2 -> {
                if (x == destX - srcSize && y <= destY && north >= destY &&
                    (flags[east, destY, z] and CollisionFlag.WALL_EAST) == 0
                ) return true
                if (x <= destX && y == destY + 1 && east >= destX &&
                    (flags[destX, y, z] and CollisionFlag.WALL_SOUTH) == 0
                ) return true
            }

            3 -> {
                if (x == destX + 1 && y <= destY && north >= destY &&
                    (flags[x, destY, z] and CollisionFlag.WALL_WEST) == 0
                ) return true
                if (x <= destX && y == destY + 1 && east >= destX &&
                    (flags[destX, y, z] and CollisionFlag.WALL_SOUTH) == 0
                ) return true
            }
        }
    } else if (shape == 8) {
        if (x <= destX && y == destY + 1 && east >= destX &&
            (flags[destX, y, z] and CollisionFlag.WALL_SOUTH) == 0
        ) return true
        if (x <= destX && y == destY - srcSize && east >= destX &&
            (flags[destX, north, z] and CollisionFlag.WALL_NORTH) == 0
        ) return true
        if (x == destX - srcSize && y <= destY && north >= destY &&
            (flags[east, destY, z] and CollisionFlag.WALL_EAST) == 0
        ) return true

        return x == destX + 1 && y <= destY && north >= destY &&
            (flags[x, destY, z] and CollisionFlag.WALL_WEST) == 0
    }
    return false
}

private fun Int.alteredRotation(shape: Int): Int {
    return if (shape == 7) (this + 2) and 0x3 else this
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
