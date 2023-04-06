package com.rs.game.map;

import com.rs.cache.loaders.map.Region;
import com.rs.game.World;
import com.rs.game.map.instance.InstancedChunk;
import com.rs.game.model.entity.pathing.WorldCollision;
import com.rs.game.model.object.GameObject;
import com.rs.lib.game.Tile;
import com.rs.lib.game.WorldObject;
import com.rs.lib.util.MapUtils;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSets;

import java.util.Map;
import java.util.Set;

public final class ChunkManager {
    private static final Object CHUNK_LOCK = new Object();

    private static final Map<Integer, Chunk> CHUNKS = new Int2ObjectOpenHashMap<>();
    private static final Set<Integer> ACTIVE_CHUNKS = IntSets.synchronize(new IntOpenHashSet());
    private static final Set<Integer> UNLOADABLE_CHUNKS = IntSets.synchronize(new IntOpenHashSet());
    private static final Set<Integer> PERMANENTLY_LOADED_CHUNKS = IntSets.synchronize(new IntOpenHashSet());

    public static Chunk loadChunk(int chunkId) {
        synchronized (CHUNK_LOCK) {
            Chunk chunk = CHUNKS.get(chunkId);
            if (CHUNKS.get(chunkId) != null)
                return chunk;
            chunk = new Chunk(chunkId);
            CHUNKS.put(chunkId, chunk);
            int regionId = MapUtils.chunkToRegionId(chunkId);
            Region region = new Region(regionId);
            region.loadRegionMap(false);
            if (!region.hasData())
                return chunk;
            for (int x = 0; x < 64; x++)
                for (int y = 0; y < 64; y++)
                    for (int plane = 0; plane < 4; plane++)
                        WorldCollision.addFlag(Tile.of(region.getBaseX() + x, region.getBaseY() + y, plane), region.getClipFlags()[plane][x][y]);
            if (region.getObjects() == null || region.getObjects().isEmpty())
                return chunk;
            for (WorldObject object : region.getObjects()) {
                int oCid = object.getTile().getChunkId();
                Chunk oChunk = CHUNKS.get(chunkId);
                if (oChunk instanceof InstancedChunk)
                    continue;
                if (oChunk == null)
                    oChunk = new Chunk(oCid);
                oChunk.addBaseObject(new GameObject(object));
                oChunk.setMapDataLoaded();
                oChunk.checkLoaded();
                CHUNKS.put(oCid, oChunk);
            }
            return chunk;
        }
    }
}
