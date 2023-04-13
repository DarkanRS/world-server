package com.rs.game.model.entity.pathing;

import com.rs.Settings;
import com.rs.cache.Cache;
import com.rs.cache.IndexType;
import com.rs.cache.loaders.ObjectDefinitions;
import com.rs.cache.loaders.ObjectType;
import com.rs.cache.loaders.map.ClipFlag;
import com.rs.cache.loaders.map.Region;
import com.rs.game.World;
import com.rs.game.map.Chunk;
import com.rs.game.map.ChunkManager;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.object.GameObject;
import com.rs.lib.game.Tile;
import com.rs.lib.game.WorldObject;
import com.rs.lib.util.Logger;
import com.rs.lib.util.MapUtils;
import com.rs.lib.util.MapUtils.Structure;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.annotations.ServerStartupEvent;
import com.rs.plugin.annotations.ServerStartupEvent.Priority;

import static com.rs.cache.loaders.map.ClipFlag.*;

public class WorldCollision {
    private static final int CHUNK_SIZE = 2048; //2048 chunk size = max capacity 16384x16384 tiles
    private static final int[][] CLIP_FLAGS = new int[CHUNK_SIZE * CHUNK_SIZE * 4][];
    private static final Object LOCK = new Object();

    public static void clipNPC(NPC npc) {
        if (!npc.blocksOtherNpcs())
            return;
        Tile lastTile = npc.getLastTile() == null ? npc.getTile() : npc.getLastTile();
        fillNPCClip(lastTile, npc.getSize(), false);
        if (!npc.hasFinished())
            fillNPCClip(npc.getTile(), npc.getSize(), true);
    }

    public static void fillNPCClip(Tile tile, int size, boolean blocks) {
        for (int x = 0; x < size; x++)
            for (int y = 0; y < size; y++) {
                Tile local = tile.transform(x, y);
                if (blocks)
                    WorldCollision.addClipNPC(local);
                else
                    WorldCollision.removeClipNPC(local);
            }
    }

    public static boolean getClipNPC(Tile tile) {
        return ClipFlag.flagged(getFlags(tile), ClipFlag.BW_NPC);
    }

    public static boolean checkNPCClip(NPC npc, Direction dir) {
        int size = npc.getSize();
        int toX = npc.getX() + dir.getDx();
        int toY = npc.getY() + dir.getDy();
        int eastMostX = npc.getX() + (size - 1);
        int northMostY = npc.getY() + (size - 1);
        for (int x = toX; x < (toX + size); x++)
            for (int y = toY; y < (toY + size); y++) {
                if (x >= npc.getX() && x <= eastMostX && y >= npc.getY() && y <= northMostY)
                    /* stepping within itself, allow it */
                    continue;
                if (getClipNPC(Tile.of(x, y, npc.getPlane())))
                    return false;
            }
        return true;
    }

    public static void clearChunk(int chunkCollisionHash) {
        synchronized (LOCK) {
            CLIP_FLAGS[chunkCollisionHash] = null;
        }
    }

    public static void removeFlag(Tile tile, ClipFlag... flags) {
        int flag = 0;
        for (ClipFlag f : flags)
            flag |= f.flag;
        removeFlag(tile, flag);
    }

    public static void addFlag(Tile tile, ClipFlag... flags) {
        int flag = 0;
        for (ClipFlag f : flags)
            flag |= f.flag;
        addFlag(tile, flag);
    }

    public static void setFlags(Tile tile, ClipFlag... flags) {
        int flag = 0;
        for (ClipFlag f : flags)
            flag |= f.flag;
        setFlags(tile, flag);
    }

    public static void addBlockedTile(Tile tile) {
        addFlag(tile, ClipFlag.PFBW_FLOOR);
    }

    public static void removeBlockedTile(Tile tile) {
        removeFlag(tile, ClipFlag.PFBW_FLOOR);
    }

    public static void addBlockWalkAndProj(Tile tile) {
        addFlag(tile, ClipFlag.PFBW_GROUND_DECO);
    }

