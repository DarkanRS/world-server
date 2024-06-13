package com.rs.game.content.quests.plague_city.dialogues.objects

import com.rs.engine.dialogue.HeadE.*
import com.rs.engine.dialogue.startConversation
import com.rs.engine.quest.Quest
import com.rs.game.content.quests.plague_city.utils.*
import com.rs.game.content.world.doors.Doors
import com.rs.game.model.entity.player.Player
import com.rs.game.model.`object`.GameObject

class BravekDoorD (player: Player, obj: GameObject) {
    init {
        when (player.questManager.getStage(Quest.PLAGUE_CITY)) {

            in STAGE_UNSTARTED..STAGE_SPOKEN_TO_MILLI -> {
                player.startConversation {
                    npc(BRAVEK, FRUSTRATED, "Go away, I'm busy! I'm... Umm... In a meeting!")
                    player.sendMessage("The door won't open.")
                }
            }
            else -> { Doors.handleDoor(player, obj) }
        }

    }
}
