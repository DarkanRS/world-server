package com.rs.game.content.quests.biohazard.dialogue.objects.east_varrock

import com.rs.engine.dialogue.HeadE.*
import com.rs.engine.dialogue.startConversation
import com.rs.engine.quest.Quest
import com.rs.game.content.quests.biohazard.utils.*
import com.rs.game.content.world.doors.Doors.handleDoor
import com.rs.game.model.entity.player.Equipment
import com.rs.game.model.entity.player.Player
import com.rs.game.model.`object`.GameObject

class GuidorDoorD(player: Player, obj: GameObject) {
    init {

        val wearingPriestTop = player.equipment.getId(Equipment.CHEST) == PRIEST_GOWN_TOP
        val wearingPriestBottom = player.equipment.getId(Equipment.LEGS) == PRIEST_GOWN_BOTTOM

        player.startConversation {
            when (player.questManager.getStage(Quest.BIOHAZARD)) {

                in STAGE_RECEIVED_VIALS..STAGE_COMPLETE -> {
                    if (!wearingPriestTop || !wearingPriestBottom) {
                        npc(GUIDOR_WIFE, SAD, "Please leave my husband alone. He's very sick, and I don't want anyone bothering him.") { player.sendMessage("Guidor's wife refuses to let you enter.") }
                        player(CALM_TALK, "I'm sorry to hear that. Is there anything I can do?")
                        npc(GUIDOR_WIFE, SAD, "Thank you, but I just want him to see a priest.")
                        player(CALM_TALK, "A priest? Hmmm...")
                    } else {
                        player.walkToAndExecute(obj.tile) {
                            player.sendMessage("Guidor's wife allows you to go in.")
                            handleDoor(player, obj)
                        }
                    }
                }

                else -> {
                    player(CALM_TALK, "That's someone's bedroom.<br>I'm not going in there without a reason.")
                }

            }

        }
    }
}
