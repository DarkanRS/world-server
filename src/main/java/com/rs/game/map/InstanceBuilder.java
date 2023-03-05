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
package com.rs.game.map;

import java.util.HashSet;
import java.util.Set;

import com.rs.engine.thread.TaskExecutor;
import com.rs.game.World;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.game.Tile;
import com.rs.lib.util.Logger;
import com.rs.lib.util.MapUtils;
import com.rs.lib.util.MapUtils.Structure;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.ints.IntSets;

public final class InstanceBuilder {

	/**
	 * Instance builder
	 */
	public static class InstanceReference {
		private int[] base;
		private IntSet chunkIds = new IntOpenHashSet();
		private int width;
		private int height;
		private boolean destroyed;

		public InstanceReference(int width, int height) {
			this.width = width;
			this.height = height;
			destroyed = false;
		}

		public void requestChunkBound(Runnable callback) {
			destroyed = false;
			InstanceBuilder.findEmptyChunkBound(this, callback);
		}

		public void copyChunk(int localChunkX, int localChunkY, int plane, int fromChunkX, int fromChunkY, int fromPlane, int rotation, Runnable callback) {
			if (base == null)
				requestChunkBound(() -> {
					InstanceBuilder.copyChunk(this, localChunkX, localChunkY, plane, fromChunkX, fromChunkY, fromPlane, rotation, callback);
				});
			else
				InstanceBuilder.copyChunk(this, localChunkX, localChunkY, plane, fromChunkX, fromChunkY, fromPlane, rotation, callback);
		}

		public void clearChunk(int localChunkX, int localChunkY, int plane, Runnable callback) {
			if (base == null)
				requestChunkBound(() -> {
					InstanceBuilder.clearChunk(this, localChunkX, localChunkY, plane, callback);
				});
			else
				InstanceBuilder.clearChunk(this, localChunkX, localChunkY, plane, callback);
		}

		public void copy2x2ChunkSquare(int localChunkX, int localChunkY, int fromChunkX, int fromChunkY, int rotation, int[] planes, Runnable callback) {
			if (base == null)
				requestChunkBound(() -> {
					InstanceBuilder.copy2x2ChunkSquare(this, localChunkX, localChunkY, fromChunkX, fromChunkY, rotation, planes, callback);
				});
			else
				InstanceBuilder.copy2x2ChunkSquare(this, localChunkX, localChunkY, fromChunkX, fromChunkY, rotation, planes, callback);
		}

		public void copyMap(int localChunkX, int localChunkY, int fromChunkX, int fromChunkY, int size, Runnable callback) {
			if (base == null)
				requestChunkBound(() -> {
					InstanceBuilder.copyMap(this, localChunkX, localChunkY, fromChunkX, fromChunkY, size, callback);
				});
			else
				InstanceBuilder.copyMap(this, localChunkX, localChunkY, fromChunkX, fromChunkY, size, callback);
		}

		public void copyMapAllPlanes(int fromChunkX, int fromChunkY, int size, Runnable callback) {
			copyMap(0, 0, fromChunkX, fromChunkY, size, callback);
		}

		public void copyMap(int localChunkX, int localChunkY, int[] planes, int fromChunkX, int fromChunkY, int[] fromPlanes, int width, int height, Runnable callback) {
			if (base == null)
				requestChunkBound(() -> {
					InstanceBuilder.copyMap(this, localChunkX, localChunkY, planes, fromChunkX, fromChunkY, fromPlanes, width, height, callback);
				});
			else
				InstanceBuilder.copyMap(this, localChunkX, localChunkY, planes, fromChunkX, fromChunkY, fromPlanes, width, height, callback);
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
					InstanceBuilder.clearMap(this, chunkX, chunkY, width, height, planes, callback);
				});
			else
				InstanceBuilder.clearMap(this, chunkX, chunkY, width, height, planes, callback);
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
			InstanceBuilder.destroyMap(this, callback);
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

		public Tile getBase() {
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

		public boolean isDestroyed() {
			return destroyed;
		}

		public boolean isCreated() {
			return base != null;
		}

		public IntSet getChunkIds() {
			return chunkIds;
		}
	}

	public static final int NORTH = 0, EAST = 1, SOUTH = 2, WEST = 3;

	private static final Set<Integer> EXISTING_CHUNKS = new HashSet<>();

	private static final int MAX_REGION_X = 127;
	private static final int MAX_REGION_Y = 255;

	private static void reserveChunks(int fromChunkX, int fromChunkY, int width, int height, boolean remove) {
		synchronized(EXISTING_CHUNKS) {
			for (int plane = 0;plane < 4;plane++) {
				for (int chunkX = fromChunkX; chunkX < fromChunkX + width; chunkX++) {
					for (int chunkY = fromChunkY; chunkY < fromChunkY + height; chunkY++)
						if (remove)
							EXISTING_CHUNKS.remove(MapUtils.encode(Structure.CHUNK, chunkX, chunkY, plane));
						else
							EXISTING_CHUNKS.add(MapUtils.encode(Structure.CHUNK, chunkX, chunkY, plane));
				}
			}
		}
	}

