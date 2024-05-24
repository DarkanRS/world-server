package com.rs.game.content.world.areas.ardougne.npcs.east_ardougne

import com.rs.engine.quest.Quest
import com.rs.game.content.quests.biohazard.dialogue.npcs.east_ardougne.KingLathasD
import com.rs.game.content.quests.biohazard.utils.*
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.player.Player
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.kts.onNpcClick

class KingLathas(player: Player, npc: NPC) {
    init {
        val biohazardStage = player.questManager.getStage(Quest.BIOHAZARD)

        if (biohazardStage == STAGE_SPEAK_TO_KING) {
            KingLathasD(player, npc)
        } else {
            player.sendMessage("<col=A31818>The king is too busy to talk.</col>")
        }
    }
}

@ServerStartupEvent
fun mapKingLathas() {
    onNpcClick(364) { (player, npc) -> KingLathas(player, npc) }
}
