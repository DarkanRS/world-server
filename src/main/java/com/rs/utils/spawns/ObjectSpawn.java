package com.rs.utils.spawns;

import com.rs.cache.loaders.ObjectType;
import com.rs.game.World;
import com.rs.game.object.GameObject;
import com.rs.lib.game.WorldTile;

public class ObjectSpawn {
	
	private String comment;
	private int objectId;
	private int type;
	private int rotation;
	private WorldTile tile;
	
	public ObjectSpawn(int objectId, int type, int rotation, WorldTile tile) {
		this(objectId, type, rotation, tile, null);
	}
	
	public ObjectSpawn(int objectId, int type, int rotation, WorldTile tile, String comment) {
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

	public WorldTile getTile() {
		return tile;
	}

	public void spawn() {
		World.spawnObject(new GameObject(objectId, ObjectType.forId(type), rotation, tile.getX(), tile.getY(), tile.getPlane()));
	}
}
