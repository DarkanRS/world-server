// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program.  If not, see <http://www.gnu.org/licenses/>.
//
//  Copyright (C) 2021 Trenton Kress
//  This file is part of project: Darkan
//
package com.rs.game.region;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import com.rs.cache.Cache;
import com.rs.cache.IndexType;
import com.rs.cache.loaders.ObjectDefinitions;
import com.rs.cache.loaders.ObjectType;
import com.rs.game.World;
import com.rs.game.content.ItemConstants;
import com.rs.game.model.WorldProjectile;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.object.GameObject;
import com.rs.lib.game.GroundItem;
import com.rs.lib.game.WorldTile;
import com.rs.lib.io.InputStream;
import com.rs.lib.util.Logger;
import com.rs.lib.util.MapXTEAs;
import com.rs.lib.util.Utils;
import com.rs.utils.music.Music;
import com.rs.utils.spawns.ItemSpawns;
import com.rs.utils.spawns.NPCSpawns;
import com.rs.utils.spawns.ObjectSpawns;

public class Region {

	public static final int OBJECT_SLOT_WALL = 0;
	public static final int OBJECT_SLOT_WALL_DECORATION = 1;
	public static final int OBJECT_SLOT_FLOOR = 2;
	public static final int OBJECT_SLOT_FLOOR_DECORATION = 3;

	public static final int WIDTH = 64;
	public static final int HEIGHT = 64;

	protected int regionId;
	protected ClipMap clipMap;
	protected ClipMap clipMapProj;

	private boolean[][][] npcClipping;
	private int[][][] overlayIds;
	private int[][][] underlayIds;
	private byte[][][] overlayPathShapes;
	private byte[][][] overlayRotations;
	private byte[][][] tileFlags;
	private boolean hasData;
	private boolean xtea;

	protected Set<Integer> playersIndexes;
	protected Set<Integer> npcsIndexes;
	protected List<GameObject> spawnedObjects;
	protected Map<Integer, GameObject> removedObjects;
	private Map<Integer, Map<Integer, List<GroundItem>>> groundItems;
	private List<GroundItem> groundItemList;
	protected List<WorldProjectile> projectiles;
	protected GameObject[][][][] objects;
	private int loadMapStage;
	private boolean loadedNPCSpawns;
	private boolean loadedObjectSpawns;
	private boolean loadedItemSpawns;
	private int[] musicIds;

	public Region(int regionId) {
		this.regionId = regionId;
		spawnedObjects = new CopyOnWriteArrayList<>();
		removedObjects = new ConcurrentHashMap<>();
		projectiles = new CopyOnWriteArrayList<>();
		loadMusicIds();
	}

	public int getBaseX() {
		return (regionId >> 8 & 0xFF) << 6;
	}

