package com.rs.engine.pathfinder

/**
 * @author Kris | 16/03/2022
 *
 * A class to hold all the flags for every tile in the game.
 * The flags are placed into a two-dimensional array, where the outer array
 * returns the flags array for a given zone(1 x 8 x 8 flags total).
 * This is done for memory reasons, as it is rather expensive to allocate a
 * 16384 x 16384 x 4 size int array all in one go(it would also require over a
 * gigabyte of memory).
 *
 * Without any flags, this object initializes as a size 16,777,216 array of
 * int arrays, all of which are null. This consumes roughly ~67mb of memory to
 * initialize.
 */
@Suppress("NOTHING_TO_INLINE")
public class ZoneFlags {
    /**
     * A two-dimensional array to carry all the flags of the game, including instances.
     */
    public val flags: Array<IntArray?> = arrayOfNulls(TOTAL_ZONES)

    /**
     * Destroys the flags array for the zone at [zoneCoords].
     */
    public inline fun alloc(zoneCoords: ZoneCoords): IntArray {
        val packed = zoneCoords.packedCoords
        val current = flags[packed]
        if (current != null) return current
        val new = IntArray(ZONE_SIZE)
        flags[packed] = new
        return new
    }

    /**
     * Destroys the flags array for the zone at [zoneCoords].
     * It should be noted that [zoneCoords] are not absolute.
     * To convert from absolute coordinates to zone coordinates, divide the x and y values
     * each by 8(the size of one zone).
     * Example:
     * Converting absolute coordinates [3251, 9422, 1] to [zoneCoords] produces [406, 1177, 1].
     */
    public inline fun destroy(zoneCoords: ZoneCoords) {
        flags[zoneCoords.packedCoords] = null
    }

    /**
     * Gets the flag at the absolute coordinates [x, y, z], returning the [default] if the zone is not allocated.
     */
    public inline operator fun get(x: Int, y: Int, z: Int, default: Int = -1): Int {
        val zoneCoords = ZoneCoords(x shr 3, y shr 3, z)
        val array = flags[zoneCoords.packedCoords] ?: return default
        return array[zoneLocal(x, y)]
    }

    /**
     * Sets the flag at the absolute coordinates [x, y, z] to [flag].
     */
    public inline operator fun set(x: Int, y: Int, z: Int, flag: Int) {
        alloc(ZoneCoords(x shr 3, y shr 3, z))[zoneLocal(x, y)] = flag
    }

    /**
     * Adds the [flag] bits to the existing flag at the absolute coordinates [x, y, z].
     */
    public inline fun add(x: Int, y: Int, z: Int, flag: Int) {
        val flags = alloc(ZoneCoords(x shr 3, y shr 3, z))
        val index = zoneLocal(x, y)
        val cur = flags[index]
        flags[index] = cur or flag
    }

    /**
     * Removes the [flag] bits from the existing flag at the absolute coordinates [x, y, z].
     */
    public inline fun remove(x: Int, y: Int, z: Int, flag: Int) {
        val flags = alloc(ZoneCoords(x shr 3, y shr 3, z))
        val index = zoneLocal(x, y)
        val cur = flags[index]
        flags[index] = cur and flag.inv()
    }

    /**
     * Gets the flag at the absolute coordinates, returning the [default] if the zone is not allocated.
     */
    public inline operator fun get(absoluteCoords: AbsoluteCoords, default: Int = -1): Int {
        return get(absoluteCoords.x, absoluteCoords.y, absoluteCoords.z, default)
    }

    /**
     * Sets the flag at the absolute coordinates to [flag].
     */
    public inline operator fun set(absoluteCoords: AbsoluteCoords, flag: Int) {
        set(absoluteCoords.x, absoluteCoords.y, absoluteCoords.z, flag)
    }

    /**
     * Adds the [flag] bits to the existing flag at the absolute coordinates.
     */
    public inline fun add(absoluteCoords: AbsoluteCoords, flag: Int) {
        add(absoluteCoords.x, absoluteCoords.y, absoluteCoords.z, flag)
    }

    /**
     * Removes the [flag] bits from the existing flag at the absolute coordinates.
     */
    public inline fun remove(absoluteCoords: AbsoluteCoords, flag: Int) {
        remove(absoluteCoords.x, absoluteCoords.y, absoluteCoords.z, flag)
    }

    public inline fun zoneLocal(x: Int, y: Int): Int = (x and 0x7) or ((y and 0x7) shl 3)

    public companion object {
        public const val TOTAL_ZONES: Int = 2048 * 2048 * 4
        public const val ZONE_SIZE: Int = 8 * 8
    }
}
