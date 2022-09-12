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

import java.util.HashSet;
import java.util.Set;

import com.rs.cores.CoresManager;
import com.rs.game.World;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.game.WorldTile;
import com.rs.lib.util.Logger;

public final class RegionBuilder {

	/**
	 * Instance builder
	 */
	public static class DynamicRegionReference {

		private int[] base;
		private int width;
		private int height;
		private boolean destroyed;

		public DynamicRegionReference(int width, int height) {
			this.width = width;
			this.height = height;
			destroyed = false;
		}
		
		@Deprecated
		public void setBase(int[] base) {
			this.base = base;
		}

		public void requestChunkBound(Runnable callback) {
			destroyed = false;
			RegionBuilder.findEmptyChunkBound(this, callback);
		}

		public void copyChunk(int localChunkX, int localChunkY, int plane, int fromChunkX, int fromChunkY, int fromPlane, int rotation, Runnable callback) {
			if (base == null)
				requestChunkBound(() -> {
					RegionBuilder.copyChunk(this, localChunkX, localChunkY, plane, fromChunkX, fromChunkY, fromPlane, rotation, callback);
				});
			else
				RegionBuilder.copyChunk(this, localChunkX, localChunkY, plane, fromChunkX, fromChunkY, fromPlane, rotation, callback);
		}

		public void clearChunk(int localChunkX, int localChunkY, int plane, Runnable callback) {
			if (base == null)
				requestChunkBound(() -> {
					RegionBuilder.clearChunk(this, localChunkX, localChunkY, plane, callback);
				});
			else
				RegionBuilder.clearChunk(this, localChunkX, localChunkY, plane, callback);
		}

		public void copy2x2ChunkSquare(int localChunkX, int localChunkY, int fromChunkX, int fromChunkY, int rotation, int[] planes, Runnable callback) {
			if (base == null)
				requestChunkBound(() -> {
					RegionBuilder.copy2x2ChunkSquare(this, localChunkX, localChunkY, fromChunkX, fromChunkY, rotation, planes, callback);
				});
			else
				RegionBuilder.copy2x2ChunkSquare(this, localChunkX, localChunkY, fromChunkX, fromChunkY, rotation, planes, callback);
		}

		public void copyMap(int localChunkX, int localChunkY, int fromChunkX, int fromChunkY, int size, Runnable callback) {
			if (base == null)
				requestChunkBound(() -> {
					RegionBuilder.copyMap(this, localChunkX, localChunkY, fromChunkX, fromChunkY, size, callback);
				});
			else
				RegionBuilder.copyMap(this, localChunkX, localChunkY, fromChunkX, fromChunkY, size, callback);
		}

		public void copyMapAllPlanes(int fromChunkX, int fromChunkY, int size, Runnable callback) {
			copyMap(0, 0, fromChunkX, fromChunkY, size, callback);
		}

		public void copyMap(int localChunkX, int localChunkY, int[] planes, int fromChunkX, int fromChunkY, int[] fromPlanes, int width, int height, Runnable callback) {
			if (base == null)
				requestChunkBound(() -> {
					RegionBuilder.copyMap(this, localChunkX, localChunkY, planes, fromChunkX, fromChunkY, fromPlanes, width, height, callback);
				});
			else
				RegionBuilder.copyMap(this, localChunkX, localChunkY, planes, fromChunkX, fromChunkY, fromPlanes, width, height, callback);
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
			if (base == null)
				requestChunkBound(() -> {
					RegionBuilder.clearMap(this, chunkX, chunkY, width, height, planes, callback);
				});
			else
				RegionBuilder.clearMap(this, chunkX, chunkY, width, height, planes, callback);
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
			RegionBuilder.destroyMap(this, callback);
		}

		public void destroy() {
			destroy(null);
		}

		public int[] getBaseChunks() {
			return base;
		}

		public int getBaseChunkX() {
			return base[0];
		}

		public int getBaseChunkY() {
			return base[1];
		}

		public int getBaseX() {
			return base[0] << 3;
		}

		public int getBaseY() {
			return base[1] << 3;
		}

		public WorldTile getBase() {
			return new WorldTile(getBaseX(), getBaseY(), 0);
		}

		public WorldTile getLocalTile(int offsetX, int offsetY, int plane) {
			return new WorldTile(getLocalX(offsetX), getLocalY(offsetY), plane);
		}

