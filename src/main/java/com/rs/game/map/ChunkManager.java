package com.rs.game.map;

import com.rs.Settings;
import com.rs.cache.loaders.map.Region;
import com.rs.cache.loaders.map.RegionSize;
import com.rs.engine.pathfinder.WorldCollision;
import com.rs.game.map.instance.InstancedChunk;
import com.rs.game.model.entity.Entity;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.object.GameObject;
import com.rs.lib.game.Tile;
import com.rs.lib.game.WorldObject;
import com.rs.lib.util.Logger;
import com.rs.lib.util.MapUtils;
import com.rs.lib.util.MapUtils.Structure;
import com.rs.plugin.PluginManager;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.annotations.ServerStartupEvent;
import com.rs.plugin.events.EnterChunkEvent;
import com.rs.utils.music.Music;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSets;

import java.util.*;

@PluginEventHandler
public final class ChunkManager {
    private static final Map<Integer, Chunk> CHUNKS = Int2ObjectMaps.synchronize(new Int2ObjectOpenHashMap<>());
    private static final Set<Integer> ACTIVE_CHUNKS = IntSets.synchronize(new IntOpenHashSet());
    private static final Set<Integer> UNLOADABLE_REGIONS = IntSets.synchronize(new IntOpenHashSet());
    private static final Set<Integer> PERMANENTLY_LOADED_REGIONS = IntSets.synchronize(new IntOpenHashSet());
    private static final Map<Integer, UpdateZone> UPDATE_ZONES = new HashMap<>();
    private static final Map<Integer, List<UpdateZone>> UPDATE_ZONES_REGION = new HashMap<>();

