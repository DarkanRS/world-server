package com.rs.game.content.quests.biohazard.dialogue.objects.west_ardougne

import com.rs.engine.dialogue.HeadE.*
import com.rs.engine.dialogue.startConversation
import com.rs.engine.quest.Quest
import com.rs.game.content.quests.biohazard.utils.*
import com.rs.game.content.world.doors.Doors.handleDoor
import com.rs.game.model.entity.player.Equipment
import com.rs.game.model.entity.player.Player
import com.rs.game.model.`object`.GameObject
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.kts.onObjectClick

class MournerHQDoorsD(player: Player, obj: GameObject) {
    init {
        val stage = player.questManager.getStage(Quest.BIOHAZARD)
        val wearingDoctorsGown = player.equipment.getId(Equipment.CHEST) == DOCTORS_GOWN

        player.startConversation {
            when (stage) {

                STAGE_COMPLETED_WALL_CROSSING -> {
                    if (BiohazardUtils(player).isInMournerHQ(player.tile)) {
                        handleDoor(player, obj)
                    } else {
                        player.sendMessage("The door is locked. You can hear the mourners eating... You need to distract them from their stew.")
                    }
                }

                in STAGE_APPLE_IN_CAULDRON..STAGE_FOUND_DISTILLATOR -> {
                    if (BiohazardUtils(player).isInMournerHQ(player.tile)) {
                        handleDoor(player, obj)
                    } else {
                        if (!wearingDoctorsGown) {
                            npc(MOURNER_WITH_KEY, CALM_TALK, "Stay away from there.")
                            player(CONFUSED, "Why?")
                            npc(MOURNER_WITH_KEY, CALM_TALK, "Several mourners are ill with food poisoning, we're waiting for a doctor.")
                        } else {
                            npc(MOURNER_WITH_KEY, CALM_TALK, "In you go doc.")
                            exec { handleDoor(player, obj) }
                        }
                    }
                }

                STAGE_COMPLETE -> {
                    handleDoor(player, obj)
                }

                else -> {
                    player.sendMessage("The door is locked.")
                }

            }
        }
    }
}

@ServerStartupEvent
fun mapMournerHQDoorsD() {
    onObjectClick(2036) { (player, obj) -> MournerHQDoorsD(player, obj) }
}
