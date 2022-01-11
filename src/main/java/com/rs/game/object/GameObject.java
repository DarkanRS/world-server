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
//  Copyright © 2021 Trenton Kress
//  This file is part of project: Darkan
//
package com.rs.game.object;

import com.rs.cache.loaders.ObjectDefinitions;
import com.rs.cache.loaders.ObjectType;
import com.rs.game.World;
import com.rs.game.player.Player;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Animation;
import com.rs.lib.game.WorldObject;
import com.rs.lib.game.WorldTile;

public class GameObject extends WorldObject {

	public enum RouteType {
		NORMAL, WALK_ONTO
	}

	protected RouteType routeType = RouteType.NORMAL;

	public GameObject(int id, ObjectType type, int rotation, WorldTile tile) {
		super(id, type, rotation, tile);
	}

	public GameObject(int id, int rotation, int x, int y, int plane) {
		super(id, rotation, x, y, plane);
	}

	public GameObject(int id, ObjectType type, int rotation, int x, int y, int plane) {
		super(id, type, rotation, x, y, plane);
	}

	public GameObject(GameObject object) {
		super(object);
		routeType = object.getRouteType();
	}

	@Override
	public boolean equals(Object other) {
		if ((other == null) || !(other instanceof GameObject obj))
			return false;
		return obj.hashCode() == hashCode();
	}

	@Override
	public int hashCode() {
		int hash = getTileHash();
		hash = ((hash << 5) - hash) + id;
		hash = ((hash << 5) - hash) + rotation;
		hash = ((hash << 5) - hash) + type.id;
		return hash;
	}

	@Override
	public String toString() {
		return "[id:"+id+" loc:("+getX()+","+getY()+","+getPlane()+") type:"+type+" rot:"+rotation+" name:"+getDefinitions().getName()+"]";
	}

	public GameObject setId(int id) {
		int lastId = this.id;
		this.id = id;
		if (lastId != id)
			World.refreshObject(this);
		return this;
	}

	public void setIdTemporary(int id, int ticks) {
		if (this.id == id)
			return;
		final int original = this.id;
		setId(id);
		WorldTasks.schedule(new WorldTask() {
			@Override
			public void run() {
				setId(original);
			}
		}, ticks);
	}

	public GameObject setIdNoRefresh(int id) {
		this.id = id;
		return this;
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
}
