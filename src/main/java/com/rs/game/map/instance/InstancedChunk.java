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

import com.rs.cache.loaders.map.ClipFlag;
import com.rs.game.World;
import com.rs.game.map.Chunk;
import com.rs.game.map.ChunkManager;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.pathing.Direction;
import com.rs.game.model.entity.pathing.WorldCollision;
import com.rs.game.model.object.GameObject;
import com.rs.lib.game.Tile;
import com.rs.lib.game.WorldObject;
import com.rs.lib.util.MapUtils;
import com.rs.lib.util.MapUtils.Structure;
import com.rs.utils.spawns.NPCSpawn;
import com.rs.utils.spawns.NPCSpawns;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectLists;

import java.util.List;

public class InstancedChunk extends Chunk {
	private int fromChunkId;
	private int fromChunkX;
	private int fromChunkY;
	private int fromPlane;

	private int rotation;

	private List<NPC> npcSpawns = ObjectLists.synchronize(new ObjectArrayList<>());

	public InstancedChunk(int fromChunkId, int toChunkId, int rotation) {
		super(toChunkId);
		loadingMapData = true;
		loadedMapData = true;
		loadingSpawnData = true;
		loadedSpawnData = true;
		this.fromChunkId = fromChunkId;
		int[] coords = MapUtils.decode(Structure.CHUNK, fromChunkId);
		this.fromChunkX = coords[0];
		this.fromChunkY = coords[1];
		this.fromPlane = coords[2];
		this.rotation = rotation;
	}

	@Override
	public void checkLoaded() {

	}

	public void loadMap(boolean copyNpcs) {
		Chunk realChunk = ChunkManager.getChunk(getFromChunkId(), true);
		setMapDataLoaded();
		for (int x = 0;x < 8;x++) {
			for (int y = 0;y < 8;y++) {
				Tile original = Tile.of(getOriginalBaseX()+x, getOriginalBaseY()+y, fromPlane);
				int[] coords = transform(x, y, rotation);
				Tile toTile = Tile.of(getBaseX()+coords[0], getBaseY()+coords[1], getPlane());
				WorldCollision.addFlag(toTile, WorldCollision.getFlags(original) & (ClipFlag.PFBW_FLOOR.flag | ClipFlag.UNDER_ROOF.flag));
			}
		}
		for (WorldObject orig : realChunk.getBaseObjects()) {
			GameObject adjusted = new GameObject(orig);
			int[] coords = InstancedChunk.transform(orig.getTile().getXInChunk(),orig.getTile().getYInChunk(), rotation, orig.getDefinitions().getSizeX(), orig.getDefinitions().getSizeY(), orig.getRotation());
			adjusted.setTile(Tile.of(getBaseX() + coords[0], getBaseY() + coords[1], getPlane()));
			adjusted.setRotation((adjusted.getRotation() + rotation) & 0x3);
			ChunkManager.getChunk(adjusted.getTile().getChunkId()).addBaseObject(adjusted);
		}
		if (copyNpcs) {
			List<NPCSpawn> npcSpawns = NPCSpawns.getSpawnsForChunk(getFromChunkId());
			if (npcSpawns != null) {
				for (NPCSpawn npcSpawn : npcSpawns) {
					int[] coords = InstancedChunk.transform(npcSpawn.getTile().getXInChunk(), npcSpawn.getTile().getYInChunk(), rotation, npcSpawn.getDefs().size, npcSpawn.getDefs().size, 0);
					npcSpawn.spawnAtCoords(Tile.of(getBaseX() + coords[0], getBaseY() + coords[1], getPlane()), npcSpawn.getDir() == null ? null : Direction.rotateClockwise(npcSpawn.getDir(), rotation*2));
				}
			}
		}
	}

	public static int[] transform(int localX, int localY, int rotation) {
		return switch(rotation) {
			default -> new int[] { localX, localY };
			case 1 -> new int[] { localY, 7 - localX };
			case 2 -> new int[] { 7 - localX, 7 - localY };
			case 3 -> new int[] { 7 - localY, localX };
		};
	}

	public static int[] transform(int localX, int localY, int mapRotation, int sizeX, int sizeY, int objectRotation) {
		if ((objectRotation & 0x1) == 1) {
			int prevSizeX = sizeX;
			sizeX = sizeY;
			sizeY = prevSizeX;
		}
		return switch(mapRotation) {
			default -> new int[] { localX, localY };
			case 1 -> new int[] { localY, 7 - localX - (sizeX - 1) };
			case 2 -> new int[] { 7 - localX - (sizeX - 1), 7 - localY - (sizeY - 1) };
			case 3 -> new int[] { 7 - localY - (sizeY - 1), localX };
		};
	}

	@Override
	public void destroy() {
		clearCollisionData();
		super.destroy();
		for (NPC npc : npcSpawns) {
			if (npc != null)
				npc.permanentlyDelete();
		}
	}

	@Override
	public int getRotation() {
		return rotation;
	}

	public void setRotation(int rotation) {
		this.rotation = rotation;
	}

	public int getFromChunkId() {
		return fromChunkId;
	}

	public int getRenderChunkX() {
		return fromChunkX;
	}

	public int getRenderChunkY() {
		return fromChunkY;
	}

	public int getRenderPlane() {
		return fromPlane;
	}

	public int getOriginalBaseX() {
		return fromChunkX << 3;
	}

	public int getOriginalBaseY() {
		return fromChunkY << 3;
	}
}
