package com.rs.game.map;

import com.rs.Settings;
import com.rs.cache.loaders.map.Region;
import com.rs.cache.loaders.map.RegionSize;
import com.rs.game.World;
import com.rs.game.map.instance.InstancedChunk;
import com.rs.game.model.entity.Entity;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.pathing.WorldCollision;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.object.GameObject;
import com.rs.lib.game.Tile;
import com.rs.lib.game.WorldObject;
import com.rs.lib.util.Logger;
import com.rs.lib.util.MapUtils;
import com.rs.plugin.PluginManager;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.annotations.ServerStartupEvent;
import com.rs.plugin.events.EnterChunkEvent;
import com.rs.utils.music.Music;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSets;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@PluginEventHandler
public final class ChunkManager {
    private static final Object CHUNK_LOCK = new Object();

    private static final Map<Integer, Chunk> CHUNKS = new Int2ObjectOpenHashMap<>();
    private static final Set<Integer> ACTIVE_CHUNKS = IntSets.synchronize(new IntOpenHashSet());
    private static final Set<Integer> UNLOADABLE_CHUNKS = IntSets.synchronize(new IntOpenHashSet());
    private static final Set<Integer> PERMANENTLY_LOADED_CHUNKS = IntSets.synchronize(new IntOpenHashSet());
    private static final Map<Integer, UpdateZone> UPDATE_ZONES = new HashMap<>();

    @ServerStartupEvent(ServerStartupEvent.Priority.FILE_IO)
    public static void loadAllMapData() {
        Logger.info(WorldCollision.class, "loadAllMapData", Runtime.getRuntime().maxMemory()/(1024*1024)+"mb heap space available. For better performance, allocate at least 1024mb or 3072mb");
        boolean preloadCollision = Settings.getConfig().isAllowHighMemUseOptimizations() && Runtime.getRuntime().maxMemory() != Integer.MAX_VALUE && Runtime.getRuntime().maxMemory() > 1024*1024*1024; //1024mb HEAP
        boolean preloadObjects = Settings.getConfig().isAllowHighMemUseOptimizations() && Runtime.getRuntime().maxMemory() != Integer.MAX_VALUE && Runtime.getRuntime().maxMemory() > 3072*1024*1024; //3072mb HEAP

        if (preloadCollision) {
            for (int regionId = 0; regionId < 0xFFFF; regionId++) {
                if (regionId == 18754) //citadel map that gets partially decoded with xteas but fails to pass gzip format tests
                    continue;
                Region region = new Region(regionId);
                region.loadRegionMap(false);
                if (!region.hasData())
                    continue;
                for (int x = 0; x < 64; x++)
                    for (int y = 0; y < 64; y++)
                        for (int plane = 0; plane < 4; plane++)
                            WorldCollision.addFlag(Tile.of(region.getBaseX() + x, region.getBaseY() + y, plane), region.getClipFlags()[plane][x][y]);
                if (preloadObjects) {
                    if (region.getObjects() == null || region.getObjects().isEmpty())
                        continue;
                    for (WorldObject object : region.getObjects()) {
                        Chunk chunk = getChunk(object.getTile().getChunkId());
                        chunk.addBaseObject(new GameObject(object));
                        chunk.setMapDataLoaded();
                    }
                }
            }
        }
    }

    public static final void updateChunks(Entity entity) {
        if (entity instanceof NPC)
            WorldCollision.clipNPC((NPC) entity);
        if (entity.hasFinished()) {
            if (entity instanceof Player) {
                getChunk(entity.getLastChunkId()).removePlayerIndex(entity.getIndex());
                getUpdateZone(entity.getSceneBaseChunkId(), entity.getMapSize()).removeWatcher(entity.getIndex());
            } else
                getChunk(entity.getLastChunkId()).removeNPCIndex(entity.getIndex());
            return;
        }
        int chunkId = MapUtils.encode(MapUtils.Structure.CHUNK, entity.getChunkX(), entity.getChunkY(), entity.getPlane());
        int chunkIdNoPlane = MapUtils.encode(MapUtils.Structure.CHUNK, entity.getChunkX(), entity.getChunkY());
        if (entity.getLastChunkId() != chunkId || entity.isForceUpdateEntityRegion()) {
            PluginManager.handle(new EnterChunkEvent(entity, chunkId));
            PluginManager.handle(new EnterChunkEvent(entity, chunkIdNoPlane));
            entity.checkMultiArea();

            if (entity instanceof Player player) {
                if(Settings.getConfig().isDebug() && player.hasStarted() && Music.getGenre(player) == null && !(getChunk(player.getChunkId()) instanceof InstancedChunk))
                    player.sendMessage(chunkIdNoPlane + " has no music genre!");
                if (entity.getLastChunkId() > 0)
                    getChunk(entity.getLastChunkId()).removePlayerIndex(entity.getIndex());
                Chunk chunk = getChunk(chunkId);
                chunk.addPlayerIndex(entity.getIndex());

                //Unlock all region music at once.
                int[] musicIds = chunk.getMusicIds();
                if (player.hasStarted() && musicIds != null && musicIds.length > 0)
                    for (int musicId : musicIds)
                        if (!player.getMusicsManager().hasMusic(musicId))
                            player.getMusicsManager().unlockMusic(musicId);

                //if should play random song on enter region

                /**
                 * If the player is in the world and no genre is playing
                 * if there is no controller and the region and playing genres don't match, play a song
                 * same if there is a controller but check if the controller allows region play.
                 */
                if(player.hasStarted() && (Music.getGenre(player) == null || player.getMusicsManager().getPlayingGenre() == null
                        || !player.getMusicsManager().getPlayingGenre().matches(Music.getGenre(player)))) {//tested, looks good.
                    if (player.getControllerManager().getController() == null) {
                        player.getMusicsManager().nextAmbientSong();
                    } else if (player.getControllerManager().getController().playAmbientOnControllerRegionEnter() && !player.getDungManager().isInsideDungeon()) { //if we start the dungeon controller before the region enter we can get rid of that inside dungeon thing.
                        if(player.getMusicsManager().getPlayingGenre() == null || !player.getMusicsManager().getPlayingGenre().matches(player.getControllerManager().getController().getGenre())) {
                            player.getMusicsManager().nextAmbientSong();
                        }
                    }
                }

                player.getControllerManager().moved();
            } else {
                if (entity.getLastChunkId() > 0)
                    getChunk(entity.getLastChunkId()).removeNPCIndex(entity.getIndex());
                getChunk(chunkId).addNPCIndex(entity.getIndex());
            }
            entity.setForceUpdateEntityRegion(false);
            entity.setLastChunkId(chunkId);
        } else if (entity instanceof Player player) {
            player.getControllerManager().moved();
        }
    }

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

