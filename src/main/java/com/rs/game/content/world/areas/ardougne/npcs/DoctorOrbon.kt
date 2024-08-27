package com.rs.game.content.world.areas.ardougne.npcs

import com.rs.engine.dialogue.startConversation
import com.rs.game.content.achievements.AchievementSystemD
import com.rs.game.content.achievements.SetReward
import com.rs.game.content.quests.sheep_herder.dialogues.DoctorOrbonD
import com.rs.game.content.quests.sheep_herder.utils.*
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.player.Player
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.kts.onNpcClick

class DoctorOrbon (player: Player, npc: NPC) {
    init {
        player.startConversation {
            options {
                op("About the Task System...") {
                    exec { AchievementSystemD(player, npc.id, SetReward.ARDOUGNE_CLOAK) }
                }
                op("Talk about something else.") {
                    exec { DoctorOrbonD(player, npc) }
                }
            }
        }
    }
}

@ServerStartupEvent
fun mapDoctorOrbon() {
    onNpcClick(DOCTOR_ORBON) { (player, npc) ->
        DoctorOrbon(player, npc)
    }
}
