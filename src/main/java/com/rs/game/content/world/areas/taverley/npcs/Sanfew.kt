package com.rs.game.content.world.areas.taverley.npcs

import com.rs.engine.dialogue.HeadE.*
import com.rs.engine.dialogue.startConversation
import com.rs.engine.quest.Quest
import com.rs.game.model.entity.player.Player
import com.rs.game.content.quests.druidic_ritual.dialogues.npcs.SanfewD
import com.rs.game.model.entity.npc.NPC
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.kts.onNpcClick

class Sanfew(player: Player, npc: NPC) {
    init {
        if (player.isQuestStarted(Quest.DRUIDIC_RITUAL)) {
            SanfewD(player, npc)
        } else {
            player.startConversation {
                npc(npc, CALM_TALK, "What can I do for you young 'un?") { player.voiceEffect(77263, false) }
                player(CALM_TALK, "Nothing at the moment.")
            }
        }
    }
}

@ServerStartupEvent
fun mapSanfew() {
    onNpcClick(454, options = arrayOf("Talk-to")) { (player, npc) -> Sanfew(player, npc) }
}
