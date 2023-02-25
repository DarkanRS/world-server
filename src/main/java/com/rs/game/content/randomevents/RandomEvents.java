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
package com.rs.game.content.randomevents;

import java.util.ArrayList;
import java.util.List;

import com.rs.game.World;
import com.rs.game.content.skills.dungeoneering.DamonheimController;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.game.Tile;
import com.rs.lib.util.Utils;

public class RandomEvents {

	private static List<Tile> RANDOM_TILES = new ArrayList<>();
	static {
		RANDOM_TILES.add(Tile.of(3208, 3219, 3));
		RANDOM_TILES.add(Tile.of(2707, 3472, 1));
		RANDOM_TILES.add(Tile.of(2995, 3341, 3));
		RANDOM_TILES.add(Tile.of(3217, 3475, 1));
		RANDOM_TILES.add(Tile.of(3083, 3415, 0));
	}

	public static Tile getRandomTile() {
		return RANDOM_TILES.get(Utils.random(RANDOM_TILES.size()));
	}

	public static void attemptSpawnRandom(Player player) {
		attemptSpawnRandom(player, false);
	}

	public static void attemptSpawnRandom(Player player, boolean force) {
		if ((!force && (World.getServerTicks() - player.getNSV().getL("lastRandom") < 3000)) || (player.getControllerManager().getController() != null && !(player.getControllerManager().getController() instanceof DamonheimController)))
			return;
		Tile spawnTile = player.getNearestTeleTile(1);
		if (spawnTile == null)
			return;
		player.getNSV().setL("lastRandom", World.getServerTicks());
		int random = Utils.random(0, 100);
		if(random < 90)//90% chance
			new SandwichLady(player, spawnTile);
		else
			new Genie(player, spawnTile);
		//TODO add more than this rofl.
	}

}
