package com.rs.game.map.instance;

import com.rs.lib.game.Tile;
import com.rs.lib.util.MapUtils;
import com.rs.lib.util.MapUtils.Structure;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;

public class Instance {
    private int[] chunkBase;
    private IntSet chunkIds = new IntOpenHashSet();
    private int width;
    private int height;
    private boolean destroyed;

    public Instance(int width, int height) {
        this.width = width;
        this.height = height;
        destroyed = false;
    }

    public void requestChunkBound(Runnable callback) {
        destroyed = false;
        InstanceBuilder.findEmptyChunkBound(this, callback);
    }

    public void copyChunk(int localChunkX, int localChunkY, int plane, int fromChunkX, int fromChunkY, int fromPlane, int rotation, Runnable callback) {
        if (chunkBase == null)
            requestChunkBound(() -> {
                InstanceBuilder.copyChunk(this, localChunkX, localChunkY, plane, fromChunkX, fromChunkY, fromPlane, rotation, callback);
            });
        else
            InstanceBuilder.copyChunk(this, localChunkX, localChunkY, plane, fromChunkX, fromChunkY, fromPlane, rotation, callback);
    }

    public void clearChunk(int localChunkX, int localChunkY, int plane, Runnable callback) {
        if (chunkBase == null)
            requestChunkBound(() -> {
                InstanceBuilder.clearChunk(this, localChunkX, localChunkY, plane, callback);
            });
        else
            InstanceBuilder.clearChunk(this, localChunkX, localChunkY, plane, callback);
    }

    public void copy2x2ChunkSquare(int localChunkX, int localChunkY, int fromChunkX, int fromChunkY, int rotation, int[] planes, Runnable callback) {
        if (chunkBase == null)
            requestChunkBound(() -> {
                InstanceBuilder.copy2x2ChunkSquare(this, localChunkX, localChunkY, fromChunkX, fromChunkY, rotation, planes, callback);
            });
        else
            InstanceBuilder.copy2x2ChunkSquare(this, localChunkX, localChunkY, fromChunkX, fromChunkY, rotation, planes, callback);
    }

    public void copyMap(int localChunkX, int localChunkY, int fromChunkX, int fromChunkY, int size, Runnable callback) {
        if (chunkBase == null)
            requestChunkBound(() -> {
                InstanceBuilder.copyMap(this, localChunkX, localChunkY, fromChunkX, fromChunkY, size, callback);
            });
        else
            InstanceBuilder.copyMap(this, localChunkX, localChunkY, fromChunkX, fromChunkY, size, callback);
    }

    public void copyMapAllPlanes(int fromChunkX, int fromChunkY, int size, Runnable callback) {
        copyMap(0, 0, fromChunkX, fromChunkY, size, callback);
    }

    public void copyMap(int localChunkX, int localChunkY, int[] planes, int fromChunkX, int fromChunkY, int[] fromPlanes, int width, int height, Runnable callback) {
        if (chunkBase == null)
            requestChunkBound(() -> {
                InstanceBuilder.copyMap(this, localChunkX, localChunkY, planes, fromChunkX, fromChunkY, fromPlanes, width, height, callback);
            });
        else
            InstanceBuilder.copyMap(this, localChunkX, localChunkY, planes, fromChunkX, fromChunkY, fromPlanes, width, height, callback);
    }

    public void copyMap(int[] planes, int fromChunkX, int fromChunkY, int[] fromPlanes, int width, int height, Runnable callback) {
        copyMap(0, 0, planes, fromChunkX, fromChunkY, fromPlanes, width, height, callback);
    }

    public void copyMapSinglePlane(int fromChunkX, int fromChunkY, Runnable callback) {
        copyMap(0, 0, new int[1], fromChunkX, fromChunkY, new int[1], width, height, callback);
    }

    public void copyMapAllPlanes(int fromChunkX, int fromChunkY, Runnable callback) {
        copyMap(0, 0, new int[] { 0, 1, 2, 3 }, fromChunkX, fromChunkY, new int[] { 0, 1, 2, 3 }, width, height, callback);
    }

    public void clearMap(int chunkX, int chunkY, int width, int height, int[] planes, Runnable callback) {
        if (chunkBase == null)
            requestChunkBound(() -> {
                InstanceBuilder.clearMap(this, chunkX, chunkY, width, height, planes, callback);
            });
        else
            InstanceBuilder.clearMap(this, chunkX, chunkY, width, height, planes, callback);
    }

    public void clearMap(int width, int height, int[] planes, Runnable callback) {
        clearMap(0, 0, width, height, planes, callback);
    }

    public void clearMap(int[] planes, Runnable callback) {
        clearMap(width, height, planes, callback);
    }

    public void destroy(Runnable callback) {
        if (destroyed)
            return;
        destroyed = true;
        InstanceBuilder.destroyMap(this, callback);
    }

    public void destroy() {
        destroy(null);
    }

    public int[] getChunkBase() {
        return chunkBase;
    }

    public int getBaseChunkX() {
        return chunkBase[0];
    }

    public int getBaseChunkY() {
        return chunkBase[1];
    }

    public int getBaseX() {
        return chunkBase[0] << 3;
    }

    public int getBaseY() {
        return chunkBase[1] << 3;
    }

    public Tile getTileBase() {
        return Tile.of(getBaseX(), getBaseY(), 0);
    }

    public Tile getLocalTile(int offsetX, int offsetY, int plane) {
        return Tile.of(getLocalX(offsetX), getLocalY(offsetY), plane);
    }

    public Tile getLocalTile(int offsetX, int offsetY) {
        return getLocalTile(offsetX, offsetY, 0);
    }

    public int getLocalX(int offset) {
        return getBaseX() + offset;
    }

    public int getLocalY(int offset) {
        return getBaseY() + offset;
    }

    public int getLocalX(int chunkXOffset, int tileXOffset) {
        return (getChunkX(chunkXOffset) << 3) + tileXOffset;
    }

    public int getLocalY(int chunkYOffset, int tileYOffset) {
        return (getChunkY(chunkYOffset) << 3) + tileYOffset;
    }

    public int getChunkX(int offsetX) {
        return getBaseChunkX() + offsetX;
    }

    public int getChunkY(int offsetY) {
        return getBaseChunkY() + offsetY;
    }

    public int getChunkId(int offsetX, int offsetY, int plane) {
        return MapUtils.encode(Structure.CHUNK, getBaseChunkX()+offsetX, getBaseChunkY()+offsetY, plane);
    }

    public boolean isDestroyed() {
        return destroyed;
    }

    public boolean isCreated() {
        return chunkBase != null;
    }

    public IntSet getChunkIds() {
        return chunkIds;
    }

    /**
     * Only the instance builder should be setting this value.
     */
    @Deprecated
    public void setChunkBase(int[] chunkBase) {
        this.chunkBase = chunkBase;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
