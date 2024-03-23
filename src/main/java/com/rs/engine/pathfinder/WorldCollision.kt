package com.rs.engine.pathfinder

import com.rs.cache.loaders.ObjectDefinitions
import com.rs.cache.loaders.ObjectType
import com.rs.cache.loaders.map.ClipFlag
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.`object`.GameObject
import com.rs.lib.game.Tile
import com.rs.lib.util.MapUtils

object WorldCollision {
    private const val CHUNK_SIZE = 2048 //2048 chunk size = max capacity 16384x16384 tiles
    val allFlags: Array<IntArray?> = arrayOfNulls(CHUNK_SIZE * CHUNK_SIZE * 4)
    private val LOCK = Any()

    @JvmStatic
    fun clipNPC(npc: NPC) {
        if (!npc.blocksOtherNpcs()) return
        val lastTile = if (npc.lastTile == null) npc.tile else npc.lastTile
        fillNPCClip(lastTile, npc.size, false)
        if (!npc.hasFinished()) fillNPCClip(npc.tile, npc.size, true)
    }

    @JvmStatic
    fun fillNPCClip(tile: Tile, size: Int, blocks: Boolean) {
        for (x in 0 until size) for (y in 0 until size) {
            val local = tile.transform(x, y)
            if (blocks) addClipNPC(local)
            else removeClipNPC(local)
        }
    }

    @JvmStatic
    fun getClipNPC(tile: Tile): Boolean {
        return ClipFlag.flagged(getFlags(tile), ClipFlag.BW_NPC)
    }

    @JvmStatic
    fun checkNPCClip(npc: NPC, dir: Direction): Boolean {
        val size = npc.size
        val toX = npc.x + dir.dx
        val toY = npc.y + dir.dy
        val eastMostX = npc.x + (size - 1)
        val northMostY = npc.y + (size - 1)
        for (x in toX until (toX + size)) for (y in toY until (toY + size)) {
            if (x >= npc.x && x <= eastMostX && y >= npc.y && y <= northMostY) /* stepping within itself, allow it */
                continue
            if (getClipNPC(Tile.of(x, y, npc.plane))) return false
        }
        return true
    }

    @JvmStatic
    fun clearChunk(chunkCollisionHash: Int) {
        synchronized(LOCK) {
            allFlags[chunkCollisionHash] = null
        }
    }

    @JvmStatic
    fun removeFlag(tile: Tile, vararg flags: ClipFlag) {
        var flag = 0
        for (f in flags) flag = flag or f.flag
        removeFlag(tile, flag)
    }

    @JvmStatic
    fun addFlag(tile: Tile, vararg flags: ClipFlag) {
        var flag = 0
        for (f in flags) flag = flag or f.flag
        addFlag(tile, flag)
    }

    @JvmStatic
    fun setFlags(tile: Tile, vararg flags: ClipFlag) {
        var flag = 0
        for (f in flags) flag = flag or f.flag
        setFlags(tile, flag)
    }

    @JvmStatic
    fun addBlockedTile(tile: Tile) {
        addFlag(tile, ClipFlag.PFBW_FLOOR)
    }

    @JvmStatic
    fun removeBlockedTile(tile: Tile) {
        removeFlag(tile, ClipFlag.PFBW_FLOOR)
    }

    @JvmStatic
    fun addBlockWalkAndProj(tile: Tile) {
        addFlag(tile, ClipFlag.PFBW_GROUND_DECO)
    }

    @JvmStatic
    fun removeBlockWalkAndProj(tile: Tile) {
        removeFlag(tile, ClipFlag.PFBW_GROUND_DECO)
    }

    @JvmStatic
    fun addClipNPC(tile: Tile) {
        addFlag(tile, ClipFlag.BW_NPC)
    }

    @JvmStatic
    fun removeClipNPC(tile: Tile) {
        removeFlag(tile, ClipFlag.BW_NPC)
    }

    @JvmStatic
    fun addClipPlayer(tile: Tile) {
        addFlag(tile, ClipFlag.BW_PLAYER)
    }

    @JvmStatic
    fun removeClipPlayer(tile: Tile) {
        removeFlag(tile, ClipFlag.BW_PLAYER)
    }