		public WorldTile getLocalTile(int offsetX, int offsetY) {
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

		public int getRegionId() {
			return ((getBaseChunkX() >> 3) << 8) + (getBaseChunkY() >> 3);
		}

		public boolean isDestroyed() {
			return destroyed;
		}

		public boolean isCreated() {
			return base != null;
		}
	}

	public static final int NORTH = 0, EAST = 1, SOUTH = 2, WEST = 3;

	private static final Set<Integer> EXISTING_MAPS = new HashSet<>();

	private static final int MAX_REGION_X = 127;
	private static final int MAX_REGION_Y = 255;

	private static void reserveArea(int fromRegionX, int fromRegionY, int width, int height, boolean remove) {
		synchronized(EXISTING_MAPS) {
			for (int regionX = fromRegionX; regionX < fromRegionX + width; regionX++)
				for (int regionY = fromRegionY; regionY < fromRegionY + height; regionY++)
					if (remove)
						EXISTING_MAPS.remove(getRegionId(regionX, regionY));
					else
						EXISTING_MAPS.add(getRegionId(regionX, regionY));
		}
	}

	private static boolean regionExists(int regionId) {
		synchronized(EXISTING_MAPS) {
			return EXISTING_MAPS.contains(regionId);
		}
	}

	private static int getRegionId(int mapX, int mapY) {
		return (mapX << 8) + mapY;
	}

	private static DynamicRegion createDynamicRegion(int regionId) {
		Region region = World.getRegion(regionId);
		if (region != null) {
			if (region instanceof DynamicRegion dr)
				return dr;
			destroyRegion(regionId);
		}
		DynamicRegion newRegion = new DynamicRegion(regionId);
		World.putRegion(regionId, newRegion);
		return newRegion;
	}

	public static void destroyRegion(int regionId) {
		Region region = World.getRegion(regionId);
		if (region != null) {
			Set<Integer> playerIndexes = region.getPlayerIndexes();
			Set<Integer> npcIndexes = region.getNPCsIndexes();
			if (region.getAllGroundItems() != null)
				region.getAllGroundItems().clear();
			if (region.getGroundItems() != null)
				region.getGroundItems().clear();
			region.getSpawnedObjects().clear();
			region.getRemovedObjects().clear();
			if (npcIndexes != null)
				for (int npcIndex : npcIndexes) {
					NPC npc = World.getNPCs().get(npcIndex);
					if (npc == null)
						continue;
					npc.finish();
				}
			World.removeRegion(regionId);

			if (playerIndexes != null)
				for (int playerIndex : playerIndexes) {
					Player player = World.getPlayers().get(playerIndex);
					if (player == null || !player.hasStarted() || player.hasFinished())
						continue;
					player.setForceNextMapLoadRefresh(true);
					player.loadMapRegions();
				}
		}
	}

	public static void findEmptyChunkBound(DynamicRegionReference ref, Runnable callback) {
		CoresManager.execute(() -> {
			try {
				ref.base = findEmptyChunkBound(ref.width, ref.height);
				if (callback != null)
					callback.run();
			} catch (Throwable e) {
				Logger.handle(RegionBuilder.class, "findEmptyChunkBound", e);
			}
		});
	}

	private static int[] findEmptyRegionBound(int widthChunks, int heightChunks) {
		int regionHash = findEmptyRegionHash(widthChunks, heightChunks);
		return new int[] { (regionHash >> 8), regionHash & 0xff };
	}

	private static int[] findEmptyChunkBound(int widthChunks, int heightChunks) {
		int[] map = findEmptyRegionBound(widthChunks, heightChunks);
		map[0] *= 8;
		map[1] *= 8;
		return map;
	}

	private static int findEmptyRegionHash(int widthChunks, int heightChunks) {
		int regionsDistanceX = 1;
		while (widthChunks > 8) {
			regionsDistanceX += 1;
			widthChunks -= 8;
		}
		int regionsDistanceY = 1;
		while (heightChunks > 8) {
			regionsDistanceY += 1;
			heightChunks -= 8;
		}
		for (int regionX = 1; regionX <= MAX_REGION_X - regionsDistanceX; regionX++)
			skip: for (int regionY = 1; regionY <= MAX_REGION_Y - regionsDistanceY; regionY++) {
				int regionHash = getRegionId(regionX, regionY);
				for (int checkRegionX = regionX - 1; checkRegionX <= regionX + regionsDistanceX; checkRegionX++)
					for (int checkRegionY = regionY - 1; checkRegionY <= regionY + regionsDistanceY; checkRegionY++) {
						int regionId = getRegionId(checkRegionX, checkRegionY);
						if (regionExists(regionId))
							continue skip;

					}
				reserveArea(regionX, regionY, regionsDistanceX, regionsDistanceY, false);
				return regionHash;
			}
		return -1;
	}

