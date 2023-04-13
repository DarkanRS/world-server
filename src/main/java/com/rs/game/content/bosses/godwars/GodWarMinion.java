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
package com.rs.game.content.bosses.godwars;

import com.rs.game.World;
import com.rs.game.map.ChunkManager;
import com.rs.game.model.entity.npc.NPC;
import com.rs.lib.game.Tile;

public class GodWarMinion extends NPC {

	public GodWarMinion(int id, Tile tile, boolean spawned) {
		super(id, tile, spawned);
		setIgnoreDocile(true);
		setForceAgressive(true);
		setForceAggroDistance(64);
		setIntelligentRouteFinder(true);
	}

	public void respawn() {
		setFinished(false);
		World.addNPC(this);
		setLastChunkId(0);
		ChunkManager.updateChunks(this);
		loadMapRegions();
		checkMultiArea();
	}
}
