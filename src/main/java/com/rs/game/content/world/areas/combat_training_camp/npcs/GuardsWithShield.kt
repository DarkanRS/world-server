package com.rs.game.content.world.areas.combat_training_camp.npcs

import com.rs.engine.dialogue.HeadE.*
import com.rs.engine.dialogue.startConversation
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.player.Player
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.kts.onNpcClick

class GuardsWithShield(player: Player, npc: NPC) {
    init {
        player.startConversation {
            player(CALM_TALK, "Hello.")
            npc(npc, FRUSTRATED, "What do you want? Leave us be!")
        }
    }
}


@ServerStartupEvent
fun mapGuardsWithShield() {
    onNpcClick(344, options = arrayOf("Talk-to")) { (player, npc) -> GuardsWithShield(player, npc) }
}
