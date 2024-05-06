package com.rs.game.content.world.areas.varrock.npcs.east_varrock

import com.rs.game.content.quests.biohazard.dialogue.npcs.east_varrock.GuidorWifeD
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.kts.onNpcClick

@ServerStartupEvent
fun mapGuidorWifeEastVarrock() {
    onNpcClick(342, options = arrayOf("Talk-to")) { (player, npc) -> GuidorWifeD(player, npc) }
}
