package com.rs.game.content.randomevents

import com.rs.game.World
import com.rs.game.content.skills.dungeoneering.DamonheimController
import com.rs.game.model.entity.player.Player
import com.rs.lib.game.Tile
import com.rs.lib.util.Utils
import java.util.ArrayList

object RandomEvents {
	private val RANDOM_TILES = ArrayList<Tile>().apply {
		add(Tile(2966, 3393, 0)) // North entrance of Falador
		add(Tile(3007, 3322, 0)) // South entrance of Falador
		add(Tile(3011, 3465, 0)) // Ice Mountain
		add(Tile(3253, 3421, 1)) // Varrock East Bank
		add(Tile(3295, 3279, 0)) // Near the Al Kharid Mine
		add(Tile(3214, 3246, 0)) // Near the Lumbridge general store
		add(Tile(3211, 9622, 0)) // Lumbridge castle basement
		add(Tile(2636, 3373, 0)) // North of Ardougne
		add(Tile(2995, 3341, 3)) // White Knights' Castle
		add(Tile(2845, 3164, 0)) // Karamja Volcano
		add(Tile(3110, 3161, 2)) // Wizard's Tower
		add(Tile(3199, 3217, 2)) // Behind Lumbridge Castle
		add(Tile(3256, 3485, 1)) // 1st floor of Varrock Church
	}

	@JvmStatic
	fun getRandomTile(): Tile {
		return RANDOM_TILES[Utils.random(RANDOM_TILES.size)]
	}

	@JvmStatic
	fun attemptSpawnRandom(player: Player) {
		attemptSpawnRandom(player, false)
	}

	@JvmStatic
	fun attemptSpawnRandom(player: Player, force: Boolean) {
		if ((!force && (World.getServerTicks() - player.nsv.getL("lastRandom") < 3000)) ||
			(player.controllerManager.controller != null && player.controllerManager.controller !is DamonheimController)) {
			return
		}

		var spawnTile = player.getNearestTeleTile(1)
		if (spawnTile == null)
			spawnTile = Tile.of(player.tile)
		player.nsv.setL("lastRandom", World.getServerTicks())
		val random = Utils.random(0, 100)

		val genieChance = 5
		val drunkenDwarfChance = 45
		val sandwichLadyChance = 50

		when {
			random < genieChance -> Genie(player, spawnTile)
			random < genieChance + drunkenDwarfChance -> DrunkenDwarf(player, spawnTile)
			random < genieChance + drunkenDwarfChance + sandwichLadyChance -> SandwichLady(player, spawnTile)
		}
	}

}
