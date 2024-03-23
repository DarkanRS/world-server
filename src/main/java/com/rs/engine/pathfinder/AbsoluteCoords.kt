package com.rs.engine.pathfinder

/**
 * @author Kris | 16/03/2022
 */
@Suppress("MemberVisibilityCanBePrivate")
@JvmInline
public value class AbsoluteCoords(public val packedCoord: Int) {
    public constructor(
        x: Int,
        y: Int,
        z: Int,
    ) : this((y and 0x3FFF) or ((x and 0x3FFF) shl 14) or ((z and 0x3) shl 28))

    public val x: Int get() = (packedCoord shr 14) and 0x3FFF
    public val y: Int get() = packedCoord and 0x3FFF
    public val z: Int get() = (packedCoord shr 28) and 0x3

    /**
     * Converts these absolute coords to the zone coords in which these absolute coords are.
     */
    public fun toZoneCoords(): ZoneCoords = ZoneCoords(x shr 3, y shr 3, z)
    override fun toString(): String = "AbsoluteCoords($x, $y, $z)"
}
