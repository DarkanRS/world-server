package com.rs.engine.pathfinder

/**
 * @author Kris | 16/03/2022
 */
@JvmInline
public value class ZoneCoords(public val packedCoords: Int) {
    public constructor(
        x: Int,
        y: Int,
        z: Int,
    ) : this((x and 0x7FF) or ((y and 0x7FF) shl 11) or ((z and 0x3) shl 22))

    public val x: Int
        get() = packedCoords and 0x7FF
    public val y: Int
        get() = (packedCoords shr 11) and 0x7FF
    public val z: Int
        get() = (packedCoords shr 22) and 0x3

    /**
     * Converts these zone coordinates to the absolute coordinates of the south-western
     * tile of this zone.
     */
    public fun toAbsoluteCoords(): AbsoluteCoords = AbsoluteCoords(x shl 3, y shl 3, z)
    override fun toString(): String = "ZoneCoords($x, $y, $z)"
}
