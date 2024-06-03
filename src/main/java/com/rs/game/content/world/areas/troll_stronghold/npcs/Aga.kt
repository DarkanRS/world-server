package com.rs.game.content.world.areas.troll_stronghold.npcs

import com.rs.engine.dialogue.HeadE.*
import com.rs.engine.dialogue.startConversation
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.player.Player
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.kts.onNpcClick

class Aga(player: Player, npc: NPC) {
    init {
        player.startConversation {
            npc(npc, T_CONFUSED, "What man-thing want?")
            player(SCARED, "Errr... I've got to go.")
        }
    }
}

@ServerStartupEvent
fun mapAga() {
    onNpcClick(1554, options = arrayOf("Talk-to")) { (player, npc) -> Aga(player, npc) }
}
