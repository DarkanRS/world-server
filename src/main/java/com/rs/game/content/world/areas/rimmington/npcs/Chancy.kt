package com.rs.game.content.world.areas.rimmington.npcs

import com.rs.engine.quest.Quest
import com.rs.game.content.quests.biohazard.dialogue.npcs.rimmington.ChancyD
import com.rs.game.content.quests.biohazard.utils.*
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.player.Player
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.kts.onNpcClick

class Chancy(player: Player, npc: NPC) {
    init {
        val collectedItems = player.questManager.getAttribs(Quest.BIOHAZARD).getI(GAVE_CHANCY_VIAL_OF) == 2
        if (player.questManager.getStage(Quest.BIOHAZARD) == STAGE_RECEIVED_TOUCH_PAPER && !collectedItems) {
            ChancyD(player, npc)
        } else {
            player.sendMessage("Chancy doesn't feel like talking.")
        }
    }
}

@ServerStartupEvent
fun mapChancyRimmington() {
    onNpcClick(338, options = arrayOf("Talk-to")) { (player, npc) -> Chancy(player, npc) }
}
