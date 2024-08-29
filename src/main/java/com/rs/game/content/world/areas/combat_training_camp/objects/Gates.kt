package com.rs.game.content.world.areas.combat_training_camp.objects

import com.rs.engine.dialogue.HeadE.*
import com.rs.engine.dialogue.startConversation
import com.rs.engine.quest.Quest
import com.rs.game.content.world.doors.Doors.handleDoubleDoor
import com.rs.game.model.entity.player.Player
import com.rs.game.model.`object`.GameObject
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.kts.onObjectClick

class Gates(player: Player, obj: GameObject) {
    init {
        if (player.y == 3357) {
            handleDoubleDoor(player, obj)
        } else {
            if (player.isQuestComplete(Quest.BIOHAZARD)) {
                val enteredBefore = player.getBool("enteredCombatTrainingCamp")
                if (!enteredBefore)
                    player.startConversation {
                        npc(344, CALM_TALK, "The king has granted you access to this training area. Make good use of it, soon all your strength will be needed.")
                        exec {
                            handleDoubleDoor(player, obj)
                            player.set("enteredCombatTrainingCamp", true)
                        }
                    }
                else handleDoubleDoor(player, obj)
            } else {
                player.npcDialogue(344, CALM_TALK, "This is a restricted area, you can only enter under the authority of King Lathas.")
                player.sendMessage("The gates are locked.")
            }
        }
    }
}


@ServerStartupEvent
fun mapCombatTrainingCampGates() {
    onObjectClick(2039, 2041) { (player, obj) -> Gates(player, obj) }
}
