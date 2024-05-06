package com.rs.game.content.world.areas.varrock.objects.east_varrock

import com.rs.game.content.quests.biohazard.dialogue.objects.east_varrock.GuardGateD
import com.rs.game.content.world.doors.Doors.handleGate
import com.rs.game.model.entity.player.Player
import com.rs.game.model.`object`.GameObject
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.kts.onObjectClick

class GuardGate(player: Player, obj: GameObject) {
    init {
        if (player.x == 3264) {
            handleGate(player, obj)
        } else {
            GuardGateD(player, obj)
        }
    }
}

@ServerStartupEvent
fun mapGuardGateEastVarrock() {
    onObjectClick(2050, 2051) { (player, obj) -> GuardGate(player, obj) }
}
