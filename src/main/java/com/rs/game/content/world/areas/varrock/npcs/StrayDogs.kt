package com.rs.game.content.world.areas.varrock.npcs

import com.rs.game.model.entity.async.schedule
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.player.Player
import com.rs.lib.game.Tile
import com.rs.lib.util.Utils
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.kts.instantiateNpc
import com.rs.plugin.kts.onNpcClick
import kotlin.math.pow
import kotlin.math.sqrt

class StrayDogs(id: Int, tile: Tile) : NPC(id, tile) {

    fun shoo(player: Player, npc: NPC) {
        player.schedule {
            if (!checkDistanceFromSpawn()) {
                player.lock(1)
                player.faceEntityTile(npc)
                player.anim(2110)
                player.forceTalk("Thbbbbt!")
                wait(2)
                faceEntity(player)
                forceTalk("Whine!")
                soundEffect(2067, true)
                val diffX = tile.x - player.tile.x
                val diffY = tile.y - player.tile.y
                val randomSteps = Utils.random(2, 3)
                val destination = tile.transform(diffX * randomSteps, diffY * randomSteps)
                setForceWalk(destination)
                walkToAndExecute(destination) { stopFaceEntity() }
            } else {
                player.lock(1)
                player.sendMessage("The stray dog feels lost and runs away from you.")
                val randomOffsetX = Utils.random(-2, 2)
                val randomOffsetY = Utils.random(-2, 2)
                val destination = respawnTile.transform(randomOffsetX, randomOffsetY)
                setForceWalk(destination)
            }
        }
    }

    private fun checkDistanceFromSpawn(): Boolean {
        val distance = sqrt((respawnTile.x - tile.x).toDouble().pow(2.0) + (respawnTile.y - tile.y).toDouble().pow(2.0))
        return distance > 20
    }

}

@ServerStartupEvent
fun mapStrayDogsVarrock() {
    onNpcClick(5917, 5918 , options = arrayOf("Shoo-away")) { (player, npc) -> if (npc is StrayDogs) npc.shoo(player, npc) }
    instantiateNpc(5917, 5918) { id, tile -> StrayDogs(id, tile) }
}
