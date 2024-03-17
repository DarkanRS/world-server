package com.rs.game.content.world.areas.varrock.npcs;

import com.rs.game.content.transportation.canoes.CanoeD
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.kts.onNpcClick

@ServerStartupEvent
fun handleTarquin() {
    onNpcClick(3328) { e ->
            e.player.startConversation(CanoeD(e.player, e.npc))
    }
}