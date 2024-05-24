package com.rs.game.content.world.areas.edgeville.npcs

import com.rs.game.content.transportation.canoes.CanoeD
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.kts.onNpcClick

@ServerStartupEvent
fun handleHari() {
    onNpcClick(3330) { e ->
        e.player.startConversation(CanoeD(e.player, e.npc))
    }
}