    public static void removeBlockWalkAndProj(Tile tile) {
        removeFlag(tile, ClipFlag.PFBW_GROUND_DECO);
    }

    public static void addClipNPC(Tile tile) {
        addFlag(tile, ClipFlag.BW_NPC);
    }

    public static void removeClipNPC(Tile tile) {
        removeFlag(tile, ClipFlag.BW_NPC);
    }

    public static void addClipPlayer(Tile tile) {
        addFlag(tile, ClipFlag.BW_PLAYER);
    }

    public static void removeClipPlayer(Tile tile) {
        removeFlag(tile, ClipFlag.BW_PLAYER);
    }

    public static void addObject(Tile tile, int sizeX, int sizeY, boolean blocksProjectiles, boolean pathfinder) {
        int flag = ClipFlag.BW_FULL.flag;
        if (blocksProjectiles)
            flag |= ClipFlag.BP_FULL.flag;
        if (pathfinder)
            flag |= ClipFlag.PF_FULL.flag;
        for (int tileX = tile.x(); tileX < tile.x() + sizeX; tileX++)
            for (int tileY = tile.y(); tileY < tile.y() + sizeY; tileY++)
                addFlag(Tile.of(tileX, tileY, tile.plane()), flag);
    }

    public static void removeObject(Tile tile, int sizeX, int sizeY, boolean blocksProjectiles, boolean pathfinder) {
        int flag = ClipFlag.BW_FULL.flag;
        if (blocksProjectiles)
            flag |= ClipFlag.BP_FULL.flag;
        if (pathfinder)
            flag |= ClipFlag.PF_FULL.flag;
        for (int tileX = tile.x(); tileX < tile.x() + sizeX; tileX++)
            for (int tileY = tile.y(); tileY < tile.y() + sizeY; tileY++)
                removeFlag(Tile.of(tileX, tileY, tile.plane()), flag);
    }

    public static void addWall(Tile tile, ObjectType type, int rotation, boolean blocksProjectiles, boolean pathfinder) {
        switch(type) {
            case WALL_STRAIGHT -> {
                switch (rotation) {
                    case 0 -> {
                        addFlag(tile, blockWest(true, blocksProjectiles, pathfinder));
                        addFlag(tile.transform(-1, 0, 0), blockEast(true, blocksProjectiles, pathfinder));
                    }
                    case 1 -> {
                        addFlag(tile, blockNorth(true, blocksProjectiles, pathfinder));
                        addFlag(tile.transform(0, 1, 0), blockSouth(true, blocksProjectiles, pathfinder));
                    }
                    case 2 -> {
                        addFlag(tile, blockEast(true, blocksProjectiles, pathfinder));
                        addFlag(tile.transform(1, 0, 0), blockWest(true, blocksProjectiles, pathfinder));
                    }
                    case 3 -> {
                        addFlag(tile, blockSouth(true, blocksProjectiles, pathfinder));
                        addFlag(tile.transform(0, -1, 0), blockNorth(true, blocksProjectiles, pathfinder));
                    }
                }
            }
            case WALL_DIAGONAL_CORNER, WALL_STRAIGHT_CORNER -> {
                switch (rotation) {
                    case 0 -> {
                        addFlag(tile, blockNorthWest(true, blocksProjectiles, pathfinder));
                        addFlag(tile.transform(-1, 1, 0), blockSouthEast(true, blocksProjectiles, pathfinder));
                    }
                    case 1 -> {
                        addFlag(tile, blockNorthEast(true, blocksProjectiles, pathfinder));
                        addFlag(tile.transform(1, 1, 0), blockSouthWest(true, blocksProjectiles, pathfinder));
                    }
                    case 2 -> {
                        addFlag(tile, blockSouthEast(true, blocksProjectiles, pathfinder));
                        addFlag(tile.transform(1, -1, 0), blockNorthWest(true, blocksProjectiles, pathfinder));
                    }
                    case 3 -> {
                        addFlag(tile, blockSouthWest(true, blocksProjectiles, pathfinder));
                        addFlag(tile.transform(-1, -1, 0), blockNorthEast(true, blocksProjectiles, pathfinder));
                    }
                }
            }
            case WALL_WHOLE_CORNER -> {
                switch (rotation) {
                    case 0 -> {
                        addFlag(tile, blockNorth(true, blocksProjectiles, pathfinder) | blockWest(true, blocksProjectiles, pathfinder));
                        addFlag(tile.transform(-1, 0, 0), blockEast(true, blocksProjectiles, pathfinder));
                        addFlag(tile.transform(0, 1, 0), blockSouth(true, blocksProjectiles, pathfinder));
                    }
                    case 1 -> {
                        addFlag(tile, blockNorth(true, blocksProjectiles, pathfinder) | blockEast(true, blocksProjectiles, pathfinder));
                        addFlag(tile.transform(0, 1, 0), blockSouth(true, blocksProjectiles, pathfinder));
                        addFlag(tile.transform(1, 0, 0), blockWest(true, blocksProjectiles, pathfinder));
                    }
                    case 2 -> {
                        addFlag(tile, blockEast(true, blocksProjectiles, pathfinder) | blockSouth(true, blocksProjectiles, pathfinder));
                        addFlag(tile.transform(1, 0, 0), blockWest(true, blocksProjectiles, pathfinder));
                        addFlag(tile.transform(0, -1, 0), blockNorth(true, blocksProjectiles, pathfinder));
                    }
                    case 3 -> {
                        addFlag(tile, blockSouth(true, blocksProjectiles, pathfinder) | blockWest(true, blocksProjectiles, pathfinder));
                        addFlag(tile.transform(0, -1, 0), blockNorth(true, blocksProjectiles, pathfinder));
                        addFlag(tile.transform(-1, 0, 0), blockEast(true, blocksProjectiles, pathfinder));
                    }
                }
            }
        }
    }

