package com.rs.game.content.world.areas.dig_site.npcs

import com.rs.engine.dialogue.HeadE
import com.rs.engine.dialogue.startConversation
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.player.Player
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.kts.onNpcClick

class MuseumGuard(player: Player, npc: NPC) {
    init {
        player.startConversation {
            npc(npc, HeadE.CALM_TALK, "Hello there! Sorry, I can't stop to talk. I'm guarding this workman's gate. I'm afraid you can't come through here - you'll need to find another way around.")
        }
    }
}

@ServerStartupEvent
fun mapMuseumGuard() {
    onNpcClick(5942) { (player, npc) -> MuseumGuard(player, npc) }
}
