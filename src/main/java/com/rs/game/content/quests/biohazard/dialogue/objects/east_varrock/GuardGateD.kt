package com.rs.game.content.quests.biohazard.dialogue.objects.east_varrock

import com.rs.engine.cutscenekt.cutscene
import com.rs.engine.dialogue.HeadE.*
import com.rs.engine.dialogue.startConversation
import com.rs.engine.quest.Quest
import com.rs.game.World
import com.rs.game.content.quests.biohazard.utils.*
import com.rs.game.content.world.doors.Doors.handleGate
import com.rs.game.model.entity.player.Player
import com.rs.game.model.`object`.GameObject

class GuardGateD(player: Player, obj: GameObject) {
    init {
        player.startConversation {
            when (player.questManager.getStage(Quest.BIOHAZARD)) {

                in STAGE_RECEIVED_VIALS..STAGE_SPEAK_TO_KING -> {
                    val guard = World.getNPCsInChunkRange(player.chunkId, 1).firstOrNull { it.id == VARROCK_GUARD }
                    if (guard != null) {
                        player.faceEntityTile(guard)
                        guard.faceEntityTile(player)
                    }
                    npc(VARROCK_GUARD, CALM_TALK, "Halt. I need to conduct a search on you. There have been reports of someone bringing a virus into Varrock.")
                    exec {
                        player.cutscene {
                            player.sendMessage("The guard searches you.")
                            wait(2)
                            BiohazardUtils(player).handleGuardRemovingItems()
                            wait(2)
                            dialogue {
                                npc(VARROCK_GUARD, CALM_TALK, "You may now pass.")
                            }
                            waitForDialogue()
                            handleGate(player, obj)
                        }
                    }
                }

                else -> handleGate(player, obj)

            }

        }
    }
}
