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

import com.rs.lib.game.WorldTile;
import com.rs.plugin.events.PlayerStepEvent;

public abstract class PlayerStepHandler extends PluginHandler<PlayerStepEvent> {

	public PlayerStepHandler(WorldTile... tiles) {
		super(null);
		List<Integer> tileHashes = new ArrayList<>();
		for (WorldTile tile : tiles)
			tileHashes.add(tile.getTileHash());
		keys = tileHashes.toArray();
	}
}
