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
package com.rs.game.map.instance;

import com.rs.engine.thread.LowPriorityTaskExecutor;
import com.rs.game.map.Chunk;
import com.rs.game.map.ChunkManager;
import com.rs.lib.util.Logger;
import com.rs.lib.util.MapUtils;
import com.rs.lib.util.MapUtils.Structure;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public final class InstanceBuilder {
	public static final int NORTH = 0, EAST = 1, SOUTH = 2, WEST = 3;
	private static final Set<Integer> RESERVED_REGIONS = new HashSet<>();

	private static final int MAX_REGION_X = 127;
	private static final int MAX_REGION_Y = 255;

	private static void reserveRegions(int fromRegionX, int fromRegionY, int width, int height, boolean remove) {
		synchronized(RESERVED_REGIONS) {
			for (int regionX = fromRegionX; regionX < fromRegionX + width; regionX++)
				for (int regionY = fromRegionY; regionY < fromRegionY + height; regionY++)
					if (remove)
						RESERVED_REGIONS.remove(MapUtils.encode(Structure.REGION, regionX, regionY));
					else
						RESERVED_REGIONS.add(MapUtils.encode(Structure.REGION, regionX, regionY));

		}
	}

	private static boolean regionReserved(int regionId) {
		synchronized(RESERVED_REGIONS) {
			return RESERVED_REGIONS.contains(regionId);
		}
	}

	public static InstancedChunk createAndReserveChunk(int fromChunkId, int toChunkId, int rotation) {
		Chunk chunk = ChunkManager.getChunk(toChunkId);
		if (chunk != null)
			chunk.destroy();
		InstancedChunk newChunk = new InstancedChunk(fromChunkId, toChunkId, rotation);
		ChunkManager.putChunk(toChunkId, newChunk);
		return newChunk;
	}

	public static void destroyChunk(int chunkId) {
		Chunk chunk = ChunkManager.getChunk(chunkId);
		if (chunk != null)
			chunk.destroy();
	}

	public static void findEmptyChunkBound(Instance ref, CompletableFuture<Boolean> future) {
		LowPriorityTaskExecutor.execute(() -> {
			try {
				ref.setChunkBase(findEmptyChunkBound(ref.getWidth(), ref.getHeight()));
				for (int plane = 0;plane < 4;plane++) {
					for (int x = ref.getChunkBase()[0]; x < ref.getWidth(); x++) {
						for (int y = ref.getChunkBase()[1];y < ref.getHeight();y++) {
							ref.getChunkIds().add(MapUtils.encode(Structure.CHUNK, x, y, plane));
						}
					}
				}
				future.complete(true);
			} catch (Throwable e) {
				Logger.handle(InstanceBuilder.class, "findEmptyChunkBound", e);
				future.completeExceptionally(e);
			}
		});
	}

	private static int[] findEmptyChunkBounds(int widthChunks, int heightChunks) {
		int chunkId = findEmptyBaseChunkId(widthChunks, heightChunks);
		int[] xyz = MapUtils.decode(Structure.CHUNK, chunkId);
		return new int[] { xyz[0], xyz[1] };
	}

	private static int[] findEmptyChunkBound(int widthChunks, int heightChunks) {
		return findEmptyChunkBounds(widthChunks, heightChunks);
	}

	private static int findEmptyBaseChunkId(int widthChunks, int heightChunks) {
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
				for (int checkRegionX = regionX - 1; checkRegionX <= regionX + regionsDistanceX; checkRegionX++)
					for (int checkRegionY = regionY - 1; checkRegionY <= regionY + regionsDistanceY; checkRegionY++) {
						int regionId = MapUtils.encode(Structure.REGION, checkRegionX, checkRegionY);
						if (regionReserved(regionId))
							continue skip;

					}
				reserveRegions(regionX, regionY, regionsDistanceX, regionsDistanceY, false);
				return MapUtils.encode(Structure.CHUNK, regionX << 3, regionY << 3);
			}
		return -1;
	}

	static final void destroyMap(Instance ref, CompletableFuture<Boolean> future) {
		if (ref.getChunkBase() == null) {
			future.complete(true);
			return;
		}
		LowPriorityTaskExecutor.schedule(() -> {
			try {
				destroyMap(ref.getBaseChunkX(), ref.getBaseChunkY(), ref.getWidth(), ref.getHeight());
				future.complete(true);
			} catch (Throwable e) {
				Logger.handle(InstanceBuilder.class, "destroyMap", e);
				future.completeExceptionally(e);
			}
		}, 8);
	}

	private static final void destroyMap(int chunkX, int chunkY, int width, int height) {
		int fromRegionX = chunkX / 8;
		int fromRegionY = chunkY / 8;
		int regionsDistanceX = 1;
		while (width > 8) {
			regionsDistanceX += 1;
			width -= 8;
		}
		int regionsDistanceY = 1;
		while (height > 8) {
			regionsDistanceY += 1;
			height -= 8;
		}

		for (int z = 0;z < 4;z++)
			for (int x = chunkX; x < chunkX + width; x++)
				for (int y = chunkY; y < chunkY + height; y++)
					destroyChunk(MapUtils.encode(Structure.CHUNK, x, y, z));
		reserveRegions(fromRegionX, fromRegionY, regionsDistanceX, regionsDistanceY, true);
	}

	static void copyChunk(Instance ref, int localChunkX, int localChunkY, int plane, int fromChunkX, int fromChunkY, int fromPlane, int rotation, CompletableFuture<Boolean> future) {
		LowPriorityTaskExecutor.execute(() -> {
			try {
				InstancedChunk chunk = createAndReserveChunk(fromChunkX, fromChunkY, fromPlane, ref.getBaseChunkX()+localChunkX, ref.getBaseChunkY()+localChunkY, plane, rotation);
				chunk.clearCollisionData();
				chunk.loadMap(ref.isCopyNpcs());
				future.complete(true);
			} catch (Throwable e) {
				Logger.handle(InstanceBuilder.class, "copyChunk", e);
				future.completeExceptionally(e);
			}
		});
	}

	public static InstancedChunk createAndReserveChunk(int fromChunkX, int fromChunkY, int fromPlane, int toChunkX, int toChunkY, int toPlane, int rotation) {
		return createAndReserveChunk(MapUtils.encode(Structure.CHUNK, fromChunkX, fromChunkY, fromPlane), MapUtils.encode(Structure.CHUNK, toChunkX, toChunkY, toPlane), rotation);
	}

	static void copy2x2ChunkSquare(Instance ref, int chunkX, int chunkY, int fromChunkX, int fromChunkY, int rotation, int[] planes, CompletableFuture<Boolean> future) {
		LowPriorityTaskExecutor.execute(() -> {
			try {
				List<InstancedChunk> chunks = copy2x2ChunkSquare(fromChunkX, fromChunkY, ref.getBaseChunkX()+chunkX, ref.getBaseChunkY()+chunkY, rotation, planes);
				for (InstancedChunk chunk : chunks)
					chunk.clearCollisionData();
				for (InstancedChunk chunk : chunks)
					chunk.loadMap(ref.isCopyNpcs());
				future.complete(true);
			} catch (Throwable e) {
				Logger.handle(InstanceBuilder.class, "copy2x2ChunkSquare", e);
				future.completeExceptionally(e);
			}
		});
	}

	private static final List<InstancedChunk> copyChunkRect(int fromRegionX, int fromRegionY, int toRegionX, int toRegionY, int width, int height, int rotation, int... planes) {
		List<InstancedChunk> chunks = new ObjectArrayList<>();
		int[][] offsets = getOffsets(width, height, rotation);
		for (int plane : planes) {
			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x++) {
					int xOffset = offsets[y][x] % width;
					int yOffset = offsets[y][x] / width;
					chunks.add(createAndReserveChunk(fromRegionX + x, fromRegionY + y, plane, toRegionX + xOffset, toRegionY + yOffset, plane, rotation));
				}
			}
		}
		return chunks;
	}

	private static int[][] getOffsets(int width, int height, int rotation) {
		int[][] offsets = new int[height][width];
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int index = y * width + x;
				switch (rotation) {
					case 1 -> offsets[x][height - 1 - y] = index;
					case 2 -> offsets[height - 1 - y][width - 1 - x] = index;
					case 3 -> offsets[width - 1 - x][y] = index;
					default -> offsets[y][x] = index;
				}
			}
		}
		return offsets;
	}

	private static final List<InstancedChunk> copy2x2ChunkSquare(int fromChunkBaseX, int fromChunkBaseY, int toChunkBaseX, int toChunkBaseY, int rotation, int... planes) {
		return copyChunkRect(fromChunkBaseX, fromChunkBaseY, toChunkBaseX, toChunkBaseY, 2, 2, rotation, planes);
	}

	static void clearChunk(Instance ref, int localChunkX, int localChunkY, int plane, CompletableFuture<Boolean> future) {
		LowPriorityTaskExecutor.execute(() -> {
			try {
				cutChunk(ref.getBaseChunkX()+localChunkX, ref.getBaseChunkY()+localChunkY, plane);
				future.complete(true);
			} catch (Throwable e) {
				Logger.handle(InstanceBuilder.class, "clearChunk", e);
				future.completeExceptionally(e);
			}
		});
	}

	private static void cutChunk(int toChunkX, int toChunkY, int toPlane) {
		InstancedChunk chunk = createAndReserveChunk(0, 0, 0, toChunkX, toChunkY, toPlane, 0);
		chunk.clearCollisionData();
		chunk.loadMap(false);
	}

	private static final List<InstancedChunk> repeatMap(int toChunkX, int toChunkY, int widthChunks, int heightChunks, int fromChunkX, int fromChunkY, int fromPlane, int rotation, int... toPlanes) {
		List<InstancedChunk> chunks = new ObjectArrayList<>();
		for (int plane : toPlanes) {
			for (int xOffset = 0; xOffset < widthChunks; xOffset++) {
				for (int yOffset = 0; yOffset < heightChunks; yOffset++) {
					chunks.add(createAndReserveChunk(
						MapUtils.encode(Structure.CHUNK, fromChunkX, fromChunkY, fromPlane),
						MapUtils.encode(Structure.CHUNK, toChunkX + xOffset, toChunkY + yOffset, plane), rotation
					));
				}
			}
		}
		return chunks;
	}

	static void clearMap(Instance ref, int localChunkX, int localChunkY, int width, int height, int[] planes, CompletableFuture<Boolean> future) {
		LowPriorityTaskExecutor.execute(() -> {
			try {
				cutMap(ref.getBaseChunkX()+localChunkX, ref.getBaseChunkY()+localChunkY, width, height, planes);
				future.complete(true);
			} catch (Throwable e) {
				Logger.handle(InstanceBuilder.class, "clearMap", e);
				future.completeExceptionally(e);
			}
		});
	}

	private static final void cutMap(int toChunkX, int toChunkY, int widthChunks, int heightChunks, int... toPlanes) {
		List<InstancedChunk> chunks = repeatMap(toChunkX, toChunkY, widthChunks, heightChunks, 0, 0, 0, 0, toPlanes);
		for (InstancedChunk chunk : chunks) {
			chunk.clearCollisionData();
			chunk.loadMap(false);
		}
	}

	static void copyMap(Instance ref, int localChunkX, int localChunkY, int fromChunkX, int fromChunkY, int size, CompletableFuture<Boolean> future) {
		LowPriorityTaskExecutor.execute(() -> {
			try {
				copyMap(fromChunkX, fromChunkY, ref.getBaseChunkX()+localChunkX, ref.getBaseChunkY()+localChunkY, size, ref.isCopyNpcs());
				future.complete(true);
			} catch (Throwable e) {
				Logger.handle(InstanceBuilder.class, "copyMap", e);
				future.completeExceptionally(e);
			}
		});
	}

	private static final void copyMap(int fromRegionX, int fromRegionY, int toRegionX, int toRegionY, int size, boolean copyNpcs) {
		int[] planes = new int[4];
		for (int plane = 1; plane < 4; plane++)
			planes[plane] = plane;
		copyMap(fromRegionX, fromRegionY, toRegionX, toRegionY, size, planes, planes, copyNpcs);
	}

	@SuppressWarnings("unused")
	private static final void copyMap(int fromRegionX, int fromRegionY, int toRegionX, int toRegionY, int widthRegions, int heightRegions, boolean copyNpcs) {
		int[] planes = new int[4];
		for (int plane = 1; plane < 4; plane++)
			planes[plane] = plane;
		copyMap(fromRegionX, fromRegionY, toRegionX, toRegionY, widthRegions, heightRegions, planes, planes, copyNpcs);
	}

	private static final void copyMap(int fromRegionX, int fromRegionY, int toRegionX, int toRegionY, int size, int[] fromPlanes, int[] toPlanes, boolean copyNpcs) {
		copyMap(fromRegionX, fromRegionY, toRegionX, toRegionY, size, size, fromPlanes, toPlanes, copyNpcs);
	}

	static void copyMap(Instance ref, int localChunkX, int localChunkY, int[] planes, int fromChunkX, int fromChunkY, int[] fromPlanes, int width, int height, boolean copyNpcs, CompletableFuture<Boolean> future) {
		LowPriorityTaskExecutor.execute(() -> {
			try {
				copyMap(fromChunkX, fromChunkY, ref.getBaseChunkX()+localChunkX, ref.getBaseChunkY()+localChunkY, width, height, fromPlanes, planes, copyNpcs);
				future.complete(true);
			} catch (Throwable e) {
				Logger.handle(InstanceBuilder.class, "copyMap", e);
				future.completeExceptionally(e);
			}
		});
	}

	private static final void copyMap(int fromChunkX, int fromChunkY, int toChunkX, int toChunkY, int width, int height, int[] fromPlanes, int[] toPlanes, boolean copyNpcs) {
		if (fromPlanes.length != toPlanes.length)
			throw new RuntimeException("PLANES LENGTH ISNT SAME OF THE NEW PLANES ORDER!");
		for (int planeIdx = 0; planeIdx < fromPlanes.length; planeIdx++) {
			for (int xOffset = 0; xOffset < width; xOffset++)
				for (int yOffset = 0; yOffset < height; yOffset++) {
					InstancedChunk instance = createAndReserveChunk(
						MapUtils.encode(Structure.CHUNK, fromChunkX + xOffset, fromChunkY + yOffset, fromPlanes[planeIdx]),
						MapUtils.encode(Structure.CHUNK, toChunkX + xOffset, toChunkY + yOffset, toPlanes[planeIdx]), 0
					);
					instance.loadMap(copyNpcs);
				}
		}
	}
}
