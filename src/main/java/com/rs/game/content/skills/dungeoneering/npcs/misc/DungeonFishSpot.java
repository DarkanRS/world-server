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
package com.rs.game.content.skills.dungeoneering.npcs.misc;

import com.rs.game.content.skills.dungeoneering.DungeonManager;
import com.rs.game.content.skills.dungeoneering.npcs.DungeonNPC;
import com.rs.game.content.skills.dungeoneering.skills.DungeoneeringFishing.Fish;
import com.rs.lib.game.Tile;
import com.rs.lib.util.Utils;

public class DungeonFishSpot extends DungeonNPC {

	private Fish fish;
	private int fishes;

	public DungeonFishSpot(int id, Tile tile, DungeonManager manager, Fish fish) {
		super(id, tile, manager);
		this.fish = fish;
		setName(Utils.formatPlayerNameForDisplay(fish.toString()));
		fishes = 14;
	}

	@Override
	public void processNPC() {

	}

	public Fish getFish() {
		return fish;
	}

	public int desecreaseFishes() {
		return fishes--;
	}

	public void addFishes() {
		fishes += Utils.random(5, 10);
	}
}
