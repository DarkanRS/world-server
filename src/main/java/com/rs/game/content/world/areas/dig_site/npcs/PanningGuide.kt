package com.rs.game.content.world.areas.dig_site.npcs

import com.rs.game.content.quests.dig_site.dialogue.npcs.PanningGuideD
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.player.Player
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.kts.onNpcClick

class PanningGuide(player: Player, npc: NPC) {
    init {
        PanningGuideD(player, npc)
    }
}

@ServerStartupEvent
fun mapPanningGuide() {
    onNpcClick(620) { (player, npc) -> PanningGuide(player, npc) }
}
