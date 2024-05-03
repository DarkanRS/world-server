package com.rs.game.content.world.areas.rimmington.npcs

import com.rs.engine.quest.Quest
import com.rs.game.content.quests.biohazard.dialogue.npcs.rimmington.HopsD
import com.rs.game.content.quests.biohazard.utils.*
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.player.Player
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.kts.onNpcClick

class Hops(player: Player, npc: NPC) {
    init {
        val collectedItems = player.questManager.getAttribs(Quest.BIOHAZARD).getI(GAVE_HOPS_VIAL_OF) == 2
        if (player.questManager.getStage(Quest.BIOHAZARD) == STAGE_RECEIVED_TOUCH_PAPER && !collectedItems) {
            HopsD(player, npc)
        } else {
            player.sendMessage("He isn't in a fit state to talk.")
        }
    }
}

@ServerStartupEvent
fun mapHopsRimmington() {
    onNpcClick(340, options = arrayOf("Talk-to")) { (player, npc) -> Hops(player, npc) }
}
