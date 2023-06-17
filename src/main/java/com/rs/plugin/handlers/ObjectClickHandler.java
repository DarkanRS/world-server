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
package com.rs.plugin.handlers;

import com.rs.cache.loaders.ObjectType;
import com.rs.lib.game.Tile;
import com.rs.plugin.events.ObjectClickEvent;

import java.util.function.Consumer;

public class ObjectClickHandler extends PluginHandler<ObjectClickEvent> {

	private Tile[] tiles;
	private ObjectType type;
	private boolean checkDistance = true;

	public ObjectClickHandler(boolean checkDistance, Object[] namesOrIds, Tile[] tiles, Consumer<ObjectClickEvent> handler) {
		super(namesOrIds, handler);
		this.tiles = tiles;
		this.checkDistance = checkDistance;
	}
	
	public ObjectClickHandler(boolean checkDistance, Object[] namesOrIds, Consumer<ObjectClickEvent> handler) {
		super(namesOrIds, handler);
		this.checkDistance = checkDistance;
	}

	public ObjectClickHandler(Object[] namesOrIds, Tile[] tiles, Consumer<ObjectClickEvent> handler) {
		this(true, namesOrIds, tiles, handler);
	}
	
	public ObjectClickHandler(Object[] namesOrIds, Tile tile, Consumer<ObjectClickEvent> handler) {
		this(true, namesOrIds, new Tile[] { tile }, handler);
	}

	public ObjectClickHandler(Object[] namesOrIds, ObjectType type, Consumer<ObjectClickEvent> handler) {
		this(true, namesOrIds, null, handler);
		this.type = type;
	}

	public ObjectClickHandler(Object[] namesOrIds, Consumer<ObjectClickEvent> handler) {
		this(true, namesOrIds, null, handler);
	}

	public ObjectType getType() {
		return type;
	}

	public boolean isCheckDistance() {
		return checkDistance;
	}

	public Tile[] getTiles() {
		return tiles;
	}
}
