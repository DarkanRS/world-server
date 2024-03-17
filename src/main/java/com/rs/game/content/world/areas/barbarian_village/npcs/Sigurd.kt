package com.rs.game.content.world.areas.barbarian_village.npcs

import com.rs.game.content.transportation.CanoeD
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.kts.onNpcClick

@ServerStartupEvent
fun handleSigurd() {
    onNpcClick(3329) { e ->
        e.player.startConversation(CanoeD(e.player, e.npc))
    }
}