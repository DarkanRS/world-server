package com.rs.plugin.handlers;

import com.rs.cache.loaders.ObjectType;
import com.rs.lib.game.WorldTile;
import com.rs.plugin.events.ObjectClickEvent;

public abstract class ObjectClickHandler extends PluginHandler<ObjectClickEvent> {
	
	private WorldTile[] tiles;
	private ObjectType type;
	private boolean checkDistance = true;

	public ObjectClickHandler(boolean checkDistance, Object[] namesOrIds, WorldTile... tiles) {
		super(namesOrIds);
		this.tiles = tiles;
		this.checkDistance = checkDistance;
	}
	
	public ObjectClickHandler(Object[] namesOrIds, WorldTile... tiles) {
		this(true, namesOrIds, tiles);
	}
	
	public ObjectClickHandler(Object[] namesOrIds, ObjectType type) {
		this(true, namesOrIds);
		this.type = type;
	}
	
	public ObjectClickHandler(Object[] namesOrIds) {
		this(true, namesOrIds);
	}
	
	public ObjectType getType() {
		return type;
	}

	public boolean isCheckDistance() {
		return checkDistance;
	}

	public WorldTile[] getTiles() {
		return tiles;
	}
}