    public static void removeWall(Tile tile, ObjectType type, int rotation, boolean blocksProjectiles, boolean pathfinder) {
        switch(type) {
            case WALL_STRAIGHT -> {
                switch (rotation) {
                    case 0 -> {
                        removeFlag(tile, blockWest(true, blocksProjectiles, pathfinder));
                        removeFlag(tile.transform(-1, 0, 0), blockEast(true, blocksProjectiles, pathfinder));
                    }
                    case 1 -> {
                        removeFlag(tile, blockNorth(true, blocksProjectiles, pathfinder));
                        removeFlag(tile.transform(0, 1, 0), blockSouth(true, blocksProjectiles, pathfinder));
                    }
                    case 2 -> {
                        removeFlag(tile, blockEast(true, blocksProjectiles, pathfinder));
                        removeFlag(tile.transform(1, 0, 0), blockWest(true, blocksProjectiles, pathfinder));
                    }
                    case 3 -> {
                        removeFlag(tile, blockSouth(true, blocksProjectiles, pathfinder));
                        removeFlag(tile.transform(0, -1, 0), blockNorth(true, blocksProjectiles, pathfinder));
                    }
                }
            }
            case WALL_DIAGONAL_CORNER, WALL_STRAIGHT_CORNER -> {
                switch (rotation) {
                    case 0 -> {
                        removeFlag(tile, blockNorthWest(true, blocksProjectiles, pathfinder));
                        removeFlag(tile.transform(-1, 1, 0), blockSouthEast(true, blocksProjectiles, pathfinder));
                    }
                    case 1 -> {
                        removeFlag(tile, blockNorthEast(true, blocksProjectiles, pathfinder));
                        removeFlag(tile.transform(1, 1, 0), blockSouthWest(true, blocksProjectiles, pathfinder));
                    }
                    case 2 -> {
                        removeFlag(tile, blockSouthEast(true, blocksProjectiles, pathfinder));
                        removeFlag(tile.transform(1, -1, 0), blockNorthWest(true, blocksProjectiles, pathfinder));
                    }
                    case 3 -> {
                        removeFlag(tile, blockSouthWest(true, blocksProjectiles, pathfinder));
                        removeFlag(tile.transform(-1, -1, 0), blockNorthEast(true, blocksProjectiles, pathfinder));
                    }
                }
            }
            case WALL_WHOLE_CORNER -> {
                switch (rotation) {
                    case 0 -> {
                        removeFlag(tile, blockNorth(true, blocksProjectiles, pathfinder) | blockWest(true, blocksProjectiles, pathfinder));
                        removeFlag(tile.transform(-1, 0, 0), blockEast(true, blocksProjectiles, pathfinder));
                        removeFlag(tile.transform(0, 1, 0), blockSouth(true, blocksProjectiles, pathfinder));
                    }
                    case 1 -> {
                        removeFlag(tile, blockNorth(true, blocksProjectiles, pathfinder) | blockEast(true, blocksProjectiles, pathfinder));
                        removeFlag(tile.transform(0, 1, 0), blockSouth(true, blocksProjectiles, pathfinder));
                        removeFlag(tile.transform(1, 0, 0), blockWest(true, blocksProjectiles, pathfinder));
                    }
                    case 2 -> {
                        removeFlag(tile, blockEast(true, blocksProjectiles, pathfinder) | blockSouth(true, blocksProjectiles, pathfinder));
                        removeFlag(tile.transform(1, 0, 0), blockWest(true, blocksProjectiles, pathfinder));
                        removeFlag(tile.transform(0, -1, 0), blockNorth(true, blocksProjectiles, pathfinder));
                    }
                    case 3 -> {
                        removeFlag(tile, blockSouth(true, blocksProjectiles, pathfinder) | blockWest(true, blocksProjectiles, pathfinder));
                        removeFlag(tile.transform(0, -1, 0), blockNorth(true, blocksProjectiles, pathfinder));
                        removeFlag(tile.transform(-1, 0, 0), blockEast(true, blocksProjectiles, pathfinder));
                    }
                }
            }
        }
    }

