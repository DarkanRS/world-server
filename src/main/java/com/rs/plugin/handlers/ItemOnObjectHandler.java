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

import com.rs.lib.game.Tile;
import com.rs.plugin.events.ItemOnObjectEvent;

import java.util.function.Consumer;

public class ItemOnObjectHandler extends PluginHandler<ItemOnObjectEvent> {

	private Object[] objectKeys, itemKeys;
	private Tile[] tiles;
	private boolean checkDistance = true;

	public ItemOnObjectHandler(boolean checkDistance, Object[] objectNamesOrIds, Object[] itemNamesOrIds, Tile[] tiles, Consumer<ItemOnObjectEvent> handler) {
		super(new Object[] { "meme" }, handler);
		this.tiles = tiles;
		this.checkDistance = checkDistance;
		this.objectKeys = objectNamesOrIds;
		this.itemKeys = itemNamesOrIds;
	}

	public ItemOnObjectHandler(Object[] objectNamesOrIds, Object[] itemNamesOrIds, Tile[] tiles, Consumer<ItemOnObjectEvent> handler) {
		this(true, objectNamesOrIds, itemNamesOrIds, tiles, handler);
	}
	
	public ItemOnObjectHandler(boolean checkDistance, Object[] objectNamesOrIds, Object[] itemNamesOrIds, Consumer<ItemOnObjectEvent> handler) {
		this(true, objectNamesOrIds, itemNamesOrIds, null, handler);
		this.checkDistance = checkDistance;
	}

	public ItemOnObjectHandler(Object[] objectNamesOrIds, Object[] itemNamesOrIds, Consumer<ItemOnObjectEvent> handler) {
		this(true, objectNamesOrIds, itemNamesOrIds, null, handler);
	}

	public boolean isCheckDistance() {
		return checkDistance;
	}

	public Tile[] getTiles() {
		return tiles;
	}

	public Object[] getObjectKeys() {
		return objectKeys;
	}

	public Object[] getItemKeys() {
		return itemKeys;
	}
}
