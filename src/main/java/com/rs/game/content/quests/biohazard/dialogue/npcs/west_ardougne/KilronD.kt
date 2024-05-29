package com.rs.game.content.quests.biohazard.dialogue.npcs.west_ardougne

import com.rs.engine.dialogue.HeadE.*
import com.rs.engine.dialogue.startConversation
import com.rs.engine.quest.Quest
import com.rs.game.content.quests.biohazard.utils.*
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.player.Player

class KilronD(player: Player, npc: NPC) {
    init {
        player.startConversation {
            when (player.questManager.getStage(Quest.BIOHAZARD)) {

                in STAGE_COMPLETED_WALL_CROSSING..STAGE_RECEIVED_VIALS -> {
                    player(CALM_TALK, "Hello Kilron.")
                    npc(npc, CALM_TALK, "Hello traveller. Do you need to go back over?")
                    options {
                        op("Not yet Kilron.") {
                            player(CALM_TALK, "Not yet Kilron.")
                            npc(npc, CALM_TALK, "Okay, just give me the word.")
                        }
                        op("Yes I do.") {
                            player(CALM_TALK, "Yes I do.")
                            npc(npc, CALM_TALK, "Okay, quickly now!")
                            exec { BiohazardUtils(player).handleWallCrossing() }
                        }
                    }
                }

                else -> {
                    player(CALM_TALK, "Hello there.")
                    npc(npc, CALM_TALK, "Hello.")
                    player(CALM_TALK, "How are you?")
                    npc(npc, CALM_TALK, "Busy.")
                }

            }
        }
    }
}
