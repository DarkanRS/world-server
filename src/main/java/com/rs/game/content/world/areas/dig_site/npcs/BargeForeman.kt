package com.rs.game.content.world.areas.dig_site.npcs

import com.rs.engine.dialogue.HeadE.*
import com.rs.engine.dialogue.startConversation
import com.rs.game.model.entity.Entity
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.player.Player
import com.rs.lib.game.Tile
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.kts.instantiateNpc
import com.rs.plugin.kts.onNpcClick

class BargeForeman(player: Player) {
    init {
        player.startConversation {
            player(CALM_TALK, "They look a bit busy... I wonder what's going on here, though.")
        }
    }
}

class BargeForemanInstance(id: Int, tile: Tile) : NPC(id, tile) {
    override fun faceEntityTile(target: Entity?) { }
}

@ServerStartupEvent
fun mapBargeForeman() {
    onNpcClick(5963) { (player) -> BargeForeman(player) }
    instantiateNpc(5963) { id, tile -> BargeForemanInstance(id, tile) }
}
