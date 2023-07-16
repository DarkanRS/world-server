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
package com.rs.utils.spawns;

import com.rs.cache.loaders.ObjectType;
import com.rs.game.World;
import com.rs.game.model.object.GameObject;
import com.rs.lib.game.Tile;

public class ObjectSpawn {

	private String comment;
	private int objectId;
	private int type;
	private int rotation;
	private Tile tile;

	public ObjectSpawn(int objectId, int type, int rotation, Tile tile) {
		this(objectId, type, rotation, tile, null);
	}

	public ObjectSpawn(int objectId, int type, int rotation, Tile tile, String comment) {
		this.objectId = objectId;
		this.type = type;
		this.rotation = rotation;
		this.tile = tile;
		this.comment = comment;
	}

	public String getComment() {
		return comment;
	}

	public int getObjectId() {
		return objectId;
	}

	public int getType() {
		return type;
	}

	public int getRotation() {
		return rotation;
	}

	public Tile getTile() {
		return tile;
	}

	public void spawn() {
		World.spawnObject(new GameObject(objectId, ObjectType.forId(type), rotation, tile.getX(), tile.getY(), tile.getPlane()));
	}
}