	private static final void destroyMap(DynamicRegionReference ref, Runnable callback) {
		CoresManager.schedule(() -> {
			try {
				destroyMap(ref.getBaseChunkX(), ref.getBaseChunkY(), ref.width, ref.height);
				if (callback != null)
					callback.run();
			} catch (Throwable e) {
				Logger.handle(RegionBuilder.class, "destroyMap", e);
			}
		}, 8);
	}

	private static final void destroyMap(int chunkX, int chunkY, int widthRegions, int heightRegions) {
		int fromRegionX = chunkX / 8;
		int fromRegionY = chunkY / 8;
		int regionsDistanceX = 1;
		while (widthRegions > 8) {
			regionsDistanceX += 1;
			widthRegions -= 8;
		}
		int regionsDistanceY = 1;
		while (heightRegions > 8) {
			regionsDistanceY += 1;
			heightRegions -= 8;
		}
		for (int regionX = fromRegionX; regionX < fromRegionX + regionsDistanceX; regionX++)
			for (int regionY = fromRegionY; regionY < fromRegionY + regionsDistanceY; regionY++)
				destroyRegion(getRegionId(regionX, regionY));
		reserveArea(fromRegionX, fromRegionY, regionsDistanceX, regionsDistanceY, true);
	}

	private static void copyChunk(DynamicRegionReference ref, int localChunkX, int localChunkY, int plane, int fromChunkX, int fromChunkY, int fromPlane, int rotation, Runnable callback) {
		CoresManager.execute(() -> {
			try {
				copyChunk(fromChunkX, fromChunkY, fromPlane, ref.getBaseChunkX()+localChunkX, ref.getBaseChunkY()+localChunkY, plane, rotation);
				if (callback != null)
					callback.run();
			} catch (Throwable e) {
				Logger.handle(RegionBuilder.class, "copyChunk", e);
			}
		});
	}

	private static void copyChunk(int fromChunkX, int fromChunkY, int fromPlane, int toChunkX, int toChunkY, int toPlane, int rotation) {
		DynamicRegion toRegion = createDynamicRegion(((toChunkX / 8) << 8) + (toChunkY / 8));
		int regionOffsetX = toChunkX - ((toChunkX / 8) * 8);
		int regionOffsetY = toChunkY - ((toChunkY / 8) * 8);
		toRegion.getRegionCoords()[toPlane][regionOffsetX][regionOffsetY][0] = fromChunkX;
		toRegion.getRegionCoords()[toPlane][regionOffsetX][regionOffsetY][1] = fromChunkY;
		toRegion.getRegionCoords()[toPlane][regionOffsetX][regionOffsetY][2] = fromPlane;
		toRegion.getRegionCoords()[toPlane][regionOffsetX][regionOffsetY][3] = rotation;
		toRegion.setReloadObjects(toPlane, regionOffsetX, regionOffsetY);
	}

	private static void copy2x2ChunkSquare(DynamicRegionReference ref, int chunkX, int chunkY, int fromChunkX, int fromChunkY, int rotation, int[] planes, Runnable callback) {
		CoresManager.execute(() -> {
			try {
				copy2x2ChunkSquare(fromChunkX, fromChunkY, ref.getBaseChunkX()+chunkX, ref.getBaseChunkY()+chunkY, rotation, planes);
				if (callback != null)
					callback.run();
			} catch (Throwable e) {
				Logger.handle(RegionBuilder.class, "copy2x2ChunkSquare", e);
			}
		});
	}

