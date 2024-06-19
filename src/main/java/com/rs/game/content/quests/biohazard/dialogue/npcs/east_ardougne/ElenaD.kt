package com.rs.game.content.quests.biohazard.dialogue.npcs.east_ardougne

import com.rs.engine.cutscenekt.cutscene
import com.rs.engine.dialogue.HeadE.*
import com.rs.engine.dialogue.startConversation
import com.rs.engine.quest.Quest
import com.rs.game.content.quests.biohazard.utils.*
import com.rs.game.content.quests.plague_city.utils.MUD_PATCH_VB
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.player.Player

class ElenaD(player: Player, npc: NPC) {
    init {

        val gotDistillator = player.questManager.getAttribs(Quest.BIOHAZARD).getB(GOT_DISTILLATOR)

        player.startConversation {
            when (player.questManager.getStage(Quest.BIOHAZARD)) {

                STAGE_UNSTARTED -> {
                    player(CALM_TALK, "Good day to you, Elena.")
                    npc(npc, HAPPY_TALKING, "You too, thanks for freeing me.")
                    npc(npc, SAD, "It's just a shame the mourners confiscated my equipment.")
                    player(SKEPTICAL_THINKING, "What did they take?")
                    npc(npc, CALM_TALK, "My distillator, I can't test any plague samples without it. They're holding it in the mourner quarters in West Ardougne.")
                    npc(npc, CALM_TALK, "I must somehow retrieve that distillator if I am to find a cure for this awful affliction.")
                    questStart(Quest.BIOHAZARD)
                    player(CALM_TALK, "I'll try to retrieve it for you.")
                    npc(npc, CALM_TALK, "I was hoping you would say that. Unfortunately they discovered the tunnel and filled it in. We need another way over the wall.")
                    player(SKEPTICAL_THINKING, "Any ideas?")
                    npc(npc, CALM_TALK, "My father's friend Jerico is in communication with West Ardougne. He might be able to help us, he lives next to the chapel.") {
                        player.questManager.setStage(Quest.BIOHAZARD, STAGE_SPEAK_TO_JERICO)
                        player.vars.setVarBit(MUD_PATCH_VB, 0)
                    }
                }

                STAGE_SPEAK_TO_JERICO -> {
                    player(CALM_TALK, "Hello Elena.")
                    npc(npc, CALM_TALK, "Hello brave adventurer. Any luck finding my distillator?")
                    player(SHAKING_HEAD, "No, I'm afraid not.")
                    npc(npc, CALM_TALK, "Speak to Jerico, he will help you to cross the wall. He lives next to the chapel.")
                }

                in STAGE_SPEAK_TO_OMART..STAGE_JERICO_SUGGESTS_PIGEONS -> {
                    player(CALM_TALK, "Hello Elena, I've spoken to Jerico.")
                    npc(npc, CALM_TALK, "Was he able to help?")
                    player(CALM_TALK, "He has two friends who will help me cross the wall, but first I need to distract the watch tower.")
                    npc(npc, SKEPTICAL, "Hmm, could be tricky.")
                }

                STAGE_MOURNERS_DISTRACTED -> {
                    player(CALM_TALK, "Elena: I've distracted the guards at the watch tower.")
                    npc(npc, CALM_TALK, "Yes, I saw. Quickly meet with Jerico's friends and cross the wall before the pigeons fly off.")
                }

                STAGE_COMPLETED_WALL_CROSSING -> {
                    player(CALM_TALK, "Hello again.")
                    npc(npc, CALM_TALK, "You're back, did you find the distillator?")
                    player(SHAKING_HEAD, "I'm afraid not.")
                    npc(npc, CALM_TALK, "I can't test the samples without the distillator. Please don't give up until you find it.")
                }

                STAGE_FOUND_DISTILLATOR -> {
                    npc(npc, CALM_TALK, "So, have you managed to retrieve my distillator?")
                    if (!gotDistillator) {
                        player(SHAKING_HEAD, "I'm afraid not.")
                        npc(npc, CALM_TALK, "Oh, you haven't... People may be dying even as we speak.")
                    } else {
                        if (!player.inventory.containsOneItem(DISTILLATOR) && player.bank.containsItem(DISTILLATOR)) {
                            player(SHAKING_HEAD, "I'm afraid I've left it in my bank.")
                            npc(npc, CALM_TALK, "Oh, please bring it to me at once... People may be dying even as we speak.")
                        } else if (player.inventory.containsOneItem(DISTILLATOR)) {
                            player(CALM_TALK, "Yes, here it is!")
                            npc(npc, CALM_TALK, "You have? That's great! Now can you pass me those reaction agents please?")
                            player(CALM_TALK, "Those look pretty fancy.") { player.sendMessage("You hand Elena the distillator and an assortment of vials.") }
                            npc(npc, CALM_TALK, "Well, yes and no. The liquid honey isn't worth much, but the others are. Especially this colourless ethenea. Be careful with the sulphuric broline, it's highly poisonous.")
                            player(CALM_TALK, "You're not kidding, I can smell it from here!")
                            exec {
                                player.cutscene {
                                    player.sendMessage("Elena puts the agents through the distillator.")
                                    wait(3)
                                    dialogue {
                                        npc(npc, CALM_TALK, "I don't understand... the touch paper hasn't changed colour at all...")
                                        npc(npc, CALM_TALK, "You'll need to go and see my old mentor Guidor. He lives in Varrock. Take these vials and this sample to him.")
                                    }
                                    waitForDialogue()
                                    player.inventory.deleteItem(DISTILLATOR, player.inventory.getAmountOf(DISTILLATOR))
                                    BiohazardUtils(player).handleReceivingVials()
                                    player.questManager.setStage(Quest.BIOHAZARD, STAGE_RECEIVED_VIALS)
                                    wait(3)
                                    dialogue {
                                        npc(npc, CALM_TALK, "But first you'll need some more touch paper. Go and see the chemist in Rimmington.")
                                        npc(npc, CALM_TALK, "Just don't get into any fights, and be careful who you speak to.")
                                        npc(npc, CALM_TALK, "Those vials are fragile, and plague carriers don't tend to be too popular.")
                                    }
                                    waitForDialogue()
                                }
                            }
                        }
                    }
                }

                in STAGE_RECEIVED_VIALS..STAGE_RECEIVED_TOUCH_PAPER -> {
                    npc(npc, CALM_TALK, "What are you doing back here?")
                    options {
                        op("I just find it hard to say goodbye sometimes.") {
                            player(CALM_TALK, "I just find it hard to say goodbye sometimes.")
                            npc(npc, CALM_TALK, "Yes... I have feelings for you too... Now get back to work!")
                        }
                        op("I'm afraid I've lost some of the stuff that you gave me...") {
                            player(CALM_TALK, "I'm afraid I've lost some of the stuff that you gave me...")
                            npc(npc, CALM_TALK, "That's alright, I've got plenty.")
                            exec {
                                player.cutscene {
                                    wait(2)
                                    BiohazardUtils(player).handleReceivingVials()
                                    wait(2)
                                    dialogue {
                                        npc(npc, CALM_TALK, "Ok, so that's your colourless Ethenea... Some highly toxic sulphuric broline... And some bog-standard liquid honey...")
                                        player(CALM_TALK, "Great, I'll be on my way.")
                                    }
                                }
                            }
                        }
                        op("I've forgotten what I need to do.") {
                            player(CALM_TALK, "I've forgotten what I need to do.")
                            npc(npc, CALM_TALK, "Go to Rimmington and get some touch paper from the chemist. Use his errand boys to smuggle the vials into Varrock. Then collect the samples and take them to Guidor, my old mentor.")
                            player(CALM_TALK, "Ok, I'll get to it.")
                        }
                    }
                }

                STAGE_RETURN_TO_ELENA -> {
                    npc(npc, CALM_TALK, "You're back! So what did Guidor say?")
                    player(CALM_TALK, "Nothing.")
                    npc(npc, CONFUSED, "What?")
                    player(CALM_TALK, "He said that there is no plague.")
                    npc(npc, CONFUSED, "So what, this thing has all been a big hoax?")
                    player(CALM_TALK, "Or maybe we're about to uncover something huge.")
                    npc(npc, CALM_TALK, "Then I think this thing may be bigger than both of us.")
                    player(CONFUSED, "What do you mean?")
                    npc(npc, CALM_TALK, "I mean you need to go right to the top... You need to see the King of East Ardougne!") { player.setQuestStage(Quest.BIOHAZARD, STAGE_SPEAK_TO_KING) }
                }

                STAGE_SPEAK_TO_KING -> {
                    player(CALM_TALK, "Hello Elena.")
                    npc(npc, CALM_TALK, "You must go see King Lathas immediately!")
                }

                STAGE_COMPLETE -> {
                    player(HAPPY_TALKING, "Hello Elena.")
                    npc(npc, HAPPY_TALKING, "Hey, how are you?")
                    player(HAPPY_TALKING, "Good thanks, yourself?")
                    npc(npc, HAPPY_TALKING, "Not bad, let me know when you hear from King Lathas again.")
                    player(HAPPY_TALKING, "Will do.")
                }

            }
        }
    }
}