    public static int getFlags(Tile tile) {
        synchronized (LOCK) {
            int chunkId = tile.getChunkId();
            if (CLIP_FLAGS[chunkId] == null)
                return -1;
            return CLIP_FLAGS[chunkId][tile.getXInChunk() | tile.getYInChunk() << 3];
        }
    }

    public static void addFlag(Tile tile, int flag) {
        synchronized (LOCK) {
            int chunkId = tile.getChunkId();
            if (CLIP_FLAGS[chunkId] == null)
                CLIP_FLAGS[chunkId] = new int[64];
            CLIP_FLAGS[chunkId][tile.getXInChunk() | tile.getYInChunk() << 3] |= flag;
        }
    }

    public static void removeFlag(Tile tile, int flag) {
        synchronized (LOCK) {
            int chunkId = tile.getChunkId();
            if (CLIP_FLAGS[chunkId] == null)
                CLIP_FLAGS[chunkId] = new int[64];
            CLIP_FLAGS[chunkId][tile.getXInChunk() | tile.getYInChunk() << 3] &= ~flag;
        }
    }

    public static void setFlags(Tile tile, int flag) {
        synchronized (LOCK) {
            int chunkId = tile.getChunkId();
            if (CLIP_FLAGS[chunkId] == null)
                CLIP_FLAGS[chunkId] = new int[64];
            CLIP_FLAGS[chunkId][tile.getXInChunk() | tile.getYInChunk() << 3] = flag;
        }
    }