	private static final void copy2x2ChunkSquare(int fromRegionX, int fromRegionY, int toRegionX, int toRegionY, int rotation, int... planes) {
		for (int i : planes)
			// plane 1 and 2
			if (rotation == 0) {
				copyChunk(fromRegionX, fromRegionY, i, toRegionX, toRegionY, i, rotation);
				copyChunk(fromRegionX + 1, fromRegionY, i, toRegionX + 1, toRegionY, i, rotation);
				copyChunk(fromRegionX, fromRegionY + 1, i, toRegionX, toRegionY + 1, i, rotation);
				copyChunk(fromRegionX + 1, fromRegionY + 1, i, toRegionX + 1, toRegionY + 1, i, rotation);
			} else if (rotation == 1) {
				copyChunk(fromRegionX, fromRegionY, i, toRegionX, toRegionY + 1, i, rotation);
				copyChunk(fromRegionX + 1, fromRegionY, i, toRegionX, toRegionY, i, rotation);
				copyChunk(fromRegionX, fromRegionY + 1, i, toRegionX + 1, toRegionY + 1, i, rotation);
				copyChunk(fromRegionX + 1, fromRegionY + 1, i, toRegionX + 1, toRegionY, i, rotation);
			} else if (rotation == 2) {
				copyChunk(fromRegionX, fromRegionY, i, toRegionX + 1, toRegionY + 1, i, rotation);
				copyChunk(fromRegionX + 1, fromRegionY, i, toRegionX, toRegionY + 1, i, rotation);
				copyChunk(fromRegionX, fromRegionY + 1, i, toRegionX + 1, toRegionY, i, rotation);
				copyChunk(fromRegionX + 1, fromRegionY + 1, i, toRegionX, toRegionY, i, rotation);
			} else if (rotation == 3) {
				copyChunk(fromRegionX, fromRegionY, i, toRegionX + 1, toRegionY, i, rotation);
				copyChunk(fromRegionX + 1, fromRegionY, i, toRegionX + 1, toRegionY + 1, i, rotation);
				copyChunk(fromRegionX, fromRegionY + 1, i, toRegionX, toRegionY, i, rotation);
				copyChunk(fromRegionX + 1, fromRegionY + 1, i, toRegionX, toRegionY + 1, i, rotation);
			}
	}

	private static void clearChunk(DynamicRegionReference ref, int localChunkX, int localChunkY, int plane, Runnable callback) {
		CoresManager.execute(() -> {
			try {
				cutChunk(ref.getBaseChunkX()+localChunkX, ref.getBaseChunkY()+localChunkY, plane);
				if (callback != null)
					callback.run();
			} catch (Throwable e) {
				Logger.handle(RegionBuilder.class, "clearChunk", e);
			}
		});
	}

	private static void cutChunk(int toChunkX, int toChunkY, int toPlane) {
		copyChunk(0, 0, 0, toChunkX, toChunkY, toPlane, 0);
	}

	private static final void repeatMap(int toChunkX, int toChunkY, int widthChunks, int heightChunks, int fromChunkX, int fromChunkY, int fromPlane, int rotation, int... toPlanes) {
		for (int xOffset = 0; xOffset < widthChunks; xOffset++)
			for (int yOffset = 0; yOffset < heightChunks; yOffset++) {
				int nextChunkX = toChunkX + xOffset;
				int nextChunkY = toChunkY + yOffset;
				DynamicRegion toRegion = createDynamicRegion((((nextChunkX / 8) << 8) + (nextChunkY / 8)));
				int regionOffsetX = (nextChunkX - ((nextChunkX / 8) * 8));
				int regionOffsetY = (nextChunkY - ((nextChunkY / 8) * 8));
				for (int toPlane2 : toPlanes) {
					int toPlane = toPlane2;
					toRegion.getRegionCoords()[toPlane][regionOffsetX][regionOffsetY][0] = fromChunkX;
					toRegion.getRegionCoords()[toPlane][regionOffsetX][regionOffsetY][1] = fromChunkY;
					toRegion.getRegionCoords()[toPlane][regionOffsetX][regionOffsetY][2] = fromPlane;
					toRegion.getRegionCoords()[toPlane][regionOffsetX][regionOffsetY][3] = rotation;
					toRegion.setReloadObjects(toPlane, regionOffsetX, regionOffsetY);
				}
			}
	}

	private static void clearMap(DynamicRegionReference ref, int localChunkX, int localChunkY, int width, int height, int[] planes, Runnable callback) {
		CoresManager.execute(() -> {
			try {
				cutMap(ref.getBaseChunkX()+localChunkX, ref.getBaseChunkY()+localChunkY, width, height, planes);
				if (callback != null)
					callback.run();
			} catch (Throwable e) {
				Logger.handle(RegionBuilder.class, "clearMap", e);
			}
		});
	}

