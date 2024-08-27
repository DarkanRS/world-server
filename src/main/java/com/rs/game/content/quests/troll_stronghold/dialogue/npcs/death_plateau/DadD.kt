package com.rs.game.content.quests.troll_stronghold.dialogue.npcs.death_plateau

import com.rs.engine.dialogue.HeadE.*
import com.rs.engine.dialogue.startConversation
import com.rs.engine.quest.Quest
import com.rs.game.content.quests.troll_stronghold.utils.*
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.player.Player

class DadD(player: Player, npc: NPC) {
    init {
        player.startConversation {
            when (player.getQuestStage(Quest.TROLL_STRONGHOLD)) {

                in STAGE_ACCEPTED_QUEST..STAGE_ENTERED_ARENA -> {
                    npc(npc, T_CALM_TALK, "What tiny human do in troll arena? Dad challenge human to fight!")
                    label("initialOps")
                    options {
                        op("Why are you called Dad?") {
                            player(CONFUSED, "Why are you called Dad?")
                            npc(npc, T_HAPPY_TALK, "Troll named after first thing try to eat!")
                            goto("initialOps")
                        }
                        op("I accept your challenge!") {
                            player(CALM_TALK, "I accept your challenge!")
                            npc(npc, T_SURPRISED, "Tiny human brave. Dad squish!")
                            exec {
                                npc.hitpoints = npc.maxHitpoints
                                npc.combatTarget = player
                                player.hintIconsManager.addHintIcon(npc, 0, -1, false)
                                player.setQuestStage(Quest.TROLL_STRONGHOLD, STAGE_ENGAGED_DAD)
                            }
                        }
                        op("Eek! No thanks.") {
                            player(SCARED, "Eek! No thanks.")
                            npc(npc, T_LAUGH, "Coward. Dad wait for braver fighter.")
                        }
                    }
                }

                in STAGE_ENGAGED_DAD..STAGE_COMPLETE -> player.sendMessage("He doesn't seem interested in talking right now.")

                else -> player.sendMessage("He doesn't seem interested in talking right now.")

            }
        }
    }
}