    @JvmStatic
    fun addObject(tile: Tile, sizeX: Int, sizeY: Int, blocksProjectiles: Boolean, pathfinder: Boolean) {
        var flag = ClipFlag.BW_FULL.flag
        if (blocksProjectiles) flag = flag or ClipFlag.BP_FULL.flag
        if (pathfinder) flag = flag or ClipFlag.PF_FULL.flag
        for (tileX in tile.x() until tile.x() + sizeX) for (tileY in tile.y() until tile.y() + sizeY) addFlag(Tile.of(tileX, tileY, tile.plane().toInt()), flag)
    }

    @JvmStatic
    fun removeObject(tile: Tile, sizeX: Int, sizeY: Int, blocksProjectiles: Boolean, pathfinder: Boolean) {
        var flag = ClipFlag.BW_FULL.flag
        if (blocksProjectiles) flag = flag or ClipFlag.BP_FULL.flag
        if (pathfinder) flag = flag or ClipFlag.PF_FULL.flag
        for (tileX in tile.x() until tile.x() + sizeX) for (tileY in tile.y() until tile.y() + sizeY) removeFlag(Tile.of(tileX, tileY, tile.plane().toInt()), flag)
    }

    @JvmStatic
    fun addWall(tile: Tile, type: ObjectType?, rotation: Int, blocksProjectiles: Boolean, pathfinder: Boolean) {
        when (type) {
            ObjectType.WALL_STRAIGHT -> {
                when (rotation) {
                    0 -> {
                        addFlag(tile, ClipFlag.blockWest(true, blocksProjectiles, pathfinder))
                        addFlag(tile.transform(-1, 0, 0), ClipFlag.blockEast(true, blocksProjectiles, pathfinder))
                    }

                    1 -> {
                        addFlag(tile, ClipFlag.blockNorth(true, blocksProjectiles, pathfinder))
                        addFlag(tile.transform(0, 1, 0), ClipFlag.blockSouth(true, blocksProjectiles, pathfinder))
                    }

                    2 -> {
                        addFlag(tile, ClipFlag.blockEast(true, blocksProjectiles, pathfinder))
                        addFlag(tile.transform(1, 0, 0), ClipFlag.blockWest(true, blocksProjectiles, pathfinder))
                    }

                    3 -> {
                        addFlag(tile, ClipFlag.blockSouth(true, blocksProjectiles, pathfinder))
                        addFlag(tile.transform(0, -1, 0), ClipFlag.blockNorth(true, blocksProjectiles, pathfinder))
                    }
                }
            }

            ObjectType.WALL_DIAGONAL_CORNER, ObjectType.WALL_STRAIGHT_CORNER -> {
                when (rotation) {
                    0 -> {
                        addFlag(tile, ClipFlag.blockNorthWest(true, blocksProjectiles, pathfinder))
                        addFlag(tile.transform(-1, 1, 0), ClipFlag.blockSouthEast(true, blocksProjectiles, pathfinder))
                    }

                    1 -> {
                        addFlag(tile, ClipFlag.blockNorthEast(true, blocksProjectiles, pathfinder))
                        addFlag(tile.transform(1, 1, 0), ClipFlag.blockSouthWest(true, blocksProjectiles, pathfinder))
                    }

                    2 -> {
                        addFlag(tile, ClipFlag.blockSouthEast(true, blocksProjectiles, pathfinder))
                        addFlag(tile.transform(1, -1, 0), ClipFlag.blockNorthWest(true, blocksProjectiles, pathfinder))
                    }

                    3 -> {
                        addFlag(tile, ClipFlag.blockSouthWest(true, blocksProjectiles, pathfinder))
                        addFlag(tile.transform(-1, -1, 0), ClipFlag.blockNorthEast(true, blocksProjectiles, pathfinder))
                    }
                }
            }

            ObjectType.WALL_WHOLE_CORNER -> {
                when (rotation) {
                    0 -> {
                        addFlag(tile, ClipFlag.blockNorth(true, blocksProjectiles, pathfinder) or ClipFlag.blockWest(true, blocksProjectiles, pathfinder))
                        addFlag(tile.transform(-1, 0, 0), ClipFlag.blockEast(true, blocksProjectiles, pathfinder))
                        addFlag(tile.transform(0, 1, 0), ClipFlag.blockSouth(true, blocksProjectiles, pathfinder))
                    }

                    1 -> {
                        addFlag(tile, ClipFlag.blockNorth(true, blocksProjectiles, pathfinder) or ClipFlag.blockEast(true, blocksProjectiles, pathfinder))
                        addFlag(tile.transform(0, 1, 0), ClipFlag.blockSouth(true, blocksProjectiles, pathfinder))
                        addFlag(tile.transform(1, 0, 0), ClipFlag.blockWest(true, blocksProjectiles, pathfinder))
                    }

                    2 -> {
                        addFlag(tile, ClipFlag.blockEast(true, blocksProjectiles, pathfinder) or ClipFlag.blockSouth(true, blocksProjectiles, pathfinder))
                        addFlag(tile.transform(1, 0, 0), ClipFlag.blockWest(true, blocksProjectiles, pathfinder))
                        addFlag(tile.transform(0, -1, 0), ClipFlag.blockNorth(true, blocksProjectiles, pathfinder))
                    }

                    3 -> {
                        addFlag(tile, ClipFlag.blockSouth(true, blocksProjectiles, pathfinder) or ClipFlag.blockWest(true, blocksProjectiles, pathfinder))
                        addFlag(tile.transform(0, -1, 0), ClipFlag.blockNorth(true, blocksProjectiles, pathfinder))
                        addFlag(tile.transform(-1, 0, 0), ClipFlag.blockEast(true, blocksProjectiles, pathfinder))
                    }
                }
            }

            else -> {}
        }
    }

