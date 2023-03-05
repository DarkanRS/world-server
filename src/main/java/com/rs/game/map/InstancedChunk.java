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

import com.rs.cache.loaders.map.ClipFlag;
import com.rs.game.World;
import com.rs.game.model.entity.pathing.WorldCollision;
import com.rs.game.model.object.GameObject;
import com.rs.lib.game.Tile;
import com.rs.lib.game.WorldObject;
import com.rs.lib.util.MapUtils;
import com.rs.lib.util.MapUtils.Structure;

public class InstancedChunk extends Chunk {
	private int originalChunkId;
	private int originalChunkX;
	private int originalChunkY;
	private int originalPlane;

	private int rotation;

	public InstancedChunk(int originalChunkId, int rotation, int newChunk) {
		super(newChunk);
		this.originalChunkId = originalChunkId;
		int[] coords = MapUtils.decode(Structure.CHUNK, originalChunkId);
		this.originalChunkX = coords[0];
		this.originalChunkY = coords[1];
		this.originalPlane = coords[2];
		this.rotation = rotation;
	}

	@Override
	public void checkLoaded() {
		super.checkLoaded();
	}

	public void loadMap() {
		clearCollisionData();
		Chunk realChunk = World.getChunk(getOriginalChunkId());
		for (int x = 0;x < 8;x++) {
			for (int y = 0;y < 8;y++) {
				Tile original = Tile.of(getOriginalBaseX()+x, getOriginalBaseY()+y, originalPlane);
				int[] coords = transform(x, y, rotation);
				Tile toTile = Tile.of(getBaseX()+coords[0], getBaseY()+coords[1], getPlane());
				WorldCollision.setFlags(toTile, WorldCollision.getFlags(original) & (ClipFlag.PFBW_FLOOR.flag | ClipFlag.UNDER_ROOF.flag));
			}
		}
		for (WorldObject orig : realChunk.getBaseObjects()) {
			GameObject adjusted = new GameObject(orig);
			int[] coords = InstancedChunk.transform(orig.getTile().getXInChunk(),orig.getTile().getYInChunk(), rotation, orig.getDefinitions().getSizeX(), orig.getDefinitions().getSizeY(), orig.getRotation());
			adjusted.setTile(Tile.of(getBaseX() + coords[0], getBaseY() + coords[1], adjusted.getPlane()));
			adjusted.setRotation((adjusted.getRotation() + rotation) & 0x3);
			addBaseObject(adjusted);
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
	public int getRotation() {
		return rotation;
	}

	public void setRotation(int rotation) {
		this.rotation = rotation;
	}

	public int getOriginalChunkId() {
		return originalChunkId;
	}

	public int getRenderChunkX() {
		return originalChunkX;
	}

	public int getRenderChunkY() {
		return originalChunkY;
	}

	public int getRenderPlane() {
		return originalPlane;
	}

	public int getOriginalBaseX() {
		return originalChunkX << 3;
	}

	public int getOriginalBaseY() {
		return originalChunkY << 3;
	}
}
