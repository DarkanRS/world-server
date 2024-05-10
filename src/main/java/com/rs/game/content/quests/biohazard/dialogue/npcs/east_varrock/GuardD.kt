package com.rs.game.content.quests.biohazard.dialogue.npcs.east_varrock

import com.rs.engine.dialogue.HeadE.*
import com.rs.engine.dialogue.startConversation
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.player.Player

class GuardD (player: Player, npc: NPC) {
    init {
        player.startConversation {
            npc(npc, CALM_TALK, "Please don't disturb me, I've got to keep an eye out for suspicious individuals.")
        }
    }
}