	private static final void cutMap(int toChunkX, int toChunkY, int widthChunks, int heightChunks, int... toPlanes) {
		repeatMap(toChunkX, toChunkY, widthChunks, heightChunks, 0, 0, 0, 0, toPlanes);
	}

	private static void copyMap(DynamicRegionReference ref, int localChunkX, int localChunkY, int fromChunkX, int fromChunkY, int size, Runnable callback) {
		CoresManager.execute(() -> {
			try {
				copyMap(fromChunkX, fromChunkY, ref.getBaseChunkX()+localChunkX, ref.getBaseChunkY()+localChunkY, size);
				if (callback != null)
					callback.run();
			} catch (Throwable e) {
				Logger.handle(RegionBuilder.class, "copyMap", e);
			}
		});
	}

	private static final void copyMap(int fromRegionX, int fromRegionY, int toRegionX, int toRegionY, int size) {
		int[] planes = new int[4];
		for (int plane = 1; plane < 4; plane++)
			planes[plane] = plane;
		copyMap(fromRegionX, fromRegionY, toRegionX, toRegionY, size, planes, planes);
	}

	@SuppressWarnings("unused")
	private static final void copyMap(int fromRegionX, int fromRegionY, int toRegionX, int toRegionY, int widthRegions, int heightRegions) {
		int[] planes = new int[4];
		for (int plane = 1; plane < 4; plane++)
			planes[plane] = plane;
		copyMap(fromRegionX, fromRegionY, toRegionX, toRegionY, widthRegions, heightRegions, planes, planes);
	}

	private static final void copyMap(int fromRegionX, int fromRegionY, int toRegionX, int toRegionY, int size, int[] fromPlanes, int[] toPlanes) {
		copyMap(fromRegionX, fromRegionY, toRegionX, toRegionY, size, size, fromPlanes, toPlanes);
	}

	private static void copyMap(DynamicRegionReference ref, int localChunkX, int localChunkY, int[] planes, int fromChunkX, int fromChunkY, int[] fromPlanes, int width, int height, Runnable callback) {
		CoresManager.execute(() -> {
			try {
				copyMap(fromChunkX, fromChunkY, ref.getBaseChunkX()+localChunkX, ref.getBaseChunkY()+localChunkY, width, height, fromPlanes, planes);
				if (callback != null)
					callback.run();
			} catch (Throwable e) {
				Logger.handle(RegionBuilder.class, "copyMap", e);
			}
		});
	}

	private static final void copyMap(int fromRegionX, int fromRegionY, int toRegionX, int toRegionY, int widthRegions, int heightRegions, int[] fromPlanes, int[] toPlanes) {
		if (fromPlanes.length != toPlanes.length)
			throw new RuntimeException("PLANES LENGTH ISNT SAME OF THE NEW PLANES ORDER!");
		for (int xOffset = 0; xOffset < widthRegions; xOffset++)
			for (int yOffset = 0; yOffset < heightRegions; yOffset++) {
				int fromThisRegionX = fromRegionX + xOffset;
				int fromThisRegionY = fromRegionY + yOffset;
				int toThisRegionX = toRegionX + xOffset;
				int toThisRegionY = toRegionY + yOffset;
				int regionId = ((toThisRegionX / 8) << 8) + (toThisRegionY / 8);
				DynamicRegion toRegion = createDynamicRegion(regionId);
				int regionOffsetX = (toThisRegionX - ((toThisRegionX / 8) * 8));
				int regionOffsetY = (toThisRegionY - ((toThisRegionY / 8) * 8));
				for (int pIndex = 0; pIndex < fromPlanes.length; pIndex++) {
					int toPlane = toPlanes[pIndex];
					toRegion.getRegionCoords()[toPlane][regionOffsetX][regionOffsetY][0] = fromThisRegionX;
					toRegion.getRegionCoords()[toPlane][regionOffsetX][regionOffsetY][1] = fromThisRegionY;
					toRegion.getRegionCoords()[toPlane][regionOffsetX][regionOffsetY][2] = fromPlanes[pIndex];
					toRegion.setReloadObjects(toPlane, regionOffsetX, regionOffsetY);
				}
			}
	}
}
