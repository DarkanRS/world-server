package com.rs.game.content.world.areas.ardougne.npcs.west_ardougne

import com.rs.engine.dialogue.HeadE.*
import com.rs.engine.dialogue.startConversation
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.player.Player
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.kts.onNpcClick

class WestArdougneChild (player: Player, npc: NPC) {
    init {
        player.startConversation {
            player(CALM_TALK, "Hello there.")
            npc(npc, CHILD_SAD, "I'm not allowed to speak with strangers.")
        }
    }
}

@ServerStartupEvent
fun mapWestArdougneChild() {
    onNpcClick(355, 356, 6334, 6335, 6336, 6337, 6338, 6339, 6340, 6341, 6342, 6343, 6345) { (player, npc) ->
        WestArdougneChild(player, npc)
    }
}
