package com.rs.game.map.instance;

import com.rs.game.model.entity.player.Player;
import com.rs.lib.game.Tile;
import com.rs.lib.util.MapUtils;
import com.rs.lib.util.MapUtils.Structure;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.objects.Object2ObjectMaps;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class Instance {
    private static final Map<UUID, Instance> INSTANCES = Object2ObjectMaps.synchronize(new Object2ObjectOpenHashMap<>());

    private final UUID id = UUID.randomUUID();
    private Tile returnTo;
    private int[] entranceOffset;
    private boolean persistent;
    private transient int[] chunkBase;
    private transient IntSet chunkIds = new IntOpenHashSet();
    private int width;
    private int height;

    private boolean copyNpcs;

    private transient volatile boolean destroyed;

    private Instance(Tile returnTo, int width, int height, boolean copyNpcs) {
        this.returnTo = returnTo;
        this.width = width;
        this.height = height;
        destroyed = false;
        this.copyNpcs = copyNpcs;
    }

    public static Instance of(Tile returnTo, int width, int height, boolean copyNpcs) {
        Instance instance = new Instance(returnTo, width, height, copyNpcs);
        INSTANCES.put(instance.id, instance);
        return new Instance(returnTo, width, height, copyNpcs);
    }

    public static Instance of(Tile returnTo, int width, int height) {
        return of(returnTo, width, height, false);
    }

    public static Instance get(UUID uuid) {
        return INSTANCES.get(uuid);
    }

    public int[] getEntranceOffset() {
        return entranceOffset;
    }

    public Instance setEntranceOffset(int[] entranceOffset) {
        this.entranceOffset = entranceOffset;
        return this;
    }

    public Instance persist() {
        persistent = true;
        return this;
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
        INSTANCES.remove(id);
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

    public Tile getReturnTo() {
        return returnTo;
    }

    public void teleportLocal(Player player, int localX, int localY, int plane) {
        player.setInstancedArea(this);
        player.setNextTile(Tile.of(getBaseX() + localX, getBaseY() + localY, plane));
    }

    public void teleportChunkLocal(Player player, int chunkXOffset, int chunkYOffset, int xOffset, int yOffset, int plane) {
        player.setInstancedArea(this);
        player.setNextTile(Tile.of(getLocalX(chunkXOffset, xOffset), getLocalY(chunkYOffset, yOffset), plane));
    }

    public void teleportTo(Player player) {
        teleportLocal(player, entranceOffset == null ? width * 4 : entranceOffset[0], entranceOffset == null ? height * 4 : entranceOffset[1], entranceOffset == null || entranceOffset.length < 3 ? 0 : entranceOffset[2]);
    }

    public UUID getId() {
        return id;
    }

    public boolean isPersistent() {
        return persistent;
    }
}
