package com.rs.game.content.world.areas.ardougne.npcs.west_ardougne

import com.rs.engine.dialogue.startConversation
import com.rs.engine.quest.Quest
import com.rs.game.content.quests.plaguecity.dialogues.npcs.HeadMournerD
import com.rs.game.content.quests.plaguecity.utils.STAGE_UNSTARTED
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.player.Player
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.kts.onNpcClick

class HeadMourner(player: Player, npc: NPC) {
    init {
        player.startConversation {
            options {
                if (player.questManager.getStage(Quest.PLAGUE_CITY) > STAGE_UNSTARTED /*&& !player.isQuestComplete(Quest.PLAGUE_CITY)*/)
                    op("Talk about Plague City.") { exec { HeadMournerD(player, npc) } }
            }
        }
    }
}

@ServerStartupEvent
fun mapHeadMourner() {
    onNpcClick(2372) { (player, npc) ->
        HeadMourner(player, npc)
    }
}
