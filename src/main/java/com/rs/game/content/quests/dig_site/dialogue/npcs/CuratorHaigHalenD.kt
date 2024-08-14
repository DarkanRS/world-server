package com.rs.game.content.quests.dig_site.dialogue.npcs

import com.rs.engine.dialogue.HeadE.*
import com.rs.engine.dialogue.startConversation
import com.rs.engine.quest.Quest
import com.rs.game.content.quests.dig_site.utils.*
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.player.Player
import com.rs.lib.game.Item

class CuratorHaigHalenD(val player: Player, val npc: NPC) {
    init {
        player.startConversation {
            when (player.questManager.getStage(Quest.DIG_SITE)) {

                STAGE_GET_LETTER_STAMPED -> {
                    player(CALM_TALK, "I have been given this letter by an examiner at the Dig Site. Can you stamp this for me?")
                    npc(npc, SKEPTICAL_THINKING, "What have we here? A letter of recommendation indeed...")
                    npc(npc, TALKING_ALOT, "The letter here says your name is ${player.displayName}. Well, ${player.displayName}," +
                            " you appear to be fit for the task to me, and I like your proactive approach. Run this letter")
                    npc(npc, TALKING_ALOT, "back to the Examiner to begin your adventure into the world of Earth Sciences. Enjoy your studies, Student!")
                    npc(npc, TALKING_ALOT, "There you go, good luck student... Be sure to come back and show me your certificates. I would like to see how you get on.") {
                        player.inventory.replace(UNSTAMPED_LETTER, SEALED_LETTER)
                        player.setQuestStage(Quest.DIG_SITE, STAGE_RECEIVED_SEALED_LETTER)
                    }
                    player(HAPPY_TALKING, "Okay, I will. Thanks, see you later.")
                }

                STAGE_RECEIVED_SEALED_LETTER -> {
                    if (player.inventory.containsOneItem(SEALED_LETTER) || player.bank.containsItem(SEALED_LETTER)) {
                        npc(npc, CALM_TALK, "I see you still have that letter I stamped for you. Why don't you run it back to an examiner at the Exam Centre?")
                    } else {
                        player(SAD, "I seem to have lost the letter of recommendation that you stamped for me.")
                        if (player.inventory.hasFreeSlots()) {
                            npc(npc, CALM_TALK, "Yes, I saw you drop it as you walked off last time. Here it is.") { player.inventory.addItem(SEALED_LETTER) }
                            player(CALM_TALK, "Thanks!")
                        } else {
                            npc(npc, CALM_TALK, "Yes, I saw you drop it as you walked off last time. I'd give you it back but you don't have enough room in your inventory to take it from me.")
                        }

                    }
                }

            }
        }
    }

    fun handInCerts(item: Item?) {
        player.startConversation {
            val detectedItem = item ?: when {
                player.inventory.containsOneItem(EXAM_1_CERT) -> Item(EXAM_1_CERT)
                player.inventory.containsOneItem(EXAM_2_CERT) -> Item(EXAM_2_CERT)
                player.inventory.containsOneItem(EXAM_3_CERT) -> Item(EXAM_3_CERT)
                else -> null
            }

            when (detectedItem?.id) {
                EXAM_1_CERT -> {
                    player(CALM_TALK, "Look what I have been awarded.")
                    npc(npc, CALM_TALK, "Well that's great, well done. I'll take that for safekeeping; come and tell me when you are the next level.")
                    exec { player.inventory.deleteItem(EXAM_1_CERT, 1) }
                }

                EXAM_2_CERT -> {
                    npc(npc, CALM_TALK, "Excellent work! I'll take that for safekeeping. Remember to come and see me when you have graduated.")
                    exec { player.inventory.deleteItem(EXAM_2_CERT, 1) }
                }

                EXAM_3_CERT -> {
                    player(CALM_TALK, "Look at this certificate, curator...")
                    npc(npc, CALM_TALK, "Well, well - a level 3 graduate! I'll keep your certificate safe for you. I feel I must reward you for your work...")
                    npc(npc, CALM_TALK, "What would you prefer: something to eat or drink?")
                    options {
                        op("Something to eat please.") {
                            player(CALM_TALK, "Something to eat please.")
                            npc(npc, CALM_TALK, "Very good! Come and eat this cake I baked!")
                            exec {
                                player.inventory.deleteItem(EXAM_3_CERT, 1)
                                player.inventory.addItem(CHOCOLATE_CAKE)
                            }
                        }
                        op("Something to drink please.") {
                            player(CALM_TALK, "Something to drink please.")
                            npc(npc, CALM_TALK, "Very well! Come and drink this cocktail!")
                            exec {
                                player.inventory.deleteItem(EXAM_3_CERT, 1)
                                player.inventory.addItem(FRUIT_BLAST)
                            }
                        }
                    }
                }
            }
        }
    }
}
