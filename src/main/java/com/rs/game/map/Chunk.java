package com.rs.game.map;

import com.rs.cache.loaders.ObjectType;
import com.rs.game.World;
import com.rs.game.content.ItemConstants;
import com.rs.game.model.WorldProjectile;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.pathing.WorldCollision;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.object.GameObject;
import com.rs.lib.game.*;
import com.rs.lib.net.packets.encoders.Sound;
import com.rs.lib.net.packets.encoders.updatezone.*;
import com.rs.lib.util.Logger;
import com.rs.lib.util.MapUtils;
import com.rs.lib.util.MapUtils.Structure;
import com.rs.lib.util.Utils;
import com.rs.utils.music.Music;
import com.rs.utils.spawns.ItemSpawns;
import com.rs.utils.spawns.NPCSpawns;
import com.rs.utils.spawns.ObjectSpawns;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSets;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectLists;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSets;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Chunk {
    public static final int PLANE_INC = 0x400000;
    public static final int X_INC = 0x800;

    private int id;
    private int x;
    private int y;
    private int plane;

    private List<UpdateZonePacketEncoder> updates = ObjectLists.synchronize(new ObjectArrayList<>());

    protected Set<Integer> players = IntSets.synchronize(new IntOpenHashSet());
    protected Set<Integer> npcs = IntSets.synchronize(new IntOpenHashSet());
    private Set<UpdateZone> updateZones = ObjectSets.synchronize(new ObjectOpenHashSet<>());

    protected GameObject[][][] baseObjects = new GameObject[8][8][4];
    protected Map<Integer, GameObject> removedBaseObjects = Int2ObjectMaps.synchronize(new Int2ObjectOpenHashMap<>());
    protected List<GameObject> spawnedObjects = ObjectLists.synchronize(new ObjectArrayList<>());

    protected Set<GameObject> flaggedObjectsForTickProcessing = ObjectSets.synchronize(new ObjectOpenHashSet<>());

    protected Map<Integer, Map<Integer, List<GroundItem>>> groundItems = Int2ObjectMaps.synchronize(new Int2ObjectOpenHashMap<>());
    protected List<GroundItem> groundItemList = ObjectLists.synchronize(new ObjectArrayList<>());

    protected volatile boolean loadingSpawnData = false;
    protected volatile boolean loadedSpawnData = false;

    protected volatile boolean loadingMapData = false;
    protected volatile boolean loadedMapData = false;

    private int[] musicIds;

    public Chunk(int chunkId) {
        this.id = chunkId;
        int[] coords = MapUtils.decode(Structure.CHUNK, chunkId);
        this.x = coords[0];
        this.y = coords[1];
        this.plane = coords[2];
        musicIds = Music.getRegionMusics(MapUtils.chunkToRegionId(chunkId));
    }

    public void clearCollisionData() {
        WorldCollision.clearChunk(id);
    }

    public void setMusicIds(int[] musicIds) {
        this.musicIds = musicIds;
    }

    public void addProjectile(WorldProjectile projectile) {
        addChunkUpdate(new ProjAnim(projectile.getFromTile().getChunkLocalHash(), projectile));
    }

    public void addSpotAnim(Tile tile, SpotAnim spotAnim) {
        addChunkUpdate(new TileSpotAnim(tile.getChunkLocalHash(), spotAnim));
    }

    public void addObjectAnim(WorldObject object, Animation anim) {
        addChunkUpdate(new ObjectAnim(object.getTile().getChunkLocalHash(), object, anim));
    }

    public void addSound(Tile tile, Sound sound) {
        addChunkUpdate(new TileSound(tile.getChunkLocalHash(), sound));
    }

    public List<GroundItem> getAllGroundItems() {
        return groundItemList;
    }

    public GroundItem getGroundItem(int itemId, Tile tile, int playerId) {
        Map<Integer, List<GroundItem>> tileMap = groundItems.get(playerId);
        if (tileMap == null)
            return null;
        List<GroundItem> items = tileMap.get(tile.getTileHash());
        if (items == null)
            return null;
        for (GroundItem item : items)
            if (item.getId() == itemId)
                return item;
        return null;
    }

    public boolean itemExists(GroundItem item) {
        return getGroundItem(item.getId(), item.getTile(), item.getVisibleToId()) != null;
    }

    public GroundItem getGroundItem(int itemId, Tile tile, Player player) {
        GroundItem item = getGroundItem(itemId, tile, player == null ? 0 : player.getUuid());
        if (item == null)
            item = getGroundItem(itemId, tile, 0);
        return item;
    }

    public void processSpawnedObjects() {
        if (flaggedObjectsForTickProcessing.isEmpty())
            return;
        Set<GameObject> toRemove = new ObjectOpenHashSet<>();
        for (GameObject object : flaggedObjectsForTickProcessing)
            if (!object.process())
                toRemove.add(object);
        for (GameObject object : toRemove)
            unflagForProcess(object);
    }

    public void flagForProcess(GameObject object) {
        flaggedObjectsForTickProcessing.add(object);
    }

    public void unflagForProcess(GameObject object) {
        flaggedObjectsForTickProcessing.remove(object);
    }

    public void processGroundItems() {
        if (groundItems.isEmpty())
            return;
        List<GroundItem> toRemove = new ArrayList<>();
        for (GroundItem item : groundItemList) {
            item.tick();
            if (item.getDeleteTime() != -1 && item.getTicks() >= item.getDeleteTime())
                toRemove.add(item);
            if (item.getPrivateTime() != -1 && item.getTicks() >= item.getPrivateTime() && item.isPrivate()) {
                if (!item.isInvisible() || !ItemConstants.isTradeable(item))
                    continue;
                removeItemFromOwnerMapping(item);
                Player creator = item.getCreatorUsername() != null ? World.getPlayerByUsername(item.getCreatorUsername()) : null;
                addChunkUpdate(new RevealGroundItem(item.getTile().getChunkLocalHash(), item, creator == null ? -1 : creator.getIndex()));
                item.removeOwner();
                addItemToOwnerMapping(item);
            }
        }
        for (GroundItem item : toRemove)
            deleteGroundItem(item);
    }

    public boolean removeItemFromOwnerMapping(GroundItem item) {
        int tileHash = item.getTile().getTileHash();
        Map<Integer, List<GroundItem>> tileMap = groundItems.get(item.getVisibleToId());
        if (tileMap == null)
            return false;
        List<GroundItem> items = tileMap.get(tileHash);
        if (items == null)
            return false;
        if (items.remove(item)) {
            if (items.isEmpty())
                tileMap.remove(tileHash);
            if (tileMap.isEmpty())
                groundItems.remove(item.getVisibleToId());
            return true;
        }
        return false;
    }

    public boolean addItemToOwnerMapping(GroundItem item) {
        Map<Integer, List<GroundItem>> tileMap = groundItems.get(item.getVisibleToId());
        if (tileMap == null) {
            tileMap = Int2ObjectMaps.synchronize(new Int2ObjectOpenHashMap<>());
            groundItems.put(item.getVisibleToId(), tileMap);
        }
        List<GroundItem> items = tileMap.get(item.getTile().getTileHash());
        if (items == null) {
            items = ObjectLists.synchronize(new ObjectArrayList<>());
            tileMap.put(item.getTile().getTileHash(), items);
        }
        items.add(item);
        return true;
    }

    public boolean addGroundItem(GroundItem item) {
        ChunkManager.markChunkActive(id);
        Map<Integer, List<GroundItem>> tileMap = groundItems.get(item.getVisibleToId());
        if (tileMap == null) {
            tileMap = Int2ObjectMaps.synchronize(new Int2ObjectOpenHashMap<>());
            groundItems.put(item.getVisibleToId(), tileMap);
        }
        List<GroundItem> items = tileMap.get(item.getTile().getTileHash());
        if (items == null) {
            items = ObjectLists.synchronize(new ObjectArrayList<>());
            tileMap.put(item.getTile().getTileHash(), items);
        }
        GroundItem existing = getGroundItem(item.getId(), item.getTile(), item.getVisibleToId());
        if (item.getDefinitions().isStackable() && !item.containsMetaData() && existing != null) {
            int oldAmount = existing.getAmount();
            existing.setAmount(existing.getAmount() + item.getAmount());
            if (existing.getCreatorUsername() != null && World.getPlayerByUsername(existing.getCreatorUsername()) != null)
                World.getPlayerByUsername(existing.getCreatorUsername()).getPackets().sendSetGroundItemAmount(existing, oldAmount);
            else
                addChunkUpdate(new SetGroundItemAmount(existing.getTile().getChunkLocalHash(), existing, oldAmount));
            return false;
        }
        groundItemList.add(item);
        items.add(item);
        if (item.isPrivate() && World.getPlayerByUsername(item.getCreatorUsername()) != null)
            World.getPlayerByUsername(item.getCreatorUsername()).getPackets().sendGroundItem(item);
        else
            addChunkUpdate(new CreateGroundItem(item.getTile().getChunkLocalHash(), item));
        return true;
    }

    public boolean deleteGroundItem(GroundItem item) {
        ChunkManager.markChunkActive(id);
        int tileHash = item.getTile().getTileHash();
        Map<Integer, List<GroundItem>> tileMap = groundItems.get(item.getVisibleToId());
        if (tileMap == null)
            return false;
        List<GroundItem> items = tileMap.get(tileHash);
        if (items == null)
            return false;
        groundItemList.remove(item);
        if (items.remove(item)) {
            if (items.isEmpty())
                tileMap.remove(tileHash);
            if (tileMap.isEmpty())
                groundItems.remove(item.getVisibleToId());
            if (item.isPrivate() && World.getPlayerByUsername(item.getCreatorUsername()) != null)
                World.getPlayerByUsername(item.getCreatorUsername()).getPackets().removeGroundItem(item);
            else
                addChunkUpdate(new RemoveGroundItem(item.getTile().getChunkLocalHash(), item));
            return true;
        }
        return false;
    }

    public Map<Integer, Map<Integer, List<GroundItem>>> getGroundItems() {
        return groundItems;
    }

    public void checkLoaded() {
        if (!loadingMapData) {
            loadingMapData = true;
            ChunkManager.loadRegionMapDataByChunkId(id);
            loadedMapData = true;
        }
        if (!loadingSpawnData) {
            loadingSpawnData = true;
            NPCSpawns.loadNPCSpawns(id);
            ItemSpawns.loadItemSpawns(id);
            ObjectSpawns.loadObjectSpawns(id);
            loadedSpawnData = true;
        }
    }

    public void setMapDataLoaded() {
        loadingMapData = true;
        loadedMapData = true;
    }

//    public void loadMapFromCache() {
//        baseObjects = new GameObject[8][8][4];
//        Region region = new Region(getRegionId());
//        region.loadRegionMap(false);
//        for (int xOff = 0;xOff < 8;xOff++)
//            for (int yOff = 0;yOff < 8;yOff++)
//                WorldCollision.addFlag(Tile.of(getBaseX()+xOff, getBaseY()+yOff, plane), region.getClipFlags()[plane][(getBaseX()-region.getBaseX())+xOff][(getBaseY()-region.getBaseY())+yOff]);
//        if (region.getObjects() != null && !region.getObjects().isEmpty()) {
//            for (WorldObject object : region.getObjects()) {
//                if (object.getTile().getChunkId() != id)
//                    continue;
//                addBaseObject(new GameObject(object));
//            }
//        }
//    }

    public boolean isLoaded() {
        return loadedSpawnData && loadedMapData;
    }

    public void addBaseObject(GameObject obj) {
        baseObjects[obj.getTile().getXInChunk()][obj.getTile().getYInChunk()][obj.getSlot()] = obj;
        WorldCollision.clip(obj);
    }

    public void spawnObject(GameObject newObj, boolean clip) {
        GameObject baseObj = baseObjects[newObj.getTile().getXInChunk()][newObj.getTile().getYInChunk()][newObj.getSlot()];
        GameObject spawnedObj = getSpawnedObjectWithSlot(newObj.getTile(), newObj.getSlot());

        if (spawnedObj != null) {
            spawnedObjects.remove(spawnedObj);
            if (clip)
                WorldCollision.unclip(spawnedObj);
        }

        if (newObj.equals(baseObj)) {
            newObj = baseObj;
            deleteRemovedObject(baseObj);
        } else if (baseObj != newObj) {
            if (!newObj.equals(baseObj))
                addRemovedObject(baseObj);
            ChunkManager.markChunkActive(id);
            spawnedObjects.add(newObj);
            if (clip && baseObj != null)
                WorldCollision.unclip(baseObj);
        } else if (spawnedObj == null) {
            Logger.info(Chunk.class, "spawnObject", "Requested object to spawn is already spawned. (Shouldnt happen) " + baseObj);
            return;
        }

        if (clip)
            WorldCollision.clip(newObj);
        addChunkUpdate(new AddObject(newObj.getTile().getChunkLocalHash(), newObj));
    }

    public void removeObject(GameObject toRemove) {
        GameObject baseObject = baseObjects[toRemove.getTile().getXInChunk()][toRemove.getTile().getYInChunk()][toRemove.getSlot()];
        GameObject removedBaseObject = getRemovedObject(baseObject);
        boolean replace = false;
        if (removedBaseObject != null) {
            deleteRemovedObject(baseObject);
            WorldCollision.clip(baseObject);
            replace = true;
        }
        GameObject spawned = getSpawnedObjectWithSlot(toRemove.getTile(), toRemove.getSlot());
        if (spawned != null) {
            spawnedObjects.remove(toRemove);
            unflagForProcess(toRemove);
            WorldCollision.unclip(toRemove);
            if (baseObject != null)
                WorldCollision.clip(baseObject);
            replace = true;
        } else if (toRemove.equals(baseObject)) {
            unflagForProcess(toRemove);
            WorldCollision.unclip(toRemove);
            addRemovedObject(baseObject);
        } else {
            Logger.info(Chunk.class, "removeObject", "Requested object to spawn is already spawned. (Shouldnt happen) " + baseObject);
            return;
        }
        if (replace && baseObject != null)
            addChunkUpdate(new AddObject(baseObject.getTile().getChunkLocalHash(), baseObject));
        else
            addChunkUpdate(new RemoveObject(toRemove.getTile().getChunkLocalHash(), toRemove));
    }

    public void addRemovedObject(GameObject object) {
        if (removedBaseObjects == null || object == null)
            return;
        removedBaseObjects.put(object.positionHashCode(), object);
    }

    public void deleteRemovedObject(GameObject object) {
        if (removedBaseObjects == null || object == null)
            return;
        removedBaseObjects.remove(object.positionHashCode());
    }

    public GameObject getRemovedObject(GameObject object) {
        if (removedBaseObjects == null || object == null)
            return null;
        return removedBaseObjects.get(object.positionHashCode());
    }

    public GameObject getObject(Tile tile) {
        GameObject[] objects = getBaseObjects(tile);
        if (objects == null)
            return null;
        for (GameObject object : objects) {
            if (object == null || getRemovedObject(object) != null)
                continue;
            return object;
        }
        return getSpawnedObject(tile);
    }

    public GameObject getObject(Tile tile, ObjectType type) {
        GameObject[] objects = getBaseObjects(tile);
        if (objects == null)
            return null;
        for (GameObject object : objects) {
            if (object == null || getRemovedObject(object) != null)
                continue;
            if (object.getType() == type)
                return object;
        }
        return getSpawnedObject(tile, type);
    }

    public GameObject[] getBaseObjects(Tile tile) {
        GameObject[] objs = baseObjects[tile.getXInChunk()][tile.getYInChunk()];
        return objs == null ? new GameObject[0] : objs;
    }

    public GameObject[] getBaseObjects(int localX, int localY) {
        return getBaseObjects(Tile.of(getBaseX()+localX, getBaseY()+localY, plane));
    }

    public List<GameObject> getBaseObjects() {
        if (baseObjects == null)
            return null;
        List<GameObject> list = new ArrayList<>();
        for (GameObject[][] object : baseObjects) {
            if (object == null)
                continue;
            for (GameObject[] element : object) {
                if (element == null)
                    continue;
                for (GameObject o : element)
                    if (o != null)
                        list.add(o);
            }
        }
        return list;
    }

    public GameObject getSpawnedObject(Tile tile) {
        if (spawnedObjects == null)
            return null;
        for (GameObject object : spawnedObjects)
            if (object.getX() == tile.getX() && object.getY() == tile.getY() && object.getPlane() == tile.getPlane())
                return object;
        return null;
    }

    public GameObject getSpawnedObject(Tile tile, ObjectType type) {
        if (spawnedObjects == null)
            return null;
        for (GameObject object : spawnedObjects)
            if (object.getType() == type && object.getX() == tile.getX() && object.getY() == tile.getY() && object.getPlane() == tile.getPlane())
                return object;
        return null;
    }

    public List<GameObject> getSpawnedObjects() {
        return spawnedObjects == null ? new ArrayList<>() : spawnedObjects;
    }

    public Map<Integer, GameObject> getRemovedObjects() {
        return removedBaseObjects;
    }

    public List<GameObject> getAllBaseObjects(boolean ignoreRemoved) {
        if (baseObjects == null)
            return null;
        List<GameObject> list = new ObjectArrayList<>();
        for (int x = 0; x < 8; x++)
            for (int y = 0; y < 8; y++) {
                if (baseObjects[x][y] == null)
                    continue;
                for (GameObject o : baseObjects[x][y])
                    if (o != null && (ignoreRemoved || getRemovedObject(o) != null))
                        list.add(o);
            }
        return list;
    }

    public List<GameObject> getAllObjects() {
        List<GameObject> list = new ObjectArrayList<>();
        for (GameObject base : getAllBaseObjects(false))
            if (!removedBaseObjects.containsValue(base))
                list.add(base);
        list.addAll(spawnedObjects);
        return list;
    }

    public GameObject getObjectWithType(Tile tile, ObjectType type) {
        GameObject object = getObjectWithSlot(tile, type.slot);
        return object != null && object.getType() == type ? object : null;
    }

    public GameObject getObjectWithSlot(Tile tile, int slot) {
        GameObject o = getSpawnedObjectWithSlot(tile, slot);
        if (o == null) {
            GameObject real = getBaseObjects(tile)[slot];
            return real == null ? null : getRemovedObject(real) != null ? null : real;
        }
        return o;
    }

    public boolean containsObjectWithId(Tile tile, int id) {
        GameObject object = getObjectWithId(tile, id);
        return object != null && object.getId() == id;
    }

    public GameObject getSpawnedObjectWithSlot(Tile tile, int slot) {
        for (GameObject object : spawnedObjects)
            if (object.getTile().matches(tile) && object.getSlot() == slot)
                return object;
        return null;
    }

    public GameObject getObjectWithId(Tile tile, int id) {
        for (int i = 0; i < 4; i++) {
            GameObject object = getBaseObjects(tile)[i];
            if (object != null && getRemovedObject(object) != null)
                object = null;
            if (object != null && object.getId() == id) {
                GameObject spawned = getSpawnedObjectWithSlot(tile, object.getSlot());
                return spawned == null ? object : spawned;
            }
        }
        for (GameObject object : spawnedObjects)
            if (tile.matches(object.getTile()) && object.getId() == id)
                return object;
        return null;
    }

    public boolean objectExists(GameObject object) {
        return containsObjectWithId(object.getTile(), object.getId());
    }

    public GameObject getObjectWithId(int id, int plane) {
        if (baseObjects == null)
            return null;
        for (GameObject object : spawnedObjects)
            if (object.getId() == id && object.getPlane() == plane)
                return object;
        for (int x = 0; x < 64; x++)
            for (int y = 0; y < 64; y++)
                for (int slot = 0; slot < baseObjects[x][y].length; slot++) {
                    GameObject object = baseObjects[x][y][slot];
                    if (object != null && object.getId() == id && getRemovedObject(object) == null)
                        return object;
                }
        return null;
    }

    public GameObject getRealObject(GameObject spawnObject) {
        GameObject[] mapObjects = getBaseObjects(spawnObject.getTile());
        if (mapObjects == null)
            return null;
        for (GameObject object : mapObjects) {
            if (object == null)
                continue;
            if (object.getType() == spawnObject.getType())
                return object;
        }
        return null;
    }

    public int getMusicId() {
        if (musicIds == null)
            return -1;
        if (musicIds.length == 1)
            return musicIds[0];
        return musicIds[Utils.getRandomInclusive(musicIds.length - 1)];
    }

    public int[] getMusicIds() {
        return musicIds;
    }

    public Set<Integer> getPlayerIndexes() {
        return players;
    }

    public Set<Integer> getNPCsIndexes() {
        return npcs;
    }

    public void addPlayerIndex(int index) {
        players.add(index);
    }

    public void addNPCIndex(int index) {
        npcs.add(index);
    }

    public boolean removePlayerIndex(Integer index) {
        return players.remove(index);
    }

    public boolean removeNPCIndex(Object index) {
        return npcs.remove(index);
    }

    public void addChunkUpdate(UpdateZonePacketEncoder update) {
        ChunkManager.markChunkActive(id);
        updates.add(update);
    }

    public List<UpdateZonePacketEncoder> getUpdates() {
        return updates;
    }

    public int getId() {
        return id;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getPlane() {
        return plane;
    }

    public int getRenderChunkX() {
        return x;
    }

    public int getRenderChunkY() {
        return y;
    }

    public int getRenderPlane() {
        return plane;
    }

    public int getRenderRegionId() {
        return (((getRenderChunkX() / 8) << 8) + (getRenderChunkY() / 8));
    }

    public int getRotation() {
        return 0;
    }

    public void process() {
        updates.clear();
        processGroundItems();
        processSpawnedObjects();
        if (groundItems.isEmpty() && spawnedObjects.isEmpty() && flaggedObjectsForTickProcessing.isEmpty())
            ChunkManager.markChunkInactive(id);
    }

    public int getBaseX() {
        return x << 3;
    }

    public int getBaseY() {
        return y << 3;
    }

    public int getRegionX() {
        return x >> 3;
    }

    public int getRegionY() {
        return y >> 3;
    }

    public int getRegionId() {
        return (getRegionX() << 8) + getRegionY();
    }

    public void init(Player player) {
        List<UpdateZonePacketEncoder> initUpdates = new ObjectArrayList<>();
        for (GroundItem item : getAllGroundItems()) {
            if (item.isPrivate() && item.getVisibleToId() != player.getUuid())
                continue;
            initUpdates.add(new CreateGroundItem(item.getTile().getChunkLocalHash(), item));
        }
        for (GameObject object : new ArrayList<>(getRemovedObjects().values()))
            initUpdates.add(new RemoveObject(object.getTile().getChunkLocalHash(), object));
        for (GameObject object : getSpawnedObjects())
            initUpdates.add(new AddObject(object.getTile().getChunkLocalHash(), object));
        for (GameObject object : flaggedObjectsForTickProcessing)
            initUpdates.add(new AddObject(object.getTile().getChunkLocalHash(), object));
        for (GameObject object : getAllObjects()) {
            if (object.getMeshModifier() != null)
                initUpdates.add(new CustomizeObject(object.getTile().getChunkLocalHash(), object.getMeshModifier().getObject(), object.getMeshModifier().getModelIds(), object.getMeshModifier().getModifiedColors(), object.getMeshModifier().getModifiedTextures()));
        }
        if (!initUpdates.isEmpty()) {
            player.getSession().writeToQueue(new UpdateZoneFull(player.getSceneBaseChunkId(), getId()));
            for (UpdateZonePacketEncoder packet : initUpdates)
                player.getSession().writeToQueue(packet);
        }
    }

    public void destroy() {
        loadingMapData = false;
        loadedMapData = false;
        if (getAllGroundItems() != null)
            getAllGroundItems().clear();
        if (getGroundItems() != null)
            getGroundItems().clear();
        getBaseObjects().clear();
        getSpawnedObjects().clear();
        getRemovedObjects().clear();
        for (int npcIndex : new IntOpenHashSet(npcs)) {
            NPC npc = World.getNPCs().get(npcIndex);
            if (npc == null)
                continue;
            npc.finish();
        }
        ChunkManager.removeChunk(id);
        for (int playerIndex : players) {
            Player player = World.getPlayers().get(playerIndex);
            if (player == null || !player.hasStarted() || player.hasFinished())
                continue;
            player.setForceNextMapLoadRefresh(true);
            player.loadMapRegions();
        }
    }

    public void addUpdateZone(UpdateZone zone) {
        updateZones.add(zone);
        ChunkManager.unmarkRegionUnloadable(getRegionId());
    }

    public void removeUpdateZone(UpdateZone zone) {
        updateZones.remove(zone);
    }

    public Set<UpdateZone> getUpdateZones() {
        return updateZones;
    }
}
