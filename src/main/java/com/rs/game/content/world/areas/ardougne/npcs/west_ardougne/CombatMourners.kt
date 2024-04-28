package com.rs.game.content.world.areas.ardougne.npcs.west_ardougne

import com.rs.engine.dialogue.HeadE.*
import com.rs.engine.dialogue.startConversation
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.player.Player
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.kts.onNpcClick

class CombatMourners(player: Player, npc: NPC) {
    init {
        player.startConversation {
            npc(npc, FRUSTRATED, "Stand back citizen, do not approach me.")
        }
    }
}

@ServerStartupEvent
fun mapCombatMourners() {
    onNpcClick(347, 348, 357, 369, 370, 371, options = arrayOf("Talk-to")) { (player, npc) ->
        CombatMourners(player, npc)
    }
}
