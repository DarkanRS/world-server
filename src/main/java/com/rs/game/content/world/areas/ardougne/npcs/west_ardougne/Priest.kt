package com.rs.game.content.world.areas.ardougne.npcs.west_ardougne

import com.rs.engine.dialogue.HeadE.*
import com.rs.engine.dialogue.startConversation
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.player.Player
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.kts.onNpcClick

class Priest(player: Player, npc: NPC) {
    init {
        player.startConversation {
            player(CALM_TALK, "Hello there.")
            npc(npc, SAD, "I wish there was more I could do for these people.")
        }
    }
}

@ServerStartupEvent
fun mapWestArdougnePriest() {
    onNpcClick(358, options = arrayOf("Talk-to")) { (player, npc) ->
        Priest(player, npc)
    }
}
