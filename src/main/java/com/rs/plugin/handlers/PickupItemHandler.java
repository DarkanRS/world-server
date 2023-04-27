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
import com.rs.plugin.events.PickupItemEvent;

import java.util.function.Consumer;

public class PickupItemHandler extends PluginHandler<PickupItemEvent> {

	private Tile[] tiles;

	public PickupItemHandler(Object[] namesOrIds, Tile[] tiles, Consumer<PickupItemEvent> handler) {
		super(namesOrIds, handler);
		this.tiles = tiles;
	}
	
	public PickupItemHandler(Object[] namesOrIds, Tile tile, Consumer<PickupItemEvent> handler) {
		super(namesOrIds, handler);
		this.tiles = new Tile[] { tile };
	}
	
	public PickupItemHandler(int id, Tile[] tiles, Consumer<PickupItemEvent> handler) {
		this(new Object[] { id }, tiles, handler);
	}
	
	public PickupItemHandler(int id, Tile tile, Consumer<PickupItemEvent> handler) {
		this(new Object[] { id }, tile, handler);
	}
	
	public PickupItemHandler(String name, Tile[] tiles, Consumer<PickupItemEvent> handler) {
		this(new Object[] { name }, tiles, handler);
	}

	public PickupItemHandler(String name, Tile tile, Consumer<PickupItemEvent> handler) {
		this(new Object[] { name }, tile, handler);
	}
	
	public Tile[] getTiles() {
		return tiles;
	}
}