    @JvmStatic
    fun removeWall(tile: Tile, type: ObjectType?, rotation: Int, blocksProjectiles: Boolean, pathfinder: Boolean) {
        when (type) {
            ObjectType.WALL_STRAIGHT -> {
                when (rotation) {
                    0 -> {
                        removeFlag(tile, ClipFlag.blockWest(true, blocksProjectiles, pathfinder))
                        removeFlag(tile.transform(-1, 0, 0), ClipFlag.blockEast(true, blocksProjectiles, pathfinder))
                    }

                    1 -> {
                        removeFlag(tile, ClipFlag.blockNorth(true, blocksProjectiles, pathfinder))
                        removeFlag(tile.transform(0, 1, 0), ClipFlag.blockSouth(true, blocksProjectiles, pathfinder))
                    }

                    2 -> {
                        removeFlag(tile, ClipFlag.blockEast(true, blocksProjectiles, pathfinder))
                        removeFlag(tile.transform(1, 0, 0), ClipFlag.blockWest(true, blocksProjectiles, pathfinder))
                    }

                    3 -> {
                        removeFlag(tile, ClipFlag.blockSouth(true, blocksProjectiles, pathfinder))
                        removeFlag(tile.transform(0, -1, 0), ClipFlag.blockNorth(true, blocksProjectiles, pathfinder))
                    }
                }
            }

            ObjectType.WALL_DIAGONAL_CORNER, ObjectType.WALL_STRAIGHT_CORNER -> {
                when (rotation) {
                    0 -> {
                        removeFlag(tile, ClipFlag.blockNorthWest(true, blocksProjectiles, pathfinder))
                        removeFlag(tile.transform(-1, 1, 0), ClipFlag.blockSouthEast(true, blocksProjectiles, pathfinder))
                    }

                    1 -> {
                        removeFlag(tile, ClipFlag.blockNorthEast(true, blocksProjectiles, pathfinder))
                        removeFlag(tile.transform(1, 1, 0), ClipFlag.blockSouthWest(true, blocksProjectiles, pathfinder))
                    }

                    2 -> {
                        removeFlag(tile, ClipFlag.blockSouthEast(true, blocksProjectiles, pathfinder))
                        removeFlag(tile.transform(1, -1, 0), ClipFlag.blockNorthWest(true, blocksProjectiles, pathfinder))
                    }

                    3 -> {
                        removeFlag(tile, ClipFlag.blockSouthWest(true, blocksProjectiles, pathfinder))
                        removeFlag(tile.transform(-1, -1, 0), ClipFlag.blockNorthEast(true, blocksProjectiles, pathfinder))
                    }
                }
            }

            ObjectType.WALL_WHOLE_CORNER -> {
                when (rotation) {
                    0 -> {
                        removeFlag(tile, ClipFlag.blockNorth(true, blocksProjectiles, pathfinder) or ClipFlag.blockWest(true, blocksProjectiles, pathfinder))
                        removeFlag(tile.transform(-1, 0, 0), ClipFlag.blockEast(true, blocksProjectiles, pathfinder))
                        removeFlag(tile.transform(0, 1, 0), ClipFlag.blockSouth(true, blocksProjectiles, pathfinder))
                    }

                    1 -> {
                        removeFlag(tile, ClipFlag.blockNorth(true, blocksProjectiles, pathfinder) or ClipFlag.blockEast(true, blocksProjectiles, pathfinder))
                        removeFlag(tile.transform(0, 1, 0), ClipFlag.blockSouth(true, blocksProjectiles, pathfinder))
                        removeFlag(tile.transform(1, 0, 0), ClipFlag.blockWest(true, blocksProjectiles, pathfinder))
                    }

                    2 -> {
                        removeFlag(tile, ClipFlag.blockEast(true, blocksProjectiles, pathfinder) or ClipFlag.blockSouth(true, blocksProjectiles, pathfinder))
                        removeFlag(tile.transform(1, 0, 0), ClipFlag.blockWest(true, blocksProjectiles, pathfinder))
                        removeFlag(tile.transform(0, -1, 0), ClipFlag.blockNorth(true, blocksProjectiles, pathfinder))
                    }

                    3 -> {
                        removeFlag(tile, ClipFlag.blockSouth(true, blocksProjectiles, pathfinder) or ClipFlag.blockWest(true, blocksProjectiles, pathfinder))
                        removeFlag(tile.transform(0, -1, 0), ClipFlag.blockNorth(true, blocksProjectiles, pathfinder))
                        removeFlag(tile.transform(-1, 0, 0), ClipFlag.blockEast(true, blocksProjectiles, pathfinder))
                    }
                }
            }

            else -> {}
        }
    }

