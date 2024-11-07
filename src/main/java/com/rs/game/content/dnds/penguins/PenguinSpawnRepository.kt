package com.rs.game.content.dnds.penguins

import com.rs.db.WorldDB
import com.rs.lib.game.Tile
import com.rs.lib.util.Logger
import com.rs.utils.spawns.NPCSpawn

class PenguinSpawnRepository() {
    private val allSpawns = mutableMapOf<NPCSpawn, Int>()

    fun loadSpawns() {
        allSpawns.clear()
        val penguins = WorldDB.getPenguinHAS().getAllPenguins()
        penguins.forEach { penguin ->
            val spawn = NPCSpawn(penguin.npcId, penguin.location, penguin.wikiLocation)
            allSpawns[spawn] = penguin.week
            PenguinServices.penguinSpawnService.regionIds.add(penguin.location.regionId)
        }
        Logger.debug(PenguinSpawnRepository::class.java, "loadSpawns", "Loaded ${allSpawns.size} spawns from database.")
    }

    fun addSpawn(id: Int, tile: Tile, comment: String, week: Int): NPCSpawn {
        val spawn = NPCSpawn(id, tile, comment)
        allSpawns[spawn] = week
        PenguinServices.penguinSpawnService.regionIds.add(tile.regionId)
        return spawn
    }

    fun getAllSpawns(): Map<NPCSpawn, Int> = allSpawns

}
