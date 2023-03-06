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

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import com.rs.lib.game.Tile;
import com.rs.plugin.events.PlayerStepEvent;

public class PlayerStepHandler extends PluginHandler<PlayerStepEvent> {

	public PlayerStepHandler(Tile[] tiles, Consumer<PlayerStepEvent> handler) {
		super(null, handler);
		List<Integer> tileHashes = new ArrayList<>();
		for (Tile tile : tiles)
			tileHashes.add(tile.getTileHash());
		keys = tileHashes.toArray();
	}
	
	public PlayerStepHandler(Tile tile, Consumer<PlayerStepEvent> handler) {
		this(new Tile[] { tile }, handler);
	}
}