    @JvmStatic
    fun getFlags(tile: Tile): Int {
        synchronized(LOCK) {
            val chunkId = tile.chunkId
            if (allFlags[chunkId] == null) return -1
            return allFlags[chunkId]!![tile.xInChunk or (tile.yInChunk shl 3)]
        }
    }

    @JvmStatic
    fun getFlags(x: Int, y: Int, plane: Int): Int {
        synchronized(LOCK) {
            val chunkId = MapUtils.encode(MapUtils.Structure.CHUNK, x shr 3, y shr 3, plane)
            if (allFlags[chunkId] == null) return -1
            return allFlags[chunkId]!![x and 7 or ((y and 7) shl 3)]
        }
    }

    @JvmStatic
    fun addFlag(tile: Tile, flag: Int) {
        synchronized(LOCK) {
            val chunkId = tile.chunkId
            if (allFlags[chunkId] == null) allFlags[chunkId] = IntArray(64)
            allFlags[chunkId]!![tile.xInChunk or (tile.yInChunk shl 3)] = allFlags[chunkId]!![tile.xInChunk or (tile.yInChunk shl 3)] or flag
        }
    }

    @JvmStatic
    fun removeFlag(tile: Tile, flag: Int) {
        synchronized(LOCK) {
            val chunkId = tile.chunkId
            if (allFlags[chunkId] == null) allFlags[chunkId] = IntArray(64)
            allFlags[chunkId]!![tile.xInChunk or (tile.yInChunk shl 3)] = allFlags[chunkId]!![tile.xInChunk or (tile.yInChunk shl 3)] and flag.inv()
        }
    }

    @JvmStatic
    fun setFlags(tile: Tile, flag: Int) {
        synchronized(LOCK) {
            val chunkId = tile.chunkId
            if (allFlags[chunkId] == null) allFlags[chunkId] = IntArray(64)
            allFlags[chunkId]!![tile.xInChunk or (tile.yInChunk shl 3)] = flag
        }
    }

