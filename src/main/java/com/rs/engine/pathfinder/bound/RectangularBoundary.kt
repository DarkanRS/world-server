package com.rs.engine.pathfinder.bound

import com.rs.engine.pathfinder.bound.RectangleBoundaryUtils

internal fun reachRectangle(
    flags: Array<IntArray?>,
    x: Int,
    y: Int,
    z: Int,
    accessBitMask: Int,
    destX: Int,
    destY: Int,
    srcSize: Int,
    destWidth: Int,
    destHeight: Int
): Boolean = when {
    srcSize > 1 -> {
        RectangleBoundaryUtils
            .collides(x, y, destX, destY, srcSize, srcSize, destWidth, destHeight) ||
            RectangleBoundaryUtils.reachRectangleN(
                flags,
                x,
                y,
                z,
                accessBitMask,
                destX,
                destY,
                srcSize,
                srcSize,
                destWidth,
                destHeight
            )
    }

    else ->
        RectangleBoundaryUtils
            .collides(x, y, destX, destY, srcSize, srcSize, destWidth, destHeight) ||
            RectangleBoundaryUtils.reachRectangle1(
                flags,
                x,
                y,
                z,
                accessBitMask,
                destX,
                destY,
                destWidth,
                destHeight
            )
}
