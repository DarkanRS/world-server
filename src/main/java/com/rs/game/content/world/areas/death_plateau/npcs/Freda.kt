package com.rs.game.content.world.areas.death_plateau.npcs

import com.rs.game.content.quests.death_plateau.dialogue.npcs.burthorpe.FredaD
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.kts.onNpcClick

@ServerStartupEvent
fun mapFreda() {
    onNpcClick(15099) { (player, npc) -> FredaD(player, npc) }
}