	public int getBaseY() {
		return (regionId & 0xFF) << 6;
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

	public void loadMusicIds() {
		musicIds = Music.getRegionMusics(regionId);
	}

	public void removeMapFromMemory() {
		if (getLoadMapStage() == 2 && (playersIndexes == null || playersIndexes.isEmpty()) && (npcsIndexes == null || npcsIndexes.isEmpty())) {
			objects = null;
			clipMap = null;
			npcClipping = null;
			setLoadMapStage(0);
		}
	}

	public ClipMap forceGetClipMapProjectiles() {
		if (clipMapProj == null)
			clipMapProj = new ClipMap(regionId, true);
		return clipMapProj;
	}

	public ClipMap forceGetClipMap() {
		if (clipMap == null)
			clipMap = new ClipMap(regionId, false);
		return clipMap;
	}

	public void clip(GameObject object, int x, int y) {
		if (object.getId() == -1)
			return;
		if (clipMap == null)
			clipMap = new ClipMap(regionId, false);
		if (clipMapProj == null)
			clipMapProj = new ClipMap(regionId, true);
		int plane = object.getPlane();
		ObjectType type = object.getType();
		int rotation = object.getRotation();
		if (x < 0 || y < 0 || x >= clipMap.getMasks()[plane].length || y >= clipMap.getMasks()[plane][x].length)
			return;
		ObjectDefinitions defs = ObjectDefinitions.getDefs(object.getId());

		if (defs.getClipType() == 0)
			return;

		switch(type) {
		case WALL_STRAIGHT:
		case WALL_DIAGONAL_CORNER:
		case WALL_WHOLE_CORNER:
		case WALL_STRAIGHT_CORNER:
			clipMap.addWall(plane, x, y, type, rotation, defs.blocks(), !defs.ignoreAltClip);
			if (defs.blocks())
				clipMapProj.addWall(plane, x, y, type, rotation, defs.blocks(), !defs.ignoreAltClip);
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
			clipMap.addObject(plane, x, y, sizeX, sizeY, defs.blocks(), !defs.ignoreAltClip);
			if (defs.clipType != 0)
				clipMapProj.addObject(plane, x, y, sizeX, sizeY, defs.blocks(), !defs.ignoreAltClip);
			break;
		case GROUND_DECORATION:
			if (defs.clipType == 1)
				clipMap.addBlockWalkAndProj(plane, x, y);
			break;
		default:
			break;
		}
	}

	public void unclip(int plane, int x, int y) {
		if (clipMap == null)
			clipMap = new ClipMap(regionId, false);
		if (clipMapProj == null)
			clipMapProj = new ClipMap(regionId, true);
		clipMap.setFlag(plane, x, y, 0);
	}

	public void unclip(GameObject object, int x, int y) {
		if (object.getId() == -1) // dont clip or noclip with id -1
			return;
		if (clipMap == null)
			clipMap = new ClipMap(regionId, false);
		if (clipMapProj == null)
			clipMapProj = new ClipMap(regionId, true);
		int plane = object.getPlane();
		ObjectType type = object.getType();
		int rotation = object.getRotation();
		if (x < 0 || y < 0 || x >= clipMap.getMasks()[plane].length || y >= clipMap.getMasks()[plane][x].length)
			return;
		ObjectDefinitions defs = ObjectDefinitions.getDefs(object.getId());

		if (defs.getClipType() == 0)
			return;

		switch(type) {
		case WALL_STRAIGHT:
		case WALL_DIAGONAL_CORNER:
		case WALL_WHOLE_CORNER:
		case WALL_STRAIGHT_CORNER:
			clipMap.removeWall(plane, x, y, type, rotation, defs.blocks(), !defs.ignoreAltClip);
			if (defs.blocks())
				clipMapProj.removeWall(plane, x, y, type, rotation, defs.blocks(), !defs.ignoreAltClip);
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
			clipMap.removeObject(plane, x, y, sizeX, sizeY, defs.blocks(), !defs.ignoreAltClip);
			if (defs.blocks())
				clipMapProj.removeObject(plane, x, y, sizeX, sizeY, defs.blocks(), !defs.ignoreAltClip);
			break;
		case GROUND_DECORATION:
			if (defs.clipType == 1)
				clipMap.removeBlockWalkAndProj(plane, x, y);
			break;
		default:
			break;
		}
	}

	public void spawnObject(GameObject obj, int plane, int localX, int localY) {
		if (objects == null)
			objects = new GameObject[4][64][64][4];
		objects[plane][localX][localY][obj.getSlot()] = obj;
		clip(obj, localX, localY);
	}

	public void spawnObject(GameObject object, int plane, int localX, int localY, boolean clip) {
		if (objects == null)
			objects = new GameObject[4][64][64][4];
		GameObject mapObject = objects[plane][localX][localY][object.getSlot()];
		GameObject spawned = getSpawnedObjectWithSlot(plane, localX, localY, object.getSlot());

		if (spawned != null) {
			spawnedObjects.remove(spawned);
			if (clip)
				unclip(spawned, localX, localY);
		}

		if (object.equals(mapObject)) {
			object = mapObject;
			deleteRemovedObject(mapObject);
		} else if (mapObject != object) {
			if (!object.equals(mapObject))
				addRemovedObject(mapObject);
			spawnedObjects.add(object);
			if (clip && mapObject != null)
				unclip(mapObject, localX, localY);
		} else if (spawned == null) {
			Logger.info(Region.class, "spawnObject", "Requested object to spawn is already spawned. (Shouldnt happen) " + mapObject);
			return;
		}

		if (clip)
			clip(object, localX, localY);
		for (Player player : World.getPlayersInRegionRange(getRegionId())) {
			if (player == null || !player.hasStarted() || player.hasFinished())
				return;
			player.getPackets().sendAddObject(object);
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

	// override by static region to empty
	public void checkLoadMap() {
		if (getLoadMapStage() == 0) {
			setLoadMapStage(1);
//			CoresManager.execute(() -> {
//				try {
					loadRegionMap();
					setLoadMapStage(2);
					if (!isLoadedObjectSpawns()) {
						loadObjectSpawns();
						setLoadedObjectSpawns(true);
					}
					if (!isLoadedNPCSpawns()) {
						loadNPCSpawns();
						setLoadedNPCSpawns(true);
					}
					if (!isLoadedItemSpawns()) {
						loadItemSpawns();
						setLoadedItemSpawns(true);
					}
//				} catch (Throwable e) {
//					Logger.handle(this, e);
//				}
//			});
		}
	}

	void loadNPCSpawns() {
		NPCSpawns.loadNPCSpawns(regionId);
	}

	void loadObjectSpawns() {
		ObjectSpawns.loadObjectSpawns(regionId);
	}

	private void loadItemSpawns() {
		ItemSpawns.loadItemSpawns(regionId);
	}

	public int getRegionId() {
		return regionId;
	}

	public void loadRegionMap() {
		int regionX = (regionId >> 8) * 64;
		int regionY = (regionId & 0xff) * 64;
		int landArchiveId = Cache.STORE.getIndex(IndexType.MAPS).getArchiveId("l" + ((regionX >> 3) / 8) + "_" + ((regionY >> 3) / 8));
		byte[] landContainerData = landArchiveId == -1 ? null : Cache.STORE.getIndex(IndexType.MAPS).getFile(landArchiveId, 0, MapXTEAs.getMapKeys(regionId));
		//int uLandArchiveId = Cache.STORE.getIndexes()[5].getArchiveId("ul" + ((regionX >> 3) / 8) + "_" + ((regionY >> 3) / 8));
		//byte[] uLandContainerData = uLandArchiveId == -1 ? null : Cache.STORE.getIndexes()[5].getFile(uLandArchiveId);
		int mapArchiveId = Cache.STORE.getIndex(IndexType.MAPS).getArchiveId("m" + ((regionX >> 3) / 8) + "_" + ((regionY >> 3) / 8));
		byte[] mapContainerData = mapArchiveId == -1 ? null : Cache.STORE.getIndex(IndexType.MAPS).getFile(mapArchiveId, 0);

		overlayIds = mapContainerData == null ? null : new int[4][64][64];
		underlayIds = mapContainerData == null ? null : new int[4][64][64];
		overlayPathShapes = mapContainerData == null ? null : new byte[4][64][64];
		overlayRotations = mapContainerData == null ? null : new byte[4][64][64];
		tileFlags = mapContainerData == null ? null : new byte[4][64][64];

		if (mapContainerData != null) {
			hasData = true;
			InputStream mapStream = new InputStream(mapContainerData);
			for (int plane = 0; plane < 4; plane++)
				for (int x = 0; x < 64; x++)
					for (int y = 0; y < 64; y++)
						while (true) {
							int value = mapStream.readUnsignedByte();
							if (value == 0)
								break;
							if (value == 1) {
								//heights[plane][x][y] = mapStream.readByte();
								mapStream.readByte();
								break;
							}
							if (value <= 49) {
								int v = mapStream.readUnsignedByte();
								overlayIds[plane][x][y] = v;
								overlayPathShapes[plane][x][y] = (byte) ((value - 2) / 4);
								overlayRotations[plane][x][y] = (byte) ((value - 2) & 0x3);
							} else if (value <= 81)
								tileFlags[plane][x][y] = (byte) (value - 49);
							else
								underlayIds[plane][x][y] = (value - 81);
						}
			if (regionId != 11844)
				for (int plane = 0; plane < 4; plane++)
					for (int x = 0; x < 64; x++)
						for (int y = 0; y < 64; y++)
							if (RenderFlag.flagged(tileFlags[plane][x][y], RenderFlag.CLIPPED)) {
								int finalPlane = plane;
								if (RenderFlag.flagged(tileFlags[1][x][y], RenderFlag.LOWER_OBJECTS_TO_OVERRIDE_CLIPPING))
									finalPlane--;
								if (finalPlane >= 0)
									forceGetClipMap().addBlockedTile(finalPlane, x, y);
							}
		} else
			for (int plane = 0; plane < 4; plane++)
				for (int x = 0; x < 64; x++)
					for (int y = 0; y < 64; y++)
						forceGetClipMap().addBlockedTile(plane, x, y);
		if (landContainerData != null) {
			xtea = true;
			InputStream landStream = new InputStream(landContainerData);
			int objectId = -1;
			int incr;
			while ((incr = landStream.readSmart2()) != 0) {
				objectId += incr;
				int location = 0;
				int incr2;
				while ((incr2 = landStream.readUnsignedSmart()) != 0) {
					location += incr2 - 1;
					int localX = (location >> 6 & 0x3f);
					int localY = (location & 0x3f);
					int plane = location >> 12;
			int objectData = landStream.readUnsignedByte();
			int type = objectData >> 2;
				int rotation = objectData & 0x3;
				if (localX < 0 || localX >= 64 || localY < 0 || localY >= 64)
					continue;
				int objectPlane = plane;
				if (tileFlags != null && (tileFlags[1][localX][localY] & 0x2) != 0)
					objectPlane--;
				if (objectPlane < 0 || objectPlane >= 4 || plane < 0 || plane >= 4)
					continue;
				spawnObject(new GameObject(objectId, ObjectType.forId(type), rotation, localX + regionX, localY + regionY, objectPlane), objectPlane, localX, localY);
				}
			}
		}
		//		if (uLandContainerData != null) {
		//			InputStream landStream = new InputStream(uLandContainerData);
		//			int objectId = -1;
		//			int incr;
		//			while ((incr = landStream.readSmart2()) != 0) {
		//				objectId += incr;
		//				int location = 0;
		//				int incr2;
		//				while ((incr2 = landStream.readUnsignedSmart()) != 0) {
		//					location += incr2 - 1;
		//					int localX = (location >> 6 & 0x3f);
		//					int localY = (location & 0x3f);
		//					int plane = location >> 12;
		//					int objectData = landStream.readUnsignedByte();
		//					int type = objectData >> 2;
		//					int rotation = objectData & 0x3;
		//					if (localX < 0 || localX >= 64 || localY < 0 || localY >= 64)
		//						continue;
		//					int objectPlane = plane;
		//					if (mapSettings != null && (mapSettings[1][localX][localY] & 0x2) != 0)
		//						objectPlane--;
		//					if (objectPlane < 0 || objectPlane >= 4 || plane < 0 || plane >= 4)
		//						continue;
		//					spawnObject(new WorldObject(objectId, type, rotation, localX + regionX, localY + regionY, objectPlane), objectPlane, localX, localY, true);
		//				}
		//			}
		//		}
		if (landContainerData == null && landArchiveId != -1 && MapXTEAs.getMapKeys(regionId) != null)
			Logger.warn(Region.class, "loadRegionMap", "Missing xteas for region " + regionId + ".");
	}

	public Set<Integer> getPlayerIndexes() {
		if (playersIndexes == null)
			playersIndexes = ConcurrentHashMap.newKeySet();
		return playersIndexes;
	}

	public Set<Integer> getNPCsIndexes() {
		if (npcsIndexes == null)
			npcsIndexes = ConcurrentHashMap.newKeySet();
		return npcsIndexes;
	}

	public void addPlayerIndex(int index) {
		// creates list if doesnt exist
		if (playersIndexes == null)
			playersIndexes = ConcurrentHashMap.newKeySet();
		playersIndexes.add(index);
	}

	public void addNPCIndex(int index) {
		// creates list if doesnt exist
		if (npcsIndexes == null)
			npcsIndexes = ConcurrentHashMap.newKeySet();
		npcsIndexes.add(index);
	}

	public boolean removePlayerIndex(Integer index) {
		if (playersIndexes == null) // removed region example cons or dung
			return false;
		return playersIndexes.remove(index);
	}

	public boolean removeNPCIndex(Object index) {
		if (npcsIndexes == null) // removed region example cons or dung
			return false;
		return npcsIndexes.remove(index);
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
		return getSpawnedObject(new WorldTile(x + ((regionId >> 8) * 64), y + ((regionId & 0xff) * 64), plane));
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
		return getSpawnedObject(new WorldTile(x + ((regionId >> 8) * 64), y + ((regionId & 0xff) * 64), plane), type);
	}

	// override by static region to get objects from needed
	public GameObject[] getObjects(int plane, int x, int y) {
		checkLoadMap();
		// if objects just loaded now will return null, anyway after they load
		// will return correct so np
		if (objects == null)
			return null;
		return objects[plane][x][y];
	}

	/**
	 * Gets the list of world objects in this region.
	 *
	 * @return The list of world objects.
	 */
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

	public GameObject getSpawnedObject(WorldTile tile) {
		if (spawnedObjects == null)
			return null;
		for (GameObject object : spawnedObjects)
			if (object.getX() == tile.getX() && object.getY() == tile.getY() && object.getPlane() == tile.getPlane())
				return object;
		return null;
	}

	public GameObject getSpawnedObject(WorldTile tile, ObjectType type) {
		if (spawnedObjects == null)
			return null;
		for (GameObject object : spawnedObjects)
			if (object.getType() == type && object.getX() == tile.getX() && object.getY() == tile.getY() && object.getPlane() == tile.getPlane())
				return object;
		return null;
	}

	public void addObject(GameObject object) {
		if (spawnedObjects == null)
			spawnedObjects = new CopyOnWriteArrayList<>();
		spawnedObjects.add(object);
	}

	public void removeObject(GameObject object) {
		if (spawnedObjects == null)
			return;
		spawnedObjects.remove(object);
	}

	public List<GameObject> getSpawnedObjects() {
		return spawnedObjects;
	}

	public Map<Integer, GameObject> getRemovedObjects() {
		return removedObjects;
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

	public GameObject getStandartObject(int plane, int x, int y) {
		return getObjectWithSlot(plane, x, y, OBJECT_SLOT_FLOOR);
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

	public GameObject getSpawnedObjectWithSlot(int plane, int x, int y, int slot) {
		for (GameObject object : spawnedObjects)
			if (object.getXInRegion() == x && object.getYInRegion() == y && object.getPlane() == plane && object.getSlot() == slot)
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
			if (object.getXInRegion() == x && object.getYInRegion() == y && object.getPlane() == plane && object.getId() == id)
				return object;
		return null;
	}

	public boolean objectExists(GameObject object) {
		return containsObjectWithId(object.getPlane(), object.getXInRegion(), object.getYInRegion(), object.getId());
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

	public int getClipFlags(int plane, int localX, int localY) {
		if (clipMap == null || getLoadMapStage() != 2)
			return -1; // cliped tile
		return clipMap.getMasks()[plane][localX][localY];
	}

	public void setFlag(int plane, int localX, int localY, int mask) {
		if (clipMap == null || getLoadMapStage() != 2)
			return; // cliped tile

		if (localX >= 64 || localY >= 64 || localX < 0 || localY < 0) {
			WorldTile tile = new WorldTile(clipMap.getRegionX() + localX, clipMap.getRegionY() + localY, plane);
			int regionId = tile.getRegionId();
			int newRegionX = (regionId >> 8) * 64;
			int newRegionY = (regionId & 0xff) * 64;
			World.getRegion(tile.getRegionId()).setFlag(plane, tile.getX() - newRegionX, tile.getY() - newRegionY, mask);
			return;
		}

		clipMap.setFlag(plane, localX, localY, mask);
	}

	public int getRotation(int plane, int localX, int localY) {
		return 0;
	}

	public int getClipFlagsProj(int plane, int localX, int localY) {
		if (clipMapProj == null || getLoadMapStage() != 2)
			return -1;
		return clipMapProj.getMasks()[plane][localX][localY];
	}

	public List<GroundItem> getAllGroundItems() {
		if (groundItemList == null)
			groundItemList = new CopyOnWriteArrayList<>();
		return groundItemList;
	}

	public GroundItem getGroundItem(int itemId, WorldTile tile, int playerId) {
		if (groundItems == null)
			return null;
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

	public GroundItem getGroundItem(int itemId, WorldTile tile, Player player) {
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
		if (groundItems == null)
			return false;
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
		if (groundItems == null)
			groundItems = new ConcurrentHashMap<>();
		Map<Integer, List<GroundItem>> tileMap = groundItems.get(item.getVisibleToId());
		if (tileMap == null) {
			tileMap = new ConcurrentHashMap<>();
			groundItems.put(item.getVisibleToId(), tileMap);
		}
		List<GroundItem> items = tileMap.get(item.getTile().getTileHash());
		if (items == null) {
			items = new CopyOnWriteArrayList<>();
			tileMap.put(item.getTile().getTileHash(), items);
		}
		items.add(item);
		return true;
	}

	public boolean addGroundItem(GroundItem item) {
		if (groundItemList == null)
			groundItemList = new CopyOnWriteArrayList<>();
		if (groundItems == null)
			groundItems = new ConcurrentHashMap<>();
		Map<Integer, List<GroundItem>> tileMap = groundItems.get(item.getVisibleToId());
		if (tileMap == null) {
			tileMap = new ConcurrentHashMap<>();
			groundItems.put(item.getVisibleToId(), tileMap);
		}
		List<GroundItem> items = tileMap.get(item.getTile().getTileHash());
		if (items == null) {
			items = new CopyOnWriteArrayList<>();
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
		if ((groundItems == null) || (groundItemList == null))
			return false;
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

	public int getLoadMapStage() {
		return loadMapStage;
	}

	public void setLoadMapStage(int loadMapStage) {
		this.loadMapStage = loadMapStage;
	}

	public boolean isLoadedObjectSpawns() {
		return loadedObjectSpawns;
	}

	public void setLoadedObjectSpawns(boolean loadedObjectSpawns) {
		this.loadedObjectSpawns = loadedObjectSpawns;
	}

	public boolean isLoadedNPCSpawns() {
		return loadedNPCSpawns;
	}

	public void setLoadedNPCSpawns(boolean loadedNPCSpawns) {
		this.loadedNPCSpawns = loadedNPCSpawns;
	}

	public boolean isLoadedItemSpawns() {
		return loadedItemSpawns;
	}

	public void setLoadedItemSpawns(boolean loadedItemSpawns) {
		this.loadedItemSpawns = loadedItemSpawns;
	}

	public final boolean isLinkedBelow(final int z, final int x, final int y) {
		return RenderFlag.flagged(getRenderFlags(z, x, y), RenderFlag.LOWER_OBJECTS_TO_OVERRIDE_CLIPPING);
	}

	public final boolean isVisibleBelow(final int z, final int x, final int y) {
		return RenderFlag.flagged(getRenderFlags(z, x, y), RenderFlag.FORCE_TO_BOTTOM);
	}

	public int getRenderFlags(int z, int x, int y) {
		return tileFlags != null ? tileFlags[z][x][y] : 0;
	}

	public int getUnderlayId(int z, int x, int y) {
		return underlayIds != null ? underlayIds[z][x][y] & 0x7fff : -1;
	}

	public int getOverlayId(int z, int x, int y) {
		return overlayIds != null ? overlayIds[z][x][y] & 0x7fff : -1;
	}

	public int getOverlayPathShape(int z, int x, int y) {
		return overlayPathShapes != null ? overlayPathShapes[z][x][y] & 0x7fff : -1;
	}

	public int getOverlayRotation(int z, int x, int y) {
		return overlayRotations != null ? overlayRotations[z][x][y] : -1;
	}

	public boolean hasData() {
		return hasData;
	}

	public boolean hasXtea() {
		return xtea;
	}

	public boolean isMissingXtea() {
		return hasData && !xtea;
	}

	public boolean checkXtea(int[] xteas) {
		int regionX = (regionId >> 8) * 64;
		int regionY = (regionId & 0xff) * 64;
		int landArchiveId = Cache.STORE.getIndex(IndexType.MAPS).getArchiveId("l" + ((regionX >> 3) / 8) + "_" + ((regionY >> 3) / 8));
		if (landArchiveId == -1)
			return false;
		byte[] data = Cache.STORE.getIndex(IndexType.MAPS).getFile(landArchiveId, 0, xteas);
		if (data == null)
			return false;
		return true;
	}

	public boolean getClipNPC(int plane, int x, int y) {
		if (npcClipping == null)
			return false;
		return npcClipping[plane][x][y];
	}

	public void setClipNPC(int plane, int x, int y, boolean clip) {
		if (npcClipping == null)
			npcClipping = new boolean[4][64][64];
		npcClipping[plane][x][y] = clip;
	}
}