    public static final Chunk getChunk(int id, boolean load) {
        synchronized (CHUNK_LOCK) {
            Chunk chunk = CHUNKS.get(id);
            if (CHUNKS.get(id) != null)
                return chunk;
            if (load)
                return loadChunk(id);
            return new Chunk(id);
        }
    }

    public static final Chunk getChunk(int id) {
        return getChunk(id, false);
    }

    public static final Chunk putChunk(int id, Chunk chunk) {
        synchronized (CHUNK_LOCK) {
            return CHUNKS.put(id, chunk);
        }
    }

    public static final Chunk removeChunk(int id) {
        synchronized (CHUNK_LOCK) {
            return CHUNKS.remove(id);
        }
    }

    public static final UpdateZone removeUpdateZone(int baseChunkId, RegionSize size) {
        synchronized (UPDATE_ZONES) {
            markChunksUnviewed(baseChunkId, size);
            return UPDATE_ZONES.remove(UpdateZone.getId(baseChunkId, size));
        }
    }

    public static final UpdateZone getUpdateZone(int baseChunkId, RegionSize size) {
        synchronized (UPDATE_ZONES) {
            int id = UpdateZone.getId(baseChunkId, size);
            UpdateZone updateZone = UPDATE_ZONES.get(id);
            if (updateZone == null) {
                updateZone = new UpdateZone(baseChunkId, size);
                UPDATE_ZONES.put(id, updateZone);
                markChunksViewed(baseChunkId, size);
            }
            return updateZone;
        }
    }

    public static final void markChunksUnviewed(int baseChunkId, RegionSize size) {
        for (int planeOff = 0;planeOff < 4 * Chunk.PLANE_INC;planeOff += Chunk.PLANE_INC) {
            for (int chunkXOff = 0; chunkXOff <= (size.size / 8) * Chunk.X_INC; chunkXOff += Chunk.X_INC) {
                for (int chunkYOff = 0; chunkYOff <= (size.size / 8); chunkYOff++) {
                    int chunkId = baseChunkId + planeOff + chunkXOff + chunkYOff;
                    if (!PERMANENTLY_LOADED_CHUNKS.contains(chunkId))
                        UNLOADABLE_CHUNKS.add(chunkId);
                }
            }
        }
    }

    public static final void markChunksViewed(int baseChunkId, RegionSize size) {
        for (int planeOff = 0;planeOff < 4 * Chunk.PLANE_INC;planeOff += Chunk.PLANE_INC) {
            for (int chunkXOff = 0; chunkXOff <= (size.size / 8) * Chunk.X_INC; chunkXOff += Chunk.X_INC) {
                for (int chunkYOff = 0; chunkYOff <= (size.size / 8); chunkYOff++) {
                    int chunkId = baseChunkId + planeOff + chunkXOff + chunkYOff;
                    UNLOADABLE_CHUNKS.remove(chunkId);
                }
            }
        }
    }

    public static void permanentlyPreloadChunks(int centerChunkId, int radius) {
        int[] coords = MapUtils.decode(MapUtils.Structure.CHUNK, centerChunkId);
        for (int plane = 0;plane < 4;plane++) {
            for (int chunkX = coords[0] - radius; chunkX < coords[0] + radius; chunkX++) {
                for (int chunkY = coords[1] - radius; chunkY < coords[1] + radius; chunkY++) {
                    permanentlyPreloadChunks(MapUtils.encode(MapUtils.Structure.CHUNK, chunkX, chunkY, plane));
                }
            }
        }
    }

    public static void permanentlyPreloadChunks(int... chunkIds) {
        for (int chunkId : chunkIds) {
            getChunk(chunkId, true);
            PERMANENTLY_LOADED_CHUNKS.add(chunkId);
        }
    }

    public static void permanentlyPreloadChunks(Set<Integer> chunkIds) {
        for (int chunkId : chunkIds) {
            getChunk(chunkId, true);
            PERMANENTLY_LOADED_CHUNKS.add(chunkId);
        }
    }

    public static void processChunks() {
        synchronized(CHUNKS) {
            for (int chunkId : new IntOpenHashSet(ACTIVE_CHUNKS))
                ChunkManager.getChunk(chunkId).process();
        }
    }

    public static void markChunkActive(int chunkId) {
        ACTIVE_CHUNKS.add(chunkId);
    }

    public static void markChunkInactive(int chunkId) {
        ACTIVE_CHUNKS.remove(chunkId);
    }

    public static void processUpdateZones() {
        synchronized(UPDATE_ZONES) {
            for (UpdateZone c : UPDATE_ZONES.values()) {
                if (c != null)
                    c.update();
            }
        }
    }
}
