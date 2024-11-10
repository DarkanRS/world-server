package com.rs.game.content.dnds.penguins

import com.rs.engine.pathfinder.DumbRouteFinder
import com.rs.game.World
import com.rs.game.model.entity.Entity
import com.rs.game.model.entity.npc.NPC
import com.rs.lib.game.Tile
import com.rs.lib.util.Utils

class PenguinNPC(id: Int, tile: Tile) : NPC(id, tile) {

    init {
        isIgnoreNPCClipping = true
    }

    override fun resetWalkSteps() {}

    override fun faceEntityTile(target: Entity?) {}

    override fun randomWalk() {
        if (!hasWalkSteps() && shouldRandomWalk()) {
            val can = Math.random() > 0.3
            if (can) {

                val penguin = Penguins.entries.firstOrNull { it.tile == respawnTile }
                val range = penguin?.roamRange ?: 4..8

                var moveX = Utils.randomInclusive(range.first, range.last)
                var moveY = Utils.randomInclusive(range.first, range.last)

                moveX = if (Utils.random(2) == 0) -moveX else moveX
                moveY = if (Utils.random(2) == 0) -moveY else moveY

                this.walkSteps.clear()

                var distance = Utils.randomInclusive(range.first, range.last)
                World.findAdjacentFreeSpace(respawnTile.transform(moveX, moveY, respawnTile.plane), 1)?.let { adjacentTile ->
                    DumbRouteFinder.addDumbPathfinderSteps(this, adjacentTile, distance, getCollisionStrategy())
                }

                if (Utils.getDistance(this.tile, respawnTile) > range.last) {
                    distance = Utils.random(range.first, range.last)
                    DumbRouteFinder.addDumbPathfinderSteps(this, respawnTile, distance, getCollisionStrategy())
                }
            }
        }
    }


}
