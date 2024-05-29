package com.rs.game.content.quests.biohazard.dialogue.npcs.east_ardougne

import com.rs.engine.dialogue.HeadE.*
import com.rs.engine.dialogue.startConversation
import com.rs.engine.quest.Quest
import com.rs.game.content.quests.biohazard.utils.*
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.player.Player

class OmartD(player: Player, npc: NPC) {
    init {

        val omartDialogueStage = player.tempAttribs.getI(OMART_TEMP_STAGE)

        player.startConversation {
            when (player.questManager.getStage(Quest.BIOHAZARD)) {

                in STAGE_SPEAK_TO_OMART..STAGE_JERICO_SUGGESTS_PIGEONS  -> {
                    player(CALM_TALK, "Omart, Jerico said you might be able to help me.")
                    npc(npc, CALM_TALK, "He informed me of your problem traveller. I would be glad to help, I have a rope ladder and my associate, Kilron, is waiting on the other side.")
                    player(CALM_TALK, "Good stuff.")
                    npc(npc, CALM_TALK, "Unfortunately we can't risk it with the watch tower so close. So first we need to distract the guards in the tower.")
                    player(SKEPTICAL_THINKING, "How?")
                    npc(npc, CALM_TALK, "Try asking Jerico, if he's not too busy with his pigeons. I'll be waiting here for you.") { player.questManager.setStage(Quest.BIOHAZARD, STAGE_RETURN_TO_JERICO) }
                }

                STAGE_MOURNERS_DISTRACTED -> {
                    when (omartDialogueStage) {
                        0 -> {
                            npc(npc, CALM_TALK, "Well done, the guards are having real trouble with those birds. You must go now traveller, it's your only chance.")
                            exec {
                                player.sendMessage("Omart calls to his associate.")
                                player.tempAttribs.incI(OMART_TEMP_STAGE)
                            }
                        }
                        1 -> {
                            npc(npc, CALM_TALK, "Kilron!")
                            exec {
                                player.sendMessage("He throws one end of the rope ladder over the wall.")
                                player.tempAttribs.incI(OMART_TEMP_STAGE)
                            }
                        }
                        2 -> {
                            npc(npc, CALM_TALK, "You must go now traveller.")
                            options {
                                op("Ok, let's do it.") {
                                    player(CALM_TALK, "Ok, let's do it.")
                                    exec { BiohazardUtils(player).handleWallCrossing() }
                                }
                                op("I'll be back soon.") {
                                    player(CALM_TALK, "I'll be back soon.")
                                    npc(npc, CALM_TALK, "Don't take too long, those mourners will soon be rid of those birds.")
                                }
                            }
                        }
                    }
                }

                in STAGE_COMPLETED_WALL_CROSSING..STAGE_FOUND_DISTILLATOR -> {
                    player(CALM_TALK, "Hello Omart.")
                    npc(npc, CALM_TALK, "Hello traveller. The guards are still distracted if you wish to cross the wall.")
                    options {
                        op("Ok, let's do it.") {
                            player(CALM_TALK, "Ok, let's do it.")
                            exec { BiohazardUtils(player).handleWallCrossing() }
                        }
                        op("I'll be back soon.") {
                            player(CALM_TALK, "I'll be back soon.")
                            npc(npc, CALM_TALK, "Don't take too long, those mourners will soon be rid of those birds.")
                        }
                    }
                }

                in STAGE_RECEIVED_VIALS..STAGE_COMPLETE -> {
                    player(CALM_TALK, "Hello Omart.")
                    npc(npc, CALM_TALK, "Hello Adventurer. I'm afraid it's too risky to use the ladder again.")
                    player(HAPPY_TALKING, "That's alright. I have permission to enter via the main gates now.")
                }

                else -> {
                    player(CALM_TALK, "Hello there.")
                    npc(npc, CALM_TALK, "Hello.")
                    player(CALM_TALK, "How are you?")
                    npc(npc, CALM_TALK, "Fine thanks.")
                }

            }
        }
    }
}
