package com.rs.game.content.world.areas.varrock.npcs.east_varrock

import com.rs.game.content.quests.biohazard.dialogue.npcs.east_varrock.GuardD
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.kts.onNpcClick

@ServerStartupEvent
fun mapGuardEastVarrock() {
    onNpcClick(368, options = arrayOf("Talk-to")) { (player, npc) -> GuardD(player, npc) }
}
