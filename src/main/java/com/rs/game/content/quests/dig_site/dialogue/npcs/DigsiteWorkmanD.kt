package com.rs.game.content.quests.dig_site.dialogue.npcs

import com.rs.engine.dialogue.HeadE.*
import com.rs.engine.dialogue.startConversation
import com.rs.engine.quest.Quest
import com.rs.game.content.quests.dig_site.utils.*
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.player.Player

class DigsiteWorkmanD(val player: Player, val npc: NPC) {
    init {
        player.startConversation {
            when (player.getQuestStage(Quest.DIG_SITE)) {

                in STAGE_UNSTARTED..STAGE_BLOWN_UP_BRICKS -> {
                    player(CALM_TALK, "Hello there.")
                    npc(npc, CALM_TALK, "Good day; what can I do for you?")
                    options {
                        if (player.inventory.containsOneItem(INVITATION_LETTER) && player.getQuestStage(Quest.DIG_SITE) == STAGE_RECEIVED_INVITATION)
                            op("Here, have a look at this...") {
                                exec { showInvitation() }
                            }
                        op("What do you do here?") {
                            player(CALM_TALK, "What do you do here?")
                            npc(npc, CALM_TALK, "I am involved in various stages of the dig, from the initial investigation to the installation of the mine shafts.")
                            player(CALM_TALK, "Oh, okay, thanks.")
                        }
                        op("I'm not sure...") {
                            player(CALM_TALK, "I'm not sure...")
                            npc(npc, CALM_TALK, "Well, let me know when you are.")
                        }
                        op("Can I dig around here?") {
                            player(CALM_TALK, "Can I dig around here?")
                            npc(npc, CALM_TALK, "You can only use a site you have the appropriate exam level for.")
                            options {
                                op("Appropriate exam level?") {
                                    player(CALM_TALK, "Appropriate exam level?")
                                    npc(npc, CALM_TALK, "Yes, only persons with the correct Earth Sciences certificate can dig here. A level 1 certificate will let you dig in a level 1 site and so on.")
                                    player(CALM_TALK, "Ah, yes; I understand.")
                                }
                                op("I am already skilled in digging.") {
                                    player(CALM_TALK, "I am already skilled in digging.")
                                    npc(npc, CALM_TALK, "Well that's nice for you. You can't dig around here without a certificate though.")
                                }
                            }
                        }
                    }
                }

                STAGE_COMPLETE -> {
                    npc(npc, CALM_TALK, "Oh wow! You're the archaeologist who found that altar in the mines aren't you!")
                    player(CALM_TALK, "Um, yes.")
                    npc(npc, CALM_TALK, "So glad to meet you! Well done!")
                }

            }

        }
    }

    fun showInvitation() {
        if (player.inventory.containsOneItem(INVITATION_LETTER) && player.getQuestStage(Quest.DIG_SITE) == STAGE_RECEIVED_INVITATION)
            player.startConversation {
                player(CALM_TALK, "Here, have a look at this...")
                npc(npc, CALM_TALK, "I give permission... blah de blah... etc. Okay, that's all in order; you may use the mineshaft now. I'll hang onto this scroll, shall I?") {
                    player.inventory.deleteItem(INVITATION_LETTER, 1)
                    player.setQuestStage(Quest.DIG_SITE, STAGE_PERMISSION_GRANTED)
                }
            }
        else
            player.sendMessage("Nothing interesting happens.")
    }
}
