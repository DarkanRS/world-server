package com.rs.game.content.world.areas.troll_stronghold.npcs

import com.rs.engine.dialogue.HeadE.*
import com.rs.engine.dialogue.startConversation
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.player.Player
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.kts.onNpcClick

class Ug(player: Player, npc: NPC) {
    init {
        player.startConversation {
            npc(npc, T_VERY_SAD, "Arrrghhh, die man-thing! Ahhh, it no use, I too sad!")
        }
    }
}

@ServerStartupEvent
fun mapUg() {
    onNpcClick(1553, options = arrayOf("Talk-to")) { (player, npc) -> Ug(player, npc) }
}
