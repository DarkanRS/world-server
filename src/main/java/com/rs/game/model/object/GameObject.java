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
package com.rs.game.model.object;

import com.rs.cache.loaders.ObjectDefinitions;
import com.rs.cache.loaders.ObjectType;
import com.rs.game.World;
import com.rs.game.map.Chunk;
import com.rs.game.map.ChunkManager;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Tile;
import com.rs.lib.game.WorldObject;
import com.rs.lib.util.GenericAttribMap;

public class GameObject extends WorldObject {
	protected transient GenericAttribMap attribs;

	public enum RouteType {
		NORMAL, WALK_ONTO
	}

	protected RouteType routeType = RouteType.NORMAL;
	private ObjectMeshModifier meshModifier;

	private transient int originalId = -1;
	private transient int idChangeTicks = -1;
	private final int hashCode;

	public GameObject(int id, ObjectType type, int rotation, Tile tile) {
		super(id, type, rotation, tile);
		this.routeType = World.getRouteType(id);
		this.hashCode = genHashCode();
	}

	public GameObject(int id, int rotation, int x, int y, int plane) {
		super(id, rotation, x, y, plane);
		this.routeType = World.getRouteType(id);
		this.hashCode = genHashCode();
	}

	public GameObject(int id, ObjectType type, int rotation, int x, int y, int plane) {
		super(id, type, rotation, x, y, plane);
		this.routeType = World.getRouteType(id);
		this.hashCode = genHashCode();
	}

	public GameObject(WorldObject object) {
		super(object);
		this.routeType = World.getRouteType(id);
		this.hashCode = genHashCode();
	}

	public GameObject(GameObject object) {
		super(object);
		routeType = object.getRouteType();
		this.hashCode = genHashCode();
	}

	public GameObject(WorldObject object, int newId) {
		super(object);
		this.id = newId;
		this.routeType = World.getRouteType(newId);
		this.hashCode = genHashCode();
	}

	public GameObject(GameObject object, int newId) {
		super(object);
		this.id = newId;
		this.routeType = object.getRouteType();
		this.hashCode = genHashCode();
	}

	@Override
	public boolean equals(Object other) {
		if ((other == null) || !(other instanceof GameObject obj))
			return false;
		return obj.hashCode() == hashCode();
	}

	@Override
	public int hashCode() {
		return hashCode;
	}

	public int genHashCode() {
		int hash = tile.getTileHash();
		hash = ((hash << 5) - hash) + id;
		hash = ((hash << 5) - hash) + rotation;
		hash = ((hash << 5) - hash) + type.id;
		return hash;
	}

	public int positionHashCode() {
		int hash = tile.getTileHash();
		hash = ((hash << 5) - hash) + type.id;
		return hash;
	}

	@Override
	public String toString() {
		return "[id:"+id+" loc:("+getX()+","+getY()+","+getPlane()+") type:"+type+" rot:"+rotation+" name:"+getDefinitions().getName()+"]";
	}

	public boolean process() {
		boolean continueProcessing = false;
		if (idChangeTicks == -2)
			return true;
		if (idChangeTicks > -1) {
			if (idChangeTicks-- == 0)
				setId(originalId);
			else
				continueProcessing = true;
		}
		return continueProcessing;
	}

	public GameObject setId(int id) {
		int lastId = this.id;
		this.id = id;
		if (lastId != id)
			World.refreshObject(this);
		if (id != originalId) {
			ChunkManager.getChunk(getTile().getChunkId(), true).flagForProcess(this);
			idChangeTicks = -2;
		} else {
			ChunkManager.getChunk(getTile().getChunkId(), true).unflagForProcess(this);
			idChangeTicks = -1;
		}
		return this;
	}
	
	public ObjectMeshModifier modifyMesh() {
		this.meshModifier = new ObjectMeshModifier(this);
		return this.meshModifier;
	}
	
	public void refresh() {
		World.refreshObject(this);
	}

	public void setIdTemporary(int id, int ticks) {
		if (this.id == id)
			return;
		final int original = this.id;
		Chunk chunk = ChunkManager.getChunk(getTile().getChunkId(), true);
		chunk.flagForProcess(this);
		setId(id);
		originalId = original;
		idChangeTicks = ticks;
	}

	public GameObject setRouteType(RouteType routeType) {
		this.routeType = routeType;
		return this;
	}

	public ObjectDefinitions getDefinitions(Player player) {
		return ObjectDefinitions.getDefs(id, player.getVars());
	}

	public void animate(Animation animation) {
		World.sendObjectAnimation(this, animation);
	}

	public RouteType getRouteType() {
		return routeType;
	}

	public GenericAttribMap getAttribs() {
		if (attribs == null)
			attribs = new GenericAttribMap();
		return attribs;
	}

	public ObjectMeshModifier getMeshModifier() {
		return meshModifier;
	}

	public int getOriginalId() {
		return originalId;
	}
}
