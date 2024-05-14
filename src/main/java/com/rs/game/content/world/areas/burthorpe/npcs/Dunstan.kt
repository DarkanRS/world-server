package com.rs.game.content.world.areas.burthorpe.npcs

import com.rs.engine.pathfinder.Direction
import com.rs.game.content.quests.death_plateau.dialogue.npcs.burthorpe.DunstanD
import com.rs.game.model.entity.npc.NPC
import com.rs.lib.game.Tile
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.kts.instantiateNpc
import com.rs.plugin.kts.onNpcClick

class Dunstan(id: Int, tile: Tile) : NPC(id, tile) {
    override fun processNPC() {
        faceDir(Direction.SOUTH)
    }
}

@ServerStartupEvent
fun mapDunstan() {
    onNpcClick(1082) { (player, npc) -> DunstanD(player, npc) }
    instantiateNpc(1082) { id, tile -> Dunstan(id, tile) }
}
