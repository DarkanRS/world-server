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

import com.rs.game.World;
import com.rs.game.content.skills.dungeoneering.DamonheimController;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.game.Tile;
import com.rs.lib.util.Utils;

import java.util.ArrayList;
import java.util.List;

public class RandomEvents {

	private static final List<Tile> RANDOM_TILES = new ArrayList<>();
	static {
		RANDOM_TILES.add(Tile.of(2966, 3393, 0)); // North entrance of Falador
		RANDOM_TILES.add(Tile.of(3007, 3322, 0)); // South entrance of Falador
		RANDOM_TILES.add(Tile.of(3011, 3465, 0)); // Ice Mountain
		RANDOM_TILES.add(Tile.of(3253, 3421, 1)); // Varrock East Bank
		RANDOM_TILES.add(Tile.of(3295, 3279, 0)); // Near the Al Kharid Mine
		RANDOM_TILES.add(Tile.of(3214, 3246, 0)); // Near the Lumbridge general store
		RANDOM_TILES.add(Tile.of(3211, 9622, 0)); // Lumbridge castle basement
		RANDOM_TILES.add(Tile.of(2636, 3373, 0)); // North of Ardougne
		RANDOM_TILES.add(Tile.of(2995, 3341, 3)); // White Knights' Castle
		RANDOM_TILES.add(Tile.of(2845, 3164, 0)); // Karamja Volcano
		RANDOM_TILES.add(Tile.of(3110, 3161, 2)); // Wizard's Tower
		RANDOM_TILES.add(Tile.of(3199, 3217, 2)); // Behind Lumbridge Castle
		RANDOM_TILES.add(Tile.of(3256, 3485, 1)); // 1st floor of Varrock Church
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
