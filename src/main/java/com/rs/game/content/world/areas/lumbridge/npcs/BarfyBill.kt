package com.rs.game.content.world.areas.lumbridge.npcs;

import com.rs.game.content.transportation.CanoeD
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.kts.onNpcClick

@ServerStartupEvent
fun handleBarfyBill() {
    onNpcClick(3331) { e ->
        e.player.startConversation(CanoeD(e.player, e.npc))
    }
}