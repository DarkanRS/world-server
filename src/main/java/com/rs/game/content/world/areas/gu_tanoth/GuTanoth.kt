package com.rs.game.content.world.areas.gu_tanoth

import com.rs.game.content.skills.agility.Agility
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.kts.onObjectClick

@ServerStartupEvent
fun mapGuTanoth() {
    onObjectClick(2832) { e ->
        if (!Agility.hasLevel(e.player, 20)) return@onObjectClick
        Agility.handleObstacle(e.player, 3303, 2, e.player.transform(if (e.player.x < e.getObject().x) 2 else -2, 0, 0), 1.0)
    }
}
