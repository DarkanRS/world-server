package com.rs.game.content.world.areas.rimmington.npcs

import com.rs.engine.quest.Quest
import com.rs.game.content.quests.biohazard.dialogue.npcs.rimmington.DaVinciD
import com.rs.game.content.quests.biohazard.utils.*
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.player.Player
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.kts.onNpcClick

class DaVinci(player: Player, npc: NPC) {
    init {
        val collectedItems = player.questManager.getAttribs(Quest.BIOHAZARD).getI(GAVE_DA_VINCI_VIAL_OF) == 2
        if (player.questManager.getStage(Quest.BIOHAZARD) == STAGE_RECEIVED_TOUCH_PAPER && !collectedItems) {
            DaVinciD(player, npc)
        } else {
            player.sendMessage("Da Vinci does not feel sufficiently moved to talk.")
        }
    }
}

@ServerStartupEvent
fun mapDaVinciRimmington() {
    onNpcClick(336, options = arrayOf("Talk-to")) { (player, npc) -> DaVinci(player, npc) }
}
