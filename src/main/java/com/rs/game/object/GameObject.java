package com.rs.game.object;

import com.rs.cache.loaders.ObjectDefinitions;
import com.rs.cache.loaders.ObjectType;
import com.rs.game.World;
import com.rs.game.player.Player;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;
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
		this.routeType = object.getRouteType();
	}
	
	@Override
	public boolean equals(Object other) {
		if (other == null)
			return false;
		if (!(other instanceof GameObject))
			return false;
		GameObject obj = (GameObject) other;
		return obj.hashCode() == this.hashCode();
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
		WorldTasksManager.schedule(new WorldTask() {
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
