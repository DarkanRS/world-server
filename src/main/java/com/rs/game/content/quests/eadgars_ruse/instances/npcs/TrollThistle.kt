package com.rs.game.content.quests.eadgars_ruse.instances.npcs

import com.rs.engine.pathfinder.Direction
import com.rs.game.content.quests.eadgars_ruse.utils.THISTLE
import com.rs.game.model.entity.Entity
import com.rs.game.model.entity.npc.NPC
import com.rs.lib.game.Tile

class TrollThistle(id: Int, tile: Tile) : NPC(id, tile) {

    private val possibleTiles = listOf(
        Tile(2887, 3660, 0),
        Tile(2880, 3668, 0),
        Tile(2898, 3682, 0),
        Tile(2906, 3687, 0),
        Tile(2904, 3667, 0)
    )

    private var lastRespawnTile: Tile? = null

    init {
        faceDir(Direction.random())
        initializePosition()
        super.initEntity()
    }

    private fun initializePosition() {
        val randomTile = possibleTiles.filter { it != tile }.random()
        tele(randomTile)
        lastRespawnTile = randomTile
        respawnTile = randomTile
    }

    override fun faceEntityTile(target: Entity?) {}

    override fun sendDeath(source: Entity?) {
        lastRespawnTile = respawnTile
        super.sendDeath(source)
    }

    override fun spawn() {
        val randomTile = possibleTiles.filter { it != lastRespawnTile }.random()
        respawnTile = randomTile
        lastRespawnTile = randomTile
        transformIntoNPC(THISTLE)
        super.spawn()
    }
}