    public static void clip(GameObject object) {
        if (object.getId() == -1)
            return;
        ObjectType type = object.getType();
        int rotation = object.getRotation();

        ObjectDefinitions defs = ObjectDefinitions.getDefs(object.getId());

        if (defs.getClipType() == 0)
            return;

        switch(type) {
            case WALL_STRAIGHT:
            case WALL_DIAGONAL_CORNER:
            case WALL_WHOLE_CORNER:
            case WALL_STRAIGHT_CORNER:
                addWall(object.getTile(), type, rotation, defs.blocks(), !defs.ignoresPathfinder);
                break;
            case WALL_INTERACT:
            case SCENERY_INTERACT:
            case GROUND_INTERACT:
            case STRAIGHT_SLOPE_ROOF:
            case DIAGONAL_SLOPE_ROOF:
            case DIAGONAL_SLOPE_CONNECT_ROOF:
            case STRAIGHT_SLOPE_CORNER_CONNECT_ROOF:
            case STRAIGHT_SLOPE_CORNER_ROOF:
            case STRAIGHT_FLAT_ROOF:
            case STRAIGHT_BOTTOM_EDGE_ROOF:
            case DIAGONAL_BOTTOM_EDGE_CONNECT_ROOF:
            case STRAIGHT_BOTTOM_EDGE_CONNECT_ROOF:
            case STRAIGHT_BOTTOM_EDGE_CONNECT_CORNER_ROOF:
                int sizeX;
                int sizeY;
                if (rotation != 1 && rotation != 3) {
                    sizeX = defs.getSizeX();
                    sizeY = defs.getSizeY();
                } else {
                    sizeX = defs.getSizeY();
                    sizeY = defs.getSizeX();
                }
                addObject(object.getTile(), sizeX, sizeY, defs.blocks(), !defs.ignoresPathfinder);
//			if (defs.clipType != 0)
//				clipMapProj.addObject(plane, x, y, sizeX, sizeY, defs.blocks(), !defs.ignoresPathfinder);
                break;
            case GROUND_DECORATION:
                if (defs.clipType == 1)
                    addBlockWalkAndProj(object.getTile());
                break;
            default:
                break;
        }
    }

    public static void unclip(Tile tile) {
        setFlags(tile, 0);
    }

    public static void unclip(GameObject object) {
        if (object.getId() == -1) // dont clip or noclip with id -1
            return;
        ObjectType type = object.getType();
        int rotation = object.getRotation();
        ObjectDefinitions defs = ObjectDefinitions.getDefs(object.getId());

        if (defs.getClipType() == 0)
            return;

        switch(type) {
            case WALL_STRAIGHT:
            case WALL_DIAGONAL_CORNER:
            case WALL_WHOLE_CORNER:
            case WALL_STRAIGHT_CORNER:
                removeWall(object.getTile(), type, rotation, defs.blocks(), !defs.ignoresPathfinder);
                break;
            case WALL_INTERACT:
            case SCENERY_INTERACT:
            case GROUND_INTERACT:
            case STRAIGHT_SLOPE_ROOF:
            case DIAGONAL_SLOPE_ROOF:
            case DIAGONAL_SLOPE_CONNECT_ROOF:
            case STRAIGHT_SLOPE_CORNER_CONNECT_ROOF:
            case STRAIGHT_SLOPE_CORNER_ROOF:
            case STRAIGHT_FLAT_ROOF:
            case STRAIGHT_BOTTOM_EDGE_ROOF:
            case DIAGONAL_BOTTOM_EDGE_CONNECT_ROOF:
            case STRAIGHT_BOTTOM_EDGE_CONNECT_ROOF:
            case STRAIGHT_BOTTOM_EDGE_CONNECT_CORNER_ROOF:
                int sizeX;
                int sizeY;
                if (rotation == 1 || rotation == 3) {
                    sizeX = defs.getSizeY();
                    sizeY = defs.getSizeX();
                } else {
                    sizeX = defs.getSizeX();
                    sizeY = defs.getSizeY();
                }
                removeObject(object.getTile(), sizeX, sizeY, defs.blocks(), !defs.ignoresPathfinder);
                break;
            case GROUND_DECORATION:
                if (defs.clipType == 1)
                    removeBlockWalkAndProj(object.getTile());
                break;
            default:
                break;
        }
    }
}
