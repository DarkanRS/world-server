package com.rs.game.map;

import com.rs.cache.loaders.ObjectDefinitions;
import com.rs.cache.loaders.ObjectType;
import com.rs.game.World;
import com.rs.game.content.ItemConstants;
import com.rs.game.model.WorldProjectile;
import com.rs.game.model.entity.pathing.WorldCollision;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.object.GameObject;
import com.rs.lib.game.GroundItem;
import com.rs.lib.game.Tile;
import com.rs.lib.util.Logger;
import com.rs.lib.util.MapUtils;
import com.rs.lib.util.Utils;
import com.rs.utils.music.Music;
import com.rs.utils.spawns.ItemSpawns;
import com.rs.utils.spawns.NPCSpawns;
import com.rs.utils.spawns.ObjectSpawns;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import it.unimi.dsi.fastutil.ints.IntSets;
import it.unimi.dsi.fastutil.objects.ObjectLists;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class Chunk {
    private int chunkId;
    private boolean originalChunk;
    private int[] collisionMappings = new int[4];

    protected Set<Integer> players = IntSets.synchronize(IntSets.emptySet());
    protected Set<Integer> npcs = IntSets.synchronize(IntSets.emptySet());

    protected GameObject[][][][] baseObjects = new GameObject[4][8][8][4];
    protected Map<Integer, GameObject> removedBaseObjects = Int2ObjectMaps.synchronize(Int2ObjectMaps.emptyMap());
    protected List<GameObject> spawnedObjects = ObjectLists.synchronize(ObjectLists.emptyList());

    protected Map<Integer, Map<Integer, List<GroundItem>>> groundItems = Int2ObjectMaps.synchronize(Int2ObjectMaps.emptyMap());;
    protected List<GroundItem> groundItemList = ObjectLists.synchronize(ObjectLists.emptyList());
    protected List<WorldProjectile> projectiles = ObjectLists.synchronize(ObjectLists.emptyList());

    private AtomicBoolean loadedData = new AtomicBoolean(false);

    private int[] musicIds;

    public Chunk(int chunkId) {
        this.chunkId = chunkId;
        for (int i = 0;i < 4;i++)
            collisionMappings[i] = WorldCollision.getId(chunkId >> 14 & 0xfff, chunkId >> 3 & 0xfff, i);
        musicIds = Music.getRegionMusics(MapUtils.chunkToRegionId(chunkId));
    }

    public void setOriginalChunk() {
        originalChunk = true;
    }

    public boolean isOriginalChunk() {
        return originalChunk;
    }

    public void destroy() {
        for (int mapping : collisionMappings)
            WorldCollision.clearChunk(mapping);
    }

    public void setMusicIds(int[] musicIds) {
        this.musicIds = musicIds;
    }

    public boolean addProjectile(WorldProjectile projectile) {
        return projectiles.add(projectile);
    }

    public List<WorldProjectile> getProjectiles() {
        return projectiles;
    }

    public void removeProjectiles() {
        projectiles.clear();
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

    public void processGroundItems() {
        if (groundItems == null || groundItemList == null)
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
                for (Player player : World.getPlayersInRegionRange(item.getTile().getRegionId()))
                    if (player.hasStarted() && !player.hasFinished() && player.getUuid() != item.getVisibleToId())
                        player.getPackets().sendGroundItem(item);
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
            tileMap = Int2ObjectMaps.synchronize(Int2ObjectMaps.emptyMap());
            groundItems.put(item.getVisibleToId(), tileMap);
        }
        List<GroundItem> items = tileMap.get(item.getTile().getTileHash());
        if (items == null) {
            items = ObjectLists.synchronize(ObjectLists.emptyList());
            tileMap.put(item.getTile().getTileHash(), items);
        }
        items.add(item);
        return true;
    }

    public boolean addGroundItem(GroundItem item) {
        Map<Integer, List<GroundItem>> tileMap = groundItems.get(item.getVisibleToId());
        if (tileMap == null) {
            tileMap = Int2ObjectMaps.synchronize(Int2ObjectMaps.emptyMap());
            groundItems.put(item.getVisibleToId(), tileMap);
        }
        List<GroundItem> items = tileMap.get(item.getTile().getTileHash());
        if (items == null) {
            items = ObjectLists.synchronize(ObjectLists.emptyList());
            tileMap.put(item.getTile().getTileHash(), items);
        }
        GroundItem existing = getGroundItem(item.getId(), item.getTile(), item.getVisibleToId());
        if (item.getDefinitions().isStackable() && existing != null) {
            int oldAmount = existing.getAmount();
            existing.setAmount(existing.getAmount() + item.getAmount());
            if (existing.getCreatorUsername() != null && World.getPlayerByUsername(existing.getCreatorUsername()) != null)
                World.getPlayerByUsername(existing.getCreatorUsername()).getPackets().sendSetGroundItemAmount(existing, oldAmount);
            else
                for (Player player : World.getPlayersInRegionRange(item.getTile().getRegionId()))
                    if (player.hasStarted() && !player.hasFinished())
                        player.getPackets().sendSetGroundItemAmount(existing, oldAmount);
            return false;
        }
        groundItemList.add(item);
        items.add(item);
        if (item.isPrivate() && World.getPlayerByUsername(item.getCreatorUsername()) != null)
            World.getPlayerByUsername(item.getCreatorUsername()).getPackets().sendGroundItem(item);
        else
            for (Player player : World.getPlayersInRegionRange(item.getTile().getRegionId()))
                if (player.hasStarted() && !player.hasFinished())
                    player.getPackets().sendGroundItem(item);
        return true;
    }

    public boolean deleteGroundItem(GroundItem item) {
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
                for (Player player : World.getPlayersInRegionRange(item.getTile().getRegionId()))
                    if (player.hasStarted() && !player.hasFinished())
                        player.getPackets().removeGroundItem(item);
            return true;
        }
        return false;
    }

    public Map<Integer, Map<Integer, List<GroundItem>>> getGroundItems() {
        return groundItems;
    }

    public void clip(GameObject object) {
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
                WorldCollision.addWall(object.getTile(), type, rotation, defs.blocks(), !defs.ignoresPathfinder);
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
                WorldCollision.addObject(object.getTile(), sizeX, sizeY, defs.blocks(), !defs.ignoresPathfinder);
//			if (defs.clipType != 0)
//				clipMapProj.addObject(plane, x, y, sizeX, sizeY, defs.blocks(), !defs.ignoresPathfinder);
                break;
            case GROUND_DECORATION:
                if (defs.clipType == 1)
                    WorldCollision.addBlockWalkAndProj(object.getTile());
                break;
            default:
                break;
        }
    }

    public void unclip(Tile tile) {
        WorldCollision.setFlags(tile, 0);
    }

    public void unclip(GameObject object) {
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
                WorldCollision.removeWall(object.getTile(), type, rotation, defs.blocks(), !defs.ignoresPathfinder);
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
                WorldCollision.removeObject(object.getTile(), sizeX, sizeY, defs.blocks(), !defs.ignoresPathfinder);
                break;
            case GROUND_DECORATION:
                if (defs.clipType == 1)
                    WorldCollision.removeBlockWalkAndProj(object.getTile());
                break;
            default:
                break;
        }
    }

    public void checkLoaded() {
        if (!loadedData.get()) {
            loadedData.set(true);
            NPCSpawns.loadNPCSpawns(chunkId);
            ItemSpawns.loadItemSpawns(chunkId);
            ObjectSpawns.loadObjectSpawns(chunkId);
        }
    }

    public void addBaseObject(GameObject obj) {
        baseObjects[obj.getPlane()][obj.getTile().getXInChunk()][obj.getTile().getYInChunk()][obj.getSlot()] = obj;
        clip(obj);
    }

    public void spawnObject(GameObject newObj, boolean clip) {
        GameObject baseObj = baseObjects[newObj.getPlane()][newObj.getTile().getXInChunk()][newObj.getTile().getYInChunk()][newObj.getSlot()];
        GameObject spawnedObj = getSpawnedObjectWithSlot(newObj.getTile(), newObj.getSlot());

        if (spawnedObj != null) {
            spawnedObjects.remove(spawnedObj);
            if (clip)
                unclip(spawnedObj, localX, localY);
        }

        if (newObj.equals(baseObj)) {
            newObj = baseObj;
            deleteRemovedObject(baseObj);
        } else if (baseObj != newObj) {
            if (!newObj.equals(baseObj))
                addRemovedObject(baseObj);
            spawnedObjects.add(newObj);
            if (clip && baseObj != null)
                unclip(baseObj, localX, localY);
        } else if (spawnedObj == null) {
            Logger.info(Region.class, "spawnObject", "Requested object to spawn is already spawned. (Shouldnt happen) " + baseObj);
            return;
        }

        if (clip)
            clip(newObj, localX, localY);
        for (Player player : World.getPlayersInRegionRange(getRegionId())) {
            if (player == null || !player.hasStarted() || player.hasFinished())
                return;
            player.getPackets().sendAddObject(newObj);
        }
    }

    public void removeObject(GameObject object, int plane, int localX, int localY) {
        if (objects == null)
            objects = new GameObject[4][64][64][4];
        GameObject mapObject = objects[plane][localX][localY][object.getSlot()];
        GameObject removed = getRemovedObject(mapObject);
        boolean replace = false;
        if (removed != null) {
            deleteRemovedObject(mapObject);
            clip(mapObject, localX, localY);
            replace = true;
        }
        GameObject spawned = getSpawnedObjectWithSlot(plane, localX, localY, object.getSlot());
        if (spawned != null) {
            spawnedObjects.remove(object);
            unclip(object, localX, localY);
            if (mapObject != null)
                clip(mapObject, localX, localY);
            replace = true;
        } else if (object.equals(mapObject)) {
            unclip(object, localX, localY);
            addRemovedObject(mapObject);
        } else {
            Logger.info(Region.class, "removeObject", "Requested object to spawn is already spawned. (Shouldnt happen) " + mapObject);
            return;
        }
        for (Player player : World.getPlayersInRegionRange(getRegionId())) {
            if (player == null || !player.hasStarted() || player.hasFinished())
                return;
            if (replace && mapObject != null)
                player.getPackets().sendAddObject(mapObject);
            else
                player.getPackets().sendRemoveObject(object);
        }
    }

    public void addRemovedObject(GameObject object) {
        if (object == null)
            return;
        if (removedObjects == null)
            removedObjects = new ConcurrentHashMap<>();
        removedObjects.put(object.hashCode(), object);
    }

    public void deleteRemovedObject(GameObject object) {
        if (removedObjects == null || object == null)
            return;
        removedObjects.remove(object.hashCode());
    }

    public GameObject getRemovedObject(GameObject object) {
        if (removedObjects == null || object == null)
            return null;
        return removedObjects.get(object.hashCode());
    }

    public GameObject getObject(int plane, int x, int y) {
        GameObject[] objects = getObjects(plane, x, y);
        if (objects == null)
            return null;
        for (GameObject object : objects) {
            if (object == null || getRemovedObject(object) != null)
                continue;
            return object;
        }
        return getSpawnedObject(Tile.of(x + ((regionId >> 8) * 64), y + ((regionId & 0xff) * 64), plane));
    }

    public GameObject getObject(int plane, int x, int y, ObjectType type) {
        GameObject[] objects = getObjects(plane, x, y);
        if (objects == null)
            return null;
        for (GameObject object : objects) {
            if (object == null || getRemovedObject(object) != null)
                continue;
            if (object.getType() == type)
                return object;
        }
        return getSpawnedObject(Tile.of(x + ((regionId >> 8) * 64), y + ((regionId & 0xff) * 64), plane), type);
    }

    public GameObject[] getObjects(int plane, int x, int y) {
        checkLoadMap();
        // if objects just loaded now will return null, anyway after they load
        // will return correct so np
        if (objects == null)
            return null;
        return objects[plane][x][y];
    }

    public List<GameObject> getObjects() {
        if (objects == null)
            return null;
        List<GameObject> list = new ArrayList<>();
        for (GameObject[][][] object : objects) {
            if (object == null)
                continue;
            for (GameObject[][] element : object) {
                if (element == null)
                    continue;
                for (int y = 0; y < element.length; y++) {
                    if (element[y] == null)
                        continue;
                    for (GameObject o : element[y])
                        if (o != null)
                            list.add(o);
                }
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

    public GameObject[] getAllObjects(int plane, int x, int y) {
        if (objects == null)
            return null;
        return objects[plane][x][y];
    }

    public List<GameObject> getAllObjects() {
        if (objects == null)
            return null;
        List<GameObject> list = new ArrayList<>();
        for (int z = 0; z < 4; z++)
            for (int x = 0; x < 64; x++)
                for (int y = 0; y < 64; y++) {
                    if (objects[z][x][y] == null)
                        continue;
                    for (GameObject o : objects[z][x][y])
                        if (o != null)
                            list.add(o);
                }
        return list;
    }

    public GameObject getObjectWithType(int plane, int x, int y, ObjectType type) {
        GameObject object = getObjectWithSlot(plane, x, y, type.slot);
        return object != null && object.getType() == type ? object : null;
    }

    public GameObject getObjectWithSlot(int plane, int x, int y, int slot) {
        if (objects == null || objects[plane] == null || objects[plane][x] == null || objects[plane][x][y] == null)
            return null;
        GameObject o = getSpawnedObjectWithSlot(plane, x, y, slot);
        if (o == null) {
            GameObject real = objects[plane][x][y][slot];
            return real == null ? null : getRemovedObject(real) != null ? null : real;
        }
        return o;
    }

    public boolean containsObjectWithId(int plane, int x, int y, int id) {
        GameObject object = getObjectWithId(plane, x, y, id);
        return object != null && object.getId() == id;
    }

    public GameObject getSpawnedObjectWithSlot(Tile tile, int slot) {
        for (GameObject object : spawnedObjects)
            if (object.getTile().matches(tile) && object.getSlot() == slot)
                return object;
        return null;
    }

    public GameObject getObjectWithId(int plane, int x, int y, int id) {
        if (objects == null || objects[plane] == null || objects[plane][x] == null || objects[plane][x][y] == null)
            return null;
        for (int i = 0; i < 4; i++) {
            GameObject object = objects[plane][x][y][i];
            if (object != null && getRemovedObject(object) != null)
                object = null;
            if (object != null && object.getId() == id) {
                GameObject spawned = getSpawnedObjectWithSlot(plane, x, y, object.getSlot());
                return spawned == null ? object : spawned;
            }
        }
        for (GameObject object : spawnedObjects)
            if (object.getTile().getXInRegion() == x && object.getTile().getYInRegion() == y && object.getPlane() == plane && object.getId() == id)
                return object;
        return null;
    }

    public boolean objectExists(GameObject object) {
        return containsObjectWithId(object.getPlane(), object.getTile().getXInRegion(), object.getTile().getYInRegion(), object.getId());
    }

    public GameObject getObjectWithId(int id, int plane) {
        if (objects == null)
            return null;
        for (GameObject object : spawnedObjects)
            if (object.getId() == id && object.getPlane() == plane)
                return object;
        for (int x = 0; x < 64; x++)
            for (int y = 0; y < 64; y++)
                for (int slot = 0; slot < objects[plane][x][y].length; slot++) {
                    GameObject object = objects[plane][x][y][slot];
                    if (object != null && object.getId() == id && getRemovedObject(object) == null)
                        return object;
                }
        return null;
    }

    public GameObject getRealObject(GameObject spawnObject) {
        int absX = (regionId >> 8) * 64;
        int absY = (regionId & 0xff) * 64;
        int localX = spawnObject.getX() - absX;
        int localY = spawnObject.getY() - absY;
        GameObject[] mapObjects = getObjects(spawnObject.getPlane(), localX, localY);
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

    private void loadNPCSpawns() {
        NPCSpawns.loadNPCSpawns(chunkId);
    }

    private void loadObjectSpawns() {
        ObjectSpawns.loadObjectSpawns(chunkId);
    }

    private void loadItemSpawns() {
        ItemSpawns.loadItemSpawns(chunkId);
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
}
