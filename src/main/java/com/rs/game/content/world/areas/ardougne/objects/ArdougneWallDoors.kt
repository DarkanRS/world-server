package com.rs.game.content.world.areas.ardougne.objects

import com.rs.engine.dialogue.HeadE.*
import com.rs.engine.quest.Quest
import com.rs.game.World.getObjectWithId
import com.rs.game.World.spawnObjectTemporary
import com.rs.game.model.entity.player.Player
import com.rs.game.model.`object`.GameObject
import com.rs.lib.game.Tile
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.kts.onObjectClick

class ArdougneWallDoors(player: Player, obj: GameObject) {
    init {
        if (player.isQuestComplete(Quest.BIOHAZARD)) {
            val northDoor = getObjectWithId(Tile.of(2558, 3300, 0), 9330)
            val southDoor = getObjectWithId(Tile.of(2558, 3299, 0), 9738)
            if (northDoor != null && southDoor != null) {
                spawnObjectTemporary(GameObject(northDoor, 83), 3, true)
                spawnObjectTemporary(GameObject(southDoor, 83), 3, true)
                spawnObjectTemporary(GameObject(northDoor.id, northDoor.type, northDoor.getRotation(1), northDoor.tile.transform(-1, 0, 0)), 3, true)
                spawnObjectTemporary(GameObject(southDoor.id, southDoor.type, southDoor.getRotation(3), southDoor.tile.transform(-1, 0, 0)), 3, true)
                player.addWalkSteps(player.x + (if (player.x >= 2559) -3 else 2), obj.y, -1, false)
            } else {
                player.sendMessage("Someone else is using the doors at the moment.")
            }
        } else {
            player.npcDialogue(372, FRUSTRATED, "Oi! What are you doing? Get away from there!")
        }
    }
}


@ServerStartupEvent
fun mapArdougneWallDoors() {
    onObjectClick(9330, 9738) { (player, obj) -> ArdougneWallDoors(player, obj) }
}
