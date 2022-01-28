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
package com.rs.utils.spawns;

import com.rs.game.World;
import com.rs.game.pathing.Direction;
import com.rs.lib.game.WorldTile;

public class NPCSpawn {

	private String comment;
	private int npcId;
	private WorldTile tile;
	private Direction dir;
	private String customName;

	public NPCSpawn(int npcId, WorldTile tile, Direction dir, String comment) {
		this.npcId = npcId;
		this.tile = tile;
		this.dir = dir;
		this.comment = comment;
	}

	public NPCSpawn(int npcId, WorldTile tile, String comment) {
		this(npcId, tile, null, comment);
	}

	public void spawn() {
		World.spawnNPC(npcId, tile, dir, false, true, customName);
	}

	public WorldTile getTile() {
		return tile;
	}

	public int getNPCId() {
		return npcId;
	}

	public String getComment() {
		return comment;
	}

	public Direction getDir() {
		return dir;
	}

	public NPCSpawn setCustomName(String customName) {
		this.customName = customName;
		return this;
	}

	public String getCustomName() {
		return customName;
	}
}
