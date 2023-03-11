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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.rs.engine.thread.LowPriorityTaskExecutor;
import com.rs.game.World;
import com.rs.game.map.Chunk;
import com.rs.lib.util.Logger;
import com.rs.lib.util.MapUtils;
import com.rs.lib.util.MapUtils.Structure;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

public final class InstanceBuilder {
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

	public static InstancedChunk createInstancedChunk(int fromChunkId, int toChunkId, int rotation) {
		Chunk chunk = World.getChunk(toChunkId);
		if (chunk != null)
			chunk.destroy();
		InstancedChunk newChunk = new InstancedChunk(fromChunkId, toChunkId, rotation);
		World.putChunk(toChunkId, newChunk);
		return newChunk;
	}

	public static void destroyChunk(int chunkId) {
		Chunk chunk = World.getChunk(chunkId);
		if (chunk != null)
			chunk.destroy();
	}

	public static void findEmptyChunkBound(Instance ref, Runnable callback) {
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
				if (callback != null)
					callback.run();
			} catch (Throwable e) {
				Logger.handle(InstanceBuilder.class, "findEmptyChunkBound", e);
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

	static final void destroyMap(Instance ref, Runnable callback) {
		LowPriorityTaskExecutor.schedule(() -> {
			try {
				destroyMap(ref.getBaseChunkX(), ref.getBaseChunkY(), ref.getWidth(), ref.getHeight());
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

	static void copyChunk(Instance ref, int localChunkX, int localChunkY, int plane, int fromChunkX, int fromChunkY, int fromPlane, int rotation, Runnable callback) {
		LowPriorityTaskExecutor.execute(() -> {
			try {
				InstancedChunk chunk = copyChunk(fromChunkX, fromChunkY, fromPlane, ref.getBaseChunkX()+localChunkX, ref.getBaseChunkY()+localChunkY, plane, rotation);
				chunk.clearCollisionData();
				chunk.loadMap(ref.isCopyNpcs());
				if (callback != null)
					callback.run();
			} catch (Throwable e) {
				Logger.handle(InstanceBuilder.class, "copyChunk", e);
			}
		});
	}

	private static InstancedChunk copyChunk(int fromChunkX, int fromChunkY, int fromPlane, int toChunkX, int toChunkY, int toPlane, int rotation) {
		InstancedChunk toChunk = createInstancedChunk(
			MapUtils.encode(Structure.CHUNK, fromChunkX, fromChunkY, fromPlane),
			MapUtils.encode(Structure.CHUNK, toChunkX, toChunkY, toPlane), rotation
		);
		return toChunk;
	}

	static void copy2x2ChunkSquare(Instance ref, int chunkX, int chunkY, int fromChunkX, int fromChunkY, int rotation, int[] planes, Runnable callback) {
		LowPriorityTaskExecutor.execute(() -> {
			try {
				List<InstancedChunk> chunks = copy2x2ChunkSquare(fromChunkX, fromChunkY, ref.getBaseChunkX()+chunkX, ref.getBaseChunkY()+chunkY, rotation, planes);
				for (InstancedChunk chunk : chunks)
					chunk.clearCollisionData();
				for (InstancedChunk chunk : chunks)
					chunk.loadMap(ref.isCopyNpcs());
				if (callback != null)
					callback.run();
			} catch (Throwable e) {
				Logger.handle(InstanceBuilder.class, "copy2x2ChunkSquare", e);
			}
		});
	}

	private static final List<InstancedChunk> copy2x2ChunkSquare(int fromRegionX, int fromRegionY, int toRegionX, int toRegionY, int rotation, int... planes) {
		List<InstancedChunk> chunks = new ObjectArrayList<>();
		for (int i : planes) {
			switch (rotation) {
				default -> {
					chunks.add(copyChunk(fromRegionX, fromRegionY, i, toRegionX, toRegionY, i, rotation));
					chunks.add(copyChunk(fromRegionX + 1, fromRegionY, i, toRegionX + 1, toRegionY, i, rotation));
					chunks.add(copyChunk(fromRegionX, fromRegionY + 1, i, toRegionX, toRegionY + 1, i, rotation));
					chunks.add(copyChunk(fromRegionX + 1, fromRegionY + 1, i, toRegionX + 1, toRegionY + 1, i, rotation));
				}
				case 1 -> {
					chunks.add(copyChunk(fromRegionX, fromRegionY, i, toRegionX, toRegionY + 1, i, rotation));
					chunks.add(copyChunk(fromRegionX + 1, fromRegionY, i, toRegionX, toRegionY, i, rotation));
					chunks.add(copyChunk(fromRegionX, fromRegionY + 1, i, toRegionX + 1, toRegionY + 1, i, rotation));
					chunks.add(copyChunk(fromRegionX + 1, fromRegionY + 1, i, toRegionX + 1, toRegionY, i, rotation));
				}
				case 2 -> {
					chunks.add(copyChunk(fromRegionX, fromRegionY, i, toRegionX + 1, toRegionY + 1, i, rotation));
					chunks.add(copyChunk(fromRegionX + 1, fromRegionY, i, toRegionX, toRegionY + 1, i, rotation));
					chunks.add(copyChunk(fromRegionX, fromRegionY + 1, i, toRegionX + 1, toRegionY, i, rotation));
					chunks.add(copyChunk(fromRegionX + 1, fromRegionY + 1, i, toRegionX, toRegionY, i, rotation));
				}
				case 3 -> {
					chunks.add(copyChunk(fromRegionX, fromRegionY, i, toRegionX + 1, toRegionY, i, rotation));
					chunks.add(copyChunk(fromRegionX + 1, fromRegionY, i, toRegionX + 1, toRegionY + 1, i, rotation));
					chunks.add(copyChunk(fromRegionX, fromRegionY + 1, i, toRegionX, toRegionY, i, rotation));
					chunks.add(copyChunk(fromRegionX + 1, fromRegionY + 1, i, toRegionX, toRegionY + 1, i, rotation));
				}
			}
		}
		return chunks;
	}

	static void clearChunk(Instance ref, int localChunkX, int localChunkY, int plane, Runnable callback) {
		LowPriorityTaskExecutor.execute(() -> {
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
		InstancedChunk chunk = copyChunk(0, 0, 0, toChunkX, toChunkY, toPlane, 0);
		chunk.clearCollisionData();
		chunk.loadMap(false);
	}

	private static final List<InstancedChunk> repeatMap(int toChunkX, int toChunkY, int widthChunks, int heightChunks, int fromChunkX, int fromChunkY, int fromPlane, int rotation, int... toPlanes) {
		List<InstancedChunk> chunks = new ObjectArrayList<>();
		for (int plane : toPlanes) {
			for (int xOffset = 0; xOffset < widthChunks; xOffset++) {
				for (int yOffset = 0; yOffset < heightChunks; yOffset++) {
					chunks.add(createInstancedChunk(
						MapUtils.encode(Structure.CHUNK, fromChunkX, fromChunkY, fromPlane),
						MapUtils.encode(Structure.CHUNK, toChunkX + xOffset, toChunkY + yOffset, plane), rotation
					));
				}
			}
		}
		return chunks;
	}

	static void clearMap(Instance ref, int localChunkX, int localChunkY, int width, int height, int[] planes, Runnable callback) {
		LowPriorityTaskExecutor.execute(() -> {
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
		List<InstancedChunk> chunks = repeatMap(toChunkX, toChunkY, widthChunks, heightChunks, 0, 0, 0, 0, toPlanes);
		for (InstancedChunk chunk : chunks) {
			chunk.clearCollisionData();
			chunk.loadMap(false);
		}
	}

	static void copyMap(Instance ref, int localChunkX, int localChunkY, int fromChunkX, int fromChunkY, int size, Runnable callback) {
		LowPriorityTaskExecutor.execute(() -> {
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

	static void copyMap(Instance ref, int localChunkX, int localChunkY, int[] planes, int fromChunkX, int fromChunkY, int[] fromPlanes, int width, int height, Runnable callback) {
		LowPriorityTaskExecutor.execute(() -> {
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
						MapUtils.encode(Structure.CHUNK, toChunkX + xOffset, toChunkY + yOffset, toPlanes[planeIdx]), 0
					);
					instance.loadMap(true);
				}
		}
	}
}
