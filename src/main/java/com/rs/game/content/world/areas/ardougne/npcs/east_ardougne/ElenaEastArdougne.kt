package com.rs.game.content.world.areas.ardougne.npcs.east_ardougne

import com.rs.engine.dialogue.HeadE.*
import com.rs.engine.dialogue.startConversation
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.player.Player
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.kts.onNpcClick

class ElenaEastArdougne(player: Player, npc: NPC) {
    init {
        player.startConversation {
            player(HAPPY_TALKING, "Good day to you, Elena.")
            npc(npc, HAPPY_TALKING, "You too, thanks for freeing me.")
        }
    }
}

@ServerStartupEvent
fun mapElenaEastArdougne() {
    onNpcClick(335) { (player, npc) ->
        ElenaEastArdougne(player, npc)
    }
}