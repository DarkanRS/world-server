package com.rs.game.model.entity.pathing;

import com.rs.cache.loaders.ObjectType;
import com.rs.game.region.ClipFlag;
import com.rs.lib.game.Tile;

import static com.rs.game.region.ClipFlag.*;

public class WorldCollision {
    private static final int CHUNK_SIZE = 2048; //2048 chunk size = max capacity 16384x16384 tiles
    private static final int[][] CLIP_FLAGS = new int[CHUNK_SIZE * CHUNK_SIZE * 4][];

    public static void createChunk(int chunkX, int chunkY, int plane, int[] flags) {
        CLIP_FLAGS[getId(chunkX, chunkY, plane)] = flags;
    }

    public static void clearChunk(int chunkX, int chunkY, int plane) {
        clearChunk(getId(chunkX, chunkY, plane));
    }

    public static void clearChunk(int chunkCollisionHash) {
        CLIP_FLAGS[chunkCollisionHash] = null;
    }

    public static int getId(int chunkX, int chunkY, int plane) {
        return (chunkX & 0xfff) + ((chunkY & 0xfff) << 11) + ((plane & 0x3) << 22);
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

    public static void addFlag(Tile tile, int flag) {
        int chunkX = tile.getChunkX(); int chunkY = tile.getChunkY();
        if (CLIP_FLAGS[getId(chunkX, chunkY, tile.getPlane())] == null)
            CLIP_FLAGS[getId(chunkX, chunkY, tile.getPlane())] = new int[64];
        CLIP_FLAGS[getId(chunkX, chunkY, tile.getPlane())][tile.getXInChunk() + tile.getYInChunk() << 5] |= flag;
    }

    public static void removeFlag(Tile tile, int flag) {
        int chunkX = tile.getChunkX(); int chunkY = tile.getChunkY();
        if (CLIP_FLAGS[getId(chunkX, chunkY, tile.getPlane())] == null)
            CLIP_FLAGS[getId(chunkX, chunkY, tile.getPlane())] = new int[64];
        CLIP_FLAGS[getId(chunkX, chunkY, tile.getPlane())][tile.getXInChunk() + tile.getYInChunk() << 5] &= ~flag;
    }

    public static void setFlags(Tile tile, int flag) {
        int chunkX = tile.getChunkX(); int chunkY = tile.getChunkY();
        if (CLIP_FLAGS[getId(chunkX, chunkY, tile.getPlane())] == null)
            CLIP_FLAGS[getId(chunkX, chunkY, tile.getPlane())] = new int[64];
        CLIP_FLAGS[getId(chunkX, chunkY, tile.getPlane())][tile.getXInChunk() + tile.getYInChunk() << 5] = flag;
    }
}
