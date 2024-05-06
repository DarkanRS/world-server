package com.rs.game.content.world.areas.varrock.objects.east_varrock

import com.rs.game.content.quests.biohazard.dialogue.objects.east_varrock.GuidorDoorD
import com.rs.game.content.world.doors.Doors.handleDoor
import com.rs.game.model.entity.player.Player
import com.rs.game.model.`object`.GameObject
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.kts.onObjectClick

class GuidorDoor(player: Player, obj: GameObject) {
    init {
        if (player.x == 3283) {
            handleDoor(player, obj)
        } else {
            GuidorDoorD(player, obj)
        }
    }
}

@ServerStartupEvent
fun mapGuidorDoorEastVarrock() {
    onObjectClick(2032) { (player, obj) -> GuidorDoor(player, obj) }
}
