package com.rs.engine.pathfinder.reach

public interface ReachStrategy {

    public fun reached(
        flags: Array<IntArray?>,
        x: Int,
        y: Int,
        z: Int,
        destX: Int,
        destY: Int,
        destWidth: Int,
        destHeight: Int,
        srcSize: Int,
        rotation: Int,
        shape: Int,
        accessBitMask: Int,
    ): Boolean
}