    @JvmStatic
    fun clip(obj: GameObject) {
        if (obj.getId() == -1) return
        val type: ObjectType = obj.type
        val rotation: Int = obj.rotation

        val defs: ObjectDefinitions = ObjectDefinitions.getDefs(obj.id)

        if (defs.getClipType() == 0) return

        when (type) {
            ObjectType.WALL_STRAIGHT, ObjectType.WALL_DIAGONAL_CORNER, ObjectType.WALL_WHOLE_CORNER, ObjectType.WALL_STRAIGHT_CORNER -> addWall(obj.tile, type, rotation, defs.blocks(), !defs.ignoresPathfinder)
            ObjectType.WALL_INTERACT, ObjectType.SCENERY_INTERACT, ObjectType.GROUND_INTERACT, ObjectType.STRAIGHT_SLOPE_ROOF, ObjectType.DIAGONAL_SLOPE_ROOF, ObjectType.DIAGONAL_SLOPE_CONNECT_ROOF, ObjectType.STRAIGHT_SLOPE_CORNER_CONNECT_ROOF, ObjectType.STRAIGHT_SLOPE_CORNER_ROOF, ObjectType.STRAIGHT_FLAT_ROOF, ObjectType.STRAIGHT_BOTTOM_EDGE_ROOF, ObjectType.DIAGONAL_BOTTOM_EDGE_CONNECT_ROOF, ObjectType.STRAIGHT_BOTTOM_EDGE_CONNECT_ROOF, ObjectType.STRAIGHT_BOTTOM_EDGE_CONNECT_CORNER_ROOF -> {
                val sizeX: Int
                val sizeY: Int
                if (rotation != 1 && rotation != 3) {
                    sizeX = defs.getSizeX()
                    sizeY = defs.getSizeY()
                } else {
                    sizeX = defs.getSizeY()
                    sizeY = defs.getSizeX()
                }
                addObject(obj.tile, sizeX, sizeY, defs.blocks(), !defs.ignoresPathfinder)
            }

            ObjectType.GROUND_DECORATION -> if (defs.clipType == 1) addBlockWalkAndProj(obj.tile)
            else -> {}
        }
    }

    @JvmStatic
    fun unclip(tile: Tile) {
        setFlags(tile, 0)
    }

    @JvmStatic
    fun unclip(obj: GameObject) {
        if (obj.id == -1) // dont clip or noclip with id -1
            return
        val type: ObjectType = obj.type
        val rotation: Int = obj.rotation
        val defs: ObjectDefinitions = ObjectDefinitions.getDefs(obj.id)

        if (defs.getClipType() == 0) return

        when (type) {
            ObjectType.WALL_STRAIGHT, ObjectType.WALL_DIAGONAL_CORNER, ObjectType.WALL_WHOLE_CORNER, ObjectType.WALL_STRAIGHT_CORNER -> removeWall(obj.getTile(), type, rotation, defs.blocks(), !defs.ignoresPathfinder)
            ObjectType.WALL_INTERACT, ObjectType.SCENERY_INTERACT, ObjectType.GROUND_INTERACT, ObjectType.STRAIGHT_SLOPE_ROOF, ObjectType.DIAGONAL_SLOPE_ROOF, ObjectType.DIAGONAL_SLOPE_CONNECT_ROOF, ObjectType.STRAIGHT_SLOPE_CORNER_CONNECT_ROOF, ObjectType.STRAIGHT_SLOPE_CORNER_ROOF, ObjectType.STRAIGHT_FLAT_ROOF, ObjectType.STRAIGHT_BOTTOM_EDGE_ROOF, ObjectType.DIAGONAL_BOTTOM_EDGE_CONNECT_ROOF, ObjectType.STRAIGHT_BOTTOM_EDGE_CONNECT_ROOF, ObjectType.STRAIGHT_BOTTOM_EDGE_CONNECT_CORNER_ROOF -> {
                val sizeX: Int
                val sizeY: Int
                if (rotation == 1 || rotation == 3) {
                    sizeX = defs.getSizeY()
                    sizeY = defs.getSizeX()
                } else {
                    sizeX = defs.getSizeX()
                    sizeY = defs.getSizeY()
                }
                removeObject(obj.tile, sizeX, sizeY, defs.blocks(), !defs.ignoresPathfinder)
            }

            ObjectType.GROUND_DECORATION -> if (defs.clipType == 1) removeBlockWalkAndProj(obj.tile)
            else -> {}
        }
    }
}
