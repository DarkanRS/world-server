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

import java.util.ArrayList;
import java.util.List;

import com.rs.cache.Cache;
import com.rs.cache.IndexType;
import com.rs.cache.loaders.ObjectDefinitions;
import com.rs.cache.loaders.ObjectType;
import com.rs.game.model.object.GameObject;
import com.rs.lib.io.InputStream;
import com.rs.lib.util.MapUtils;
import com.rs.lib.util.MapUtils.Structure;
import com.rs.lib.util.MapXTEAs;

public class InstancedChunk extends Chunk {
	private int originalChunkId;
	private int originalChunkX;
	private int originalChunkY;
	private int originalPlane;

	private int rotation;
	private boolean needsReload = true;

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

	public void loadRegionMap() {
		if (needsReload)
			clearCollisionData();

	}

	@Override
	public int getRotation() {
		return rotation;
	}

	public void setRotation(int rotation) {
		this.rotation = rotation;
		needsReload = true;
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
}
