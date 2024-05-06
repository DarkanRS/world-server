package com.rs.game.content.world.areas.varrock.npcs.east_varrock

import com.rs.engine.quest.Quest
import com.rs.game.content.quests.biohazard.dialogue.npcs.east_varrock.ChancyD
import com.rs.game.content.quests.biohazard.utils.*
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.player.Player
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.kts.onNpcClick

class Chancy(player: Player, npc: NPC) {
    init {
        val hasItem = player.questManager.getAttribs(Quest.BIOHAZARD).getI(GAVE_CHANCY_VIAL_OF) > 2
        if (player.questManager.getStage(Quest.BIOHAZARD) == STAGE_RECEIVED_TOUCH_PAPER && hasItem) {
            ChancyD(player, npc)
        } else {
            player.sendMessage("Chancy doesn't feel like talking.")
        }
    }
}

@ServerStartupEvent
fun mapChancyEastVarrock() {
    onNpcClick(339, options = arrayOf("Talk-to")) { (player, npc) -> Chancy(player, npc) }
}
