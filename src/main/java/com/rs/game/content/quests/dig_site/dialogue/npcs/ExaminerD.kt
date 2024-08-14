package com.rs.game.content.quests.dig_site.dialogue.npcs

import com.rs.engine.dialogue.HeadE.*
import com.rs.engine.dialogue.startConversation
import com.rs.engine.quest.Quest
import com.rs.game.content.quests.dig_site.dialogue.ExamsD
import com.rs.game.content.quests.dig_site.utils.*
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.player.Player

class ExaminerD(val player: Player, val npc: NPC) {
    init {
        player.startConversation {
            when (player.questManager.getStage(Quest.DIG_SITE)) {

                STAGE_UNSTARTED -> {
                    player(CALM_TALK, "Hello.")
                    npc(npc, CALM_TALK, "Ah hello there! I am the resident lecturer on antiquities and artefacts. I also set the Earth Sciences exams.")
                    player(CALM_TALK, "Earth Sciences?")
                    npc(npc, CALM_TALK, "That is right dear, the world of Gielinor holds many wonders beneath its surface. Students come to me to take exams so that they may join in on the archaeological dig going on just north of here.")
                    player(CALM_TALK, "So if they don't pass the exams they can't dig at all?")
                    npc(npc, CALM_TALK, "That's right! We have to make sure that students know enough to be able to dig safely and not damage the artefacts.")
                    if (!Quest.DIG_SITE.meetsReqs(player, "before you can take an exam.")) else
                        if (player.inventory.hasFreeSlots()) {
                            questStart(Quest.DIG_SITE)
                            player(CALM_TALK, "Can I take an exam?")
                            npc(npc, CALM_TALK, "You can if you get this letter stamped by the Curator of Varrock's museum.") {
                                player.inventory.addItem(UNSTAMPED_LETTER)
                                player.setQuestStage(Quest.DIG_SITE, STAGE_GET_LETTER_STAMPED)
                            }
                            player(CALM_TALK, "Why's that then?")
                            npc(npc, CALM_TALK, "Because he is a very knowledgeable man and employs our archaeological expert. I'm sure he knows a lot about your exploits and can judge whether you'd make a good archaeologist or not.")
                            npc(npc, CALM_TALK, "Besides, the museum contributes funds to the dig.")
                            player(CALM_TALK, "But why are you writing the letter? Shouldn't he?")
                            npc(npc, CALM_TALK, "He's also a very busy man, so I write the letters and he just stamps them if he approves.")
                            player(CALM_TALK, "Oh, I see. I'll ask him if he'll approve me, and bring my stamped letter back here. Thanks.")
                        } else {
                            simple("You need at least 1 free inventory slot to start the Dig Site quest.")
                        }
                }

                STAGE_GET_LETTER_STAMPED -> {
                    player(CALM_TALK, "Hello.")
                    npc(npc, CALM_TALK, "Hello again.")
                    npc(npc, CALM_TALK, "I am still waiting for your letter of recommendation.")
                    options {
                        op("I have lost the letter you gave me.") {
                            player(CALM_TALK, "I have lost the letter you gave me.")
                            if (player.inventory.containsOneItem(UNSTAMPED_LETTER)) {
                                npc(npc, CALM_TALK, "Oh now come on. You have it with you!")
                            } else if (player.bank.containsItem(UNSTAMPED_LETTER)) {
                                npc(npc, CALM_TALK, "You already have the letter in your bank.")
                            } else {
                                if (player.inventory.hasFreeSlots()) {
                                    npc(npc, CALM_TALK, "That was foolish. Take this one and don't lose it!") { player.inventory.addItem(UNSTAMPED_LETTER) }
                                } else {
                                    npc(npc, CALM_TALK, "That was foolish. I'd give you another but you don't have room in your inventory for it.")
                                }
                            }
                        }
                        op("Alright I'll try and get it.") {
                            player(CALM_TALK, "Alright, I'll try and get it.")
                            npc(npc, CALM_TALK, "I am sure you won't get any problems. Speak to the Curator of Varrock's Museum.")
                        }
                    }
                }

                STAGE_RECEIVED_SEALED_LETTER -> {
                    player(CALM_TALK, "Hello.")
                    npc(npc, CALM_TALK, "Hello again.")
                    if (!player.inventory.containsOneItem(SEALED_LETTER)) {
                        npc(npc, CALM_TALK, "I am still waiting for your letter of recommendation.")
                        player(CALM_TALK, "Alright, I'll try and get it.")
                        npc(npc, CALM_TALK, "I am sure you won't get any problems. Speak to the Curator of Varrock's Museum.")
                    } else {
                        player(CALM_TALK, "Here is the stamped letter you asked for.")
                        npc(npc, CALM_TALK, "Good, good. We will begin the exam...") {
                            player.inventory.deleteItem(SEALED_LETTER, player.inventory.getAmountOf(SEALED_LETTER))
                            player.setQuestStage(Quest.DIG_SITE, STAGE_BEGIN_EXAM_1)
                        }
                        exec { ExamsD(player, npc).exam1() }
                    }
                }

                STAGE_BEGIN_EXAM_1 -> {
                    player(CALM_TALK, "Hello.")
                    npc(npc, CALM_TALK, "Hello again. Are you ready for another shot at the exam?")
                    options {
                        op("Yes, I certainly am.") {
                            player(CALM_TALK, "Yes, I certainly am.")
                            exec { ExamsD(player, npc).exam1() }
                        }
                        op("No, not at the moment.") {
                            player(CALM_TALK, "No, not at the moment.")
                            npc(npc, CALM_TALK, "Okay, take your time if you wish.")
                        }
                    }
                }

                STAGE_BEGIN_EXAM_2 -> {
                    player(CALM_TALK, "Hello.")
                    npc(npc, CALM_TALK, "Hi there!")
                    options {
                        op("I am ready for the next exam.") {
                            player(CALM_TALK, "I am ready for the next exam.")
                            exec { ExamsD(player, npc).exam2() }
                        }
                        op("I am stuck on a question.") {
                            player(CALM_TALK, "I am stuck on a question.")
                            npc(npc, CALM_TALK, "Well, well, have you not been doing any studies?")
                            npc(npc, CALM_TALK, "I am not giving you the answers, talk to the other students and remember the answers.")
                        }
                        op("Sorry, I didn't mean to disturb you.") {
                            player(CALM_TALK, "Sorry, I didn't mean to disturb you.")
                            npc(npc, CALM_TALK, "Oh, no problem at all.")
                        }
                        op("I have lost my trowel.") {
                            player(CALM_TALK, "I have lost my trowel.")
                            if (!player.inventory.containsOneItem(TROWEL)) {
                                if (player.inventory.hasFreeSlots()) {
                                    npc(npc, CALM_TALK, "Deary me. That was a good one as well. It's a good job I have another. Here you go...") {
                                        player.inventory.addItem(TROWEL)
                                    }
                                } else {
                                    npc(npc, CALM_TALK, "Deary me. That was a good one as well. It's a good job I have another, but you'll need a free inventory slot first.")
                                }
                            } else {
                                npc(npc, CALM_TALK, "Really? Look in your backpack and make sure first.")
                            }
                        }
                    }
                }

                STAGE_BEGIN_EXAM_3 -> {
                    player(CALM_TALK, "Hello.")
                    npc(npc, CALM_TALK, "Ah, hello again.")
                    options {
                        op("I am ready for the last exam...") {
                            player(CALM_TALK, "I am ready for the last exam...")
                            exec { ExamsD(player, npc).exam3() }
                        }
                        op("I am stuck on a question.") {
                            player(CALM_TALK, "I am stuck on a question.")
                            npc(npc, CALM_TALK, "Well, well, have you not been doing any studies?")
                            npc(npc, CALM_TALK, "I am not giving you the answers, talk to the other students and remember the answers.")
                        }
                        op("Sorry, I didn't mean to disturb you.") {
                            player(CALM_TALK, "Sorry, I didn't mean to disturb you.")
                            npc(npc, CALM_TALK, "Oh, no problem at all.")
                        }
                        op("I have lost my trowel.") {
                            player(CALM_TALK, "I have lost my trowel.")
                            if (!player.inventory.containsOneItem(TROWEL)) {
                                if (player.inventory.hasFreeSlots()) {
                                    npc(npc, CALM_TALK, "Deary me. That was a good one as well. It's a good job I have another. Here you go...") {
                                        player.inventory.addItem(TROWEL)
                                    }
                                } else {
                                    npc(npc, CALM_TALK, "Deary me. That was a good one as well. It's a good job I have another, but you'll need a free inventory slot first.")
                                }
                            } else {
                                npc(npc, CALM_TALK, "Really? Look in your backpack and make sure first.")
                            }
                        }
                    }
                }

                in STAGE_COMPLETED_EXAMS..STAGE_BLOWN_UP_BRICKS -> {
                    npc(npc, CALM_TALK, "Well, what are you doing here? Get digging!")
                    options {
                        op("Sorry, I didn't mean to disturb you.") {
                            player(CALM_TALK, "Sorry, I didn't mean to disturb you.")
                            npc(npc, CALM_TALK, "Oh, no problem at all.")
                        }
                        op("I have lost my trowel.") {
                            player(CALM_TALK, "I have lost my trowel.")
                            if (!player.inventory.containsOneItem(TROWEL)) {
                                if (player.inventory.hasFreeSlots()) {
                                    npc(npc, CALM_TALK, "Deary me. That was a good one as well. It's a good job I have another. Here you go...") {
                                        player.inventory.addItem(TROWEL)
                                    }
                                } else {
                                    npc(npc, CALM_TALK, "Deary me. That was a good one as well. It's a good job I have another, but you'll need a free inventory slot first.")
                                }
                            } else {
                                npc(npc, CALM_TALK, "Really? Look in your backpack and make sure first.")
                            }
                        }
                    }
                }

                STAGE_COMPLETE -> {
                    npc(npc, CALM_TALK, "Hello there! My colleague tells me you helped to uncover a hidden altar to the god Zaros.")
                    npc(npc, CALM_TALK, "A great scholar and archaeologist indeed! Good health and prosperity to you.")
                    options {
                        op("Thanks!") { player(CALM_TALK, "Thanks!") }
                        op("I have lost my trowel.") {
                            player(CALM_TALK, "I have lost my trowel.")
                            if (!player.inventory.containsOneItem(TROWEL)) {
                                if (player.inventory.hasFreeSlots()) {
                                    npc(npc, CALM_TALK, "Deary me. That was a good one as well. It's a good job I have another. Here you go...") {
                                        player.inventory.addItem(TROWEL)
                                    }
                                } else {
                                    npc(npc, CALM_TALK, "Deary me. That was a good one as well. It's a good job I have another, but you'll need a free inventory slot first.")
                                }
                            } else {
                                npc(npc, CALM_TALK, "Really? Look in your backpack and make sure first.")
                            }
                        }
                    }
                }

            }
        }
    }
}
