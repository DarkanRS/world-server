package com.rs.game.content.world.areas.troll_stronghold.npcs

import com.rs.engine.dialogue.HeadE.*
import com.rs.engine.dialogue.startConversation
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.player.Player
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.kts.onNpcClick

class Arrg(player: Player, npc: NPC) {
    init {
        player.startConversation {
            npc(npc, T_ANGRY, "Aaaarrrrrghhh!")
            player(SCARED, "O-oh, he seems cheesed off...")
        }
    }
}

@ServerStartupEvent
fun mapArrg() {
    onNpcClick(1555, options = arrayOf("Talk-to")) { (player, npc) -> Arrg(player, npc) }
}
