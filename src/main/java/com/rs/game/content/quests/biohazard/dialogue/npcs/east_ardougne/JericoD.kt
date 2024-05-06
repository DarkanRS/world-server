package com.rs.game.content.quests.biohazard.dialogue.npcs.east_ardougne

import com.rs.engine.dialogue.HeadE.*
import com.rs.engine.dialogue.startConversation
import com.rs.engine.quest.Quest
import com.rs.game.content.quests.biohazard.utils.*
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.player.Player

class JericoD(player: Player, npc: NPC) {
    init {
        player.startConversation {
            when (player.questManager.getStage(Quest.BIOHAZARD)) {

                in STAGE_SPEAK_TO_JERICO..STAGE_SPEAK_TO_OMART -> {
                    player(CALM_TALK, "Hello Jerico.")
                    npc(npc, CALM_TALK, "Hello, I've been expecting you. Elena tells me you need to cross the wall.")
                    player(CALM_TALK, "That's right.")
                    npc(npc, CALM_TALK, "My messenger pigeons help me communicate with friends over the wall.")
                    npc(npc, CALM_TALK, "I have arranged for two friends to aid you with a rope ladder. Omart is waiting for you at the south end of the wall.") { player.questManager.setStage(Quest.BIOHAZARD, STAGE_SPEAK_TO_OMART) }
                    npc(npc, CALM_TALK, "But be careful, if the mourners catch you the punishment will be severe.")
                    player(CALM_TALK, "Thanks Jerico.")
                }

                in STAGE_RETURN_TO_JERICO..STAGE_JERICO_SUGGESTS_PIGEONS -> {
                    player(SKEPTICAL_THINKING, "Hello Jerico, I need someway to distract the watch tower, any ideas?")
                    npc(npc, SHAKING_HEAD, "Hmmm. Nothing springs to mind.")
                    label("initialOps")
                    options {
                        op("Maybe you could shout and scream, and call them away?") {
                            player(CALM_TALK, "Maybe you could shout and scream, and call them away?")
                            npc(npc, LAUGH, "So they chase after me?")
                            player(CALM_TALK, "Yes. How quickly can you run?")
                            npc(npc, CALM_TALK, "No. I don't like this idea.")
                            goto("initialOps")
                        }
                        op("Maybe I could use your messenger pigeons to distract them?") {
                            player(SKEPTICAL, "Maybe I could use your messenger pigeons to distract them?")
                            npc(npc, CALM_TALK, "You might have some luck with that idea. The pigeons are around the back of my house if you want to try that.")
                            player(SKEPTICAL_THINKING, "Ok, maybe I'll give it a go.") {
                                player.questManager.setStage(Quest.BIOHAZARD, STAGE_JERICO_SUGGESTS_PIGEONS)
                            }
                        }
                        op("Maybe if I'm really quiet they won't notice me?") {
                            player(CALM_TALK, "Maybe if I'm really quiet they won't notice me?")
                            npc(npc, CALM_TALK, "And what stops them seeing you?")
                            player(CALM_TALK, "Well... perhaps I wait till nightfall?")
                            npc(npc, CALM_TALK, "There's no time for that.")
                            goto("initialOps")
                        }
                        op("I can't think of anything either.") {
                            player(SHAKING_HEAD, "I can't think of anything either.")
                            npc(npc, CALM_TALK, "That's too bad.")
                        }
                    }
                }

                STAGE_MOURNERS_DISTRACTED -> {
                    player(CALM_TALK, "Hello there.")
                    npc(npc, CALM_TALK, "The guards are distracted by the birds, you must go now, quickly traveller.")
                }

                in STAGE_COMPLETED_WALL_CROSSING..STAGE_APPLE_IN_CAULDRON -> {
                    player(CALM_TALK, "Hello again Jerico.")
                    npc(npc, CALM_TALK, "So you've returned traveller. Did you get what you wanted?")
                    player(SHAKING_HEAD, "Not yet.")
                    npc(npc, CALM_TALK, "Omart will be waiting by the wall, in case you need to cross again.")
                }

                in STAGE_FOUND_DISTILLATOR..STAGE_COMPLETE -> { player.sendMessage("Jerico is busy looking for his bird feed.") }

                else -> {
                    player(CALM_TALK, "Hello.")
                    npc(npc, CALM_TALK, "Can I help you?")
                    player(CALM_TALK, "Just passing by.")
                }

            }
        }
    }
}
