package com.rs.game.content.world.areas.varrock.npcs.east_varrock

import com.rs.engine.quest.Quest
import com.rs.game.content.quests.biohazard.dialogue.npcs.east_varrock.HopsD
import com.rs.game.content.quests.biohazard.utils.*
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.player.Player
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.kts.onNpcClick

class Hops(player: Player, npc: NPC) {
    init {
        val hasItem = player.questManager.getAttribs(Quest.BIOHAZARD).getI(GAVE_HOPS_VIAL_OF) > 2
        if (player.questManager.getStage(Quest.BIOHAZARD) == STAGE_RECEIVED_TOUCH_PAPER && hasItem) {
            HopsD(player, npc)
        } else {
            player.sendMessage("Hops doesn't feel like talking.")
        }
    }
}

@ServerStartupEvent
fun mapHopsEastVarrock() {
    onNpcClick(341, options = arrayOf("Talk-to")) { (player, npc) -> Hops(player, npc) }
}