    @ServerStartupEvent(ServerStartupEvent.Priority.FILE_IO)
    public static void loadAllMapData() {
        Logger.info(WorldCollision.class, "loadAllMapData", Runtime.getRuntime().maxMemory()/(1024*1024)+"mb heap space available. For better performance, allocate at least 1024mb or 3072mb");
        boolean preloadCollision = Settings.getConfig().isAllowHighMemUseOptimizations() && Runtime.getRuntime().maxMemory() != Integer.MAX_VALUE && Runtime.getRuntime().maxMemory() > 1024*1024*1024; //1024mb HEAP
        boolean preloadObjects = Settings.getConfig().isAllowHighMemUseOptimizations() && Runtime.getRuntime().maxMemory() != Integer.MAX_VALUE && Runtime.getRuntime().maxMemory() > 3072L *1024*1024; //3072mb HEAP

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

    public static void updateChunks(Entity entity) {
        if (entity instanceof NPC)
            WorldCollision.clipNPC((NPC) entity);
        if (entity.hasFinished()) {
            if (entity instanceof Player) {
                getChunk(entity.getLastChunkId()).removePlayerIndex(entity.getIndex());
                getUpdateZone(entity.getSceneBaseChunkId(), entity.getMapSize()).removePlayerWatcher(entity.getIndex());
            } else {
                getChunk(entity.getLastChunkId()).removeNPCIndex(entity.getIndex());
                if (entity instanceof NPC n && n.isLoadsUpdateZones())
                    getUpdateZone(entity.getSceneBaseChunkId(), entity.getMapSize()).removeNPCWatcher(entity.getIndex());
            }
            return;
        }
        int chunkId = MapUtils.encode(MapUtils.Structure.CHUNK, entity.getChunkX(), entity.getChunkY(), entity.getPlane());
        if (entity.getLastChunkId() != chunkId || entity.isForceUpdateEntityRegion()) {
            PluginManager.handle(new EnterChunkEvent(entity, chunkId));
            entity.checkMultiArea();

            if (entity instanceof Player player) {
                if(Settings.getConfig().isDebug() && player.hasStarted() && Music.getGenre(player) == null && !(getChunk(player.getChunkId()) instanceof InstancedChunk))
                    player.sendMessage(chunkId + " has no music genre!", true);
                if (entity.getLastChunkId() > 0)
                    getChunk(entity.getLastChunkId()).removePlayerIndex(entity.getIndex());
                Chunk chunk = getChunk(chunkId);
                chunk.addPlayerIndex(entity.getIndex());

                //Unlock all region music at once.
                int[] musicIds = chunk.getMusicIds();
                if (player.hasStarted() && musicIds != null)
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

    public static void loadRegionMapDataByChunkId(int chunkId) {
        synchronized (CHUNKS) {
            Chunk chunk = CHUNKS.get(chunkId);
            if (chunk == null) {
                chunk = new Chunk(chunkId);
                CHUNKS.put(chunkId, chunk);
                return;
            }
            int regionId = MapUtils.chunkToRegionId(chunkId);
            Region region = new Region(regionId);
            verifyChunksInited(region);
            region.loadRegionMap(false);
            if (!region.hasData()) {
                markAllChunksInRegionLoaded(region);
                return;
            }
            for (int x = 0; x < 64; x++)
                for (int y = 0; y < 64; y++)
                    for (int plane = 0; plane < 4; plane++)
                        WorldCollision.addFlag(Tile.of(region.getBaseX() + x, region.getBaseY() + y, plane), region.getClipFlags()[plane][x][y]);
            if (region.getObjects() == null || region.getObjects().isEmpty()) {
                markAllChunksInRegionLoaded(region);
                return;
            }
            for (WorldObject object : region.getObjects()) {
                int oCid = object.getTile().getChunkId();
                Chunk oChunk = CHUNKS.get(oCid);
                if (oChunk instanceof InstancedChunk)
                    continue;
                if (oChunk == null)
                    oChunk = new Chunk(oCid);
                oChunk.addBaseObject(new GameObject(object));
                CHUNKS.put(oCid, oChunk);
            }
            markAllChunksInRegionLoaded(region);
        }
    }

    private static void verifyChunksInited(Region region) {
        int chunkBaseId = MapUtils.encode(Structure.CHUNK, region.getBaseX() >> 3, region.getBaseY() >> 3, 0);
        for (int planeOff = 0; planeOff < 4 * Chunk.PLANE_INC; planeOff += Chunk.PLANE_INC) {
            for (int chunkXOff = 0; chunkXOff < 8 * Chunk.X_INC; chunkXOff += Chunk.X_INC) {
                for (int chunkYOff = 0; chunkYOff < 8; chunkYOff++) {
                    int cid = chunkBaseId + chunkXOff + chunkYOff + planeOff;
                    Chunk c = CHUNKS.get(cid);
                    if (c == null) {
                        c = new Chunk(cid);
                        CHUNKS.put(cid, c);
                        return;
                    }
                }
            }
        }
    }

    private static void markAllChunksInRegionLoaded(Region region) {
        int chunkBaseId = MapUtils.encode(Structure.CHUNK, region.getBaseX() >> 3, region.getBaseY() >> 3, 0);
        for (int planeOff = 0; planeOff < 4 * Chunk.PLANE_INC; planeOff += Chunk.PLANE_INC) {
            for (int chunkXOff = 0; chunkXOff < 8 * Chunk.X_INC; chunkXOff += Chunk.X_INC) {
                for (int chunkYOff = 0; chunkYOff < 8; chunkYOff++) {
                    int cid = chunkBaseId + chunkXOff + chunkYOff + planeOff;
                    Chunk c = CHUNKS.get(cid);
                    if (c == null)
                        continue;
                    c.setMapDataLoaded();
                }
            }
        }
    }

    public static Chunk getChunk(int id, boolean load) {
        synchronized (CHUNKS) {
            Chunk chunk = CHUNKS.get(id);
            if (CHUNKS.get(id) != null) {
                if (load)
                    chunk.checkLoaded();
                return chunk;
            }
            chunk = new Chunk(id);
            CHUNKS.put(id, chunk);
            if (load)
                chunk.checkLoaded();
            return chunk;
        }
    }

    public static Chunk getChunk(int id) {
        return getChunk(id, false);
    }

    public static Chunk putChunk(int id, Chunk chunk) {
        synchronized (CHUNKS) {
            return CHUNKS.put(id, chunk);
        }
    }

    public static Chunk removeChunk(int id) {
        synchronized (CHUNKS) {
            return CHUNKS.remove(id);
        }
    }

    public static UpdateZone removeUpdateZone(int baseChunkId, RegionSize size) {
        synchronized (UPDATE_ZONES) {
            UpdateZone zone = UPDATE_ZONES.remove(UpdateZone.getId(baseChunkId, size));
            if (zone == null)
                return null;
            //Logger.debug(ChunkManager.class, "removeUpdateZone", UpdateZone.getId(baseChunkId, size) + " update zone removed");
            for (int chunkId : zone.getChunkIds())
                ChunkManager.getChunk(chunkId).removeUpdateZone(zone);
            for (int regionId : zone.getRegionIds()) {
                List<UpdateZone> zones = UPDATE_ZONES_REGION.get(regionId);
                if (zones != null) {
                    zones.remove(zone);
                    if (zones.isEmpty()) {
                        UPDATE_ZONES_REGION.remove(regionId);
                        markRegionUnloadable(regionId);
                    }
                }
            }
            return zone;
        }
    }

    public static UpdateZone getUpdateZone(int baseChunkId, RegionSize size) {
        synchronized (UPDATE_ZONES) {
            int id = UpdateZone.getId(baseChunkId, size);
            UpdateZone zone = UPDATE_ZONES.get(id);
            if (zone == null) {
                zone = new UpdateZone(baseChunkId, size);
                UPDATE_ZONES.put(id, zone);
                //Logger.debug(ChunkManager.class, "getUpdateZone", UpdateZone.getId(baseChunkId, size) + " update zone added");
                for (int regionId : zone.getRegionIds()) {
                    List<UpdateZone> zones = UPDATE_ZONES_REGION.get(regionId);
                    if (zones == null) {
                        zones = new ArrayList<>();
                        zones.add(zone);
                        UPDATE_ZONES_REGION.put(regionId, zones);
                    }
                }
                for (int chunkId : zone.getChunkIds())
                    ChunkManager.getChunk(chunkId).addUpdateZone(zone);
            }
            return zone;
        }
    }

    public static void markRegionUnloadable(int regionId) {
        if (!PERMANENTLY_LOADED_REGIONS.contains(regionId)) {
            UNLOADABLE_REGIONS.add(regionId);
            //Logger.debug(ChunkManager.class, "markRegionUnloadable", regionId + " UNLOADABLE");
        }
    }

    public static void unmarkRegionUnloadable(int regionId) {
        if (!PERMANENTLY_LOADED_REGIONS.contains(regionId)) {
            UNLOADABLE_REGIONS.remove(regionId);
        }
    }

    public static void permanentlyPreloadRegions(int... regionIds) {
        for (int regionId : regionIds) {
            if (PERMANENTLY_LOADED_REGIONS.contains(regionId))
                continue;
            getChunk(Tile.of((regionId >> 8) * 64 + 32, (regionId & 0xff) * 64 + 32, 0).getChunkId(), true);
            PERMANENTLY_LOADED_REGIONS.add(regionId);
        }
    }

    public static void permanentlyPreloadRegions(Set<Integer> regionIds) {
        for (int regionId : regionIds) {
            if (PERMANENTLY_LOADED_REGIONS.contains(regionId))
                continue;
            getChunk(Tile.of((regionId >> 8) * 64 + 32, (regionId & 0xff) * 64 + 32, 0).getChunkId(), true);
            PERMANENTLY_LOADED_REGIONS.add(regionId);
        }
    }

    public static void removePermanentlyPreloadedRegions(Set<Integer> regionIds) {
        for (int regionId : regionIds) {
            if (!PERMANENTLY_LOADED_REGIONS.contains(regionId))
                continue;
            PERMANENTLY_LOADED_REGIONS.remove(regionId);
        }
    }

    public static void processChunks() {
        synchronized(CHUNKS) {
            try {
                for (int chunkId : new IntOpenHashSet(ACTIVE_CHUNKS))
                    ChunkManager.getChunk(chunkId).process();
            } catch(Throwable e) {
                Logger.handle(ChunkManager.class, "processChunks", e);
            }
        }
    }

    public static void markChunkActive(int chunkId) {
        ACTIVE_CHUNKS.add(chunkId);
        //Logger.debug(ChunkManager.class, "markChunkActive", "Marking chunk active: " + chunkId);
    }

    public static void markChunkInactive(int chunkId) {
        ACTIVE_CHUNKS.remove(chunkId);
        //Logger.debug(ChunkManager.class, "markChunkInactive", "Marking chunk inactive: " + chunkId);
    }

    public static void processUpdateZones() {
        synchronized(UPDATE_ZONES) {
            for (UpdateZone c : UPDATE_ZONES.values()) {
                if (c != null)
                    c.update();
            }
        }
    }

    public static void clearUnusedMemory() {
        synchronized (CHUNKS) {
            Iterator<Integer> iterator = UNLOADABLE_REGIONS.iterator();
            while (iterator.hasNext()) {
                int regionId = iterator.next();
                int chunkBaseId = Tile.of((regionId >> 8) * 64, (regionId & 0xff) * 64, 0).getChunkId();
                boolean skipRegion = false;
                Set<Integer> chunksToDestroy = new IntOpenHashSet();

                outerLoop:
                for (int planeOff = 0; planeOff < 4 * Chunk.PLANE_INC; planeOff += Chunk.PLANE_INC) {
                    for (int chunkXOff = 0; chunkXOff < 8 * Chunk.X_INC; chunkXOff += Chunk.X_INC) {
                        for (int chunkYOff = 0; chunkYOff < 8; chunkYOff++) {
                            int chunkId = chunkBaseId + chunkXOff + chunkYOff + planeOff;
                            if (ACTIVE_CHUNKS.contains(chunkId)) {
                                Logger.debug(ChunkManager.class, "clearUnusedMemory", "Chunk " + chunkId + " is marked as active. Skipping region.");
                                skipRegion = true;
                                break outerLoop;
                            }
                            Chunk chunk = getChunk(chunkId);
                            if (chunk != null && !(chunk instanceof InstancedChunk))
                                chunksToDestroy.add(chunkId);
                        }
                    }
                }

                if (!skipRegion) {
                    Logger.debug(ChunkManager.class, "clearUnusedMemory", "Clearing region: " + regionId);
                    chunksToDestroy.forEach(chunkId -> {
                        Chunk chunk = getChunk(chunkId);
                        if (chunk != null) {
                            chunk.clearCollisionData();
                            chunk.destroy();
                        }
                    });
                    iterator.remove();
                } else
                    Logger.debug(ChunkManager.class, "clearUnusedMemory", "Skipping region: " + regionId);
            }
        }
    }
}
