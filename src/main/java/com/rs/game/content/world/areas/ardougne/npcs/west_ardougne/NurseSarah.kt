package com.rs.game.content.world.areas.ardougne.npcs.west_ardougne

import com.rs.engine.dialogue.HeadE.*
import com.rs.engine.dialogue.startConversation
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.player.Player
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.kts.onNpcClick

class NurseSarah(player: Player, npc: NPC) {
    init {
        player.startConversation {
            player(CALM_TALK, "Hello there.")
            npc(npc, CALM_TALK, "Hello my dear, how are you feeling?")
            player(CALM_TALK, "I'm ok thanks.")
            npc(npc, CALM_TALK, "Well in that case I'd better get back to work. Take care.")
            player(CALM_TALK, "You too.")
        }
    }
}

@ServerStartupEvent
fun mapNurseSarah() {
    onNpcClick(373) { (player, npc) ->
        NurseSarah(player, npc)
    }
}
