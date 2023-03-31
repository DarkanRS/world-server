package com.rs.game.map.instance;

import com.rs.lib.game.Tile;
import com.rs.lib.util.MapUtils;
import com.rs.lib.util.MapUtils.Structure;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;

import java.util.concurrent.CompletableFuture;

public class Instance {
    private int[] chunkBase;
    private IntSet chunkIds = new IntOpenHashSet();
    private int width;
    private int height;

    private boolean copyNpcs;
    private volatile boolean destroyed;

    public Instance(int width, int height, boolean copyNpcs) {
        this.width = width;
        this.height = height;
        destroyed = false;
        this.copyNpcs = copyNpcs;
    }

    public Instance(int width, int height) {
        this(width, height, false);
    }

    public CompletableFuture<Boolean> requestChunkBound() {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        destroyed = false;
        InstanceBuilder.findEmptyChunkBound(this, future);
        return future;
    }

    public CompletableFuture<Boolean> copyChunk(int localChunkX, int localChunkY, int plane, int fromChunkX, int fromChunkY, int fromPlane, int rotation) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        if (chunkBase == null)
            requestChunkBound().thenAccept(bool -> InstanceBuilder.copyChunk(this, localChunkX, localChunkY, plane, fromChunkX, fromChunkY, fromPlane, rotation, future)).exceptionally(e -> { future.completeExceptionally(e); return null; });
        else
            InstanceBuilder.copyChunk(this, localChunkX, localChunkY, plane, fromChunkX, fromChunkY, fromPlane, rotation, future);
        return future;
    }

    public CompletableFuture<Boolean> clearChunk(int localChunkX, int localChunkY, int plane) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        if (chunkBase == null)
            requestChunkBound().thenAccept(bool -> InstanceBuilder.clearChunk(this, localChunkX, localChunkY, plane, future)).exceptionally(e -> { future.completeExceptionally(e); return null; });
        else
            InstanceBuilder.clearChunk(this, localChunkX, localChunkY, plane, future);
        return future;
    }

    public CompletableFuture<Boolean> copy2x2ChunkSquare(int localChunkX, int localChunkY, int fromChunkX, int fromChunkY, int rotation, int[] planes) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        if (chunkBase == null)
            requestChunkBound().thenAccept(bool -> InstanceBuilder.copy2x2ChunkSquare(this, localChunkX, localChunkY, fromChunkX, fromChunkY, rotation, planes, future)).exceptionally(e -> { future.completeExceptionally(e); return null; });
        else
            InstanceBuilder.copy2x2ChunkSquare(this, localChunkX, localChunkY, fromChunkX, fromChunkY, rotation, planes, future);
        return future;
    }

    public CompletableFuture<Boolean> copyMap(int localChunkX, int localChunkY, int fromChunkX, int fromChunkY, int size) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        if (chunkBase == null)
            requestChunkBound().thenAccept(bool -> InstanceBuilder.copyMap(this, localChunkX, localChunkY, fromChunkX, fromChunkY, size, future)).exceptionally(e -> { future.completeExceptionally(e); return null; });
        else
            InstanceBuilder.copyMap(this, localChunkX, localChunkY, fromChunkX, fromChunkY, size, future);
        return future;
    }

    public CompletableFuture<Boolean> copyMapAllPlanes(int fromChunkX, int fromChunkY, int size) {
        return copyMap(0, 0, fromChunkX, fromChunkY, size);
    }

    public CompletableFuture<Boolean> copyMap(int localChunkX, int localChunkY, int[] planes, int fromChunkX, int fromChunkY, int[] fromPlanes, int width, int height) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        if (chunkBase == null)
            requestChunkBound().thenAccept(bool -> InstanceBuilder.copyMap(this, localChunkX, localChunkY, planes, fromChunkX, fromChunkY, fromPlanes, width, height, copyNpcs, future)).exceptionally(e -> { future.completeExceptionally(e); return null; });
        else
            InstanceBuilder.copyMap(this, localChunkX, localChunkY, planes, fromChunkX, fromChunkY, fromPlanes, width, height, copyNpcs, future);
        return future;
    }

    public CompletableFuture<Boolean> copyMap(int[] planes, int fromChunkX, int fromChunkY, int[] fromPlanes, int width, int height) {
        return copyMap(0, 0, planes, fromChunkX, fromChunkY, fromPlanes, width, height);
    }

    public CompletableFuture<Boolean> copyMapSinglePlane(int fromChunkX, int fromChunkY) {
        return copyMap(0, 0, new int[1], fromChunkX, fromChunkY, new int[1], width, height);
    }

    public CompletableFuture<Boolean> copyMapAllPlanes(int fromChunkX, int fromChunkY) {
        return copyMap(0, 0, new int[] { 0, 1, 2, 3 }, fromChunkX, fromChunkY, new int[] { 0, 1, 2, 3 }, width, height);
    }

    public CompletableFuture<Boolean> clearMap(int chunkX, int chunkY, int width, int height, int[] planes) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        if (chunkBase == null)
            requestChunkBound().thenAccept(bool -> InstanceBuilder.clearMap(this, chunkX, chunkY, width, height, planes, future)).exceptionally(e -> { future.completeExceptionally(e); return null; });
        else
            InstanceBuilder.clearMap(this, chunkX, chunkY, width, height, planes, future);
        return future;
    }

    public CompletableFuture<Boolean> clearMap(int width, int height, int[] planes) {
        return clearMap(0, 0, width, height, planes);
    }

    public CompletableFuture<Boolean> clearMap(int[] planes) {
        return clearMap(width, height, planes);
    }

    public CompletableFuture<Boolean> destroy() {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        if (destroyed) {
            future.complete(null);
            return future;
        }
        destroyed = true;
        InstanceBuilder.destroyMap(this, future);
        return future;
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

    public boolean isCopyNpcs() {
        return copyNpcs;
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
