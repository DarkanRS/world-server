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
package com.rs.plugin.handlers;

import com.rs.lib.game.WorldTile;
import com.rs.plugin.events.ItemOnObjectEvent;

public abstract class ItemOnObjectHandler extends PluginHandler<ItemOnObjectEvent> {

	private WorldTile[] tiles;
	private boolean checkDistance = true;

	public ItemOnObjectHandler(boolean checkDistance, Object[] namesOrIds, WorldTile... tiles) {
		super(namesOrIds);
		this.tiles = tiles;
		this.checkDistance = checkDistance;
	}

	public ItemOnObjectHandler(Object[] namesOrIds, WorldTile... tiles) {
		this(true, namesOrIds, tiles);
	}

	public ItemOnObjectHandler(Object[] namesOrIds) {
		this(true, namesOrIds);
	}

	public boolean isCheckDistance() {
		return checkDistance;
	}

	public WorldTile[] getTiles() {
		return tiles;
	}
}