	private static boolean chunkExists(int chunkId) {
		synchronized(EXISTING_CHUNKS) {
			return EXISTING_CHUNKS.contains(chunkId);
		}
	}

	private static InstancedChunk createInstancedChunk(int fromChunkId, int rotation, int toChunkId) {
		Chunk chunk = World.getChunk(toChunkId);
		if (chunk != null) {
			if (chunk instanceof InstancedChunk dr)
				return dr;
			destroyChunk(toChunkId);
		}
		InstancedChunk newChunk = new InstancedChunk(fromChunkId, rotation, toChunkId);
		World.putChunk(toChunkId, newChunk);
		return newChunk;
	}

	public static void destroyChunk(int chunkId) {
		Chunk chunk = World.getChunk(chunkId);
		if (chunk != null) {
			Set<Integer> playerIndexes = chunk.getPlayerIndexes();
			Set<Integer> npcIndexes = chunk.getNPCsIndexes();
			if (chunk.getAllGroundItems() != null)
				chunk.getAllGroundItems().clear();
			if (chunk.getGroundItems() != null)
				chunk.getGroundItems().clear();
			chunk.getSpawnedObjects().clear();
			chunk.getRemovedObjects().clear();
			if (npcIndexes != null)
				for (int npcIndex : npcIndexes) {
					NPC npc = World.getNPCs().get(npcIndex);
					if (npc == null)
						continue;
					npc.finish();
				}
			World.removeChunk(chunkId);

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

	public static void findEmptyChunkBound(InstanceReference ref, Runnable callback) {
		TaskExecutor.execute(() -> {
			try {
				ref.base = findEmptyChunkBound(ref.width, ref.height);
				for (int plane = 0;plane < 4;plane++) {
					for (int x = ref.base[0];x < ref.width;x++) {
						for (int y = ref.base[1];y < ref.height;y++) {
							ref.chunkIds.add(MapUtils.encode(Structure.CHUNK, x, y, plane));
						}
					}
				}
				if (callback != null)
					callback.run();
			} catch (Throwable e) {
				Logger.handle(InstanceBuilder.class, "findEmptyChunkBound", e);
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
		for (int chunkX = 8; chunkX <= MAX_REGION_X - widthChunks; chunkX++) {
			skip: for (int chunkY = 8; chunkY <= MAX_REGION_Y - heightChunks; chunkY++) {
				int chunkId = MapUtils.encode(Structure.CHUNK, chunkX, chunkY, 0);
				for (int checkChunkX = chunkX - 1; checkChunkX <= chunkX + widthChunks; checkChunkX++) {
					for (int checkChunkY = chunkY - 1; checkChunkY <= chunkY + heightChunks; checkChunkY++) {
						int checkChunkId = MapUtils.encode(Structure.CHUNK, checkChunkX, checkChunkY, 0);
						if (chunkExists(checkChunkId))
							continue skip;

					}
				}
				reserveChunks(chunkX, chunkY, widthChunks, heightChunks, false);
				return chunkId;
			}
		}
		return -1;
	}

	private static final void destroyMap(InstanceReference ref, Runnable callback) {
		TaskExecutor.schedule(() -> {
			try {
				destroyMap(ref.getBaseChunkX(), ref.getBaseChunkY(), ref.width, ref.height);
				if (callback != null)
					callback.run();
			} catch (Throwable e) {
				Logger.handle(InstanceBuilder.class, "destroyMap", e);
			}
		}, 8);
	}

	private static final void destroyMap(int chunkX, int chunkY, int width, int height) {
		for (int z = 0;z < 4;z++)
			for (int x = chunkX; x < chunkX + width; x++)
				for (int y = chunkY; y < chunkY + height; y++)
					destroyChunk(MapUtils.encode(Structure.CHUNK, x, y, z));
		reserveChunks(chunkX, chunkY, width, height, true);
	}

	private static void copyChunk(InstanceReference ref, int localChunkX, int localChunkY, int plane, int fromChunkX, int fromChunkY, int fromPlane, int rotation, Runnable callback) {
		TaskExecutor.execute(() -> {
			try {
				copyChunk(fromChunkX, fromChunkY, fromPlane, ref.getBaseChunkX()+localChunkX, ref.getBaseChunkY()+localChunkY, plane, rotation);
				if (callback != null)
					callback.run();
			} catch (Throwable e) {
				Logger.handle(InstanceBuilder.class, "copyChunk", e);
			}
		});
	}

	private static void copyChunk(int fromChunkX, int fromChunkY, int fromPlane, int toChunkX, int toChunkY, int toPlane, int rotation) {
		InstancedChunk toChunk = createInstancedChunk(
			MapUtils.encode(Structure.CHUNK, fromChunkX, fromChunkY, fromPlane),
			rotation,
			MapUtils.encode(Structure.CHUNK, toChunkX, toChunkY, toPlane)
		);
		toChunk.loadMap();
	}

	private static void copy2x2ChunkSquare(InstanceReference ref, int chunkX, int chunkY, int fromChunkX, int fromChunkY, int rotation, int[] planes, Runnable callback) {
		TaskExecutor.execute(() -> {
			try {
				copy2x2ChunkSquare(fromChunkX, fromChunkY, ref.getBaseChunkX()+chunkX, ref.getBaseChunkY()+chunkY, rotation, planes);
				if (callback != null)
					callback.run();
			} catch (Throwable e) {
				Logger.handle(InstanceBuilder.class, "copy2x2ChunkSquare", e);
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

	private static void clearChunk(InstanceReference ref, int localChunkX, int localChunkY, int plane, Runnable callback) {
		TaskExecutor.execute(() -> {
			try {
				cutChunk(ref.getBaseChunkX()+localChunkX, ref.getBaseChunkY()+localChunkY, plane);
				if (callback != null)
					callback.run();
			} catch (Throwable e) {
				Logger.handle(InstanceBuilder.class, "clearChunk", e);
			}
		});
	}

	private static void cutChunk(int toChunkX, int toChunkY, int toPlane) {
		copyChunk(0, 0, 0, toChunkX, toChunkY, toPlane, 0);
	}

	private static final void repeatMap(int toChunkX, int toChunkY, int widthChunks, int heightChunks, int fromChunkX, int fromChunkY, int fromPlane, int rotation, int... toPlanes) {
		for (int plane : toPlanes) {
			for (int xOffset = 0; xOffset < widthChunks; xOffset++) {
				for (int yOffset = 0; yOffset < heightChunks; yOffset++) {
					InstancedChunk toChunk = createInstancedChunk(
						MapUtils.encode(Structure.CHUNK, fromChunkX, fromChunkY, fromPlane),
						rotation,
						MapUtils.encode(Structure.CHUNK, toChunkX + xOffset, toChunkY + yOffset, plane)
					);
					toChunk.loadMap();
				}
			}
		}
	}

	private static void clearMap(InstanceReference ref, int localChunkX, int localChunkY, int width, int height, int[] planes, Runnable callback) {
		TaskExecutor.execute(() -> {
			try {
				cutMap(ref.getBaseChunkX()+localChunkX, ref.getBaseChunkY()+localChunkY, width, height, planes);
				if (callback != null)
					callback.run();
			} catch (Throwable e) {
				Logger.handle(InstanceBuilder.class, "clearMap", e);
			}
		});
	}

	private static final void cutMap(int toChunkX, int toChunkY, int widthChunks, int heightChunks, int... toPlanes) {
		repeatMap(toChunkX, toChunkY, widthChunks, heightChunks, 0, 0, 0, 0, toPlanes);
	}

	private static void copyMap(InstanceReference ref, int localChunkX, int localChunkY, int fromChunkX, int fromChunkY, int size, Runnable callback) {
		TaskExecutor.execute(() -> {
			try {
				copyMap(fromChunkX, fromChunkY, ref.getBaseChunkX()+localChunkX, ref.getBaseChunkY()+localChunkY, size);
				if (callback != null)
					callback.run();
			} catch (Throwable e) {
				Logger.handle(InstanceBuilder.class, "copyMap", e);
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

	private static void copyMap(InstanceReference ref, int localChunkX, int localChunkY, int[] planes, int fromChunkX, int fromChunkY, int[] fromPlanes, int width, int height, Runnable callback) {
		TaskExecutor.execute(() -> {
			try {
				copyMap(fromChunkX, fromChunkY, ref.getBaseChunkX()+localChunkX, ref.getBaseChunkY()+localChunkY, width, height, fromPlanes, planes);
				if (callback != null)
					callback.run();
			} catch (Throwable e) {
				Logger.handle(InstanceBuilder.class, "copyMap", e);
			}
		});
	}

	private static final void copyMap(int fromChunkX, int fromChunkY, int toChunkX, int toChunkY, int width, int height, int[] fromPlanes, int[] toPlanes) {
		if (fromPlanes.length != toPlanes.length)
			throw new RuntimeException("PLANES LENGTH ISNT SAME OF THE NEW PLANES ORDER!");
		for (int planeIdx = 0; planeIdx < fromPlanes.length; planeIdx++) {
			for (int xOffset = 0; xOffset < width; xOffset++)
				for (int yOffset = 0; yOffset < height; yOffset++) {
					InstancedChunk instance = createInstancedChunk(
						MapUtils.encode(Structure.CHUNK, fromChunkX + xOffset, fromChunkY + yOffset, fromPlanes[planeIdx]),
						0,
						MapUtils.encode(Structure.CHUNK, toChunkX + xOffset, toChunkY + yOffset, toPlanes[planeIdx])
					);
					instance.loadMap();
				}
		}
	}
}
