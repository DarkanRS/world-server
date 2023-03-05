package com.rs.game.map;

import com.rs.cache.loaders.ObjectDefinitions;
import com.rs.cache.loaders.ObjectType;
import com.rs.game.World;
import com.rs.game.content.ItemConstants;
import com.rs.game.model.WorldProjectile;
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
import it.unimi.dsi.fastutil.ints.IntSets;
import it.unimi.dsi.fastutil.objects.ObjectLists;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

public class Chunk {
    private int chunkId;
    private int chunkX;
    private int chunkY;
    private int plane;
    private boolean staticChunk;

    private List<UpdateZonePacketEncoder> updates = ObjectLists.synchronize(ObjectLists.emptyList());
    private UpdateZonePartialEnclosed updateZonePacket = null;

    protected Set<Integer> players = IntSets.synchronize(IntSets.emptySet());
    protected Set<Integer> npcs = IntSets.synchronize(IntSets.emptySet());

    protected GameObject[][][] baseObjects = new GameObject[8][8][4];
    protected Map<Integer, GameObject> removedBaseObjects = Int2ObjectMaps.synchronize(Int2ObjectMaps.emptyMap());
    protected List<GameObject> spawnedObjects = ObjectLists.synchronize(ObjectLists.emptyList());

    protected Map<Integer, Map<Integer, List<GroundItem>>> groundItems = Int2ObjectMaps.synchronize(Int2ObjectMaps.emptyMap());;
    protected List<GroundItem> groundItemList = ObjectLists.synchronize(ObjectLists.emptyList());

    private AtomicBoolean loadedData = new AtomicBoolean(false);

    private int[] musicIds;

    public Chunk(int chunkId) {
        this.chunkId = chunkId;
        int[] coords = MapUtils.decode(Structure.CHUNK, chunkId);
        this.chunkX = coords[0];
        this.chunkY = coords[1];
        this.plane = coords[2];
        musicIds = Music.getRegionMusics(MapUtils.chunkToRegionId(chunkId));
    }

    public void setStaticChunk() {
        staticChunk = true;
    }

    public boolean isStaticChunk() {
        return staticChunk;
    }

    public void clearCollisionData() {
        WorldCollision.clearChunk(chunkId);
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

    public boolean isLoaded() {
        return loadedData.get();
    }

    public void addBaseObject(GameObject obj) {
        baseObjects[obj.getTile().getXInChunk()][obj.getTile().getYInChunk()][obj.getSlot()] = obj;
        clip(obj);
    }

    public void spawnObject(GameObject newObj, boolean clip) {
        GameObject baseObj = baseObjects[newObj.getTile().getXInChunk()][newObj.getTile().getYInChunk()][newObj.getSlot()];
        GameObject spawnedObj = getSpawnedObjectWithSlot(newObj.getTile(), newObj.getSlot());

        if (spawnedObj != null) {
            spawnedObjects.remove(spawnedObj);
            if (clip)
                unclip(spawnedObj);
        }

        if (newObj.equals(baseObj)) {
            newObj = baseObj;
            deleteRemovedObject(baseObj);
        } else if (baseObj != newObj) {
            if (!newObj.equals(baseObj))
                addRemovedObject(baseObj);
            spawnedObjects.add(newObj);
            if (clip && baseObj != null)
                unclip(baseObj);
        } else if (spawnedObj == null) {
            Logger.info(Chunk.class, "spawnObject", "Requested object to spawn is already spawned. (Shouldnt happen) " + baseObj);
            return;
        }

        if (clip)
            clip(newObj);
        addChunkUpdate(new AddObject(newObj.getTile().getChunkLocalHash(), newObj));
    }

    public void removeObject(GameObject toRemove) {
        GameObject baseObject = baseObjects[toRemove.getTile().getXInChunk()][toRemove.getTile().getYInChunk()][toRemove.getSlot()];
        GameObject removedBaseObject = getRemovedObject(baseObject);
        boolean replace = false;
        if (removedBaseObject != null) {
            deleteRemovedObject(baseObject);
            clip(baseObject);
            replace = true;
        }
        GameObject spawned = getSpawnedObjectWithSlot(toRemove.getTile(), toRemove.getSlot());
        if (spawned != null) {
            spawnedObjects.remove(toRemove);
            unclip(toRemove);
            if (baseObject != null)
                clip(baseObject);
            replace = true;
        } else if (toRemove.equals(baseObject)) {
            unclip(toRemove);
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
        removedBaseObjects.put(object.hashCode(), object);
    }

    public void deleteRemovedObject(GameObject object) {
        if (removedBaseObjects == null || object == null)
            return;
        removedBaseObjects.remove(object.hashCode());
    }

    public GameObject getRemovedObject(GameObject object) {
        if (removedBaseObjects == null || object == null)
            return null;
        return removedBaseObjects.get(object.hashCode());
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

    public List<GameObject> getAllBaseObjects() {
        if (baseObjects == null)
            return null;
        List<GameObject> list = new ArrayList<>();
        for (int x = 0; x < 8; x++)
            for (int y = 0; y < 8; y++) {
                if (baseObjects[x][y] == null)
                    continue;
                for (GameObject o : baseObjects[x][y])
                    if (o != null)
                        list.add(o);
            }
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
        updates.add(update);
    }

    public void rebuildUpdateZone() {
        updateZonePacket = new UpdateZonePartialEnclosed(chunkId, updates);
    }

    public void sendUpdates(Player player) {
        if (!player.hasStarted() || player.hasFinished())
            return;
        player.getSession().write(updateZonePacket);
    }

    public int getChunkX() {
        return chunkX;
    }

    public int getChunkY() {
        return chunkY;
    }

    public int getPlane() {
        return plane;
    }

    public int getRenderChunkX() {
        return chunkX;
    }

    public int getRenderChunkY() {
        return chunkY;
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

    public void preTick() {
    }

    public void update() {
    }

    public int getBaseX() {
        return chunkX << 3;
    }

    public int getBaseY() {
        return chunkY << 3;
    }
}
